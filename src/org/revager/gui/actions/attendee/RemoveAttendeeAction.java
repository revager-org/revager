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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.revager.app.Application;
import org.revager.app.AttendeeManagement;
import org.revager.app.model.Data;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.Meeting;
import org.revager.gui.UI;
import org.revager.gui.helpers.TreeMeeting;
import org.revager.tools.GUITools;


/**
 * The Class RemoveAttendeeAction.
 */
@SuppressWarnings("serial")
public class RemoveAttendeeAction extends AbstractAction {

	private AttendeeManagement attMgmt = Application.getInstance()
			.getAttendeeMgmt();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Attendee attendee = UI.getInstance().getMainFrame()
				.getSelectedAttendee();
		if (attMgmt.isAttendeeRemovable(attendee)) {
			attMgmt.removeAttendee(attendee);

			UI.getInstance().getMainFrame().updateAttendeesTable(false);

			UI.getInstance().getMainFrame().updateButtons();

			UI.getInstance().getAspectsManagerFrame().updateViews();
		} else {
			String beginTxt = Data.getInstance().getLocaleStr(
					"message.attNotRemovableF");
			String endTxt = Data.getInstance().getLocaleStr(
					"message.attNotRemovableL");
			String nameTxt = attendee.getName();
			String meetTxt = "";

			for (int index = 0; index < attMgmt.getMeetings(attendee).size(); index++) {
				meetTxt = meetTxt.concat("\n\n");
				meetTxt = meetTxt.concat("- ");
				Meeting meet = attMgmt.getMeetings(attendee).get(index);
				TreeMeeting treeMeet = new TreeMeeting();
				treeMeet.setMeeting(meet);
				meetTxt = meetTxt.concat(treeMeet.toString());
			}

			String compTxt = beginTxt.concat(nameTxt).concat(endTxt).concat(
					meetTxt);

			JOptionPane.showMessageDialog(org.revager.gui.UI.getInstance()
					.getMainFrame(), GUITools.getMessagePane(compTxt), Data
					.getInstance().getLocaleStr("warningDialog.title"),
					JOptionPane.OK_OPTION);
		}
	}

}
