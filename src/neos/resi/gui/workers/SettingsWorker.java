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

import javax.swing.SwingWorker;

import neos.resi.gui.UI;
import neos.resi.gui.dialogs.SettingsDialog;

/**
 * Worker to refresh the application settings dialog.
 */
public class SettingsWorker extends SwingWorker<Void, Void> {

	/**
	 * Possible modes for the worker.
	 */
	public enum Mode {
		STORE_APPDATA, LOAD_APPDATA, UPDATE_LOGO_VIEWS;
	}

	/**
	 * The current mode of the worker.
	 */
	private Mode mode;

	/**
	 * Instantiates a new settings worker.
	 * 
	 * @param mode
	 *            the worker mode
	 */
	public SettingsWorker(Mode mode) {
		super();

		this.mode = mode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() throws Exception {
		SettingsDialog settDialog = UI.getInstance().getSettingsDialog();

		if (this.mode == Mode.STORE_APPDATA) {
			settDialog.updateAppData();
		} else if (this.mode == Mode.LOAD_APPDATA) {
			settDialog.switchToProgressMode();

			settDialog.updateDialogData();

			settDialog.switchToEditMode();
		} else if (this.mode == Mode.UPDATE_LOGO_VIEWS) {
			settDialog.updateLogoViews();
		}

		return null;
	}
}
