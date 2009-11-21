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

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import neos.resi.app.Application;
import neos.resi.app.model.Data;
import neos.resi.gui.MainFrame;
import neos.resi.gui.UI;
import neos.resi.tools.GUITools;

/**
 * Worker for loading a review file.
 */
public class LoadReviewWorker extends SwingWorker<Void, Void> {

	/**
	 * The file path.
	 */
	private String filePath = null;

	/**
	 * Instantiates a new load review worker.
	 * 
	 * @param filePath
	 *            the file path
	 */
	public LoadReviewWorker(String filePath) {
		this.filePath = filePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() {
		boolean showAssistantDialog = UI.getInstance().getAssistantDialog()
				.isVisible();
		MainFrame mainframe = UI.getInstance().getMainFrame();

		mainframe.switchToProgressMode();

		mainframe.setStatusMessage(Data.getInstance().getLocaleStr(
				"status.loadingReview"), true);

		UI.getInstance().getAssistantDialog().setVisible(false);

		try {
			Application.getInstance().getApplicationCtl().loadReview(filePath);

			mainframe.setStatusMessage(Data.getInstance().getLocaleStr(
					"status.loadReviewSuccessful"), false);

			UI.getInstance().setStatus(UI.Status.DATA_SAVED);

			mainframe.switchToEditMode();
		} catch (Exception e) {
			mainframe.setStatusMessage(Data.getInstance().getLocaleStr(
					"status.noReviewInProcess"), false);

			mainframe.switchToClearMode();

			JOptionPane.showMessageDialog(null, GUITools.getMessagePane(Data
					.getInstance().getLocaleStr("message.loadReviewFailed")
					+ "\n\n" + e.getMessage()), Data.getInstance()
					.getLocaleStr("error"), JOptionPane.ERROR_MESSAGE);

			UI.getInstance().getAssistantDialog().setVisible(
					showAssistantDialog);
		}

		return null;
	}

}
