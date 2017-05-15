package org.revager.gui.aspects_manager;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingEvent;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingListener;

public class CheckBoxListener implements TreeCheckingListener {

	private JTree tree;
	private TreePath path;

	@Override
	public void valueChanged(TreeCheckingEvent e) {

		setNodeBehaviour(true, e);

	}

	public CheckBoxListener(CheckboxTree tree) {
		this.tree = tree;
	}

	/**
	 * Deselect parent.
	 * 
	 * @param path
	 *            the path
	 */
	private void deselectParent(TreePath path) {
		if (path.getParentPath() == null) {
			return;
		}

		CheckNode node = (CheckNode) path.getParentPath().getLastPathComponent();

		boolean allChildsSelected = true;

		for (int i = 0; i < node.getChildCount(); i++) {
			if (((CheckNode) node.getChildAt(i)).isSelected() == false)
				allChildsSelected = false;
		}

		if (!allChildsSelected) {
			node.setSelected(false);
			deselectParent(path.getParentPath());
		}

		// UI.getInstance().getAspectsManagerFrame().getTree().repaint();
	}

	/**
	 * Select parent.
	 * 
	 * @param path
	 *            the path
	 */
	private void selectParent(TreePath path) {
		if (path.getParentPath() == null) {
			return;
		}

		CheckNode node = (CheckNode) path.getParentPath().getLastPathComponent();

		boolean allChildsSelected = true;

		for (int i = 0; i < node.getChildCount(); i++) {
			if (((CheckNode) node.getChildAt(i)).isSelected() == false)
				allChildsSelected = false;
		}

		if (allChildsSelected) {
			node.setSelected(true);
			selectParent(path.getParentPath());
		}

		// UI.getInstance().getAspectsManagerFrame().getTree().repaint();
	}

	/**
	 * Deselect childs.
	 * 
	 * @param path
	 *            the path
	 */
	private void deselectChilds(TreePath path) {
		CheckNode node = (CheckNode) path.getLastPathComponent();
		node.setSelected(false);

		for (int i = 0; i < node.getChildCount(); i++) {
			deselectChilds(path.pathByAddingChild(node.getChildAt(i)));
		}

		// UI.getInstance().getAspectsManagerFrame().getTree().repaint();
	}

	/**
	 * Sets the node behaviour.
	 * 
	 * @param selectable
	 *            the selectable
	 * @param e
	 *            the e
	 */
	private void setNodeBehaviour(boolean selectable, TreeCheckingEvent e) {

		path = e.getPath();

		// try {
		// tree.expandPath(path);
		// } catch (Exception exc) {
		// tree.collapsePath(path);
		// }

		// TreePath path = tree.getSelectionPath();

		if (path != null) {
			CheckNode node = (CheckNode) path.getLastPathComponent();

			if (selectable) {
				boolean isSelected = !(node.isSelected());
				node.setSelected(isSelected);

				if (isSelected) {
					selectParent(path);
				} else {
					deselectParent(path);
					deselectChilds(path);
				}

				((DefaultTreeModel) tree.getModel()).nodeChanged(node);
			}
		}
	}

}
