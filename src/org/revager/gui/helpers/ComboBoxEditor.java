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
package org.revager.gui.helpers;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

/**
 * The Class ComboBoxEditor.
 */
@SuppressWarnings("serial")
public class ComboBoxEditor extends DefaultCellEditor {

	/**
	 * Instantiates a new combo box editor.
	 * 
	 * @param items
	 *            the items
	 */
	public ComboBoxEditor(String[] items) {
		super(new JComboBox(items));
	}

}