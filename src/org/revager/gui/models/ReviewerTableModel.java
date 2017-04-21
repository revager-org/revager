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
import org.revager.app.model.schema.Aspect;
import org.revager.app.model.schema.Attendee;

/**
 * The Class ReviewerTableModel.
 */
@SuppressWarnings("serial")
public class ReviewerTableModel extends AbstractTableModel {

	Attendee reviewer = null;

	/**
	 * Instantiates a new reviewer table model.
	 * 
	 * @param reviewer
	 *            the reviewer
	 */
	public ReviewerTableModel(Attendee reviewer) {
		this.reviewer = reviewer;
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
		return Application.getInstance().getAttendeeMgmt().getNumberOfAspects(reviewer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int row, int column) {
		List<Aspect> aspList = Application.getInstance().getAttendeeMgmt().getAspects(reviewer);

		if (column == 0) {
			String direc = aspList.get(row).getDirective();
			String desc = aspList.get(row).getDescription();

			return direc + "\n\n" + desc;
		} else {
			return aspList.get(row).getCategory();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		if (column == 1) {
			return _("Category");
		} else {
			return _("Aspect");
		}
	}

}
