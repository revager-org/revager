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

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.revager.app.Application;
import org.revager.app.model.Data;
import org.revager.app.model.appdata.AppAspect;
import org.revager.app.model.appdata.AppCatalog;
import org.revager.app.model.schema.Aspect;
import org.revager.app.model.schema.Aspects;
import org.revager.app.model.schema.Catalog;
import org.revager.gui.UI;
import org.revager.tools.GUITools;


/**
 * Worker to export a catalog with aspects to a XML file.
 */
public class ExportCatalogWorker extends SwingWorker<Void, Void> {

	/**
	 * The file path.
	 */
	private String filePath = null;

	/**
	 * The catalog.
	 */
	private AppCatalog catalog = null;

	/**
	 * Instantiates a new export catalog worker.
	 * 
	 * @param filePath
	 *            the file path
	 * @param catalog
	 *            the catalog
	 */
	public ExportCatalogWorker(String filePath, AppCatalog catalog) {
		super();

		this.filePath = filePath;
		this.catalog = catalog;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() throws Exception {
		UI.getInstance().getAspectsManagerFrame().switchToProgressMode(
				Data.getInstance().getLocaleStr("status.exportingCatalog"));

		try {
			Catalog cat = new Catalog();

			if (catalog.getDescription() != null) {
				cat.setDescription(catalog.getDescription());
			} else {
				cat.setDescription("");
			}

			cat.setAspects(new Aspects());

			for (String c : this.catalog.getCategories()) {
				for (AppAspect a : this.catalog.getAspects(c)) {
					Aspect asp = new Aspect();
					asp.setCategory(c);
					asp.setDescription(a.getDescription());
					asp.setDirective(a.getDirective());
					asp.setId(Integer.toString(a.getId()));

					cat.getAspects().getAspects().add(asp);
				}
			}

			Application.getInstance().getImportExportCtl().exportCatalogXML(
					this.filePath, cat);

			UI.getInstance().getAspectsManagerFrame().setStatusMessage(
					Data.getInstance().getLocaleStr("status.catalogExported"),
					false);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, GUITools.getMessagePane(e
					.getMessage()), Data.getInstance().getLocaleStr("error"),
					JOptionPane.ERROR_MESSAGE);

			UI.getInstance().getAspectsManagerFrame().setStatusMessage(
					Data.getInstance().getLocaleStr(
							"status.exportingCatalogFailed"), false);
		}

		UI.getInstance().getAspectsManagerFrame().switchToEditMode();

		return null;
	}

}
