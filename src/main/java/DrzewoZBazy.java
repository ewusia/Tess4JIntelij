import pl.ewa.tess4j.db.DB;
import pl.ewa.tess4j.db.DBService;
import pl.ewa.tess4j.db.Kategoria;
import pl.ewa.tess4j.db.Produkt;
import pl.ewa.tess4j.db.Sklep;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.ObjectOutputStream;

public class DrzewoZBazy extends JFrame {

    private DefaultMutableTreeNode top;
    private JTree tree;

    public DrzewoZBazy() {
        top = new DefaultMutableTreeNode("");
        createNodes(top);

        // Utworzy drzewo, które można wybrać jednocześnie.
        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

       /* JScrollPane scroll = new JScrollPane(tree);
        this.add(scroll);*/
/*        JScrollPane scrollTree = new JScrollPane(tree);
        scrollTree.setViewportView(tree);*/
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