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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.revager.app.Application;
import org.revager.app.ResiFileFilter;
import org.revager.app.model.Data;
import org.revager.app.model.schema.Meeting;
import org.revager.gui.MainFrame;
import org.revager.gui.UI;
import org.revager.gui.helpers.FileChooser;
import org.revager.gui.protocol.ProtocolFrame;
import org.revager.tools.GUITools;


/**
 * Worker for creating protocols.
 */
public class ExportPDFProtocolWorker extends SwingWorker<Void, Void> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() {
		MainFrame mainFrame = UI.getInstance().getMainFrame();
		ProtocolFrame protFrame = UI.getInstance().getProtocolFrame();

		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		boolean showFields = UI.getInstance().getExportPDFProtocolDialog()
				.showFields();
		boolean addExProRef = UI.getInstance().getExportPDFProtocolDialog()
				.addExProRef();
		boolean addExFindRef = UI.getInstance().getExportPDFProtocolDialog()
				.addExFindRef();

		Meeting meet = UI.getInstance().getExportPDFProtocolDialog()
				.getSelectedMeeting();

		FileChooser fileChooser = UI.getInstance().getFileChooser();

		String fileName = null;

		if (UI.getInstance().getExportPDFProtocolDialog().exportRev()) {
			fileName = sdf.format(new Date()) + "_"
					+ Data.getInstance().getLocaleStr("pdfExport.reviewProt");
		} else {
			int year = meet.getProtocol().getDate().get(Calendar.YEAR);
			int month = meet.getProtocol().getDate().get(Calendar.MONTH) + 1;
			int day = meet.getProtocol().getDate().get(Calendar.DAY_OF_MONTH);

			String monthStr = Integer.toString(month);
			String dayStr = Integer.toString(day);

			if (monthStr.length() == 1) {
				monthStr = "0" + monthStr;
			}

			if (dayStr.length() == 1) {
				dayStr = "0" + dayStr;
			}

			fileName = year + "-" + monthStr + "-" + dayStr + "_"
					+ Data.getInstance().getLocaleStr("pdfExport.meetingProt");
		}

		fileChooser.setFile(new File(fileName));

		if (fileChooser.showDialog(UI.getInstance()
				.getExportPDFProtocolDialog(), FileChooser.MODE_SAVE_FILE,
				ResiFileFilter.TYPE_PDF) == FileChooser.SELECTED_APPROVE) {
			UI.getInstance().getExportPDFProtocolDialog()
					.switchToProgressMode();

			String filePath = fileChooser.getFile().getAbsolutePath();
			File expFile = null;

			try {
				if (UI.getInstance().getExportPDFProtocolDialog().exportRev()) {
					expFile = Application.getInstance().getImportExportCtl()
							.exportReviewProtocolPDF(filePath, showFields,
									addExProRef, addExFindRef);
				} else {
					expFile = Application.getInstance().getImportExportCtl()
							.exportMeetingProtocolPDF(filePath, meet,
									showFields, addExProRef, addExFindRef);
				}

				UI.getInstance().getExportPDFProtocolDialog().setVisible(false);
				UI.getInstance().getExportPDFProtocolDialog()
						.switchToEditMode();

				if (protFrame.isVisible()) {
					protFrame.setStatusMessage(Data.getInstance().getLocaleStr(
							"pdfExport.expSuccessful"), false);
				} else {
					mainFrame.setStatusMessage(Data.getInstance().getLocaleStr(
							"pdfExport.expSuccessful"), false);
				}

				try {
					Desktop.getDesktop().open(expFile);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, GUITools
							.getMessagePane(Data.getInstance().getLocaleStr(
									"message.openPdfFailed")), Data
							.getInstance().getLocaleStr("error"),
							JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception exc) {
				UI.getInstance().getExportPDFProtocolDialog()
						.switchToEditMode();

				JOptionPane.showMessageDialog(null, GUITools.getMessagePane(exc
						.getMessage()), Data.getInstance()
						.getLocaleStr("error"), JOptionPane.ERROR_MESSAGE);
			}

		}

		return null;
	}
}
