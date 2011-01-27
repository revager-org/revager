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

import java.awt.Desktop;
import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.revager.app.Application;
import org.revager.app.ImportExportControl.InvitationType;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.Meeting;
import org.revager.gui.UI;
import org.revager.gui.dialogs.CreateInvitationsDialog;
import org.revager.tools.GUITools;

/**
 * Worker for creating invitations.
 */
public class CreateInvitationsWorker extends SwingWorker<Void, Void> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() throws Exception {
		final CreateInvitationsDialog dialog = UI.getInstance()
				.getCreateInvitationsDialog();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				dialog.unmarkAllComp();
			}
		});

		if (!dialog.getSelectedPath().trim().equals("")
				&& !dialog.getSelectedAttendees().isEmpty()) {
			dialog.notifySwitchToProgressMode();

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					dialog.switchToProgressMode(_("Creating invitation(s) ..."));
				}
			});

			boolean attachProdRefs = dialog.isProdSelected();
			Meeting meeting = dialog.getSelectedMeeting();

			InvitationType type = null;

			try {
				for (Attendee att : dialog.getSelectedAttendees()) {
					if (dialog.isPdfSelected()) {
						type = InvitationType.PDF;
					} else if (dialog.isDirSelected()) {
						type = InvitationType.DIRECTORY;
					} else if (dialog.isZipSelected()) {
						type = InvitationType.ZIP;
					}

					Application
							.getInstance()
							.getImportExportCtl()
							.exportInvitations(dialog.getSelectedPath(), type,
									meeting, att, attachProdRefs);
				}
			} catch (Exception exc) {
				dialog.notifySwitchToEditMode();

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						dialog.switchToEditMode();
					}
				});

				JOptionPane.showMessageDialog(UI.getInstance()
						.getExportCSVDialog(), GUITools.getMessagePane(exc
						.getMessage()), _("Error"), JOptionPane.ERROR_MESSAGE);
			}

			dialog.notifySwitchToEditMode();

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					dialog.setVisible(false);
					dialog.switchToEditMode();

					UI.getInstance()
							.getMainFrame()
							.setStatusMessage(
									_("The invitations have been created successfully."),
									false);
				}
			});

			Desktop.getDesktop().open(new File(dialog.getSelectedPath()));
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (dialog.getSelectedAttendees().isEmpty()) {
						dialog.markAttScrollPane();
						dialog.setMessage(_("You have to choose at least one attendee in order to create an invitation."));

						return;
					}

					if (dialog.getSelectedPath().trim().equals("")) {
						dialog.markPathTxtField();
						dialog.setMessage(_("Please choose a directory where to store the invitation(s)."));

						return;
					}
				}
			});
		}

		return null;
	}

}
