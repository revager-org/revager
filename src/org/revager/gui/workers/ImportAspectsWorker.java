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

import static org.revager.app.model.Data.translate;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.revager.app.Application;
import org.revager.app.model.appdata.AppCatalog;
import org.revager.app.model.schema.Aspect;
import org.revager.app.model.schema.Aspects;
import org.revager.gui.UI;
import org.revager.tools.GUITools;

/**
 * Worker for importing aspects from a XML file.
 */
public class ImportAspectsWorker extends SwingWorker<Void, Void> {

	/**
	 * The file path.
	 */
	private String filePath = null;

	/**
	 * The catalog.
	 */
	private AppCatalog catalog = null;

	/**
	 * Instantiates a new import aspects worker.
	 * 
	 * @param filePath
	 *            the file path
	 * @param catalog
	 *            the catalog
	 */
	public ImportAspectsWorker(String filePath, AppCatalog catalog) {
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
		UI.getInstance().getAspectsManagerFrame().notifySwitchToProgressMode();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				UI.getInstance().getAspectsManagerFrame().switchToProgressMode(translate("Importing aspects ..."));
			}
		});

		try {
			Aspects asps = Application.getInstance().getImportExportCtl().importAspectsXML(this.filePath);

			for (Aspect asp : asps.getAspects()) {
				String cate = asp.getCategory();
				String dir = asp.getDirective();

				if (cate.trim().equals("")) {
					cate = translate("(No Category)");
				}

				if (dir.trim().equals("")) {
					dir = translate("(No Directive)");
				}

				catalog.newAspect(dir, asp.getDescription(), cate);
			}

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					UI.getInstance().getAspectsManagerFrame().setStatusMessage(translate("Aspects imported successfully."),
							false);
				}
			});
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					GUITools.getMessagePane(
							translate("Cannot import selected file. The content isn't conform to the expected format (Resi XML Schema).")
									+ "\n\n" + e.getMessage()),
					translate("Error"), JOptionPane.ERROR_MESSAGE);

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					UI.getInstance().getAspectsManagerFrame().setStatusMessage(translate("Cannot import aspects!"), false);
				}
			});
		}

		UI.getInstance().getAspectsManagerFrame().notifySwitchToEditMode();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				UI.getInstance().getAspectsManagerFrame().updateTree(catalog, null, null);

				UI.getInstance().getAspectsManagerFrame().switchToEditMode();
			}
		});

		return null;
	}

}
