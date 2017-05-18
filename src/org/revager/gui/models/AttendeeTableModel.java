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

import javax.swing.JLabel;
import javax.swing.table.AbstractTableModel;

import org.revager.app.Application;
import org.revager.app.model.Data;

/**
 * The Class AttendeeTableModel.
 */
@SuppressWarnings("serial")
public class AttendeeTableModel extends AbstractTableModel {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 4;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		if (Data.getInstance().getResiData().getReview() == null) {
			return 0;
		} else {
			return Application.getInstance().getAttendeeMgmt().getNumberOfAttendees();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		if (column == 1)
			return translate("Name");
		else if (column == 2)
			return translate("Role");
		else if (column == 3)
			return translate("Aspects");
		else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 1) {
			String name = Application.getInstance().getAttendeeMgmt().getAttendees().get(rowIndex).getName();
			String contact = Application.getInstance().getAttendeeMgmt().getAttendees().get(rowIndex).getContact();

			return name + "\n\n" + contact;
		} else if (columnIndex == 2) {
			String roleString = Application.getInstance().getAttendeeMgmt().getAttendees().get(rowIndex).getRole()
					.toString();

			return translate(roleString);
		} else if (columnIndex == 3) {
			try {
				String value = String.valueOf(Application.getInstance().getAttendeeMgmt().getAttendees().get(rowIndex)
						.getAspects().getAspectIds().size());

				return value.concat(" ").concat(translate("Aspect(s)"));
			} catch (Exception e) {
				return "";
			}
		} else {
			JLabel localLbl = new JLabel(Data.getInstance().getIcon("attendee_20x20.png"));
			return localLbl;
		}
	}

}
