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
package org.revager.gui.workers;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.revager.app.Application;
import org.revager.app.AspectManagement;
import org.revager.app.AttendeeManagement;
import org.revager.app.model.Data;
import org.revager.gui.MainFrame;
import org.revager.gui.UI;
import org.revager.tools.GUITools;


/**
 * Worker for creating a new empty review.
 */
public class NewReviewWorker extends SwingWorker<Void, Void> {
	AttendeeManagement attMgmt = Application.getInstance().getAttendeeMgmt();
	AspectManagement aspMgmt = Application.getInstance().getAspectMgmt();

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() throws Exception {
		boolean showAssistantDialog = UI.getInstance().getAssistantDialog()
				.isVisible();
		MainFrame mainframe = UI.getInstance().getMainFrame();

		mainframe.switchToProgressMode();

		mainframe.setStatusMessage(Data.getInstance().getLocaleStr(
				"status.creatingNewReview"), true);

		
		try {
			Application.getInstance().getApplicationCtl().newReview();
			

				mainframe.setStatusMessage(Data.getInstance().getLocaleStr(
					"status.createNewReviewSuccessful"), false);

			mainframe.switchToEditMode();

			UI.getInstance().setStatus(UI.Status.DATA_SAVED);
			UI.getInstance().getAssistantDialog().setVisible(false);
		} catch (Exception e) {
			mainframe.setStatusMessage(Data.getInstance().getLocaleStr(
					"status.noReviewInProcess"), false);

			mainframe.switchToClearMode();

			JOptionPane.showMessageDialog(UI.getInstance().getMainFrame(),
					GUITools.getMessagePane(Data.getInstance().getLocaleStr(
							"message.createNewReviewFailed")
							+ "\n\n" + e.getMessage()), Data.getInstance()
							.getLocaleStr("error"), JOptionPane.ERROR_MESSAGE);

			UI.getInstance().getAssistantDialog().setVisible(
					showAssistantDialog);
		}

		return null;
	}

}
