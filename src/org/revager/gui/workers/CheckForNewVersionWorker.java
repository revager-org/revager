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

import static org.revager.app.model.Data._;

import java.awt.Desktop;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.revager.app.model.Data;
import org.revager.app.model.appdata.AppSettingKey;
import org.revager.app.model.appdata.AppSettingValue;
import org.revager.gui.UI;
import org.revager.tools.GUITools;

/**
 * Worker for checking if a new version of this software is available.
 */
public class CheckForNewVersionWorker extends SwingWorker<Void, Void> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() {
		try {
			Thread.sleep(1000);

			if (Data.getInstance().getAppData()
					.getSettingValue(AppSettingKey.APP_CHECK_VERSION) == AppSettingValue.TRUE) {
				Properties versionProp = new Properties();

				URL versionUrl = new URL(Data.getInstance().getResource("currVerFileLocation"));

				versionProp.load(versionUrl.openStream());

				int localBuild = Integer.parseInt(Data.getInstance().getResource("appBuild"));
				String localVersion = Data.getInstance().getResource("appVersion");

				int remoteBuild = Integer.parseInt(versionProp.getProperty("build", Integer.toString(localBuild)));
				String remoteVersion = versionProp.getProperty("version", localVersion);

				String newVersionAvail = MessageFormat.format(
						_("A new version of RevAger is available!\n\nLatest version: {0}\nYour version: {1}\n\nPlease choose 'Update' to get the latest version. If you don't like to see this message again, you can turn it off in the application settings."),
						remoteVersion, localVersion);

				if (remoteBuild > localBuild) {
					Object[] options = { _("Update RevAger"), _("Ignore") };

					if (JOptionPane.showOptionDialog(UI.getInstance().getMainFrame(),
							GUITools.getMessagePane(newVersionAvail), _("Question"), JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options, options[0]) == JOptionPane.YES_OPTION) {
						Desktop.getDesktop()
								.browse(new URL(Data.getInstance().getResource("currVerBrowseURL")).toURI());
					}

					// UI.getInstance().getMainFrame().setEnabled(true);
				}
			}
		} catch (Exception e) {
			/*
			 * do nothing
			 */
		}

		return null;
	}
}
