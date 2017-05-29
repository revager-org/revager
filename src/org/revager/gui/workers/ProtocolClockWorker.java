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

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * Worker for the clock in the protocol window.
 */
public class ProtocolClockWorker extends SwingWorker<Void, Void> {

	private static final String PROPERTY_STRING_CLOCK = "clock";

	private boolean warningDisplayed = false;
	private long startingPoint = 0;
	private long pausePoint = 0;
	private boolean clockRunning = false;
	private int seconds;
	private int oldSeconds;

	public void stopClock() {
		this.clockRunning = false;
		this.pausePoint = System.currentTimeMillis();
	}

	public void startClock() {
		if (startingPoint == 0) {
			this.startingPoint = System.currentTimeMillis();
		} else {
			this.startingPoint = startingPoint + (System.currentTimeMillis() - pausePoint);
		}
		this.clockRunning = true;
	}

	public void resetClock() {
		this.startingPoint = 0;
		this.clockRunning = false;
		this.warningDisplayed = false;
		firePropertyChange(PROPERTY_STRING_CLOCK, oldSeconds, 0);
	}

	public boolean isClockRunning() {
		return clockRunning;
	}

	public boolean isWarningDisplayed() {
		return warningDisplayed;
	}

	public void setWarningDisplayed(boolean warningDisplayed) {
		this.warningDisplayed = warningDisplayed;
	}

	@Override
	protected Void doInBackground() throws Exception {
		while (true) {
			SwingUtilities.invokeLater(() -> {
				if (clockRunning) {
					oldSeconds = seconds;
					seconds = (int) (System.currentTimeMillis() - startingPoint) / 1000;
					firePropertyChange(PROPERTY_STRING_CLOCK, oldSeconds, seconds);
				}
			});
			Thread.sleep(500);
		}
	}
}
