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
package org.revager.export;

import static org.revager.app.model.Data.translate;

import org.revager.app.Application;
import org.revager.app.model.ApplicationData;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.appdata.AppSettingKey;
import org.revager.app.model.schema.Meeting;
import org.revager.app.model.schema.Protocol;
import org.revager.tools.PDFTools;

import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

/**
 * This class implements the functionality to export whole review protocols.
 */
public class ReviewProtocolPDFExporter extends ProtocolPDFExporter {

	/**
	 * Reference to application data.
	 */
	private static ApplicationData appData = Data.getInstance().getAppData();

	/**
	 * The title of the review.
	 */
	private static String reviewTitle = ProtocolPDFExporter.getReviewTitle();

	/**
	 * True, if the signature fields should be part of the protocol.
	 */
	private boolean showSignFields = false;

	/**
	 * True, if the external product references should be part of the protocol.
	 */
	private boolean attachProdExtRefs = true;

	/**
	 * True, if the external references of the findings should be part of the
	 * protocol.
	 */
	private boolean attachFindExtRefs = true;

	/**
	 * Instantiates a new review protocol PDF exporter.
	 * 
	 * @param filePath
	 *            the file path
	 * @param showSignFields
	 *            true, if the signature fields should be part of the protocol
	 * @param attachProdExtRefs
	 *            true, if the external product references should be part of the
	 *            protocol
	 * @param attachFindExtRefs
	 *            true, if the external references of the findings should be
	 *            part of the protocol
	 * 
	 * @throws ExportException
	 *             If an error occurs while instantiating the exporter
	 * @throws DataException
	 *             If an error occurs while getting the data for the export
	 *             process
	 */
	public ReviewProtocolPDFExporter(String filePath, boolean showSignFields, boolean attachProdExtRefs,
			boolean attachFindExtRefs) throws ExportException, DataException {
		super(filePath, translate("List of Findings") + " · " + reviewTitle,
				appData.getSetting(AppSettingKey.PDF_PROTOCOL_LOGO),
				appData.getSetting(AppSettingKey.PDF_PROTOCOL_FOOT_TEXT));

		this.showSignFields = showSignFields;
		this.attachProdExtRefs = attachProdExtRefs;
		this.attachFindExtRefs = attachFindExtRefs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.revager.export.PDFExporter#writeContent()
	 */
	@Override
	protected void writeContent() throws ExportException {
		try {
			/*
			 * Write the title page of the protocol
			 */
			writeTitlePage(Application.getInstance().getMeetingMgmt().getMeetings(), attachProdExtRefs);

			/*
			 * Write attendees of the whole review
			 */
			int numOfAtts = Application.getInstance().getAttendeeMgmt().getNumberOfAttendees();

			if (showSignFields == true && numOfAtts > 0) {
				Font introFont = new Font(
						BaseFont.createFont(BaseFont.HELVETICA_BOLDOBLIQUE, BaseFont.CP1252, BaseFont.EMBEDDED), 10);

				pdfDoc.newPage();

				PdfPTable table = new PdfPTable(1);
				table.setWidthPercentage(100);

				PdfPCell cellSignIntro = new PdfPCell(
						new Phrase(translate("The following persons participated in the whole review:"), introFont));
				cellSignIntro.setBorderWidth(0);
				cellSignIntro.setPadding(padding);
				cellSignIntro.setPaddingBottom(PDFTools.cmToPt(0.8f));

				table.addCell(cellSignIntro);

				pdfDoc.add(table);

				writeAttendees(null, false, false, true);
			}

			/*
			 * Write the meetings of this review
			 */
			for (Meeting m : Application.getInstance().getMeetingMgmt().getMeetings()) {

				Protocol prot = m.getProtocol();

				if (prot != null) {
					pdfDoc.newPage();

					writeMeeting(m, attachFindExtRefs, false);
				}
			}
		} catch (Exception e) {
			/*
			 * Not part of unit testing because this exception is only thrown if
			 * an internal error occurs.
			 */
			throw new ExportException(translate("Cannot create PDF document."));
		}
	}

}
