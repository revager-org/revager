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
package org.revager;

import static org.revager.app.model.Data._;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.revager.app.Application;
import org.revager.app.model.ApplicationData;
import org.revager.app.model.Data;
import org.revager.app.model.appdata.AppSettingKey;
import org.revager.gui.UI;
import org.revager.tools.AppTools;
import org.revager.tools.FileTools;
import org.revager.tools.GUITools;

/**
 * Starts the Resi application.
 */
public class Main {

	private static String argData = null;

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
				argData = args[1].trim();

				Data.getInstance().getAppData()
						.setCustomAppDataDirectory(argData);
			}
		}

		/*
		 * Try to migrate old RevAger data folder
		 */
		migrateOldData();

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
			String lang = Data.getInstance().getAppData()
					.getSetting(AppSettingKey.APP_LANGUAGE);

			if (lang == null) {
				lang = Locale.getDefault().getLanguage();
			}

			if (!Data.getInstance().isLanguageAvailable(lang)) {
				lang = Locale.ENGLISH.getLanguage();
			}

			Data.getInstance().getAppData()
					.setSetting(AppSettingKey.APP_LANGUAGE, lang);

			Data.getInstance().setLocale(new Locale(lang));

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
			JOptionPane.showMessageDialog(null,
					GUITools.getMessagePane(e.getMessage()), _("Error"),
					JOptionPane.ERROR_MESSAGE);

			System.err.println(e.getMessage());
		}
	}

	/**
	 * Restart the application.
	 */
	public static void restartApplication() {
		ProcessBuilder pb = null;

		String appPath = AppTools.getJarLocation();
		String javaStubPath = AppTools.getJavaAppStubLocation();

		if (appPath.toLowerCase().endsWith(".jar")
				&& UI.getInstance().getPlatform() == UI.Platform.MAC
				&& javaStubPath != null) {
			if (argData != null) {
				pb = new ProcessBuilder(javaStubPath, "-data", argData);
			} else {
				pb = new ProcessBuilder(javaStubPath);
			}
		} else if (appPath.toLowerCase().endsWith(".jar")) {
			String javaBinary = "java";

			if (UI.getInstance().getPlatform() == UI.Platform.WINDOWS) {
				javaBinary = "javaw.exe";
			}

			if (argData != null) {
				pb = new ProcessBuilder(javaBinary, "-jar",
						AppTools.getJarLocation(), "-data", argData);
			} else {
				pb = new ProcessBuilder(javaBinary, "-jar",
						AppTools.getJarLocation());
			}
		} else {
			pb = new ProcessBuilder(appPath);

			if (argData != null) {
				pb = new ProcessBuilder(appPath, "-data", argData);
			} else {
				pb = new ProcessBuilder(appPath);
			}
		}

		try {
			pb.start();
		} catch (Exception e) {
			JOptionPane
					.showMessageDialog(
							UI.getInstance().getMainFrame(),
							GUITools.getMessagePane(_("The application cannot be restarted automatically on your system. Please start up RevAger manually after it has been closed.")),
							_("Warning"), JOptionPane.WARNING_MESSAGE);
		}

		exitApplication();
	}

	/**
	 * Exit the application.
	 */
	public static void exitApplication() {
		UI.getInstance().getMainFrame().dispose();

		Application.getInstance().getApplicationCtl().clearReview();

		System.exit(0);
	}

	/**
	 * Migrate data from the 1.1 series of RevAger.
	 */
	private static void migrateOldData() {
		boolean newDataPathExist = false;

		boolean oldDataPathFound = false;
		boolean newDataPathFound = false;

		String oldDataPath = null;
		String newDataPath = null;

		String currentDirectory = new File(ApplicationData.class
				.getProtectionDomain().getCodeSource().getLocation().getPath())
				.getAbsolutePath();

		int endIndex = currentDirectory.lastIndexOf(File.separator);

		String[] possibleDirectories = {
				System.getProperty("user.home") + File.separator,
				currentDirectory = currentDirectory.substring(0, endIndex + 1),
				System.getProperty("user.dir") + File.separator };

		/*
		 * Look for an existing old data path
		 */
		for (String dir : possibleDirectories) {
			if (new File(dir
					+ Data.getInstance().getResource("dataDirectoryNameOld"))
					.exists() && oldDataPathFound == false) {
				oldDataPathFound = true;

				oldDataPath = dir
						+ Data.getInstance()
								.getResource("dataDirectoryNameOld");
			}
		}

		/*
		 * Look for an existing new data path
		 */
		for (String dir : possibleDirectories) {
			if (new File(dir
					+ Data.getInstance().getResource("dataDirectoryName"))
					.exists() && newDataPathExist == false) {
				newDataPathExist = true;
			}
		}

		/*
		 * Look for new data path to create
		 */
		for (String dir : possibleDirectories) {
			if (new File(dir).canWrite() && newDataPathFound == false) {
				newDataPathFound = true;

				newDataPath = dir
						+ Data.getInstance().getResource("dataDirectoryName");
			}
		}

		/*
		 * Ask user to migrate old data
		 */
		if (oldDataPathFound && newDataPathFound && !newDataPathExist) {
			if (JOptionPane
					.showConfirmDialog(
							null,
							GUITools.getMessagePane(_("RevAger has detected an existing review data folder from an earlier version of this application. Do you want to use the data for the new version of RevAger?")),
							_("Question"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				try {
					FileTools.copyDirectory(new File(oldDataPath), new File(
							newDataPath));
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null,
							GUITools.getMessagePane(e.getMessage()),
							_("Error"), JOptionPane.ERROR_MESSAGE);

					System.err.println(e.getMessage());
				}
			}
		}
	}

}