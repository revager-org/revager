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

import java.io.File;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import neos.resi.app.Application;
import neos.resi.app.model.ApplicationData;
import neos.resi.app.model.Data;
import neos.resi.app.model.appdata.AppCatalog;
import neos.resi.app.model.schema.Aspect;
import neos.resi.app.model.schema.Catalog;
import neos.resi.gui.UI;
import neos.resi.tools.FileTools;
import neos.resi.tools.GUITools;

/**
 * Worker for loading the default catalogs.
 */
public class LoadStdCatalogsWorker extends SwingWorker<Void, Void> {

	/**
	 * Reference to application data.
	 */
	private ApplicationData appData = Data.getInstance().getAppData();

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() {
		UI.getInstance().getAspectsManagerFrame().switchToProgressMode(
				Data.getInstance().getLocaleStr("status.importingCatalog"));

		String fileEnding = Data.getInstance().getResource("fileEndingCatalog");
		String pathCatalogs = Data.getInstance().getResource("path.catalogs")
				+ Data.getInstance().getLocale().getLanguage() + "/";
		String pathWorkDir = Data.getInstance().getAppData().getAppDataPath()
				+ Data.getInstance().getResource("workDirectoryName");

		new File(pathWorkDir).mkdir();
		AppCatalog appCatalog = null;

		for (String catalogName : Data.getInstance().getLocaleStr(
				"standardCatalogs").split(",")) {
			try {
				/*
				 * Import catalog from file
				 */
				URL catalog = getClass().getResource(
						pathCatalogs + catalogName + "." + fileEnding);
				File catalogFile = new File(pathWorkDir + "catalog."
						+ fileEnding);

				UI.getInstance().getAspectsManagerFrame().switchToProgressMode(
						Data.getInstance().getLocaleStr(
								"status.importingCatalog")
								+ " " + catalogName);

				FileTools.copyFile(catalog, catalogFile);

				Catalog resiCatalog = Application.getInstance()
						.getImportExportCtl().importCatalogXML(
								catalogFile.getAbsolutePath());

				catalogFile.delete();

				/*
				 * Write catalog into the database
				 */
				int suffix = 1;

				while (appData.getCatalog(catalogName) != null) {
					catalogName = catalogName + " " + suffix;
					suffix++;
				}

				appCatalog = appData.newCatalog(catalogName);
				appCatalog.setDescription(resiCatalog.getDescription());

				for (Aspect asp : resiCatalog.getAspects().getAspects()) {
					appCatalog.newAspect(asp.getDirective(), asp
							.getDescription(), asp.getCategory());
				}

				UI.getInstance().getAspectsManagerFrame().updateTree();

				UI.getInstance().getAspectsManagerFrame().setStatusMessage(
						Data.getInstance().getLocaleStr(
								"status.catalogImported"), false);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(UI.getInstance()
						.getAspectsManagerFrame(), GUITools.getMessagePane(Data
						.getInstance().getLocaleStr("message.importFailed")
						+ "\n\n" + e.getMessage()), Data.getInstance()
						.getLocaleStr("error"), JOptionPane.ERROR_MESSAGE);

				UI.getInstance().getAspectsManagerFrame().setStatusMessage(
						Data.getInstance().getLocaleStr(
								"status.importingCatalogFailed"), false);
			}
		}

		UI.getInstance().getAspectsManagerFrame().updateTree(appCatalog, null,
				null);

		UI.getInstance().getAspectsManagerFrame().switchToEditMode();

		return null;
	}

}
