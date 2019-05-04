import pl.ewa.tess4j.db.*;

import java.io.*;
import javax.swing.*;
import javax.swing.tree.*;

public class DrzewoZPlikuTekstowego extends JFrame{

    private BufferedReader in;
    private LineNumberReader ln;
    private String line;    // wartość linii w pliku tekstowym
    private String root;    // wartość do wykorzystania dla węzła głównego JTree
    private String encoding = "UTF-8";
    String bazaProduktow = "baza.txt";
    private DefaultMutableTreeNode top;
    private JTree tree;

    public DrzewoZPlikuTekstowego() {
        getRootNode();
        top = new DefaultMutableTreeNode(root);
        createNodes(top);

        // Utworzy drzewo, które można wybrać jednocześnie.
        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

       /* JScrollPane scroll = new JScrollPane(tree);
        this.add(scroll);*/
/*        JScrollPane scrollTree = new JScrollPane(tree);
        scrollTree.setViewportView(tree);*/
    }

    // ta metoda odczytuje plik i drukuje wszystkie linie na standardowe wyjście w celach testowych
    public void readFile() {
        try {
            in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(bazaProduktow), encoding));

            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ta metoda odczytuje pierwszą linię w pliku tekstowym i przypisuje ją do zmiennej głównej,
    // która będzie używana dla węzła głównego JTree
    private void getRootNode() {
        try {
            in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(bazaProduktow), encoding));
            ln = new LineNumberReader(in);

            if (ln.getLineNumber() == 0) {
                root = ln.readLine();
                //System.out.println(root);
            }

            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ta metoda oblicza liczbę wystąpień znaku dzieki czemu tworzy sie struktura hierachiczna
    private int countOccurrences(String haystack, char needle) {
        int count = 0;
        for (int i = 0; i < haystack.length(); i++) {
            if (haystack.charAt(i) == needle) {
                count++;
            }
        }
        return count;
    }

    // tworzy wezly
    private void createNodes(DefaultMutableTreeNode top) {

        DB db  = DBService.getDb();

        for(Kategoria kategoriaDB : db.getKategorie()){
            DefaultMutableTreeNode kategoria = new DefaultMutableTreeNode(kategoriaDB.getNazwa());
            for(Produkt produktDB : kategoriaDB.getProdukty()){
              DefaultMutableTreeNode produkt =  new DefaultMutableTreeNode(produktDB.getNazwa());
              kategoria.add(produkt);
                for(Sklep sklepDB : produktDB.getSklepy()){
                    DefaultMutableTreeNode sklep =  new DefaultMutableTreeNode(sklepDB.getNazwa());
                    produkt.add(sklep);
                }
            }
            top.add(kategoria);
        }
        /*try {
            in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(bazaProduktow), encoding));

            while ((line = in.readLine()) != null) {
                if (countOccurrences(line, '\t') == 1) {
                    kategoria = new DefaultMutableTreeNode(line);
                    top.add(kategoria);
                } else if (countOccurrences(line, '\t') == 2) {
                    konretntyProdukt = new DefaultMutableTreeNode(line);
                    kategoria.add(konretntyProdukt);
                } else if (countOccurrences(line, '\t') == 3) {
                    sklep = new DefaultMutableTreeNode(line);
                    konretntyProdukt.add(sklep);
                } else if (countOccurrences(line, '\t') == 4) {
                    cena = new DefaultMutableTreeNode(line);
                    sklep.add(cena);
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public JTree getTree() {
        return tree;
    }

    public void zapisz(JTree myJTree) {

        File outFile = new File (bazaProduktow);
        FileOutputStream fos = null;

        TreeModel model = getTree().getModel();
        Object root = model.getRoot();


        try {
            fos = new FileOutputStream(outFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            oos.writeObject (myJTree.getModel());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}