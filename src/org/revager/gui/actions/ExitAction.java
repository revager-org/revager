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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.revager.app.Application;
import org.revager.app.model.Data;
import org.revager.gui.UI;
import org.revager.gui.UI.Status;
import org.revager.tools.GUITools;


/**
 * The Class ExitAction.
 */
@SuppressWarnings("serial")
public class ExitAction extends AbstractAction {

	/**
	 * Instantiates a new exit action.
	 */
	public ExitAction() {
		super();

		putValue(SMALL_ICON, Data.getInstance().getIcon("menuExit_16x16.png"));
		putValue(NAME, Data.getInstance().getLocaleStr("closeApplication"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit
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
		Status status = org.revager.gui.UI.getInstance().getStatus();

		if (status == Status.UNSAVED_CHANGES) {
			int option = JOptionPane.showConfirmDialog(org.revager.gui.UI
					.getInstance().getMainFrame(), GUITools.getMessagePane(Data
					.getInstance().getLocaleStr("message.unsavedChanges")),
					Data.getInstance().getLocaleStr("question"),
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (option == JOptionPane.YES_OPTION) {
				((SaveReviewAction) ActionRegistry.getInstance().get(
						SaveReviewAction.class.getName()))
						.setExitApplication(true);

				ActionRegistry.getInstance().get(
						SaveReviewAction.class.getName()).actionPerformed(e);
			}

			if (option == JOptionPane.NO_OPTION) {
				exitApplication();
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
		UI.getInstance().getMainFrame().dispose();
		Application.getInstance().getApplicationCtl().clearReview();

		System.exit(0);
	}

}
