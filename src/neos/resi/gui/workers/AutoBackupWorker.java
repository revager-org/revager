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

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import neos.resi.app.Application;
import neos.resi.app.model.Data;
import neos.resi.gui.AbstractFrame;
import neos.resi.gui.UI;

/**
 * Worker for automatic backups in the background.
 */
public class AutoBackupWorker extends SwingWorker<Void, Void> {

	/**
	 * The observing frames.
	 */
	private List<AbstractFrame> obsFrames = new ArrayList<AbstractFrame>();

	/**
	 * Adds the given frame as observer to the worker.
	 * 
	 * @param af
	 *            the frame
	 */
	public void addObserverFrame(AbstractFrame af) {
		obsFrames.add(af);
	}

	/**
	 * Removes the given frame as observer from the worker.
	 * 
	 * @param af
	 *            the frame
	 */
	public void removeObserverFrame(AbstractFrame af) {
		obsFrames.remove(af);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() {
		long intervalInSeconds = Long.parseLong(Data.getInstance().getResource(
				"autoBackupIntervalInSec"));

		while (true) {
			try {
				Thread.sleep(intervalInSeconds * 1000);

				if (UI.getInstance().getStatus() == UI.Status.UNSAVED_CHANGES) {
					/*
					 * Set doing message
					 */
					for (AbstractFrame af : obsFrames) {
						af.setStatusMessage(Data.getInstance().getLocaleStr(
								"status.doingAutoBackup"), true);
					}

					Application.getInstance().getApplicationCtl()
							.backupReview();

					/*
					 * Set done message
					 */
					for (AbstractFrame af : obsFrames) {
						af.setStatusMessage(Data.getInstance().getLocaleStr(
								"status.autoBackupDone"), false);
					}
				}
			} catch (Exception e) {
				/*
				 * Set failed message
				 */
				for (AbstractFrame af : obsFrames) {
					af.setStatusMessage(Data.getInstance().getLocaleStr(
							"status.autoBackupFailed"), false);
				}
			}
		}
	}

}
