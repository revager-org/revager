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
package org.revager.gui.actions.meeting;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.revager.app.Application;
import org.revager.app.MeetingManagement;
import org.revager.app.ProtocolManagement;
import org.revager.gui.MainFrame;
import org.revager.gui.UI;


/**
 * The Class RemoveMeetingAction.
 */
@SuppressWarnings("serial")
public class RemoveMeetingAction extends AbstractAction {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		ProtocolManagement protMgmt = Application.getInstance()
				.getProtocolMgmt();
		MeetingManagement meetMgmt = Application.getInstance().getMeetingMgmt();

		MainFrame mainframe = UI.getInstance().getMainFrame();

		if (mainframe.getSelectedMeeting() == null
				&& mainframe.getSelectedProtocol() == null) {
			return;
		}

		if (mainframe.getSelectedMeeting() != null) {
			meetMgmt.removeMeeting(mainframe.getSelectedMeeting());
		} else {
			protMgmt.clearProtocol(protMgmt.getMeeting(mainframe
					.getSelectedProtocol()));
		}

		mainframe.updateMeetingsTree();
		mainframe.updateButtons();
	}

}
