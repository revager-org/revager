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
package org.revager.gui.actions;

import static org.revager.app.model.Data._;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import org.revager.gui.UI;
import org.revager.gui.actions.attendee.ConfirmAttendeeAction;
import org.revager.gui.dialogs.AttendeeDialog;
import org.revager.gui.dialogs.assistant.AssistantDialog;
import org.revager.gui.workers.NewReviewWorker;
import org.revager.tools.GUITools;

/**
 * The Class InitializeMainFrameAction.
 */
@SuppressWarnings("serial")
public class InitializeNewReviewAction extends AbstractAction {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		final AssistantDialog assistant = UI.getInstance().getAssistantDialog();

		if (assistant.isInstantReview()) {
			AttendeeDialog attDiag = UI.getInstance().getAttendeeDialog();

			if (!attDiag.getNameTxtFld().getText().trim().equals("")) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						ActionRegistry.getInstance()
								.get(ConfirmAttendeeAction.class.getName())
								.actionPerformed(null);

						assistant.setVisible(false);
					}
				});

				UI.getInstance().getMainFrame().setAssistantMode(false);

				GUITools.executeSwingWorker(new NewReviewWorker(true));
			} else {
				String message = _("Please enter the name of the attendee.");

				assistant.setMessage(message);

				attDiag.getNameTxtFld().setBorder(UI.MARKED_BORDER_INLINE);
			}
		} else {
			assistant.setVisible(false);

			UI.getInstance().getMainFrame().setAssistantMode(false);

			GUITools.executeSwingWorker(new NewReviewWorker());
		}
	}

}
