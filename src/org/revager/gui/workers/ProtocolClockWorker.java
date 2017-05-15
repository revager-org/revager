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

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.revager.gui.findings_list.FindingsListFrame;

/**
 * Worker for the clock in the protocol window.
 */
public class ProtocolClockWorker extends SwingWorker<Void, Void> {

	/**
	 * True if the warning was displayed.
	 */
	private boolean warningDisplayed = false;

	/**
	 * The starting time.
	 */
	private long startingPoint = 0;

	/**
	 * The pause time.
	 */
	private long pausePoint = 0;

	/**
	 * True if the clock is running.
	 */
	private boolean clockRunning = false;

	/**
	 * The protocol frames.
	 */
	private List<FindingsListFrame> protocolFrames = new ArrayList<FindingsListFrame>();

	/**
	 * Adds the given protocol frame as observer to the clock.
	 * 
	 * @param pf
	 *            the protocol frame
	 */
	public void addObserverFrame(FindingsListFrame pf) {
		protocolFrames.add(pf);
	}

	/**
	 * Removes the given protocol frame as observer from the clock.
	 * 
	 * @param pf
	 *            the protocol frame
	 */
	public void removeObserverFrame(FindingsListFrame pf) {
		protocolFrames.remove(pf);
	}

	/**
	 * Stops the clock.
	 */
	public void stopClock() {
		this.clockRunning = false;
		this.pausePoint = System.currentTimeMillis();
	}

	/**
	 * Starts the clock.
	 */
	public void startClock() {
		if (startingPoint == 0) {
			this.startingPoint = System.currentTimeMillis();
		} else {
			this.startingPoint = startingPoint + (System.currentTimeMillis() - pausePoint);
		}

		this.clockRunning = true;
	}

	/**
	 * Resets the clock.
	 */
	public void resetClock() {
		this.startingPoint = 0;
		this.clockRunning = false;
		this.warningDisplayed = false;

		for (FindingsListFrame pf : protocolFrames) {
			pf.updateClock(0);
		}
	}

	/**
	 * Checks if the clock is running.
	 * 
	 * @return true, if the clock is running
	 */
	public boolean isClockRunning() {
		return clockRunning;
	}

	/**
	 * @return the warningDisplayed
	 */
	public boolean isWarningDisplayed() {
		return warningDisplayed;
	}

	/**
	 * @param warningDisplayed
	 *            the warningDisplayed to set
	 */
	public void setWarningDisplayed(boolean warningDisplayed) {
		this.warningDisplayed = warningDisplayed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() throws Exception {
		while (true) {

			for (final FindingsListFrame pf : protocolFrames) {
				if (clockRunning) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							pf.updateClock((int) ((System.currentTimeMillis() - startingPoint) / 1000));

							pf.updateCurrentTime();
						}
					});
				}
			}

			Thread.sleep(1000);
		}
	}
}
