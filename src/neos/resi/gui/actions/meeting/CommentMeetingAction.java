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

import neos.resi.app.model.Data;
import neos.resi.gui.MainFrame;
import neos.resi.gui.TextPopupWindow;
import neos.resi.gui.UI;
import neos.resi.gui.TextPopupWindow.ButtonClicked;

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

		if (mainframe.getSelectedMeeting() == null
				&& mainframe.getSelectedProtocol() == null) {
			return;
		}

		String comments = "";

		if (mainframe.getSelectedMeeting() != null) {
			comments = mainframe.getSelectedMeeting().getComments();
		} else {
			comments = mainframe.getSelectedProtocol().getComments();
		}

		TextPopupWindow popup = new TextPopupWindow(UI.getInstance()
				.getMainFrame(), Data.getInstance().getLocaleStr(
				"popup.commentMeeting"), comments, true);

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
