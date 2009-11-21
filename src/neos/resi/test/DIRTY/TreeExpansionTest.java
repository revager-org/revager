package neos.resi.test.DIRTY;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

public class TreeExpansionTest {
  public static void main(String args[]) {
    String title = ("JTree Expand Sample");
    JFrame frame = new JFrame(title);
    JTree tree = new JTree();
    TreeWillExpandListener treeWillExpandListener = new TreeWillExpandListener() {
      public void treeWillCollapse(TreeExpansionEvent treeExpansionEvent)
          throws ExpandVetoException {
        TreePath path = treeExpansionEvent.getPath();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
            .getLastPathComponent();
        String data = node.getUserObject().toString();
        if (data.equals("colors")) {
          throw new ExpandVetoException(treeExpansionEvent);
        }
      }

      public void treeWillExpand(TreeExpansionEvent treeExpansionEvent)
          throws ExpandVetoException {
        TreePath path = treeExpansionEvent.getPath();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
            .getLastPathComponent();
        String data = node.getUserObject().toString();
        if (data.equals("sports")) {
          throw new ExpandVetoException(treeExpansionEvent);
        }
      }
    };
    tree.addTreeWillExpandListener(treeWillExpandListener);
    JScrollPane scrollPane = new JScrollPane(tree);
    frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
    frame.setSize(300, 150);
    frame.setVisible(true);
  }
}