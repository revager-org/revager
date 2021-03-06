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
package org.revager.gui.actions.attendee;

import static org.revager.app.model.Data.translate;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.revager.app.model.appdata.AppAttendee;
import org.revager.gui.DirectoryPopupWindow;
import org.revager.gui.UI;

/**
 * The Class SelectAttOutOfDirAction.
 */
@SuppressWarnings("serial")
public class SelectAttOutOfDirAction extends AbstractAction {
	private DirectoryPopupWindow popup;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ev) {
		if (UI.getInstance().getAttendeeDialog().isVisible())
			popup = new DirectoryPopupWindow(UI.getInstance().getAttendeeDialog(), translate("Directory"));
		else if (UI.getInstance().getAssistantDialog().isVisible())
			popup = new DirectoryPopupWindow(UI.getInstance().getAssistantDialog(), translate("Directory"));
		popup.setVisible(true);

		if (popup.getButtonClicked() == DirectoryPopupWindow.ButtonClicked.OK) {
			if (popup.getAttendeeBx().getSelectedItem() != null) {
				AppAttendee appAtt = ((AppAttendee) popup.getAttendeeBx().getSelectedItem());

				UI.getInstance().getAttendeeDialog().setCurrentAppAttendee(appAtt);
			}
		}
	}

}
