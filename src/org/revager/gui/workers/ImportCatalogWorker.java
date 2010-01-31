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

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.revager.app.Application;
import org.revager.app.model.ApplicationData;
import org.revager.app.model.Data;
import org.revager.app.model.appdata.AppCatalog;
import org.revager.app.model.schema.Aspect;
import org.revager.app.model.schema.Catalog;
import org.revager.gui.UI;
import org.revager.tools.GUITools;


/**
 * Worker for importing a catalog with aspects from a XML file.
 */
public class ImportCatalogWorker extends SwingWorker<Void, Void> {

	/**
	 * Reference to application data.
	 */
	private ApplicationData appData = Data.getInstance().getAppData();

	/**
	 * The file path.
	 */
	private String filePath = null;

	/**
	 * Instantiates a new import catalog worker.
	 * 
	 * @param filePath
	 *            the file path
	 */
	public ImportCatalogWorker(String filePath) {
		super();

		this.filePath = filePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() {
		UI.getInstance().getAspectsManagerFrame().switchToProgressMode(
				Data.getInstance().getLocaleStr("status.importingCatalog"));

		AppCatalog appCat = null;

		try {
			String catalogName = new File(this.filePath).getName();

			if (catalogName.lastIndexOf(".") != -1) {
				catalogName = catalogName.substring(0, catalogName
						.lastIndexOf("."));
			}

			boolean firstTime = true;

			while (firstTime || appData.getCatalog(catalogName) != null
					|| catalogName.trim().equals("")) {
				String title = Data.getInstance().getLocaleStr(
						"aspectsManager.askForCatalogName");

				if (!firstTime) {
					title = Data.getInstance().getLocaleStr(
							"aspectsManager.catalogNameExists")
							+ "\n\n" + title;
				}

				catalogName = JOptionPane.showInputDialog(UI.getInstance()
						.getAspectsManagerFrame(), GUITools
						.getMessagePane(title), catalogName);

				firstTime = false;
			}

			if (catalogName != null) {
				Catalog cat = Application.getInstance().getImportExportCtl()
						.importCatalogXML(this.filePath);

				appCat = appData.newCatalog(catalogName);
				appCat.setDescription(cat.getDescription());

				for (Aspect asp : cat.getAspects().getAspects()) {
					String cate = asp.getCategory();
					String dir = asp.getDirective();

					if (cate.trim().equals("")) {
						cate = Data.getInstance().getLocaleStr(
								"aspectsManager.stdCategory");
					}

					if (dir.trim().equals("")) {
						dir = Data.getInstance().getLocaleStr(
								"aspectsManager.stdDirective");
					}

					appCat.newAspect(dir, asp.getDescription(), cate);
				}

				UI.getInstance().getAspectsManagerFrame().updateTree();

				UI.getInstance().getAspectsManagerFrame().setStatusMessage(
						Data.getInstance().getLocaleStr(
								"status.catalogImported"), false);
			} else {
				UI.getInstance().getAspectsManagerFrame().setStatusMessage(
						Data.getInstance().getLocaleStr(
								"status.catalogImportCanceled"), false);
			}
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

		UI.getInstance().getAspectsManagerFrame()
				.updateTree(appCat, null, null);

		UI.getInstance().getAspectsManagerFrame().switchToEditMode();

		return null;
	}
}
