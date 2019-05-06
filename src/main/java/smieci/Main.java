package smieci;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class Main {
    public TreePath[] getPaths(JTree tree, boolean expanded) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        List<TreePath> list = new ArrayList<TreePath>();
        getPaths(tree, new TreePath(root), expanded, list);

        return (TreePath[]) list.toArray(new TreePath[list.size()]);
    }

    public void getPaths(JTree tree, TreePath parent, boolean expanded, List<TreePath> list) {
        if (expanded && !tree.isVisible(parent)) {
            return;
        }
        list.add(parent);
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                getPaths(tree, path, expanded, list);
            }
        }
    }
}