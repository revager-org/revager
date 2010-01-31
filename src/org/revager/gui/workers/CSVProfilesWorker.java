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

import javax.swing.SwingWorker;

import org.revager.gui.UI;


/**
 * Worker for updating the CSV Profiles dialog.
 */
public class CSVProfilesWorker extends SwingWorker<Void, Void> {

	/**
	 * The name of the selected CSV profile.
	 */
	private String selItem = null;

	/**
	 * The index of the selected CSV profile.
	 */
	private int selIdx = -1;

	/**
	 * Instantiates a new CSV profiles worker.
	 */
	public CSVProfilesWorker() {
		super();
	}

	/**
	 * Instantiates a new CSV profiles worker.
	 * 
	 * @param selItem
	 *            the name of the selected CSV profile
	 */
	public CSVProfilesWorker(String selItem) {
		super();

		this.selItem = selItem;
	}

	/**
	 * Instantiates a new CSV profiles worker.
	 * 
	 * @param selIdx
	 *            the index of the selected CSV profile
	 */
	public CSVProfilesWorker(int selIdx) {
		super();

		this.selIdx = selIdx;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() throws Exception {
		UI.getInstance().getCSVProfilesDialog().updateAppData(selItem, selIdx);

		return null;
	}

}
