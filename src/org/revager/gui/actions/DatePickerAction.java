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
package org.revager.gui.actions;

import java.awt.event.ActionEvent;
import java.util.Date;

import javax.swing.AbstractAction;

import org.revager.gui.UI;
import org.revager.gui.helpers.DatePicker;

/**
 * The Class DatePickerAction.
 */
@SuppressWarnings("serial")
public class DatePickerAction extends AbstractAction {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// instantiate the DatePicker
		DatePicker dp = new DatePicker(UI.getInstance().getMeetingDialog(),
				UI.getInstance().getMeetingDialog().getDateTxtFld());

		// previously selected date
		Date selectedDate = dp.parseDate(UI.getInstance().getMeetingDialog().getDateTxtFld().getText());
		dp.setSelectedDate(selectedDate);
		dp.start(UI.getInstance().getMeetingDialog().getDateTxtFld());
	};

}
