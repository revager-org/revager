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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.revager.app.model.Data;
import org.revager.gui.UI;
import org.revager.gui.workers.NewReviewWorker;
import org.revager.tools.GUITools;

/**
 * The Class NewReviewAction.
 */
@SuppressWarnings("serial")
public class NewReviewAction extends AbstractAction {

	/**
	 * Instantiates a new new review action.
	 */
	public NewReviewAction() {
		super();

		putValue(SMALL_ICON, Data.getInstance().getIcon("menuNew_16x16.png"));
		putValue(NAME, _("New Review"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (UI.getInstance().getStatus() == UI.Status.UNSAVED_CHANGES) {
			int option = JOptionPane
					.showConfirmDialog(
							org.revager.gui.UI.getInstance().getMainFrame(),
							GUITools.getMessagePane(_("There are unsaved changes in the review. Would you like to save them now?\n\nAttention: If you choose 'No' all unsaved information will get lost.")),
							_("Question"), JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE);

			if (option == JOptionPane.YES_OPTION) {
				ActionRegistry.getInstance()
						.get(SaveReviewAction.class.getName())
						.actionPerformed(null);

				if (UI.getInstance().getStatus() == UI.Status.DATA_SAVED) {
					GUITools.executeSwingWorker(new NewReviewWorker());
				}
			} else if (option == JOptionPane.NO_OPTION) {
				GUITools.executeSwingWorker(new NewReviewWorker());
			} else if (option == JOptionPane.CANCEL_OPTION) {
				return;
			}
		} else {
			GUITools.executeSwingWorker(new NewReviewWorker());
		}
	}

}