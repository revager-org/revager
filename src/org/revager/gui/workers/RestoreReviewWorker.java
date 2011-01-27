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

import static org.revager.app.model.Data._;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.revager.app.Application;
import org.revager.gui.MainFrame;
import org.revager.gui.UI;
import org.revager.gui.UI.Status;
import org.revager.tools.GUITools;

/**
 * Worker for restoring a review file.
 */
public class RestoreReviewWorker extends SwingWorker<Void, Void> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() throws Exception {
		final MainFrame mainframe = UI.getInstance().getMainFrame();

		mainframe.notifySwitchToProgressMode();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mainframe.setAssistantMode(false);

				mainframe.switchToProgressMode();

				mainframe.setStatusMessage(_("Restoring backup ..."), true);
			}
		});

		try {
			Application.getInstance().getApplicationCtl().restoreReview();

			mainframe.notifySwitchToEditMode();

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					mainframe.setStatusMessage(
							_("Review restored successfully."), false);

					mainframe.switchToEditMode();
				}
			});
		} catch (Exception e) {
			mainframe.notifySwitchToClearMode();

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					mainframe.setStatusMessage(_("No review in process."),
							false);

					mainframe.switchToClearMode();

					mainframe.setAssistantMode(true);
				}
			});

			Application.getInstance().getApplicationCtl().clearReview();

			UI.getInstance().setStatus(Status.NO_FILE_LOADED);

			JOptionPane.showMessageDialog(
					UI.getInstance().getMainFrame(),
					GUITools.getMessagePane(_("Cannot restore review.")
							+ "\n\n" + e.getMessage()), _("Error"),
					JOptionPane.ERROR_MESSAGE);
		}

		return null;
	}

}
