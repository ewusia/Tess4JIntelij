import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.*;

public class SortowanieDrzewa {

    public static void sortTree(DefaultMutableTreeNode root) {
        Enumeration e = root.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (!node.isLeaf()) {
                sort2(node);   //selection sort
                //sort3(node); //JDK 1.6.0: iterative merge sort
                //sort3(node); //JDK 1.7.0: TimSort
            }
        }
    }
    public static Comparator< DefaultMutableTreeNode> tnc = new Comparator< DefaultMutableTreeNode>() {
        @Override public int compare(DefaultMutableTreeNode a, DefaultMutableTreeNode b) {
            //Sort the parent and child nodes separately:
            if (a.isLeaf() && !b.isLeaf()) {
                return 1;
            } else if (!a.isLeaf() && b.isLeaf()) {
                return -1;
            } else {
                String sa = a.getUserObject().toString();
                String sb = b.getUserObject().toString();
                return sa.compareToIgnoreCase(sb);
            }
        }
    };
    //selection sort
    public static void sort2(DefaultMutableTreeNode parent) {
        int n = parent.getChildCount();
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                if (tnc.compare((DefaultMutableTreeNode) parent.getChildAt(min),
                        (DefaultMutableTreeNode) parent.getChildAt(j)) > 0) {
                    min = j;
                }
            }
            if (i != min) {
                MutableTreeNode a = (MutableTreeNode) parent.getChildAt(i);
                MutableTreeNode b = (MutableTreeNode) parent.getChildAt(min);
                parent.insert(b, i);
                parent.insert(a, min);
            }
        }
    }

    public static void sort3(DefaultMutableTreeNode parent) {
        int n = parent.getChildCount();
        //@SuppressWarnings("unchecked")
        //Enumeration< DefaultMutableTreeNode> e = parent.children();
        //ArrayList< DefaultMutableTreeNode> children = Collections.list(e);
        List< DefaultMutableTreeNode> children = new ArrayList< DefaultMutableTreeNode>(n);
        for (int i = 0; i < n; i++) {
            children.add((DefaultMutableTreeNode) parent.getChildAt(i));
        }
        Collections.sort(children, tnc); //iterative merge sort
        parent.removeAllChildren();
        for (MutableTreeNode node: children) {
            parent.add(node);
        }
    }

    public static void sortTree(JTree treeProduktow) {
    }
}
