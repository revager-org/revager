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
import org.revager.gui.actions.ActionRegistry;
import org.revager.gui.actions.OpenFindingsListAction;
import org.revager.tools.GUITools;

/**
 * Worker for creating a new empty review.
 */
public class NewReviewWorker extends SwingWorker<Void, Void> {

	private boolean instantReview = false;

	public NewReviewWorker(boolean instantReview) {
		super();

		this.instantReview = instantReview;
	}

	public NewReviewWorker() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() throws Exception {
		final boolean showAssistantDialog = UI.getInstance()
				.getAssistantDialog().isVisible();
		final MainFrame mainframe = UI.getInstance().getMainFrame();

		mainframe.notifySwitchToProgressMode();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mainframe.switchToProgressMode();
			}
		});

		mainframe.setStatusMessage(_("Creating new review ..."), true);

		try {
			Application.getInstance().getApplicationCtl().newReview();

			mainframe.notifySwitchToEditMode();

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					mainframe.setStatusMessage(
							_("New review created successfully."), false);

					if (instantReview) {
						UI.getInstance().getEditProductDialog()
								.setVisible(true);

						/*
						 * Run OpenFindingsListAction
						 */
						OpenFindingsListAction action = (OpenFindingsListAction) ActionRegistry
								.getInstance().get(
										OpenFindingsListAction.class.getName());

						action.performActionDirectly();
					}

					mainframe.switchToEditMode();
				}
			});

			UI.getInstance().setStatus(UI.Status.DATA_SAVED);
		} catch (Exception e) {
			mainframe.notifySwitchToClearMode();

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					mainframe.setStatusMessage(_("No review in process."),
							false);

					mainframe.switchToClearMode();
				}
			});

			JOptionPane.showMessageDialog(
					UI.getInstance().getMainFrame(),
					GUITools.getMessagePane(_("Cannot create new review file.")
							+ "\n\n" + e.getMessage()), _("Error"),
					JOptionPane.ERROR_MESSAGE);

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					UI.getInstance().getAssistantDialog()
							.setVisible(showAssistantDialog);
				}
			});
		}

		return null;
	}

}
