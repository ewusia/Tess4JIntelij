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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
    private JButton button_EdytujOrazProduktZListyZakupow;
    private JButton zapiszListeDoPlikuButton;
    private JTextArea textAreaSuma;
    private JLabel label_Message;
    private JTextField tF_DodajKategorie;
    private JButton button_UsunListaZakupow;
    private JButton button_DodajCene;
    private JLabel selectedLabel;

    private DefaultListModel<RowItem> listaZakupowModel = new DefaultListModel<>();

    private Object produkt;
    private Object sklep;

    public Makieta() {

        lista_Zakupow.setModel(listaZakupowModel);
        button_UsunListaZakupow.setEnabled(false);
        button_zListyProdDoZakupow.setEnabled(false);

        DocumentListener listener=new DocumentListener()
        {
            @Override public void insertUpdate(DocumentEvent e) { aktywujPrzyciki(); }
            @Override public void removeUpdate(DocumentEvent e) { aktywujPrzyciki(); }
            @Override public void changedUpdate(DocumentEvent e) { aktywujPrzyciki(); }
        };
        textFieldElementListyProduktow.getDocument().addDocumentListener(listener);
        tF_DodajKategorie.getDocument().addDocumentListener(listener);
        tF_DodajProdukt.getDocument().addDocumentListener(listener);
        tF_DodajSklep.getDocument().addDocumentListener(listener);
        aktywujPrzyciki();

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
                        String str = FileUtils.readFileToString(file, "UTF-8");
                        findPattern(str);
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
                    instance.setDatapath("C:\\Users\\ewa\\Desktop\\tesseract\\tessdata");
                    instance.setLanguage("pol");
                    String fullText;
                    try {
                        fullText = instance.doOCR(file);
                        jTextAreaListaProduktow.append(fullText);
                        //zapiszOCRdoPliku(fullText);
                        //findPattern(fullText);
                        //zapisDoPliku(fullText);
                        //czytaniePliku();
                        FileWriter fw = new FileWriter("paragon.txt");
                        fw.write(fullText);
                        fw.close();
                        findPattern(fullText);

                        FileReader fr = new FileReader("paragon.txt");
                        BufferedReader br = new BufferedReader(fr);
                        StringBuilder sb = new StringBuilder();
                        String line = "";

                        ZnajdowanieWzorcow zw = new ZnajdowanieWzorcow(fullText);
                        String paragon = zw.znajdzParagon(fullText);
                        ArrayList<String> list = new ArrayList<String>();

                        boolean found = false;
                        while ((line = br.readLine()) != null) {

                            if (line.startsWith(paragon) || found) {
                                list.add(line);
                                found = true;
                            }
                        }
                        for (String object : list) {
                            jTextAreaListaZakupow.append("test\n");

                            jTextAreaListaZakupow.append(object);
                            //System.out.println(object);
                        }

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
        /*textFieldElementListyProduktow.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    dodajProduktDoKategorii();
                }
            }
        });*/

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
                if(e.getKeyCode() == KeyEvent.VK_DELETE) {
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
                Nameable userObject = (Nameable) wybranaGalaz.getUserObject();
                String element = textFieldElementListyProduktow.getText();
                userObject.setName(element);
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
                        dodajDoModeluListyZakupow(wybranyProdukt, sklep);
                    }
                    if (poziom == 3) {
                        button_zListyProdDoZakupow.setEnabled(true);
                        Object wybranySklep = wybranaGalaz.getUserObject();
                        TreeNode produkt = wybranaGalaz.getParent();
                        /*String sklepProdukt = (produkt + "\t" + wybranySklep);
                        System.out.println(sklepProdukt);*/
                        dodajDoModeluListyZakupow(wybranySklep, produkt);
                    }
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


                if(idx < listaZakupowModel.getSize()) {
                    lista_Zakupow.setSelectedIndex(idx);
                } else {
                    lista_Zakupow.setSelectedIndex(idx-1);
                }


                if(listaZakupowModel.size() == 0) {
                    button_UsunListaZakupow.setEnabled(false);
                } else {
                    button_UsunListaZakupow.setEnabled(true);
                }
            }
        });
    }

    private void dodajDoModeluListyZakupow(Object wybrany, TreeNode powiazany) {
        RowItem pozycja = new RowItem(wybrany, powiazany);

        listaZakupowModel.addElement(pozycja);
        lista_Zakupow.setSelectedIndex(listaZakupowModel.getSize() - 1);
        button_UsunListaZakupow.setEnabled(true);
    }

    private void dodajKategorieDoDrzewa(DefaultTreeModel model) {
        label_Message.setText("");
        DefaultMutableTreeNode wybranaGalaz = (DefaultMutableTreeNode) model.getRoot();
        if (wybranaGalaz == null) {
            label_Message.setText("Musisz wybrac galaz glowna, a nastepnie wpisac nazwe kategorii");
        } else {
            Object root = model.getRoot();
            /*if (wybranaGalaz.e) {*/
                int poziom = wybranaGalaz.getLevel();
                if (poziom == 0) {
                        DB nadKategroriaDB = (DB) wybranaGalaz.getUserObject();
                        Kategoria kategoriaDB = new Kategoria(tF_DodajKategorie.getText());
                        nadKategroriaDB.addKategoria(kategoriaDB);
                        wybranaGalaz.add(new DefaultMutableTreeNode(kategoriaDB));
                        ((DefaultTreeModel) treeProduktow.getModel()).reload();
                    tF_DodajKategorie.setText("");
                        /*              Kategoria userObject = (Kategoria) galazGlowna.getUserObject();
        Kategoria kategoria = new Kategoria(tF_DodajKategorie.getText());
        if (!pobierzKategorie().equals("")) {
            galazGlowna.add(new DefaultMutableTreeNode(tF_DodajKategorie.getText()));
            model.reload();*/
                } else {
                    label_Message.setText("Kategoria musi byc dodana do galezi glownej");
                }
            /*} else {
                label_Message.setText("Musisz wybrac galaz glowna");
            }*/
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

    public static void main(String args[]) {
        JFrame frame = new JFrame("Makieta");
        frame.setContentPane(new Makieta().panelGlowny);
        frame.setSize(1200, 700);
        /*        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true)*/
        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void aktywujPrzyciki()
    {
        String wartoscDoEdycji = textFieldElementListyProduktow.getText();
        boolean maWartoscDoEdycji=(wartoscDoEdycji.length()>0);
        button_EdytujElement.setEnabled(maWartoscDoEdycji);
        button_zListyProdDoZakupow.setEnabled(maWartoscDoEdycji);

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

        String foundPattern = null;
        Pattern pattern = Pattern.compile("P[AH]R[AH]GON");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            foundPattern = matcher.group(0);

            jTextAreaListaZakupow.append(foundPattern); //prints /{item}/
            //czytaniePliku();

            FileReader fr = new FileReader("paragon.txt");
            BufferedReader br = new BufferedReader(fr);
            StringBuilder sb = new StringBuilder();
            String line = "";

            ArrayList<String> list = new ArrayList<String>();

            boolean found = false;
            while ((line = br.readLine()) != null) {

                if (line.startsWith("PARAGON FISKALNY") || found) {
                    list.add(line);
                    found = true;
                }
            }
            for (String object : list) {
                System.out.println(object);
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
        if(text == null || text.length() == 0) {
            //this.text = text;
            button_EdytujElement.setEnabled(false);
            return;
        } else {
            button_EdytujElement.setEnabled(true);
        }
    }

    class RowItem
    {
        private Object produkt;
        private Object sklep;

        public RowItem(Object produkt, Object sklep) {
            this.produkt = produkt;
            this.sklep = sklep;
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

        @Override
        public String toString() {
            return String.format("\t\t\t %s \t\t\t %s", produkt, sklep);

        }


    }
}
