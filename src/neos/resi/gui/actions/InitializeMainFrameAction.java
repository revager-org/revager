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
package neos.resi.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import neos.resi.gui.UI;
import neos.resi.gui.dialogs.AssistantDialog;
import neos.resi.gui.workers.LoadReviewWorker;
import neos.resi.gui.workers.NewReviewWorker;

/**
 * The Class InitializeMainFrameAction.
 */
@SuppressWarnings("serial")
public class InitializeMainFrameAction extends AbstractAction {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		AssistantDialog.Selection sel = UI.getInstance().getAssistantDialog()
				.getSelected();

		if (sel == AssistantDialog.Selection.NEW_REVIEW) {
			new NewReviewWorker().execute();
		} else if (sel == AssistantDialog.Selection.LOAD_REVIEW) {
			new LoadReviewWorker(UI.getInstance().getAssistantDialog()
					.getPath()).execute();
		} else if (sel == AssistantDialog.Selection.MANAGE_ASPECTS) {
			UI.getInstance().setStatus(UI.Status.NO_FILE_LOADED);

			UI.getInstance().getAssistantDialog().setVisible(false);
			UI.getInstance().getAspectsManagerFrame().setVisible(true);
		}
	}

}
