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

import static org.revager.app.model.Data.translate;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.revager.gui.MainFrame;
import org.revager.gui.TextPopupWindow;
import org.revager.gui.TextPopupWindow.ButtonClicked;
import org.revager.gui.UI;

/**
 * The Class CommentMeetingAction.
 */
@SuppressWarnings("serial")
public class CommentMeetingAction extends AbstractAction {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		MainFrame mainframe = UI.getInstance().getMainFrame();

		if (mainframe.getSelectedMeeting() == null && mainframe.getSelectedProtocol() == null) {
			return;
		}

		String comments = "";

		if (mainframe.getSelectedMeeting() != null) {
			comments = mainframe.getSelectedMeeting().getComments();
		} else {
			comments = mainframe.getSelectedProtocol().getComments();
		}

		TextPopupWindow popup = new TextPopupWindow(UI.getInstance().getMainFrame(),
				translate("Comments on the selected meeting:"), comments, true);

		popup.setVisible(true);

		/*
		 * saving comment of the selected meeting
		 */
		if (popup.getButtonClicked() == ButtonClicked.OK) {
			if (mainframe.getSelectedMeeting() != null) {
				mainframe.getSelectedMeeting().setComments(popup.getInput());
			} else {
				mainframe.getSelectedProtocol().setComments(popup.getInput());
			}
		}
	}
}
