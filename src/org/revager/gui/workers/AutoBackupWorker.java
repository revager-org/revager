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

import static org.revager.app.model.Data.translate;

import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.revager.app.Application;
import org.revager.app.model.Data;
import org.revager.gui.AbstractFrame;
import org.revager.gui.UI;

/**
 * Worker for automatic backups in the background.
 */
public class AutoBackupWorker extends SwingWorker<Void, Void> {

	/**
	 * The observing frames.
	 */
	private Set<AbstractFrame> obsFrames = new HashSet<AbstractFrame>();

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
		long intervalInSeconds = Long.parseLong(Data.getInstance().getResource("autoBackupIntervalInSec"));

		while (true) {
			try {
				Thread.sleep(intervalInSeconds * 1000);

				if (UI.getInstance().getStatus() == UI.Status.UNSAVED_CHANGES) {
					Application.getInstance().getApplicationCtl().backupReview();

					/*
					 * Set done message
					 */
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							for (AbstractFrame af : obsFrames) {
								af.setStatusMessage(translate("Auto backup done."), false);
							}
						}
					});
				}
			} catch (Exception e) {
				/*
				 * Set failed message
				 */
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						for (AbstractFrame af : obsFrames) {
							af.setStatusMessage(translate("Auto backup failed!"), false);
						}
					}
				});
			}
		}
	}

}
