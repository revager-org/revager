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

import org.revager.app.model.Data;
import org.revager.gui.UI;
import org.revager.gui.dialogs.AttendeeDialog;
import org.revager.gui.dialogs.assistant.AssistantDialog;
import org.revager.gui.workers.NewInstantReviewWorker;
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
		if (Data.getInstance().getMode().equals("moderator")) {
			UI.getInstance().getAssistantDialog().setVisible(false);

			GUITools.executeSwingWorker(new NewReviewWorker());
		} else if (Data.getInstance().getMode().equals("instant")) {
			AssistantDialog assistant = UI.getInstance().getAssistantDialog();
			AttendeeDialog attDiag = UI.getInstance().getAttendeeDialog();

			if (!attDiag.getNameTxtFld().getText().trim().equals("")) {
				GUITools.executeSwingWorker(new NewInstantReviewWorker());
			} else {
				String message = _("Please enter the name of the attendee.");

				assistant.setMessage(message);

				attDiag.getNameTxtFld().setBorder(UI.MARKED_BORDER_INLINE);
			}
		}
	}

}
