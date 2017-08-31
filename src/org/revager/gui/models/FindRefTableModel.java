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
package org.revager.gui.models;

import static org.revager.app.model.Data.translate;

import org.revager.app.model.schema.Finding;

/**
 * The Class FindRefTableModel.
 */
@SuppressWarnings("serial")
public class FindRefTableModel extends AbstractFindingTableModel {

	public FindRefTableModel(Finding currentFinding) {
		super(currentFinding);
	}

	@Override
	public int getRowCount() {
		return findingMgmt.getReferences(localFind).size();
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		return findingMgmt.getReferences(localFind).get(arg0);
	}

	@Override
	public void setValueAt(Object insertion, int row, int column) {
		String oldRef = this.getValueAt(row, 0).toString();
		findingMgmt.editReference(oldRef, (String) insertion, localFind);
		this.fireTableDataChanged();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public String getColumnName(int column) {
		return translate("References");
	}

}
