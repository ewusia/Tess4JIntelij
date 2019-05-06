import pl.ewa.tess4j.db.DB;
import pl.ewa.tess4j.db.DBService;
import pl.ewa.tess4j.db.Kategoria;
import pl.ewa.tess4j.db.Produkt;
import pl.ewa.tess4j.db.Sklep;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

public class TworzenieDrzewa extends JFrame {

    private DefaultMutableTreeNode top;
    private JTree tree;

    public TworzenieDrzewa() {
        top = new DefaultMutableTreeNode("");
        tworzenieGalezi(top);

        // Utworzy drzewo, które można wybrać jednocześnie.
        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    // tworzy wezly
    private void tworzenieGalezi(DefaultMutableTreeNode top) {

        DB db = DBService.getDb();

        for (Kategoria kategoriaDB : db.getKategorie()) {
            DefaultMutableTreeNode kategoria = new DefaultMutableTreeNode(kategoriaDB.getNazwa());
            for (Produkt produktDB : kategoriaDB.getProdukty()) {
                DefaultMutableTreeNode produkt = new DefaultMutableTreeNode(produktDB.getNazwa());
                kategoria.add(produkt);
                for (Sklep sklepDB : produktDB.getSklepy()) {
                    DefaultMutableTreeNode sklep = new DefaultMutableTreeNode(sklepDB.getNazwa());
                    produkt.add(sklep);
                }
            }
            top.add(kategoria);
        }
    }

    public JTree getTree() {
        return tree;
    }
}