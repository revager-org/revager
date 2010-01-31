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

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.revager.app.Application;
import org.revager.app.model.Data;
import org.revager.app.model.schema.Aspect;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.Role;


/**
 * The Class AspectTableModel.
 */
@SuppressWarnings("serial")
public class AspectTableModel extends AbstractTableModel {

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
		return getAllAssignedAspects().size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		List<Attendee> attList = Application.getInstance().getAttendeeMgmt()
				.getAttendeesWithAspect(getAllAssignedAspects().get(rowIndex));

		if (columnIndex == 0) {
			String direc = getAllAssignedAspects().get(rowIndex).getDirective();
			String cat = getAllAssignedAspects().get(rowIndex).getCategory();
			String desc = getAllAssignedAspects().get(rowIndex)
					.getDescription();

			return direc + " (" + cat + ")" + "\n\n" + desc;
		} else {
			return getAttendeeAsString(attList);
		}
	}

	public Aspect getAspect(int row) {
		return getAllAssignedAspects().get(row);
	}

	/**
	 * Gets the attendee as string.
	 * 
	 * @param attList
	 *            the att list
	 * 
	 * @return the attendee as string
	 */
	private String getAttendeeAsString(List<Attendee> attList) {
		String attString = "";

		int index = 0;

		for (Attendee att : attList) {
			attString = attString.concat(att.getName());

			index++;

			if (attList.size() != index) {
				attString = attString.concat(", ");
			}
		}

		return attString;
	}

	/**
	 * Gets the all assigned aspects.
	 * 
	 * @return the all assigned aspects
	 */
	private List<Aspect> getAllAssignedAspects() {
		List<Attendee> allAttendeesList = Application.getInstance()
				.getAttendeeMgmt().getAttendees();

		List<Aspect> allAssignedAspList = new ArrayList<Aspect>();

		for (Attendee att : allAttendeesList) {
			if (att.getRole() == Role.REVIEWER) {
				List<Aspect> localAspList = Application.getInstance()
						.getAttendeeMgmt().getAspects(att);
				for (Aspect localAsp : localAspList) {
					if (!allAssignedAspList.contains(localAsp)) {
						allAssignedAspList.add(localAsp);
					}
				}
			}
		}

		return allAssignedAspList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		if (column == 0) {
			return Data.getInstance().getLocaleStr("aspectsManager.aspect");
		} else {
			return Data.getInstance().getLocaleStr("aspectsManager.reviewers");
		}
	}

}
