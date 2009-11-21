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
package neos.resi.gui.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import neos.resi.app.model.Data;
import neos.resi.app.model.appdata.AppCSVColumnName;

/**
 * The Class CSVColumnsComboBoxModel.
 */
@SuppressWarnings("serial")
public class CSVColumnsComboBoxModel extends AbstractListModel implements
		ComboBoxModel {

	private Map<String, AppCSVColumnName> columns = new HashMap<String, AppCSVColumnName>();

	private final String DESCRIPTION = Data.getInstance().getLocaleStr(
			"csvProfDialog.col.description");
	private final String REFERENCE = Data.getInstance().getLocaleStr(
			"csvProfDialog.col.reference");
	private final String SEVERITY = Data.getInstance().getLocaleStr(
			"csvProfDialog.col.severity");
	private final String REPORTER = Data.getInstance().getLocaleStr(
			"csvProfDialog.col.reporter");

	private String selection = null;

	/**
	 * Instantiates a new cSV columns combo box model.
	 */
	public CSVColumnsComboBoxModel() {
		super();

		columns.put(DESCRIPTION, AppCSVColumnName.DESCRIPTION);
		columns.put(REFERENCE, AppCSVColumnName.REFERENCE);
		columns.put(SEVERITY, AppCSVColumnName.SEVERITY);
		columns.put(REPORTER, AppCSVColumnName.REPORTER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ComboBoxModel#getSelectedItem()
	 */
	@Override
	public Object getSelectedItem() {
		return selection;
	}

	/**
	 * Gets the selected column.
	 * 
	 * @return the selected column
	 */
	public AppCSVColumnName getSelectedColumn() {
		return columns.get(getSelectedItem());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
	 */
	@Override
	public void setSelectedItem(Object item) {
		selection = item.toString();
	}

	/**
	 * Sets the selected column.
	 * 
	 * @param col
	 *            the col
	 */
	public void setSelectedColumn(AppCSVColumnName col) {
		Iterator<Entry<String, AppCSVColumnName>> iter = columns.entrySet()
				.iterator();

		while (iter.hasNext()) {
			Entry<String, AppCSVColumnName> entry = iter.next();

			if (entry.getValue() == col) {
				setSelectedItem(entry.getKey());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	@Override
	public Object getElementAt(int index) {
		switch (index) {
		case 0:
			return DESCRIPTION;
		case 1:
			return REFERENCE;
		case 2:
			return SEVERITY;
		case 3:
			return REPORTER;

		default:
			return "";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ListModel#getSize()
	 */
	@Override
	public int getSize() {
		return AppCSVColumnName.values().length;
	}

}
