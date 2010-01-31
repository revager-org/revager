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

import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.xml.datatype.Duration;

import org.revager.app.Application;
import org.revager.app.model.Data;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.Protocol;


/**
 * The Class PresentAttendeesTableModel.
 */
@SuppressWarnings("serial")
public class PresentAttendeesTableModel extends AbstractTableModel {
	Protocol prot;
	List<Attendee> localAttList;

	/**
	 * Instantiates a new present attendees table model.
	 * 
	 * @param currentProt
	 *            the current prot
	 */
	public PresentAttendeesTableModel(Protocol currentProt) {
		prot = currentProt;
		localAttList = Application.getInstance().getProtocolMgmt()
				.getAttendees(prot);

	}

	/**
	 * Sets the protocol.
	 * 
	 * @param newProt
	 *            the new protocol
	 */
	public void setProtocol(Protocol newProt) {
		prot = newProt;
		localAttList = Application.getInstance().getProtocolMgmt()
				.getAttendees(prot);

		this.fireTableDataChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 5;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return localAttList.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int row, int column) {
		if (column == 1) {
			String name = localAttList.get(row).getName();
			String contact = localAttList.get(row).getContact();

			return "<html><b>" + name + "</b><br>" + contact + "</html>";
		} else if (column == 2) {
			String roleString = "role.".concat(localAttList.get(row).getRole()
					.value());
			return Data.getInstance().getLocaleStr(roleString);
		} else if (column == 3) {
			int aspNumber = Application.getInstance().getAttendeeMgmt()
					.getNumberOfAspects(localAttList.get(row));

			if (aspNumber > 0) {
				return aspNumber
						+ " "
						+ Data.getInstance().getLocaleStr(
								"editProtocol.aspects");
			} else {
				return "";
			}
		} else if (column == 4) {
			Duration localDur = Application.getInstance().getProtocolMgmt()
					.getAttendeePrepTime(localAttList.get(row), prot);
			String hours = String.format("%02d", localDur.getHours());
			String mins = String.format("%02d", localDur.getMinutes());

			return Data.getInstance().getLocaleStr("editProtocol.preparation")
					+ ": " + hours + ":" + mins;
		} else
			return null;
	}
}
