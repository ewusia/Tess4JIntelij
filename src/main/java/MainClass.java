import java.io.File;
import java.util.Comparator;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

public class MainClass extends JFrame {
    public MainClass(String startDir) {
        super("SortTreeModel Demonstration");
        setSize(300, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        MyFile f = new MyFile(startDir);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(f);
        SortTreeModel model = new SortTreeModel(root, new TreeStringComparator());
        fillModel(model, root);

        JTree tree = new JTree(model);
        getContentPane().add(new JScrollPane(tree));
    }

    protected void fillModel(SortTreeModel model, DefaultMutableTreeNode current) {
        MyFile pf = (MyFile) current.getUserObject();
        File f = pf.getFile();
        if (f.isDirectory()) {
            String files[] = f.list();
            for (int i = 0; i < files.length; i++) {
                if (files[i].startsWith("."))
                    continue;
                MyFile tmp = new MyFile(pf, files[i]);
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(tmp);
                model.insertNodeInto(node, current);
                if (tmp.getFile().isDirectory()) {
                    fillModel(model, node);
                }
            }
        }
    }

    public class MyFile {
        File f;

        public MyFile(String s) {
            f = new File(s);
        }

        public MyFile(MyFile pf, String s) {
            f = new File(pf.f, s);
        }

        public File getFile() {
            return f;
        }

        public String toString() {
            return f.getName();
        }
    }

    public static void main(String args[]) {
        MainClass demo = new MainClass(".");
        demo.setVisible(true);
    }
}

class SortTreeModel extends DefaultTreeModel {
    private Comparator comparator;

    public SortTreeModel(TreeNode node, Comparator c) {
        super(node);
        comparator = c;
    }

    public SortTreeModel(TreeNode node, boolean asksAllowsChildren, Comparator c) {
        super(node, asksAllowsChildren);
        comparator = c;
    }

    public void insertNodeInto(MutableTreeNode child, MutableTreeNode parent) {
        int index = findIndexFor(child, parent);
        super.insertNodeInto(child, parent, index);
    }

    public void insertNodeInto(MutableTreeNode child, MutableTreeNode par, int i) {
        // The index is useless in this model, so just ignore it.
        insertNodeInto(child, par);
    }

    private int findIndexFor(MutableTreeNode child, MutableTreeNode parent) {
        int cc = parent.getChildCount();
        if (cc == 0) {
            return 0;
        }
        if (cc == 1) {
            return comparator.compare(child, parent.getChildAt(0)) <= 0 ? 0 : 1;
        }
        return findIndexFor(child, parent, 0, cc - 1);
    }

    private int findIndexFor(MutableTreeNode child, MutableTreeNode parent, int i1, int i2) {
        if (i1 == i2) {
            return comparator.compare(child, parent.getChildAt(i1)) <= 0 ? i1 : i1 + 1;
        }
        int half = (i1 + i2) / 2;
        if (comparator.compare(child, parent.getChildAt(half)) <= 0) {
            return findIndexFor(child, parent, i1, half);
        }
        return findIndexFor(child, parent, half + 1, i2);
    }
}

class TreeStringComparator implements Comparator {
    public int compare(Object o1, Object o2) {
        if (!(o1 instanceof DefaultMutableTreeNode && o2 instanceof DefaultMutableTreeNode)) {
            throw new IllegalArgumentException("Can only compare DefaultMutableTreeNode objects");
        }
        String s1 = ((DefaultMutableTreeNode) o1).getUserObject().toString();
        String s2 = ((DefaultMutableTreeNode) o2).getUserObject().toString();
        return s1.compareToIgnoreCase(s2);
    }
}