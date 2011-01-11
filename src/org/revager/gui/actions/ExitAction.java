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

import org.revager.Main;
import org.revager.app.model.Data;
import org.revager.gui.UI.Status;
import org.revager.tools.GUITools;

/**
 * The Class ExitAction.
 */
@SuppressWarnings("serial")
public class ExitAction extends AbstractAction {

	private boolean restartAgain = false;

	/**
	 * Instantiates a new exit action.
	 */
	public ExitAction() {
		super();

		putValue(SMALL_ICON, Data.getInstance().getIcon("menuExit_16x16.png"));
		putValue(NAME, _("Close Application"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	/**
	 * Whether application should be restarted or not.
	 */
	public void setRestartAgain(boolean restartAgain) {
		this.restartAgain = restartAgain;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Status status = org.revager.gui.UI.getInstance().getStatus();

		if (status == Status.UNSAVED_CHANGES) {
			int option = JOptionPane
					.showConfirmDialog(
							org.revager.gui.UI.getInstance().getMainFrame(),
							GUITools.getMessagePane(_("There are unsaved changes in the review. Would you like to save them now?\n\nAttention: If you choose 'No' all unsaved information will get lost.")),
							_("Question"), JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE);

			if (option == JOptionPane.YES_OPTION) {
				((SaveReviewAction) ActionRegistry.getInstance().get(
						SaveReviewAction.class.getName()))
						.setExitApplication(true);

				ActionRegistry.getInstance()
						.get(SaveReviewAction.class.getName())
						.actionPerformed(e);
			}

			if (option == JOptionPane.NO_OPTION) {
				exitApplication();
			}
			
			if (option == JOptionPane.CANCEL_OPTION) {
				restartAgain = false;
			}
		} else {
			exitApplication();
		}
	}

	/**
	 * This method shuts down the application by freeing all resources and
	 * returning an exit code to the operating system.
	 */
	public void exitApplication() {
		if (restartAgain) {
			Main.restartApplication();
		} else {
			Main.exitApplication();
		}
	}

}
