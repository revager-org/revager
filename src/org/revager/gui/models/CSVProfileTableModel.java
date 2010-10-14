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

import static org.revager.app.model.Data._;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.revager.app.Application;
import org.revager.app.SeverityManagement;
import org.revager.app.model.appdata.AppCSVProfile;

/**
 * The Class CSVProfileTableModel.
 */
@SuppressWarnings("serial")
public class CSVProfileTableModel extends AbstractTableModel {

	// private AppCSVProfile localProfile;

	private SeverityManagement sevMgmt = Application.getInstance()
			.getSeverityMgmt();

	private List<String> localColNameList;

	/**
	 * Instantiates a new cSV profile table model.
	 * 
	 * @param currentProfile
	 *            the current profile
	 * @param selColNameList
	 *            the sel col name list
	 */
	public CSVProfileTableModel(AppCSVProfile currentProfile,
			List<String> selColNameList) {
		// localProfile = currentProfile;
		localColNameList = selColNameList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return sevMgmt.getNumberOfSeverities();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int row, int column) {
		if (column == 0)
			return sevMgmt.getSeverities().get(row);
		else
			return localColNameList.get(row);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
	 * int, int)
	 */
	public void setValueAt(Object insertion, int row, int column) {
		localColNameList.set(row, insertion.toString());
		this.fireTableDataChanged();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int row, int column) {
		if (column == 1)
			return true;
		else
			return false;
	}

	/**
	 * Sets the maps and profile.
	 * 
	 * @param currentProfile
	 *            the current profile
	 * @param selColNameList
	 *            the sel col name list
	 */
	public void setMapsAndProfile(AppCSVProfile currentProfile,
			List<String> selColNameList) {
		// localProfile = currentProfile;
		localColNameList = selColNameList;
		fireTableDataChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName(int column) {
		if (column == 0)
			return _("Review Severity");
		else
			return _("Mapping");

	}

}
