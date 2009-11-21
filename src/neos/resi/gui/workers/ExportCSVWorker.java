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

import java.awt.Desktop;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import neos.resi.app.Application;
import neos.resi.app.ResiFileFilter;
import neos.resi.app.SeverityManagement;
import neos.resi.app.model.Data;
import neos.resi.app.model.appdata.AppCSVProfile;
import neos.resi.app.model.schema.Meeting;
import neos.resi.gui.MainFrame;
import neos.resi.gui.UI;
import neos.resi.gui.helpers.FileChooser;
import neos.resi.gui.protocol.ProtocolFrame;
import neos.resi.tools.GUITools;

/**
 * Worker to export a CSV file with findings.
 */
public class ExportCSVWorker extends SwingWorker<Void, Void> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() {
		SeverityManagement sevMgmt = Application.getInstance()
				.getSeverityMgmt();

		MainFrame mainFrame = UI.getInstance().getMainFrame();
		ProtocolFrame protFrame = UI.getInstance().getProtocolFrame();

		FileChooser fileChooser = UI.getInstance().getFileChooser();
		fileChooser.setFile(null);

		Meeting meet = UI.getInstance().getExportCSVDialog()
				.getSelectedMeeting();

		if (fileChooser.showDialog(UI.getInstance().getExportCSVDialog(),
				FileChooser.MODE_SAVE_FILE, ResiFileFilter.TYPE_CSV) == FileChooser.SELECTED_APPROVE) {
			UI.getInstance().getExportCSVDialog().switchToProgressMode();

			String dir = fileChooser.getFile().getAbsolutePath();

			try {
				AppCSVProfile profile = Data.getInstance().getAppData()
						.getCSVProfiles().get(0);

				Map<String, String> sevMaps = new HashMap<String, String>();

				List<String> mappingList = UI.getInstance()
						.getExportCSVDialog().getSelColNameList();

				for (int index = 0; index < sevMgmt.getSeverities().size(); index++) {
					sevMaps.put(sevMgmt.getSeverities().get(index), mappingList
							.get(index));
				}

				String reporter = UI.getInstance().getExportCSVDialog()
						.getReporter();

				File expFile = null;

				if (UI.getInstance().getExportCSVDialog().exportRev()) {
					expFile = Application.getInstance().getImportExportCtl()
							.exportReviewFindingsCSV(dir, profile, sevMaps,
									reporter);
				} else {
					expFile = Application.getInstance().getImportExportCtl()
							.exportMeetingFindingsCSV(dir, profile, meet,
									sevMaps, reporter);
				}

				UI.getInstance().getExportCSVDialog().setVisible(false);
				UI.getInstance().getExportCSVDialog().switchToEditMode();

				if (protFrame.isVisible()) {
					protFrame.setStatusMessage(Data.getInstance().getLocaleStr(
							"csvExport.expSuccessful"), false);
				} else {
					mainFrame.setStatusMessage(Data.getInstance().getLocaleStr(
							"csvExport.expSuccessful"), false);
				}

				Desktop.getDesktop().open(expFile.getParentFile());
			} catch (Exception e) {
				UI.getInstance().getExportCSVDialog().switchToEditMode();

				JOptionPane.showMessageDialog(UI.getInstance()
						.getExportCSVDialog(), GUITools.getMessagePane(e
						.getMessage()), Data.getInstance()
						.getLocaleStr("error"), JOptionPane.ERROR_MESSAGE);
			}

		}

		return null;
	}

}
