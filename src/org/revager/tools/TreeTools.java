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
package org.revager.tools;

import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * This class provides some useful methods to handle trees.
 */
public class TreeTools {

	/**
	 * Checks if the first given path is descendant of the second given path.
	 * 
	 * @author http://www.javalobby.org/java/forums/t19857.html
	 * 
	 * @param path1
	 *            the first path
	 * @param path2
	 *            the second path
	 * 
	 * @return true, if the first path is descendant of the second path
	 */
	public static boolean isDescendant(TreePath path1, TreePath path2) {
		// is path1 descendant of path2
		int count1 = path1.getPathCount();
		int count2 = path2.getPathCount();

		if (count1 <= count2) {
			return false;
		}

		while (count1 != count2) {
			path1 = path1.getParentPath();
			count1--;
		}

		return path1.equals(path2);
	}

	/**
	 * Gets the expansion state of the given tree.
	 * 
	 * @author http://www.javalobby.org/java/forums/t19857.html
	 * 
	 * @param tree
	 *            the tree
	 * @param row
	 *            the row
	 * 
	 * @return the expansion state
	 */
	public static String getExpansionState(JTree tree, int row) {
		TreePath rowPath = tree.getPathForRow(row);
		StringBuffer buf = new StringBuffer();
		int rowCount = tree.getRowCount();

		for (int i = row; i < rowCount; i++) {
			TreePath path = tree.getPathForRow(i);

			if (i == row || isDescendant(path, rowPath)) {
				if (tree.isExpanded(path)) {
					buf.append(',');
					buf.append(i - row);
				}
			} else {
				break;
			}
		}

		return buf.toString();
	}

	/**
	 * Restore the expanstion state of the given tree.
	 * 
	 * @author http://www.javalobby.org/java/forums/t19857.html
	 * 
	 * @param tree
	 *            the tree
	 * @param row
	 *            the row
	 * @param expansionState
	 *            the expansion state
	 */
	public static void restoreExpanstionState(JTree tree, int row,
			String expansionState) {
		StringTokenizer stok = new StringTokenizer(expansionState, ",");

		while (stok.hasMoreTokens()) {
			int token = row + Integer.parseInt(stok.nextToken());
			tree.expandRow(token);
		}
	}

	/**
	 * Expands or collapses all elements of the given tree.
	 * 
	 * @author http://www.exampledepot.com/egs/javax.swing.tree/ExpandAll.html
	 * 
	 * @param tree
	 *            the tree
	 * @param parent
	 *            the parent
	 * @param expand
	 *            true if the tree should be expanded; false for collapsing
	 */
	public static void expandAll(JTree tree, TreePath parent, boolean expand) {
		// Traverse children
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand);
			}
		}

		// Expansion or collapse must be done bottom-up
		if (expand) {
			tree.expandPath(parent);
		} else {
			tree.collapsePath(parent);
		}
	}

}
