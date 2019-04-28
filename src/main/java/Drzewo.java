import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
/**
 * JTree basic tutorial and example
 * @author wwww.codejava.net
 */
public class Drzewo extends JFrame
{
    public JTree tree;
    public JLabel selectedLabel;

    public Drzewo()
    {
        DefaultMutableTreeNode listaProduktow = new DefaultMutableTreeNode("Lista Produktow");

        //create the napoj node
        DefaultMutableTreeNode napoj = new DefaultMutableTreeNode("Napoj");
        //create the child nodes
        DefaultMutableTreeNode cola = new DefaultMutableTreeNode("cola");

        DefaultMutableTreeNode biedronka = new DefaultMutableTreeNode("Biedronka");
        DefaultMutableTreeNode kaufland = new DefaultMutableTreeNode("Kaufland");

        biedronka.add(new DefaultMutableTreeNode("cena"));
        kaufland.add(new DefaultMutableTreeNode("cena"));

        DefaultMutableTreeNode woda = new DefaultMutableTreeNode("woda");
        woda.add(new DefaultMutableTreeNode(biedronka));
        woda.add(new DefaultMutableTreeNode(kaufland));
        //add the child nodes to the napoj node
        listaProduktow.add(napoj);
        napoj.add(cola);
        cola.add(biedronka);
        cola.add(kaufland);
        //napoj.add(woda);

        //create the tree by passing in the napoj node
        tree = new JTree(listaProduktow);
        ImageIcon imageIcon = new ImageIcon(Drzewo.class.getResource("money15.png"));
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(imageIcon);

        tree.setCellRenderer(renderer);
        tree.setShowsRootHandles(true); // zwinac do roota, tj. napoju
        tree.setRootVisible(true); // nie wyswietla roota, napoju jesli wartosc ustawiona na false
        add(new JScrollPane(tree));

        selectedLabel = new JLabel();
        add(selectedLabel, BorderLayout.SOUTH); // etykieta do wyswietlania sciezki na dole
        // metoda pozwala na pobranie rzeczywistego obiektu na ktorym sie znajdujemy a .toString pozwala na wyswietlenie wartsoci a nie instancje Object
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                selectedLabel.setText(selectedNode.getUserObject().toString());
            }
        });
        // metoda wyswietla sciezke do obiektu, na ktorym sie znajdujemy
/*        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                selectedLabel.setText(e.getPath().toString());
            }
        });*/

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Drzewo");
        this.setSize(200, 200);
        this.setVisible(true);
    }

    /*public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Drzewo();
            }
        });
    }*/
}