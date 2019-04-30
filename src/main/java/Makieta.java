import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
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
import java.awt.BorderLayout;
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
    private JButton usunZListyProduktowButton;
    private JButton usunZListyZakupow;
    private JList listProduktow;
    private JTree treeProduktow;
    private JTextField elementListyZakupow;
    private JLabel labelNameDodawanegoElementu;
    private JTextField textFieldDodawanyEl;
    private JButton dodajNazwe;
    private JButton usunButton;
    private JLabel labelEl;
    private JLabel labelNadPattern;
    private JLabel labelWyswietlaniePatagonu;
    private static TreeFromTextFile tr = new TreeFromTextFile();
    DefaultMutableTreeNode selectedNode;
    private JTree tree;
    private JLabel selectedLabel;

    public Makieta() {

        fillDataToJTree();
        //createTree();

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


        //treeProduktow = new JTree();
        treeProduktow.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                if (treeProduktow.isSelectionEmpty())
                    return;
                TreePath tp = treeProduktow.getSelectionPath();
                DefaultMutableTreeNode defaultMutableTreeNode =
                        (DefaultMutableTreeNode) tp.getLastPathComponent();
                String element = defaultMutableTreeNode.getUserObject().toString();
                elementListyZakupow.setText(element);
            }
        });

        DefaultTreeModel model = fillDataToJTree();
/*        JTree model2 = createTree();
        treeProduktow = model2;*/

        dodajNazwe.addActionListener(new ActionListener() {
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
                selectedNode = (DefaultMutableTreeNode) treeProduktow.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedNode.getParent();
                    parent.remove(selectedNode);
                    model.reload(parent);
                }



            }
        });

    }
    private JTree createTree() {
        //create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        //create the child nodes
        DefaultMutableTreeNode vegetableNode = new DefaultMutableTreeNode("Vegetables");
        vegetableNode.add(new DefaultMutableTreeNode("Capsicum"));
        vegetableNode.add(new DefaultMutableTreeNode("Carrot"));
        vegetableNode.add(new DefaultMutableTreeNode("Tomato"));
        vegetableNode.add(new DefaultMutableTreeNode("Potato"));

        DefaultMutableTreeNode fruitNode = new DefaultMutableTreeNode("Fruits");
        fruitNode.add(new DefaultMutableTreeNode("Banana"));
        fruitNode.add(new DefaultMutableTreeNode("Mango"));
        fruitNode.add(new DefaultMutableTreeNode("Apple"));
        fruitNode.add(new DefaultMutableTreeNode("Grapes"));
        fruitNode.add(new DefaultMutableTreeNode("Orange"));
        //add the child nodes to the root node
        root.add(vegetableNode);
        root.add(fruitNode);

        //create the tree by passing in the root node
        tree = new JTree(root);
        //ImageIcon imageIcon = new ImageIcon(getResource("/leaf.jpg"));
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
       // renderer.setLeafIcon(imageIcon);

        tree.setCellRenderer(renderer);
        tree.setShowsRootHandles(true);
        tree.setRootVisible(false);
        add(new JScrollPane(tree));

        selectedLabel = new JLabel();
        add(selectedLabel, BorderLayout.SOUTH);
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                selectedLabel.setText(selectedNode.getUserObject().toString());
            }
        });

        return tree;
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
