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
package neos.resi;

import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import neos.resi.app.model.Data;
import neos.resi.app.model.DataException;
import neos.resi.app.model.appdata.AppSettingKey;
import neos.resi.gui.UI;
import neos.resi.tools.GUITools;

/**
 * Starts the Resi application.
 */
public class Main {

	/**
	 * The main method is not part of unit testing because it belongs to the
	 * system test.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		/*
		 * Set the standard file encoding
		 */
		System.setProperty("file.encoding", "UTF-8");

		/*
		 * Set custom path for storing the application data if given.
		 */
		if (args.length >= 2) {
			if (args[0].trim().equals("-data")) {
				Data.getInstance().getAppData().setCustomAppDataDirectory(
						args[1].trim());
			}
		}

		/*
		 * Initialize application data and start the main UI in the thread that
		 * runs the event loop.
		 */
		try {
			/*
			 * Initialize the database
			 */
			Data.getInstance().getAppData().initialize();

			/*
			 * Set the language
			 */
			Locale loc = null;

			try {
				String lang = Data.getInstance().getAppData().getSetting(
						AppSettingKey.APP_LANGUAGE);

				if (lang != null) {
					loc = new Locale(lang);
				} else {
					throw new DataException();
				}
			} catch (DataException e) {
				loc = new Locale(Data.getInstance().getResource(
						"appDefaultLang"));
			}

			Data.getInstance().setLocale(loc);

			/*
			 * Run the user interface
			 */
			Runnable ui = new Runnable() {
				@Override
				public void run() {
					UI.getInstance().run();
				}
			};

			SwingUtilities.invokeLater(ui);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, GUITools.getMessagePane(e
					.getMessage()), Data.getInstance().getLocaleStr("error"),
					JOptionPane.ERROR_MESSAGE);

			System.err.println(e.getMessage());
		}
	}

}
