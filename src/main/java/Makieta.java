import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class Makieta extends JFrame {


    private JTextArea jTextAreaListaProduktow;
    private JTextArea jTextAreaListaZakupow;
    private JButton wczytajParagonButton;
    private JButton zapiszPlikButton;
    private JButton otwórzPlikButton;
    private JButton zamknijButton;
    private JPanel panelGlowny;
    private JList listZakupow;
    private JLabel labelListaZakupow;
    private JLabel labelListaProduktow;
    private JButton zListyProdDoZakupow;
    private JButton usunZListyZakupow;
    private JList listProduktow;
    private JTree treeProduktow;
    private JTextField elementListyZakupow;
    private JTextField textFieldDodawanyEl;
    private JButton dodajKategorie;
    private JButton usunButton;
    private JLabel labelEl;
    private JLabel labelNadPattern;
    private JLabel labelWyswietlaniePatagonu;
    private JTextField textFieldDodajPodKat;
    private JButton dodajPodkatgorie;
    private JLabel labelDodajKategorie;
    private JLabel labelDodajPodKategorie;
    private JTextField textFieldDodajSklep;
    private JTextField textFielddodajKat;
    private JLabel labelDodajSklep;
    private JButton dodajSklep;
    private JLabel labelMessage;
    DefaultMutableTreeNode selectedNode;
    private JTree tree;
    private JLabel selectedLabel;
    private static TreeFromTextFile tr = new TreeFromTextFile();


    public Makieta() {

        //fillDataToJTree();
        //createTree();
        JTree t = tr.getTree();
/*
        JScrollPane scrollTree = new JScrollPane(t);
        //scrollTree.setViewportView(t);*/

        DefaultTreeModel model = (DefaultTreeModel) t.getModel();
        treeProduktow.setModel(model);
/*        add(scrollTree);*/

        otwórzPlikButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JFileChooser fc = new JFileChooser();
                int odpowiedz = fc.showOpenDialog(panelGlowny);
                if (odpowiedz == fc.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        jTextAreaListaProduktow.read( new FileReader( file.getAbsolutePath() ), null );
                        // test dla pattern
                        String str = FileUtils.readFileToString(file, "UTF-8");
                        findPattern(str);
                        // koniec testu dla pattern
                    } catch (IOException ex) {
                        System.out.println("Nie mogę otworzyć pliku: "+file.getAbsolutePath());
                        System.out.println("Problem: "+ex);
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
                        for (String object: list) {
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
                        System.out.println("Nie mogę zapisać pliku: "+file.getAbsolutePath());
                        System.out.println("Problem: "+ex);
                    }
                }            }
        });
        zamknijButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        treeProduktow.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {

/*                JScrollPane pane = new JScrollPane(t);
                pane.setPreferredSize(new Dimension(200, 400));

                getContentPane().add(pane);*/

                if (treeProduktow.isSelectionEmpty())
                    return;
                TreePath tp = treeProduktow.getSelectionPath();
                DefaultMutableTreeNode defaultMutableTreeNode =
                        (DefaultMutableTreeNode) tp.getLastPathComponent();
                String element = defaultMutableTreeNode.getUserObject().toString();
                elementListyZakupow.setText(element.trim());
            }
        });

        //DefaultTreeModel model = fillDataToJTree();
/*        JTree model2 = createTree();
        treeProduktow = model2;*/

        dodajKategorie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedNode = (DefaultMutableTreeNode) treeProduktow.getLastSelectedPathComponent();
                if (selectedNode !=null) {
                    selectedNode.insert(new DefaultMutableTreeNode(textFieldDodawanyEl.getText()), selectedNode.getIndex(selectedNode.getLastChild()));
                    model.reload(selectedNode);
                }
                textFieldDodawanyEl.setText("");
            }
        });
        usunButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*selectedNode = (DefaultMutableTreeNode) treeProduktow.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedNode.getParent();
                    parent.remove(selectedNode);
                   model.reload(parent);
                }*/
                labelMessage.setText("");
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeProduktow.getLastSelectedPathComponent();
                DefaultMutableTreeNode nowaPodKategoria = new DefaultMutableTreeNode(textFieldDodajPodKat.getText());
                if (selectedNode.isRoot()) {
                    labelMessage.setText("Nie mozesz skasowac galezi glownej");
                } else {
                    if (selectedNode != null) {
                        model.removeNodeFromParent(selectedNode);
                    } else {
                        labelMessage.setText("Musisz wybrac kategorie lub podkategorie do usuniecia");
                    }
                }
            }

        });
        dodajKategorie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                labelMessage.setText("");
                DefaultMutableTreeNode galazGlowna = (DefaultMutableTreeNode) model.getRoot();
                if (!textFielddodajKat.getText().trim().equals("")) {
                    galazGlowna.add(new DefaultMutableTreeNode(textFielddodajKat.getText()));
                    model.reload();
                } else {
                    labelMessage.setText("Musisz wpisac kategorie");
                }
            }
        });
        dodajPodkatgorie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                labelMessage.setText("");
                DefaultMutableTreeNode wybranyWezel = (DefaultMutableTreeNode) treeProduktow.getLastSelectedPathComponent();
                DefaultMutableTreeNode nowaNazwaProduktu = new DefaultMutableTreeNode(textFieldDodajPodKat.getText());
                if (wybranyWezel == null) {
                    labelMessage.setText("Musisz wybrac podkategorie, a nastepnie wpisac nazwe produktu");
                } else {
                    if (!wybranyWezel.isRoot() && !wybranyWezel.isLeaf()) {
                        int licznik = wybranyWezel.getFirstChild().getChildCount();
                        if (licznik == 2) {
                            if (!textFieldDodajPodKat.getText().trim().equals("")) {
                                model.insertNodeInto(nowaNazwaProduktu, wybranyWezel, wybranyWezel.getChildCount());
                            } else {
                                labelMessage.setText("Musisz wpisac nazwe produktu");
                            }
                        } else {
                            labelMessage.setText("Nie mozna dodac produktu do produktu ani sklepu");
                        }
                    } else {
                        labelMessage.setText("Musisz wybrac kategorie");
                    }
                }
            }
        });
        dodajSklep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                labelMessage.setText("");
                DefaultMutableTreeNode wybranyWezel = (DefaultMutableTreeNode) treeProduktow.getLastSelectedPathComponent();
                DefaultMutableTreeNode nowaNazwaSklepu = new DefaultMutableTreeNode(textFieldDodajSklep.getText());
                if (wybranyWezel == null) {
                    labelMessage.setText("Musisz wybrac produkt, a nastepnie wpisac nazwe sklepu");
                } else {
                    if (!wybranyWezel.isRoot() && !wybranyWezel.isLeaf()) {
                        int licznik = wybranyWezel.getFirstChild().getChildCount();
                        if (licznik == 1) {
                            if (!textFieldDodajSklep.getText().trim().equals("")) {
                                model.insertNodeInto(nowaNazwaSklepu, wybranyWezel, wybranyWezel.getChildCount());
                            } else {
                                labelMessage.setText("Musisz wpisac nazwe sklepu");
                            }
                        } else {
                            labelMessage.setText("Nie mozna dodac sklepu do kategorii ani sklepu");
                        }
                    } else {
                        labelMessage.setText("Musisz wybrac produkt");
                    }
                }
            }
        });
    }

    private DefaultTreeModel fillDataToJTree() {
        DefaultMutableTreeNode listaProduktow = new DefaultMutableTreeNode("Lista produktow");
        DefaultMutableTreeNode napoj = new DefaultMutableTreeNode("Napoj");
        DefaultMutableTreeNode woda = new DefaultMutableTreeNode("Woda");
        DefaultMutableTreeNode cocaCola = new DefaultMutableTreeNode("Coca-cola");
        DefaultMutableTreeNode biedronka = new DefaultMutableTreeNode("Biedronka");
        DefaultMutableTreeNode lidl = new DefaultMutableTreeNode("Lidl");
        DefaultMutableTreeNode cena = new DefaultMutableTreeNode("cena");
        listaProduktow.add(napoj);
        napoj.add(cocaCola);
        napoj.add(woda);
        cocaCola.add(biedronka);
        biedronka.add(cena);
        lidl.add(cena);
        cocaCola.add(lidl);
        woda.add(lidl);
        woda.add(lidl);
        DefaultMutableTreeNode slodycze = new DefaultMutableTreeNode("Slodycze");
        DefaultMutableTreeNode czekolada = new DefaultMutableTreeNode("Czekolada");
        listaProduktow.add(slodycze);
        slodycze.add(czekolada);
        czekolada.add(biedronka);
        biedronka.add(cena);
        czekolada.add(lidl);
        lidl.add(cena);
        DefaultTreeModel dtm = new DefaultTreeModel(listaProduktow);
        this.treeProduktow.setModel(dtm);
        return dtm;
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
            for (String object: list) {
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
            System.out.println("Problem: "+ex);
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
        while((s = br.readLine()) != null) {
            if (s.contains(yo)) {
                jTextAreaListaZakupow.append(s + "\n");
            } else {
                jTextAreaListaZakupow.append("brak");
            }
        }

    }

    public static void main(String args[]) {

        JFrame frame = new JFrame("Makieta");
        frame.setContentPane(new Makieta().panelGlowny);
        frame.setSize(1000, 1000);
        frame.setVisible(true);

    }
}
