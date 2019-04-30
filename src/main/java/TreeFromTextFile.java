import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

public class TreeFromTextFile {

    private BufferedReader in;
    private LineNumberReader ln;
    private String line;    // wartość linii w pliku tekstowym
    private String root;    // wartość do wykorzystania dla węzła głównego JTree
    private String encoding = "UTF-8";
    String bazaProduktow = "baza.txt";
    private DefaultMutableTreeNode top;
    private JTree tree;

    public TreeFromTextFile() {
        getRootNode();
        top = new DefaultMutableTreeNode(root);
        createNodes(top);

        // Utworzy drzewo, które można wybrać jednocześnie.
        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
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
        DefaultMutableTreeNode kategoria = null;     // pierwszy poziom hierachii
        DefaultMutableTreeNode konretntyProdukt = null;  // drugi
        DefaultMutableTreeNode sklep = null;         // trzeci
        DefaultMutableTreeNode cena = null;         // czwarty, ostatni

        try {
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
        }
    }

    public JTree getTree() {
        return tree;
    }
}