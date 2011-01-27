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

import java.awt.Cursor;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.revager.gui.UI;
import org.revager.gui.dialogs.SettingsDialog;

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

	private Mode mode;

	private boolean visible;

	/**
	 * Instantiates a new settings worker.
	 * 
	 * @param mode
	 *            the worker mode
	 */
	public SettingsWorker(Mode mode) {
		this(mode, true);
	}

	public SettingsWorker(Mode mode, boolean visible) {
		super();

		this.mode = mode;
		this.visible = visible;
	}

	@Override
	protected Void doInBackground() throws Exception {
		final SettingsDialog settDialog = UI.getInstance().getSettingsDialog();

		if (this.mode == Mode.STORE_APPDATA) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					settDialog.updateAppData();

					settDialog.setDialogVisible(visible);
				}
			});
		} else if (this.mode == Mode.LOAD_APPDATA) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					settDialog.updateDialogData();

					settDialog.setDialogVisible(visible);
				}
			});
		} else if (this.mode == Mode.UPDATE_LOGO_VIEWS) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					settDialog.updateLogoViews();

					settDialog.setDialogVisible(visible);
				}
			});
		}

		return null;
	}
}
