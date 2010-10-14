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
package org.revager.gui.aspects_manager;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * The Class CheckNode.
 */
@SuppressWarnings("serial")
public class CheckNode extends DefaultMutableTreeNode {

	private boolean isSelected;

	/**
	 * Instantiates a new check node.
	 */
	public CheckNode() {
		this(null);
	}

	/**
	 * Instantiates a new check node.
	 * 
	 * @param userObject
	 *            the user object
	 */
	public CheckNode(Object userObject) {
		this(userObject, true, false);
	}

	/**
	 * Instantiates a new check node.
	 * 
	 * @param userObject
	 *            the user object
	 * @param allowsChildren
	 *            the allows children
	 * @param isSelected
	 *            the is selected
	 */
	public CheckNode(Object userObject, boolean allowsChildren,
			boolean isSelected) {
		super(userObject, allowsChildren);

		this.isSelected = isSelected;
	}

	/**
	 * Sets the selected.
	 * 
	 * @param isSelected
	 *            the new selected
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		if (isSelected) {
			if (children != null) {
				@SuppressWarnings("rawtypes")
				Enumeration e = children.elements();

				while (e.hasMoreElements()) {
					CheckNode node = (CheckNode) e.nextElement();
					node.setSelected(isSelected);
				}
			}
		}
	}

	/**
	 * Checks if is selected.
	 * 
	 * @return true, if is selected
	 */
	public boolean isSelected() {
		return isSelected;
	}

	// If you want to change "isSelected" by CellEditor,
	/*
	 * public void setUserObject(Object obj) { if (obj instanceof Boolean) {
	 * setSelected(((Boolean)obj).booleanValue()); } else {
	 * super.setUserObject(obj); } }
	 */

}
