/* 
 * Copyright 2009 Davide Casciato, Sandra Reich, Johannes Wettinger
 * 
 * This file is part of Resi.
 *
 * Resi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Resi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Resi. If not, see <http://www.gnu.org/licenses/>.
 */
package neos.resi.gui.aspects_manager;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * The listener interface for receiving nodeSelection events. The class that is
 * interested in processing a nodeSelection event implements this interface, and
 * the object created with that class is registered with a component using the
 * component's <code>addNodeSelectionListener<code> method. When
 * the nodeSelection event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see NodeSelectionEvent
 */
class NodeSelectionListener implements MouseListener {
	private JTree tree;
	private TreePath path;

	/**
	 * Instantiates a new node selection listener.
	 * 
	 * @param tree
	 *            the tree
	 */
	public NodeSelectionListener(JTree tree) {
		this.tree = tree;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getClickCount() == 2) {
			setNodeBehaviour(true, e);
		} else {
			setNodeBehaviour(false, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
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

		CheckNode node = (CheckNode) path.getParentPath()
				.getLastPathComponent();

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

		CheckNode node = (CheckNode) path.getParentPath()
				.getLastPathComponent();

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
	private void setNodeBehaviour(boolean selectable, MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int row = tree.getRowForLocation(x, y);

		path = tree.getPathForRow(row);

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
					tree.expandPath(path);
					selectParent(path);
				} else {
					deselectParent(path);
					deselectChilds(path);

					if (path.getPathCount() == 1) {
						tree.expandPath(path);
					}
				}

				((DefaultTreeModel) tree.getModel()).nodeChanged(node);
			}
		}
	}

}
