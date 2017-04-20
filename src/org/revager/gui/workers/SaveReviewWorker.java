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
import org.revager.gui.actions.ExitAction;
import org.revager.tools.GUITools;

/**
 * Worker for storing the current review to a file
 */
public class SaveReviewWorker extends SwingWorker<Void, Void> {

	private String filePath = null;

	private boolean exitApplication = false;

	/**
	 * Instantiates a new save review worker.
	 * 
	 * @param filePath
	 *            the file path
	 * @param exitApplication
	 *            true if the application should be closed after saving
	 */
	public SaveReviewWorker(String filePath, boolean exitApplication) {
		super();

		this.filePath = filePath;
		this.exitApplication = exitApplication;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() {
		final MainFrame mainframe = UI.getInstance().getMainFrame();

		mainframe.notifySwitchToProgressMode();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mainframe.switchToProgressMode(_("Saving review ..."));
			}
		});

		try {
			Application.getInstance().getApplicationCtl().storeReview(filePath);

			mainframe.notifySwitchToEditMode();

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					mainframe.switchToEditMode();

					mainframe.setStatusMessage(_("Review saved successfully."),
							false);
				}
			});

			UI.getInstance().setStatus(UI.Status.DATA_SAVED);

			if (exitApplication) {
				((ExitAction) ActionRegistry.getInstance().get(
						ExitAction.class.getName())).exitApplication();
			}
		} catch (Exception e) {
			mainframe.notifySwitchToEditMode();

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					mainframe.switchToEditMode();

					mainframe.setStatusMessage(_("Cannot save review file."),
							false);
				}
			});

			JOptionPane.showMessageDialog(
					UI.getInstance().getMainFrame(),
					GUITools.getMessagePane(_("Cannot save review file.")
							+ "\n\n" + filePath + "\n\n" + e.getMessage()),
					_("Error"), JOptionPane.ERROR_MESSAGE);
		}

		return null;
	}
}
