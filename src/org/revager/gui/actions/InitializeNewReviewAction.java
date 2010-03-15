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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.revager.app.model.Data;
import org.revager.gui.UI;
import org.revager.gui.dialogs.assistant.AssistantDialog;
import org.revager.gui.workers.NewReviewWorker;

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
			new NewReviewWorker().execute();
			UI.getInstance().getAssistantDialog().setVisible(false);
		} else if (Data.getInstance().getMode().equals("instant")) {
			AssistantDialog assistant = UI.getInstance().getAssistantDialog();

			if (!assistant.getAddAttendeePanel().nameTxtFld.getText().trim().equals("")) {
				new NewReviewWorker().execute();
				UI.getInstance().getAssistantDialog().setVisible(false);
				assistant.getAddAttendeePanel().updateInstantAtt();
			}
		}
	}

}
