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
package neos.resi.gui.actions.meeting;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import neos.resi.app.model.schema.Meeting;
import neos.resi.gui.UI;

/**
 * The Class EditMeetingAction.
 */
@SuppressWarnings("serial")
public class EditMeetingAction extends AbstractAction {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Meeting meet = UI.getInstance().getMainFrame().getSelectedMeeting();

		if (meet != null) {
			UI.getInstance().getMeetingDialog().setCurrentMeeting(meet);
			UI.getInstance().getMeetingDialog().setVisible(true);
		}
	}

}
