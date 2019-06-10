package pl.ewa.tess4j.makieta;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FileUtils;
import pl.ewa.tess4j.db.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pl.ewa.tess4j.db.DBService.saveDB;

public class Makieta extends JFrame {

    private static TworzenieDrzewa tr = new TworzenieDrzewa();
    DefaultMutableTreeNode selectedNode;
    private JTextArea jTextAreaListaProduktow;
    private JTextArea jTextAreaListaZakupow;
    private JButton wczytajParagonButton;
    private JButton zapiszPlikButton;
    private JButton otwórzPlikButton;
    private JButton zamknijButton;
    private JPanel panelGlowny;
    private JList lista_Zakupow;
    private JLabel labelListaZakupow;
    private JLabel labelListaProduktow;
    private JButton button_zListyProdDoZakupow;
    private JList listProduktow;
    private JTree treeProduktow;
    private JTextField textFieldElementListyProduktow;
    private JTextField textFieldDodawanyEl;
    private JButton button_DodajKategorie;
    private JButton usunButton;
    private JLabel labelEl;
    private JLabel labelNadPattern;
    private JLabel labelWyswietlaniePatagonu;
    private JTextField tF_DodajProdukt;
    private JButton button_DodajProdukt;
    private JLabel labelDodajKategorie;
    private JLabel labelDodajPodKategorie;
    private JTextField tF_DodajSklep;
    private JLabel labelDodajSklep;
    private JButton button_DodajSklep;
    private JButton button_EdytujElement;
    private JButton zapiszListeDoPlikuButton;
    private JTextArea textAreaSuma;
    private JLabel label_Message;
    private JTextField tF_DodajKategorie;
    private JButton button_UsunListaZakupow;
    private JButton button_DodajCene;
    private JTextField tF_DodajCeneDoWpisu;
    private JButton button_DodajIlosc;
    private JTextField tF_Dodajilosc;
    private JTextArea tA_ZakupyArea;
    private JLabel selectedLabel;

    private DefaultListModel<RowItem> listaZakupowModel = new DefaultListModel<>();

    private Object produkt;
    private Object sklep;
    private double cena = 0;
    private int ilosc = 0;

    public Makieta() {

        lista_Zakupow.setModel(listaZakupowModel);
        button_UsunListaZakupow.setEnabled(false);
        button_DodajCene.setEnabled(false);
        button_DodajIlosc.setEnabled(false);

        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                aktywujPrzyciki();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                aktywujPrzyciki();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                aktywujPrzyciki();
            }
        };
        textFieldElementListyProduktow.getDocument().addDocumentListener(listener);
        tF_DodajKategorie.getDocument().addDocumentListener(listener);
        tF_DodajProdukt.getDocument().addDocumentListener(listener);
        tF_DodajSklep.getDocument().addDocumentListener(listener);
        aktywujPrzyciki();

        dodajCeneDoWpisu();
        dodajIloscDoWpisu();

        JTree t = tr.getTree();

        DefaultTreeModel model = (DefaultTreeModel) t.getModel();

        treeProduktow.setModel(model);

        treeProduktow.addTreeSelectionListener(e -> {
            DBService.cleanDB();
            TreeModel model1 = t.getModel();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) model1.getRoot();

            for (int r = 0; r < root.getChildCount(); r++) {
                DefaultMutableTreeNode kategoriaNode = (DefaultMutableTreeNode) root.getChildAt(r);
                Kategoria kategoria = (Kategoria) kategoriaNode.getUserObject();
                for (int p = 0; p < kategoriaNode.getChildCount(); p++) {
                    DefaultMutableTreeNode produktNode = (DefaultMutableTreeNode) kategoriaNode.getChildAt(p);
                    Produkt produkt = (Produkt) produktNode.getUserObject();
                    for (int s = 0; s < produktNode.getChildCount(); s++) {
                        DefaultMutableTreeNode sklepNode = (DefaultMutableTreeNode) produktNode.getChildAt(s);
                        Sklep sklep = (Sklep) sklepNode.getUserObject();

                    }
                }
                DBService.getDb().addKategoria(kategoria);
            }


        });

        otwórzPlikButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JFileChooser fc = new JFileChooser();
                int odpowiedz = fc.showOpenDialog(panelGlowny);
                if (odpowiedz == fc.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        jTextAreaListaProduktow.read(new FileReader(file.getAbsolutePath()), null);
                        // test dla pattern
                        /*String str = FileUtils.readFileToString(file, "UTF-8");
                        findPattern(str);*/
                        // koniec testu dla pattern
                    } catch (IOException ex) {
                        System.out.println("Nie mogę otworzyć pliku: " + file.getAbsolutePath());
                        System.out.println("Problem: " + ex);
                    }
                }
            }

        });
        wczytajParagonButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                int odpowiedz = fc.showOpenDialog(panelGlowny);
                if (odpowiedz == fc.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    Tesseract instance = new Tesseract();
                    instance.setDatapath("C:\\Users\\ewaam\\IdeaProjects\\tessdata");
                    instance.setLanguage("pol");
                    String fullText;
                    try {
                        fullText = instance.doOCR(file);
                        jTextAreaListaProduktow.append(fullText);
                        FileWriter fw = new FileWriter("paragon.txt");
                        fw.write(fullText);
                        fw.close();
                        findPattern(fullText);
                    } catch (TesseractException ex) {
                        Logger.getLogger(Makieta.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Makieta.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        zapiszPlikButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                int odpowiedz = fc.showSaveDialog(panelGlowny);
                if (odpowiedz == fc.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        FileWriter out = new FileWriter(file);
                        out.write(jTextAreaListaProduktow.getText());
                        out.close();
                    } catch (IOException ex) {
                        System.out.println("Nie mogę zapisać pliku: " + file.getAbsolutePath());
                        System.out.println("Problem: " + ex);
                    }
                }
            }
        });
        zamknijButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveDB();
                System.exit(0);
            }
        });
        // pozwala na edycje wybranego elementu galezi
        treeProduktow.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                edytujWybranyElementDrzewa();
            }
        });
        // pozwala ENTEREM dokonac edycji
        textFieldElementListyProduktow.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    dodajProduktDoKategorii();
                }
            }
        });

        usunButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usunWybranyElementDrzewa(model);
            }
        });

        KeyListener kl = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    usunWybranyElementDrzewa(model);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        };
        treeProduktow.addKeyListener(kl);

        tF_DodajKategorie.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    dodajProduktDoKategorii();
                }
            }
        });

        button_DodajKategorie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dodajKategorieDoDrzewa(model);
            }
        });

        tF_DodajProdukt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    dodajProduktDoKategorii();
                }
            }
        });
        button_DodajProdukt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dodajProduktDoKategorii();
            }
        });

        tF_DodajSklep.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    dodajSklepDoProduktu();
                }
            }
        });

        button_DodajSklep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dodajSklepDoProduktu();
            }
        });

        button_EdytujElement.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode wybranaGalaz = (DefaultMutableTreeNode) treeProduktow.getSelectionPath().getLastPathComponent();
                Nameable userObject = (Nameable) wybranaGalaz.getUserObject();
                String element = textFieldElementListyProduktow.getText();
                userObject.setName(element);
                DefaultTreeModel model = (DefaultTreeModel) treeProduktow.getModel();
                model.reload(wybranaGalaz);
                saveDB();
                aktywujPrzyciki();

            }
        });
        button_zListyProdDoZakupow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode wybranaGalaz = (DefaultMutableTreeNode) treeProduktow.getSelectionPath().getLastPathComponent();
                int poziom = wybranaGalaz.getLevel();
                if (poziom == 2 || poziom == 3) {
                    //button_zListyProdDoZakupow.setEnabled(true);
                    przeniesListaProduktowDoZakupow();
                } else {
                    label_Message.setText("Nie mozna przeniesc kategorii. Wybierz produkt lub sklep");
                    //button_zListyProdDoZakupow.setEnabled(false);
                }
                saveDB();
                aktywujPrzyciki();
            }
        });
        button_UsunListaZakupow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int idx = lista_Zakupow.getSelectedIndex();
                listaZakupowModel.removeElementAt(idx);
                if (idx < listaZakupowModel.getSize()) {
                    lista_Zakupow.setSelectedIndex(idx);
                } else {
                    lista_Zakupow.setSelectedIndex(idx - 1);
                }
                if (listaZakupowModel.size() == 0) {
                    button_UsunListaZakupow.setEnabled(false);
                    textAreaSuma.setText("");
                } else {
                    button_UsunListaZakupow.setEnabled(true);
                }
            }
        });
        button_DodajCene.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dodajCeneDoWpisu();
                int wybrany = lista_Zakupow.getSelectedIndex();
                if (wybrany != -1) {
                    RowItem rowItem = (RowItem) listaZakupowModel.get(wybrany);
                    String cenaText = tF_DodajCeneDoWpisu.getText();
                    rowItem.setCena(Double.parseDouble(cenaText));
                    lista_Zakupow.setModel(listaZakupowModel);
                } else {
                    button_DodajCene.setEnabled(false);
                    label_Message.setText("Musisz wybrac rekord");
                }
                sumujCene();
                tF_DodajCeneDoWpisu.setText("");
            }
        });
        tF_DodajCeneDoWpisu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dodajCeneZPola();
                tF_DodajCeneDoWpisu.setText("");
            }
        });
        button_DodajIlosc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dodajIloscDoWpisu();
                int wybrany = lista_Zakupow.getSelectedIndex();
                if (wybrany != -1) {
                    RowItem rowItem = (RowItem) listaZakupowModel.get(wybrany);
                    String iloscText = tF_Dodajilosc.getText();
                    rowItem.setIlosc(Integer.parseInt(iloscText));
                    lista_Zakupow.setModel(listaZakupowModel);
                } else {
                    button_DodajCene.setEnabled(false);
                    label_Message.setText("Musisz wybrac rekord");
                }
                sumujCene();
                tF_Dodajilosc.setText("");
            }
        });

        tF_Dodajilosc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dodajIloscDoWpisu();
                tF_Dodajilosc.setText("");
            }
        });
        zapiszListeDoPlikuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String listaZakupow;
                try {
                    save();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void sumujCene() {
        double suma = 0.0f;
        for (int i = 0; i < listaZakupowModel.size(); i++) {
            RowItem row = (RowItem) listaZakupowModel.get(i);
            double ilosc = row.getIlosc();
            double cena = row.getCena();
            if (ilosc != 0) {
                suma += row.getCena() * row.getIlosc();
            } else {
                suma += row.getCena();
            }
        }
        textAreaSuma.setText("Przewidywana kwota zakupow: " + suma + " zl");
    }

    public static void main(String args[]) {
        JFrame frame = new JFrame("Makieta");
        frame.setContentPane(new Makieta().panelGlowny);
        frame.setSize(1200, 700);
        /*        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true)*/
        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private double dodajCeneZPola() {
        String cenaStr = tF_DodajCeneDoWpisu.getText();
        try {
            cena = Double.parseDouble(cenaStr);
            button_DodajCene.setEnabled(true);
        } catch (NumberFormatException ex) {
            button_DodajCene.setEnabled(false);
        }
        return cena;
    }

    private void dodajCeneDoWpisu() {
        final DocumentListener listener = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                dopelnijBuildera();
            }

            public void removeUpdate(DocumentEvent e) {
                dopelnijBuildera();
            }

            public void insertUpdate(DocumentEvent e) {
                dopelnijBuildera();
            }

            private void dopelnijBuildera() {
                boolean enabled = false;
                try {
                    cena = Double.parseDouble(tF_DodajCeneDoWpisu.getText());
                    enabled = true;
                } catch (NumberFormatException e) {
                }
                button_DodajCene.setEnabled(enabled);
            }
        };
        tF_DodajCeneDoWpisu.getDocument().addDocumentListener(listener);
        getRootPane().setDefaultButton(button_DodajCene);
        //wrocDoEdycji();
    }

    private void dodajIloscDoWpisu() {
        final DocumentListener listener = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                dodajIlosc();
            }

            public void removeUpdate(DocumentEvent e) {
                dodajIlosc();
            }

            public void insertUpdate(DocumentEvent e) {
                dodajIlosc();
            }

            private void dodajIlosc() {
                boolean enabled = false;
                try {
                    ilosc = Integer.parseInt(tF_Dodajilosc.getText());
                    enabled = true;
                } catch (NumberFormatException e) {
                }
                button_DodajIlosc.setEnabled(enabled);
            }
        };
        tF_Dodajilosc.getDocument().addDocumentListener(listener);
        getRootPane().setDefaultButton(button_DodajIlosc);
        //wrocDoEdycji();
    }

    private String przeniesListaProduktowDoZakupow() {
        String zlaczenie = "";
        DefaultMutableTreeNode wybranaGalaz = (DefaultMutableTreeNode) treeProduktow.getSelectionPath().getLastPathComponent();
        int poziom = wybranaGalaz.getLevel();
        if (poziom == 0 || poziom == 1) {
            button_zListyProdDoZakupow.setEnabled(false);
        } else {
            if (poziom == 2) {
                button_zListyProdDoZakupow.setEnabled(true);
                Object wybranyProdukt = wybranaGalaz.getUserObject();
                TreeNode sklep = wybranaGalaz.getFirstChild();
                /*String produktSklep = (wybranyProdukt + "\t" + sklep);
                System.out.println(produktSklep);*/
                zlaczenie = dodajDoModeluListyZakupow(wybranyProdukt, sklep);
            }
            if (poziom == 3) {
                button_zListyProdDoZakupow.setEnabled(true);
                Object wybranySklep = wybranaGalaz.getUserObject();
                TreeNode produkt = wybranaGalaz.getParent();
                /*String sklepProdukt = (produkt + "\t" + wybranySklep);
                System.out.println(sklepProdukt);*/
                zlaczenie = dodajDoModeluListyZakupow(produkt, wybranySklep);
            }
        }
        return zlaczenie;
    }

    private void wrocDoEdycji() {
        tF_DodajCeneDoWpisu.selectAll();
        tF_DodajCeneDoWpisu.requestFocus();
    }

    private String dodajDoModeluListyZakupow(Object wybrany, Object powiazany) {
        RowItem pozycja = new RowItem(wybrany, powiazany);
        listaZakupowModel.addElement(pozycja);
        lista_Zakupow.setSelectedIndex(listaZakupowModel.getSize() - 1);
        button_UsunListaZakupow.setEnabled(true);
        return String.valueOf(pozycja);
    }

    private void dodajKategorieDoDrzewa(DefaultTreeModel model) {
        label_Message.setText("");
        DefaultMutableTreeNode wybranaGalaz = (DefaultMutableTreeNode) model.getRoot();
        if (wybranaGalaz == null) {
            label_Message.setText("Musisz wybrac galaz glowna, a nastepnie wpisac nazwe kategorii");
        } else {
            Object root = model.getRoot();
            int poziom = wybranaGalaz.getLevel();
            if (poziom == 0) {
                Kategoria kategoriaDB = new Kategoria(tF_DodajKategorie.getText());
                wybranaGalaz.add(new DefaultMutableTreeNode(kategoriaDB));
                ((DefaultTreeModel) treeProduktow.getModel()).reload();
                tF_DodajKategorie.setText("");
            } else {
                label_Message.setText("Kategoria musi byc dodana do galezi glownej");
            }
        }
        aktywujPrzyciki();
    }

    private void dodajSklepDoProduktu() {
        label_Message.setText("");
        DefaultMutableTreeNode wybranaGalaz = (DefaultMutableTreeNode) treeProduktow.getLastSelectedPathComponent();
        if (wybranaGalaz == null) {
            label_Message.setText("Musisz wybrac produkt, a nastepnie wpisac nazwe sklepu");
        } else {
            if (!wybranaGalaz.isRoot()) {
                int poziom = wybranaGalaz.getLevel();
                if (poziom == 2) {
                    if (!pobierzSklep().trim().equals("")) {
                        Produkt produktDB = (Produkt) wybranaGalaz.getUserObject();
                        Sklep sklepDB = new Sklep(tF_DodajSklep.getText());
                        produktDB.addSklep(sklepDB);
                        wybranaGalaz.add(new DefaultMutableTreeNode(sklepDB));
                        ((DefaultTreeModel) treeProduktow.getModel()).reload(wybranaGalaz);
                        tF_DodajSklep.setText("");
                    } else {
                        label_Message.setText("Musisz wpisac nazwe sklepu");
                    }
                } else {
                    label_Message.setText("Nie mozna dodac sklepu do kategorii ani sklepu");
                }
            } else {
                label_Message.setText("Musisz wybrac produkt");
            }
        }
        aktywujPrzyciki();
    }

    private void edytujWybranyElementDrzewa() {
        if (treeProduktow.isSelectionEmpty())
            return;
        TreePath sciezkaDrzewa = treeProduktow.getSelectionPath();
        DefaultMutableTreeNode defaultMutableTreeNode =
                (DefaultMutableTreeNode) sciezkaDrzewa.getLastPathComponent();
        String element = defaultMutableTreeNode.getUserObject().toString();
        textFieldElementListyProduktow.setText(element.trim());
    }

    private void usunWybranyElementDrzewa(DefaultTreeModel model) {
        label_Message.setText("");
        DefaultMutableTreeNode wybranaGalaz = (DefaultMutableTreeNode) treeProduktow.getLastSelectedPathComponent();
        if (wybranaGalaz.isRoot()) {
            label_Message.setText("Nie mozesz skasowac galezi glownej");
        } else {
            if (wybranaGalaz != null) {
                model.removeNodeFromParent(wybranaGalaz);
            } else {
                label_Message.setText("Musisz wybrac kategorie lub podkategorie do usuniecia");
            }
        }
        saveDB();
    }

    private void dodajProduktDoKategorii() {
        label_Message.setText("");
        DefaultMutableTreeNode wybranaGalaz = (DefaultMutableTreeNode) treeProduktow.getLastSelectedPathComponent();
        if (wybranaGalaz == null) {
            label_Message.setText("Musisz wybrac podkategorie, a nastepnie wpisac nazwe produktu");
        } else {
            if (!wybranaGalaz.isRoot()) {
                TreeNode parent = wybranaGalaz.getParent();
                boolean equals = parent.equals(wybranaGalaz.getRoot());
                if (equals) {
                    if (!pobierzProdukt().equals("")) {
                        Kategoria kategoriaDB = (Kategoria) wybranaGalaz.getUserObject();
                        Produkt produktDB = new Produkt(tF_DodajProdukt.getText());
                        kategoriaDB.addProdukt(produktDB);
                        wybranaGalaz.add(new DefaultMutableTreeNode(produktDB));
                        ((DefaultTreeModel) treeProduktow.getModel()).reload(wybranaGalaz);
                        tF_DodajProdukt.setText(""); // czysci pole po dodaniu produktu
                    } else {
                        label_Message.setText("Musisz wpisac nazwe produktu");
                    }
                } else {
                    label_Message.setText("Nie mozna dodac produktu do produktu ani sklepu");
                }
            } else {
                label_Message.setText("Musisz wybrac kategorie");
            }
        }
        aktywujPrzyciki();
    }

    public void aktywujPrzyciki() {
        String wartoscDoEdycji = textFieldElementListyProduktow.getText();
        boolean maWartoscDoEdycji = (wartoscDoEdycji.length() > 0);
        button_EdytujElement.setEnabled(maWartoscDoEdycji);

        String nazwaKategorii = pobierzKategorie();
        boolean maNazweKategorii = (nazwaKategorii.length() > 0);
        button_DodajKategorie.setEnabled(maNazweKategorii);
        String nazwaProduktu = pobierzProdukt();
        String nazwaSklepu = pobierzSklep();
        boolean maNazweProduktu = (nazwaProduktu.length() > 0);
        boolean maNazweSklepu = (nazwaSklepu.length() > 0);
        button_DodajProdukt.setEnabled(maNazweProduktu);
        button_DodajSklep.setEnabled(maNazweSklepu);
    }

    public String pobierzKategorie() {
        return tF_DodajKategorie.getText().trim();
    }

    public String pobierzProdukt() {
        return tF_DodajProdukt.getText().trim();
    }

    public String pobierzSklep() {
        return tF_DodajSklep.getText().trim();
    }

    public String pobierzWartoscDoEdycji() {
        return textFieldElementListyProduktow.getText();
    }

    private void findPattern(String text) throws IOException {

        FileWriter fw = new FileWriter("paragon.txt");
        fw.write(text);
        fw.close();

        FileReader fr = new FileReader("paragon.txt");
        BufferedReader br = new BufferedReader(fr);
        StringBuilder sb = new StringBuilder();
        String line = "";

        Pattern pattern = Pattern.compile("P[AH]R[AH]GON");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String foundPattern = matcher.group(0);

            ArrayList<String> list = new ArrayList<String>();

            boolean found = false;
            while ((line = br.readLine()) != null) {

                String paragonFiskalny = "PARAGON FISKALNY";
                if (line.startsWith(paragonFiskalny) || found) {
                    list.add(line);
                    found = true;
                }
            }
            StringBuilder stringBuilderParagon = new StringBuilder();
            for (String object : list) {
                System.out.println(object);
                stringBuilderParagon.append(object);
            }
            StringBuilder stringBuilder = new StringBuilder();
            list.set(0, ">");
            for (String object : list) {
                System.out.println(object);
                stringBuilder.append(object);
            }
            String poPierwszymPatternie = stringBuilder.toString();
            System.out.println("poPierwszymPatternie" + poPierwszymPatternie + "koniec");

            Pattern patternSprzed = Pattern.compile("(?m)^Sprzed.*$");
            Matcher matcherSprzed = patternSprzed.matcher(text);
            String replace = "";
            if (matcherSprzed.find()) {
                String sprzed = matcherSprzed.group(0);
                System.out.println("\n\n"+ sprzed);
                replace = poPierwszymPatternie.replace(sprzed, "<");
                System.out.println("zamana sprzed"+ replace);
            }

            int start = replace.indexOf(">");
            System.out.println(start);
            int koniec = replace.indexOf("<");
            System.out.println(koniec);
            String substring = replace.substring(start+1, koniec);
            System.out.println(substring);
            jTextAreaListaZakupow.append(substring);

            //String splited = new String("aaa bbb ccc");
            String[] splitedArray = null;
            splitedArray = substring.split("—");
            for (int i = 0 ; i < splitedArray.length ; i++) {
                System.out.println("*" + splitedArray [i]);
            }


        } else {
                jTextAreaListaZakupow.append("Nie znaleziono wzorca");
            }
            //return foundPattern;

    }

    public void zapiszOCRdoPliku(String text) {

        try {
            PrintWriter zapis = new PrintWriter("OCRdoPliku.txt");
            zapis.println(text);
            zapis.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Nie mogę zapisać pliku");
            System.out.println("Problem: " + ex);
        }
    }

    private void zapisDoPliku(String fullText) throws IOException {

        FileWriter fw = new FileWriter("paragon.txt");
        fw.write(fullText);
        fw.close();
    }

    private void czytaniePliku() throws FileNotFoundException, IOException {
        FileReader fr = new FileReader("a.txt");
        BufferedReader br = new BufferedReader(fr);
        String yo = "test";
        String s;
        while ((s = br.readLine()) != null) {
            if (s.contains(yo)) {
                jTextAreaListaZakupow.append(s + "\n");
            } else {
                jTextAreaListaZakupow.append("brak");
            }
        }

    }

    private void edTextValueChanged(DocumentEvent e) {
        String text = textFieldElementListyProduktow.getText();
        if (text == null || text.length() == 0) {
            //this.text = text;
            button_EdytujElement.setEnabled(false);
            return;
        } else {
            button_EdytujElement.setEnabled(true);
        }
    }

    class RowItem {
        private Object produkt;
        private Object sklep;
        private double cena;
        private int ilosc;

        public RowItem(Object produkt, Object sklep) {
            this.produkt = produkt;
            this.sklep = sklep;
            ilosc = 1;
            cena = 0.0f;
        }

        public Object getProdukt() {
            return produkt;
        }

        public void setProdukt(Object produkt) {
            this.produkt = produkt;
        }

        public Object getSklep() {
            return sklep;
        }

        public void setSklep(Object sklep) {
            this.sklep = sklep;
        }

        public double getCena() {
            return cena;
        }

        public void setCena(double cena) {
            this.cena = cena;
        }

        public int getIlosc() {
            return ilosc;
        }

        public void setIlosc(int ilosc) {
            this.ilosc = ilosc;
        }

        @Override
        public String toString() {
            //return String.format("%s,   %s,   %d,   %.2f zl", produkt, sklep, ilosc, cena);
            return String.format("\n" + produkt + ",  " + sklep + ",  " + ilosc + ",  " + cena + " zl");

        }
    }


    public void save() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new FileOutputStream("listaZakupow.txt"));
        ListModel model = lista_Zakupow.getModel();

        for (int i = 0; i < listaZakupowModel.size(); i++) {
            pw.println(model);
            System.out.println("Zapisano do: " + pw.toString());
            pw.close();
        }
    }
}
