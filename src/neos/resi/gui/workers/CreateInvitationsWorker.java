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
package neos.resi.gui.workers;

import java.awt.Desktop;
import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import neos.resi.app.Application;
import neos.resi.app.ImportExportControl.InvitationType;
import neos.resi.app.model.Data;
import neos.resi.app.model.schema.Attendee;
import neos.resi.app.model.schema.Meeting;
import neos.resi.gui.UI;
import neos.resi.gui.dialogs.CreateInvitationsDialog;
import neos.resi.tools.GUITools;

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
		CreateInvitationsDialog dialog = UI.getInstance()
				.getCreateInvitationsDialog();

		dialog.unmarkAllComp();

		if (!dialog.getSelectedPath().trim().equals("")
				&& !dialog.getSelectedAttendees().isEmpty()) {
			dialog.switchToProgressMode(Data.getInstance().getLocaleStr(
					"invitationsDialog.creating"));

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

					Application.getInstance().getImportExportCtl()
							.exportInvitations(dialog.getSelectedPath(), type,
									meeting, att, attachProdRefs);
				}
			} catch (Exception exc) {
				dialog.switchToEditMode();

				JOptionPane.showMessageDialog(UI.getInstance()
						.getExportCSVDialog(), GUITools.getMessagePane(exc
						.getMessage()), Data.getInstance()
						.getLocaleStr("error"), JOptionPane.ERROR_MESSAGE);
			}

			dialog.setVisible(false);
			dialog.switchToEditMode();
			UI.getInstance().getMainFrame().setStatusMessage(
					Data.getInstance().getLocaleStr(
							"invitationsDialog.successful"), false);

			Desktop.getDesktop().open(new File(dialog.getSelectedPath()));
		} else {
			if (dialog.getSelectedAttendees().isEmpty()) {
				dialog.markAttScrollPane();
				dialog.setMessage(Data.getInstance().getLocaleStr(
						"invitationsDialog.message.att"));

				return null;
			}

			if (dialog.getSelectedPath().trim().equals("")) {
				dialog.markPathTxtField();
				dialog.setMessage(Data.getInstance().getLocaleStr(
						"invitationsDialog.message.path"));

				return null;
			}
		}

		return null;
	}

}
