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

import static org.revager.app.model.Data._;

import java.awt.Color;
import java.io.File;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.revager.app.Application;
import org.revager.app.AttendeeManagement;
import org.revager.app.FindingManagement;
import org.revager.app.ProtocolManagement;
import org.revager.app.model.Data;
import org.revager.app.model.schema.Aspect;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.Finding;
import org.revager.app.model.schema.Meeting;
import org.revager.app.model.schema.Protocol;
import org.revager.tools.PDFTools;

import com.lowagie.text.Anchor;
import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

/**
 * This class implements the basic functionality for exporting protocols.
 */
public abstract class ProtocolPDFExporter extends PDFExporter {

	/**
	 * Reference to finding management.
	 */
	private FindingManagement findMgmt = Application.getInstance()
			.getFindingMgmt();

	/**
	 * Reference to protocol management.
	 */
	private ProtocolManagement protMgmt = Application.getInstance()
			.getProtocolMgmt();

	/**
	 * The attendee management.
	 */
	private AttendeeManagement attMgmt = Application.getInstance()
			.getAttendeeMgmt();

	/**
	 * The date formatter for dates.
	 */
	protected DateFormat sdfDate = SimpleDateFormat
			.getDateInstance(SimpleDateFormat.LONG);

	/**
	 * The date formatter for times.
	 */
	protected DateFormat sdfTime = new SimpleDateFormat(_("HH:mm"));

	/**
	 * The cell background for tables.
	 */
	protected Color cellBackground = new Color(235, 235, 235);

	/**
	 * The background color for the table titles.
	 */
	protected Color bgColorTitle = new Color(100, 100, 100);

	/**
	 * The padding for table cells.
	 */
	protected float padding = PDFTools.cmToPt(0.2f);

	/**
	 * The line leading for phrases.
	 */
	protected float leading = PDFTools.cmToPt(0.45f);

	/**
	 * The vertical border width.
	 */
	protected float verticalBorderWidth = 1.2f;

	/**
	 * The vertical border color.
	 */
	protected Color verticalBorderColor = Color.GRAY;

	/**
	 * Helper method to get the title of the invitation.
	 */
	public static String getReviewTitle() {
		String title = Data.getInstance().getResiData().getReview().getName();

		if (title.trim().equals("")) {
			title = _("Review");
		}

		return title;
	}

	/**
	 * Instantiates a new protocol pdf exporter.
	 * 
	 * @param filePath
	 *            the file path
	 * @param headTitle
	 *            the head title
	 * @param headLogoPath
	 *            the head logo path
	 * @param footText
	 *            the foot text
	 * 
	 * @throws ExportException
	 *             If an error occurs while instantiating the protocol pdf
	 *             exporter
	 */
	protected ProtocolPDFExporter(String filePath, String headTitle,
			String headLogoPath, String footText) throws ExportException {
		super(filePath, headTitle, headLogoPath, footText);
	}

	/**
	 * Write the title page of the meeting protocol.
	 * 
	 * @param meeting
	 *            the meeting
	 * @param attachProdExtRefs
	 *            true, if the external reference of the product should be part
	 *            of the protocol
	 * 
	 * @throws ExportException
	 *             If an error occurs while writing the title page
	 */
	protected void writeTitlePage(Meeting meeting, boolean attachProdExtRefs)
			throws ExportException {
		List<Meeting> meetings = new ArrayList<Meeting>();

		meetings.add(meeting);

		writeTitlePage(meetings, attachProdExtRefs);
	}

	/**
	 * Write the title page of the protocol.
	 * 
	 * @param meetings
	 *            the meetings
	 * @param attachProdExtRefs
	 *            true, if the external reference of the product should be part
	 *            of the protocol
	 * 
	 * @throws ExportException
	 *             If an error occurs while writing the title page
	 */
	protected void writeTitlePage(List<Meeting> meetings,
			boolean attachProdExtRefs) throws ExportException {
		try {
			Font plainFont = new Font(BaseFont.createFont(BaseFont.HELVETICA,
					BaseFont.CP1252, BaseFont.EMBEDDED), 10);

			Font plainFontSmall = new Font(BaseFont.createFont(
					BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED), 8);

			Font boldFont = new Font(
					BaseFont.createFont(BaseFont.HELVETICA_BOLD,
							BaseFont.CP1252, BaseFont.EMBEDDED), 11);

			Font italicFont = new Font(BaseFont.createFont(
					BaseFont.HELVETICA_OBLIQUE, BaseFont.CP1252,
					BaseFont.EMBEDDED), 10);

			Font italicFontSmall = new Font(BaseFont.createFont(
					BaseFont.HELVETICA_OBLIQUE, BaseFont.CP1252,
					BaseFont.EMBEDDED), 8);

			Font boldItalicFont = new Font(BaseFont.createFont(
					BaseFont.HELVETICA_BOLDOBLIQUE, BaseFont.CP1252,
					BaseFont.EMBEDDED), 11);

			Font boldItalicFontSmall = new Font(BaseFont.createFont(
					BaseFont.HELVETICA_BOLDOBLIQUE, BaseFont.CP1252,
					BaseFont.EMBEDDED), 8);

			Font protocolFontTitle = new Font(
					BaseFont.createFont(BaseFont.HELVETICA_BOLD,
							BaseFont.CP1252, BaseFont.EMBEDDED), 20);

			Font reviewFontTitle = new Font(BaseFont.createFont(
					BaseFont.HELVETICA_OBLIQUE, BaseFont.CP1252,
					BaseFont.EMBEDDED), 15);

			Font meetingFontTitle = new Font(BaseFont.createFont(
					BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED), 13);

			/*
			 * Title of the protocol
			 */
			PdfPTable tableTitlePage = new PdfPTable(new float[] { 0.6f, 0.4f });
			tableTitlePage.setWidthPercentage(100);
			tableTitlePage.setSplitRows(false);
			tableTitlePage.getDefaultCell().setBorder(0);
			tableTitlePage.getDefaultCell().setPadding(0);

			String protocolTitle = _("Findings List of the Review");

			if (meetings.size() == 1) {
				protocolTitle = _("Findings List of the Review Meeting");
			}

			PdfPCell cellProtocol = new PdfPCell(new Phrase(protocolTitle,
					protocolFontTitle));
			cellProtocol.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			cellProtocol.setColspan(2);
			cellProtocol.setBorderWidth(0);
			cellProtocol.setPaddingTop(PDFTools.cmToPt(0.4f));
			cellProtocol.setPaddingBottom(PDFTools.cmToPt(0.2f));

			tableTitlePage.addCell(cellProtocol);

			/*
			 * Name of the review
			 */
			PdfPCell cellRevName = new PdfPCell(new Phrase(Application
					.getInstance().getReviewMgmt().getReviewName(),
					reviewFontTitle));
			cellRevName.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			cellRevName.setColspan(2);
			cellRevName.setBorderWidth(0);
			cellRevName.setPaddingBottom(PDFTools.cmToPt(1.6f));

			tableTitlePage.addCell(cellRevName);

			/*
			 * Review meeting date and location
			 */
			if (meetings.size() == 1) {
				sdfDate.setTimeZone(meetings.get(0).getProtocol().getDate()
						.getTimeZone());
				sdfTime.setTimeZone(meetings.get(0).getProtocol().getDate()
						.getTimeZone());

				String meetingDate = sdfDate.format(meetings.get(0)
						.getProtocol().getDate().getTime());

				String meetingTime = sdfTime.format(meetings.get(0)
						.getProtocol().getStart().getTime())
						+ " - "
						+ sdfTime.format(meetings.get(0).getProtocol().getEnd()
								.getTime())
						+ " ["
						+ meetings.get(0).getProtocol().getEnd().getTimeZone()
								.getDisplayName() + "]";

				Phrase phraseMeeting = new Phrase(meetingDate + " ("
						+ meetingTime + ")", meetingFontTitle);

				PdfPCell cellMeeting = new PdfPCell(phraseMeeting);
				cellMeeting.setColspan(2);
				cellMeeting.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				cellMeeting.setPadding(0);
				cellMeeting.setBorderWidth(0);

				tableTitlePage.addCell(cellMeeting);

				String location = meetings.get(0).getProtocol().getLocation();

				if (location.trim().equals("")) {
					location = "--";
				}
				cellMeeting = new PdfPCell(new Phrase(_("Location") + ": "
						+ location, meetingFontTitle));
				cellMeeting.setColspan(2);
				cellMeeting.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				cellMeeting.setPadding(0);
				cellMeeting.setBorderWidth(0);
				cellMeeting.setPaddingTop(PDFTools.cmToPt(0.15f));
				cellMeeting.setPaddingBottom(PDFTools.cmToPt(1.9f));

				tableTitlePage.addCell(cellMeeting);
			}

			/*
			 * Review description and comments
			 */
			PdfPCell cellRevDesc = new PdfPCell(new Phrase(
					_("Review Description:"), boldItalicFont));
			cellRevDesc.setBorderWidth(0);
			cellRevDesc.setPadding(padding);

			tableTitlePage.addCell(cellRevDesc);

			cellRevDesc = new PdfPCell(new Phrase(_("Review Comments"),
					boldFont));
			cellRevDesc.setBorderWidth(0);
			cellRevDesc.setPadding(padding);
			cellRevDesc.setBackgroundColor(cellBackground);

			tableTitlePage.addCell(cellRevDesc);

			String revDesc = Application.getInstance().getReviewMgmt()
					.getReviewDescription();
			if (revDesc.trim().equals("")) {
				revDesc = "--";
			}

			Phrase phrDesc = new Phrase(revDesc, plainFont);
			phrDesc.setLeading(leading);
			cellRevDesc = new PdfPCell();
			cellRevDesc.addElement(phrDesc);
			cellRevDesc.setBorderWidth(0);
			cellRevDesc.setPadding(padding);
			cellRevDesc.setPaddingBottom(padding * 1.8f);

			tableTitlePage.addCell(cellRevDesc);

			String revComm = Application.getInstance().getReviewMgmt()
					.getReviewComments();
			if (revComm.trim().equals("")) {
				revComm = "--";
			}

			Phrase phrComm = new Phrase(revComm, italicFont);
			phrComm.setLeading(leading);
			cellRevDesc = new PdfPCell();
			cellRevDesc.addElement(phrComm);
			cellRevDesc.setBorderWidth(0);
			cellRevDesc.setPadding(padding);
			cellRevDesc.setPaddingBottom(padding * 1.8f);
			cellRevDesc.setBackgroundColor(cellBackground);

			tableTitlePage.addCell(cellRevDesc);

			tableTitlePage
					.addCell(createVerticalStrut(PDFTools.cmToPt(1.0f), 2));

			/*
			 * Product title
			 */
			PdfPTable tableProduct = new PdfPTable(new float[] { 0.07f, 0.93f });
			tableProduct.setWidthPercentage(100);
			tableProduct.setSplitRows(false);
			tableProduct.getDefaultCell().setBorderWidth(0);
			tableProduct.getDefaultCell().setPadding(0);

			PdfPCell cellProdTitle = new PdfPCell(new Phrase(
					_("Reviewed Product:"), boldItalicFont));
			cellProdTitle.setColspan(2);
			cellProdTitle.setPadding(padding);
			cellProdTitle.setBorder(0);

			tableProduct.addCell(cellProdTitle);

			/*
			 * List point used for lists
			 */
			Phrase phraseListPoint = new Phrase("»", boldFont);
			phraseListPoint.setLeading(leading);

			PdfPCell cellListPoint = new PdfPCell();
			cellListPoint.addElement(phraseListPoint);
			cellListPoint.setBorderWidth(0);
			cellListPoint.setPadding(padding);
			cellListPoint.setPaddingLeft(padding * 2);
			cellListPoint.setPaddingBottom(0);

			/*
			 * Write name and version of the reviewed product
			 */
			String prodName = Data.getInstance().getResiData().getReview()
					.getProduct().getName();

			if (prodName.trim().equals("")) {
				prodName = "--";
			}

			Phrase phrName = new Phrase(_("Product Name") + ": " + prodName,
					plainFont);
			phrName.setLeading(leading);

			PdfPCell cellName = new PdfPCell();
			cellName.addElement(phrName);
			cellName.setBorderWidth(0);
			cellName.setPadding(padding);
			cellName.setPaddingBottom(0);

			tableProduct.addCell(cellListPoint);

			tableProduct.addCell(cellName);

			String prodVersion = Data.getInstance().getResiData().getReview()
					.getProduct().getVersion();

			if (prodVersion.trim().equals("")) {
				prodVersion = "--";
			}

			Phrase phrVersion = new Phrase(_("Product Version") + ": "
					+ prodVersion, plainFont);
			phrVersion.setLeading(leading);

			PdfPCell cellVersion = new PdfPCell();
			cellVersion.addElement(phrVersion);
			cellVersion.setBorderWidth(0);
			cellVersion.setPadding(padding);
			cellVersion.setPaddingBottom(0);

			tableProduct.addCell(cellListPoint);

			tableProduct.addCell(cellVersion);

			if (Application.getInstance().getReviewMgmt().getNumberOfProdRefs() > 0) {
				/*
				 * Table of product references
				 */
				PdfPCell cellRefTitle = new PdfPCell(new Phrase(
						_("Product References:"), boldItalicFont));
				cellRefTitle.setBorderWidth(0);
				cellRefTitle.setPadding(padding);
				cellRefTitle.setPaddingTop(padding * 4);
				cellRefTitle.setColspan(2);

				tableProduct.addCell(cellRefTitle);

				/*
				 * Textual references
				 */
				for (String ref : Application.getInstance().getReviewMgmt()
						.getProductReferences()) {
					Phrase phraseRef = new Phrase(ref, plainFont);
					phraseRef.setLeading(leading);

					PdfPCell cellRef = new PdfPCell();
					cellRef.addElement(phraseRef);
					cellRef.setBorderWidth(0);
					cellRef.setPadding(padding);
					cellRef.setPaddingBottom(0);

					tableProduct.addCell(cellListPoint);

					tableProduct.addCell(cellRef);
				}

				/*
				 * External file references
				 */
				for (File ref : Application.getInstance().getReviewMgmt()
						.getExtProdReferences()) {
					Phrase phraseRef = new Phrase();
					phraseRef.add(new Chunk(ref.getName(), plainFont));
					phraseRef.add(new Chunk(" (" + _("File Attachment") + ")",
							italicFont));
					phraseRef.setFont(plainFont);
					phraseRef.setLeading(leading);

					PdfPCell cellRef = new PdfPCell();
					cellRef.addElement(phraseRef);
					cellRef.setBorderWidth(0);
					cellRef.setPadding(padding);
					cellRef.setPaddingBottom(0);

					tableProduct.addCell(cellListPoint);

					if (attachProdExtRefs) {
						cellRef.setCellEvent(new PDFCellEventExtRef(pdfWriter,
								ref));
					}

					tableProduct.addCell(cellRef);
				}
			}

			/*
			 * Add the product table to the base table
			 */
			PdfPCell cellProduct = new PdfPCell(tableProduct);
			cellProduct.setBorder(0);
			cellProduct.setPadding(0);

			tableTitlePage.addCell(cellProduct);

			/*
			 * List the meetings of this review
			 */
			PdfPCell cellInfos = new PdfPCell();
			cellInfos.setBorder(0);
			cellInfos.setPadding(0);

			/*
			 * meeting list title or meeting info title
			 */
			PdfPTable tableInfos = new PdfPTable(new float[] { 0.09f, 0.91f });
			tableInfos.setWidthPercentage(100);
			tableInfos.setSplitRows(false);
			tableInfos.getDefaultCell().setBorderWidth(0);
			tableInfos.getDefaultCell().setPadding(0);

			String title = _("Meeting Information:");

			if (meetings.size() > 1) {
				title = _("Findings Lists of the Review Meetings:");
			}

			PdfPCell cellInfosTitle = new PdfPCell(new Phrase(title, boldFont));
			cellInfosTitle.setColspan(2);
			cellInfosTitle.setPadding(padding);
			cellInfosTitle.setBorder(0);

			tableInfos.addCell(cellInfosTitle);

			if (meetings.size() > 1) {
				/*
				 * list the meetings of this review (for review protocols)
				 */
				for (Meeting m : meetings) {
					int i = 0;

					Protocol protocol = m.getProtocol();

					if (protocol != null) {
						String meetingDate = sdfDate.format(protocol.getDate()
								.getTime());

						String meetingTime = sdfTime.format(protocol.getStart()
								.getTime())
								+ " - "
								+ sdfTime.format(protocol.getEnd().getTime())
								+ " ["
								+ protocol.getEnd().getTimeZone()
										.getDisplayName() + "]";
						;

						String protLoc = protocol.getLocation();

						if (protLoc.trim().equals("")) {
							protLoc = "--";
						}

						Phrase phraseMeet = new Phrase(_("Date") + ": "
								+ meetingDate + "\n" + _("Time") + ": "
								+ meetingTime + "\n" + _("Location") + ": "
								+ protLoc, italicFont);

						Paragraph paraMeet = new Paragraph();
						paraMeet.setLeading(leading);

						Anchor anchor = new Anchor(phraseMeet);
						anchor.setReference("#"
								+ Long.toString(protocol.getDate()
										.getTimeInMillis()
										+ protocol.getStart().getTimeInMillis()));

						paraMeet.add(anchor);

						PdfPCell cellMeet = new PdfPCell();
						cellMeet.addElement(paraMeet);
						cellMeet.setBorderWidth(0);
						cellMeet.setPadding(padding);

						tableInfos.addCell(cellListPoint);

						tableInfos.addCell(cellMeet);
					}

					i++;
				}
			} else {
				/*
				 * list information of the meeting (for meeting protocols)
				 */
				Protocol prot = meetings.get(0).getProtocol();

				/*
				 * meeting duration
				 */
				Duration meetDur = DatatypeFactory.newInstance().newDuration(
						prot.getEnd().getTimeInMillis()
								- prot.getStart().getTimeInMillis());

				Phrase phraseMeetInfo = new Phrase(_("Duration of the meeting")
						+ ":\n" + meetDur.getHours() + " " + _("Hour(s)")
						+ ", " + meetDur.getMinutes() + " " + _("Minute(s)"),
						italicFont);
				phraseMeetInfo.setLeading(leading);

				PdfPCell cellMeetInfo = new PdfPCell();
				cellMeetInfo.addElement(phraseMeetInfo);
				cellMeetInfo.setBorderWidth(0);
				cellMeetInfo.setPadding(padding);

				tableInfos.addCell(cellListPoint);

				tableInfos.addCell(cellMeetInfo);

				/*
				 * meeting number of findings
				 */
				phraseMeetInfo = new Phrase(_("Number of findings") + ": "
						+ prot.getFindings().size(), italicFont);
				phraseMeetInfo.setLeading(leading);

				cellMeetInfo = new PdfPCell();
				cellMeetInfo.addElement(phraseMeetInfo);
				cellMeetInfo.setBorderWidth(0);
				cellMeetInfo.setPadding(padding);

				tableInfos.addCell(cellListPoint);

				tableInfos.addCell(cellMeetInfo);

				/*
				 * meeting number of attendees
				 */
				phraseMeetInfo = new Phrase(_("Number of attendees") + ": "
						+ protMgmt.getAttendees(prot).size(), italicFont);
				phraseMeetInfo.setLeading(leading);

				cellMeetInfo = new PdfPCell();
				cellMeetInfo.addElement(phraseMeetInfo);
				cellMeetInfo.setBorderWidth(0);
				cellMeetInfo.setPadding(padding);

				tableInfos.addCell(cellListPoint);

				tableInfos.addCell(cellMeetInfo);
			}

			cellInfos.addElement(tableInfos);
			cellInfos.setBackgroundColor(cellBackground);
			cellInfos.setPaddingBottom(padding);

			tableTitlePage.addCell(cellInfos);

			/*
			 * Insert vertical strut
			 */
			tableTitlePage
					.addCell(createVerticalStrut(PDFTools.cmToPt(1.0f), 2));

			/*
			 * Write general impression and recommendation
			 */
			PdfPTable tableRevInfo = new PdfPTable(new float[] { 0.5f, 0.5f });
			tableRevInfo.setWidthPercentage(100);
			tableRevInfo.setSplitRows(false);
			tableRevInfo.getDefaultCell().setBorderWidth(0);
			tableRevInfo.getDefaultCell().setPadding(0);

			/*
			 * Insert vertical strut
			 */
			tableRevInfo.addCell(createVerticalStrut(PDFTools.cmToPt(0.5f), 2));

			PdfPCell cellImpr = new PdfPCell(new Phrase(
					_("General impressions of the product:"), boldFont));
			cellImpr.setBorderWidth(0);
			cellImpr.setPadding(padding);
			cellImpr.setBorderColor(verticalBorderColor);
			cellImpr.setBorderWidthLeft(verticalBorderWidth);

			tableRevInfo.addCell(cellImpr);

			PdfPCell cellReco = new PdfPCell(new Phrase(
					_("Final recommendation for the product:"), boldFont));
			cellReco.setBorderWidth(0);
			cellReco.setPadding(padding);
			cellReco.setBorderColor(verticalBorderColor);
			cellReco.setBorderWidthLeft(verticalBorderWidth);

			tableRevInfo.addCell(cellReco);

			String impression = Application.getInstance().getReviewMgmt()
					.getImpression();
			if (impression.trim().equals("")) {
				impression = "--";
			}

			Phrase phrImpr = new Phrase(impression, italicFont);
			phrImpr.setLeading(leading);
			cellImpr = new PdfPCell();
			cellImpr.addElement(phrImpr);
			cellImpr.setBorderWidth(0);
			cellImpr.setPadding(padding);
			cellImpr.setPaddingBottom(padding * 1.8f);
			cellImpr.setBorderColor(verticalBorderColor);
			cellImpr.setBorderWidthLeft(verticalBorderWidth);

			tableRevInfo.addCell(cellImpr);

			String recommendation = Application.getInstance().getReviewMgmt()
					.getRecommendation();
			if (recommendation.trim().equals("")) {
				recommendation = "--";
			}

			Phrase phrReco = new Phrase(recommendation, italicFont);
			phrReco.setLeading(leading);
			cellReco = new PdfPCell();
			cellReco.addElement(phrReco);
			cellReco.setBorderWidth(0);
			cellReco.setPadding(padding);
			cellReco.setPaddingBottom(padding * 1.8f);
			cellReco.setBorderColor(verticalBorderColor);
			cellReco.setBorderWidthLeft(verticalBorderWidth);

			tableRevInfo.addCell(cellReco);

			/*
			 * Add vertical strut
			 */
			tableRevInfo.addCell(createVerticalStrut(PDFTools.cmToPt(0.8f), 2));

			/*
			 * Write possible severities for this review
			 */
			String severities = "";
			String separator = "";
			for (String sev : Application.getInstance().getSeverityMgmt()
					.getSeverities()) {
				severities = severities + separator + sev;
				separator = "; ";
			}

			Phrase phrSeverities = new Phrase();
			phrSeverities
					.add(new Chunk(
							_("The severities of the findings in this review (descending order of importance):"),
							italicFontSmall));
			phrSeverities.add(new Chunk(" " + severities, boldItalicFontSmall));
			phrSeverities.setLeading(leading);
			PdfPCell cellSevs = new PdfPCell();
			cellSevs.setColspan(2);
			cellSevs.addElement(phrSeverities);
			cellSevs.setBorderWidth(0);
			cellSevs.setPadding(padding);

			tableRevInfo.addCell(cellSevs);

			/*
			 * Short review statistics
			 */
			if (meetings.size() > 1) {
				Phrase phrRevStat = new Phrase(
						MessageFormat.format(
								_("This review consists of {0} attendees, {1} findings, {2} meetings and {3} aspects."),
								Application.getInstance().getReviewMgmt()
										.getNumberOfAttendees(), Application
										.getInstance().getReviewMgmt()
										.getNumberOfFindings(), Application
										.getInstance().getReviewMgmt()
										.getNumberOfMeetings(), Application
										.getInstance().getReviewMgmt()
										.getNumberOfAspects()), italicFontSmall);
				phrRevStat.setLeading(leading);
				PdfPCell cellRevStat = new PdfPCell();
				cellRevStat.setColspan(2);
				cellRevStat.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				cellRevStat.addElement(phrRevStat);
				cellRevStat.setBorderWidth(0);
				cellRevStat.setPadding(padding);
				cellRevStat.setPaddingTop(0);

				tableRevInfo.addCell(cellRevStat);
			}

			/*
			 * Write the date of creation
			 */
			String creationDate = sdfDate.format(new Date().getTime());

			Phrase phrCreationDate = new Phrase(
					_("This finding has been created with RevAger on") + " "
							+ creationDate, plainFontSmall);
			phrCreationDate.setLeading(leading);
			PdfPCell cellCrDate = new PdfPCell();
			cellCrDate.setColspan(2);
			cellCrDate.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			cellCrDate.addElement(phrCreationDate);
			cellCrDate.setBorderWidth(0);
			cellCrDate.setPadding(0);
			cellCrDate.setPaddingLeft(padding);
			cellCrDate.setPaddingRight(padding);

			tableRevInfo.addCell(cellCrDate);

			/*
			 * Add content to the base table
			 */
			PdfPCell cellRevInfo = new PdfPCell();
			cellRevInfo.setColspan(2);
			cellRevInfo.setBorder(0);
			cellRevInfo.setPadding(0);
			cellRevInfo.addElement(tableRevInfo);

			tableTitlePage.addCell(cellRevInfo);

			pdfDoc.add(tableTitlePage);
		} catch (Exception e) {
			/*
			 * Not part of unit testing because this exception is only thrown if
			 * an internal error occurs.
			 */
			throw new ExportException(
					_("Cannot generate front page of the PDF document."));
		}
	}

	/**
	 * Writes the given meeting to the protocol.
	 * 
	 * @param meeting
	 *            the meeting
	 * @param attachExtRefs
	 *            true if the external references should be part of the protocol
	 * @param showSignatureFields
	 *            true, if the signature fields should be part of the protocol
	 * 
	 * @throws ExportException
	 *             If an error occurs while writing the meeting
	 */
	protected void writeMeeting(Meeting meeting, boolean attachExtRefs,
			boolean showSignatureFields) throws ExportException {
		Protocol protocol = meeting.getProtocol();

		if (protocol != null) {
			try {
				Font plainFont = new Font(
						BaseFont.createFont(BaseFont.HELVETICA,
								BaseFont.CP1252, BaseFont.EMBEDDED), 10);

				Font boldFont = new Font(BaseFont.createFont(
						BaseFont.HELVETICA_BOLD, BaseFont.CP1252,
						BaseFont.EMBEDDED), 10);

				Font italicFont = new Font(BaseFont.createFont(
						BaseFont.HELVETICA_OBLIQUE, BaseFont.CP1252,
						BaseFont.EMBEDDED), 10);

				Font boldItalicFont = new Font(BaseFont.createFont(
						BaseFont.HELVETICA_BOLDOBLIQUE, BaseFont.CP1252,
						BaseFont.EMBEDDED), 10);

				Font boldFontTitle = new Font(BaseFont.createFont(
						BaseFont.HELVETICA_BOLD, BaseFont.CP1252,
						BaseFont.EMBEDDED), 17);

				Font italicFontTitle = new Font(BaseFont.createFont(
						BaseFont.HELVETICA_OBLIQUE, BaseFont.CP1252,
						BaseFont.EMBEDDED), 13);

				/*
				 * Base table for the meeting properties
				 */
				PdfPTable tableMeeting = new PdfPTable(2);
				tableMeeting.setWidthPercentage(100);
				tableMeeting.setSplitRows(false);
				tableMeeting.getDefaultCell().setBorderWidth(0);
				tableMeeting.getDefaultCell().setPadding(0);

				/*
				 * Write date, time and location of the meeting
				 */
				String meetingDate = sdfDate.format(protocol.getDate()
						.getTime());

				String meetingTime = sdfTime.format(protocol.getStart()
						.getTime())
						+ " - "
						+ sdfTime.format(protocol.getEnd().getTime())
						+ " ["
						+ protocol.getEnd().getTimeZone().getDisplayName()
						+ "]";

				Anchor anchorTitle = new Anchor(_("Review Meeting on") + " "
						+ meetingDate, boldFontTitle);
				anchorTitle.setName(Long.toString(protocol.getDate()
						.getTimeInMillis()
						+ protocol.getStart().getTimeInMillis()));

				PdfPCell cellTitle = new PdfPCell(anchorTitle);
				cellTitle.setColspan(2);
				cellTitle.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				cellTitle.setPadding(0);
				cellTitle.setBorderWidth(0);
				cellTitle.setPaddingTop(PDFTools.cmToPt(0.6f));
				cellTitle.setPaddingBottom(padding * 2);

				tableMeeting.addCell(cellTitle);

				PdfPCell cellTime = new PdfPCell(new Phrase(meetingTime,
						italicFontTitle));
				cellTime.setColspan(2);
				cellTime.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				cellTime.setPadding(0);
				cellTime.setBorderWidth(0);
				cellTime.setPaddingBottom(padding * 2);

				tableMeeting.addCell(cellTime);

				String location = protocol.getLocation();

				if (location.trim().equals("")) {
					location = "--";
				}

				PdfPCell cellLocation = new PdfPCell(new Phrase(_("Location")
						+ ": " + location, italicFontTitle));
				cellLocation.setColspan(2);
				cellLocation.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				cellLocation.setPadding(0);
				cellLocation.setBorderWidth(0);
				cellLocation.setPaddingBottom(PDFTools.cmToPt(1.5f));

				tableMeeting.addCell(cellLocation);

				/*
				 * Compare with planned meeting
				 */
				PdfPCell cellPlanned;

				boolean plannedDateEqualsProtocolDate = meeting
						.getPlannedDate().get(Calendar.DAY_OF_MONTH) == protocol
						.getDate().get(Calendar.DAY_OF_MONTH)
						&& meeting.getPlannedDate().get(Calendar.MONTH) == protocol
								.getDate().get(Calendar.MONTH)
						&& meeting.getPlannedDate().get(Calendar.YEAR) == protocol
								.getDate().get(Calendar.YEAR);
				boolean plannedStartEqualsProtocolStart = meeting
						.getPlannedStart().get(Calendar.HOUR) == protocol
						.getStart().get(Calendar.HOUR)
						&& meeting.getPlannedStart().get(Calendar.MINUTE) == protocol
								.getStart().get(Calendar.MINUTE)
						&& meeting.getPlannedStart().get(Calendar.AM_PM) == protocol
								.getStart().get(Calendar.AM_PM);
				boolean plannedEndEqualsProtocolEnd = meeting.getPlannedEnd()
						.get(Calendar.HOUR) == protocol.getEnd().get(
						Calendar.HOUR)
						&& meeting.getPlannedEnd().get(Calendar.MINUTE) == protocol
								.getEnd().get(Calendar.MINUTE)
						&& meeting.getPlannedEnd().get(Calendar.AM_PM) == protocol
								.getEnd().get(Calendar.AM_PM);
				boolean plannedLocationEqualsProtocolLocation = meeting
						.getPlannedLocation().equals(protocol.getLocation());

				if (plannedDateEqualsProtocolDate
						&& plannedStartEqualsProtocolStart
						&& plannedEndEqualsProtocolEnd
						&& plannedLocationEqualsProtocolLocation) {
					cellPlanned = new PdfPCell(
							new Phrase(
									_("The meeting took place as it has been planned."),
									plainFont));
				} else {
					cellPlanned = new PdfPCell();

					cellPlanned
							.addElement(new Phrase(
									_("The meeting didn't take place as it has been planned. The meeting was planned:"),
									plainFont));

					/*
					 * Planned date, time and location
					 */
					String plannedDate = sdfDate.format(meeting
							.getPlannedDate().getTime());

					String plannedTime = sdfTime.format(meeting
							.getPlannedStart().getTime())
							+ " - "
							+ sdfTime.format(meeting.getPlannedEnd().getTime())
							+ " ["
							+ meeting.getPlannedEnd().getTimeZone()
									.getDisplayName() + "]";

					Phrase phrasePlanned = new Phrase(plannedDate + " ("
							+ plannedTime + "); " + _("Location") + ": "
							+ meeting.getPlannedLocation(), italicFont);

					cellPlanned.addElement(phrasePlanned);
				}

				cellPlanned.setColspan(2);
				cellPlanned.setBorderWidth(0);
				cellPlanned.setPadding(padding);
				cellPlanned.setPaddingBottom(PDFTools.cmToPt(1.5f));

				tableMeeting.addCell(cellPlanned);

				/*
				 * Comments of the meeting and protocol
				 */
				Phrase phraseComments = new Phrase(
						_("Comments on the Meeting:"), boldFont);
				PdfPCell cellComments = new PdfPCell(phraseComments);
				cellComments.setBorderWidth(0);
				cellComments.setPadding(padding);
				cellComments.setBorderColor(verticalBorderColor);
				cellComments.setBorderWidthLeft(verticalBorderWidth);

				tableMeeting.addCell(cellComments);

				phraseComments = new Phrase(
						_("Comments on the Findings List:"), boldFont);
				cellComments = new PdfPCell(phraseComments);
				cellComments.setBorderWidth(0);
				cellComments.setPadding(padding);
				cellComments.setBorderColor(verticalBorderColor);
				cellComments.setBorderWidthLeft(verticalBorderWidth);

				tableMeeting.addCell(cellComments);

				String meetingComments = meeting.getComments();
				if (meetingComments.trim().equals("")) {
					meetingComments = "--";
				}

				phraseComments = new Phrase(meetingComments, italicFont);
				phraseComments.setLeading(leading);
				cellComments = new PdfPCell();
				cellComments.addElement(phraseComments);
				cellComments.setBorderWidth(0);
				cellComments.setPadding(padding);
				cellComments.setPaddingBottom(padding * 1.8f);
				cellComments.setBorderColor(verticalBorderColor);
				cellComments.setBorderWidthLeft(verticalBorderWidth);

				tableMeeting.addCell(cellComments);

				String protocolComments = protocol.getComments();
				if (protocolComments.trim().equals("")) {
					protocolComments = "--";
				}

				phraseComments = new Phrase(protocolComments, italicFont);
				phraseComments.setLeading(leading);
				cellComments = new PdfPCell();
				cellComments.addElement(phraseComments);
				cellComments.setBorderWidth(0);
				cellComments.setPadding(padding);
				cellComments.setPaddingBottom(padding * 1.8f);
				cellComments.setBorderColor(verticalBorderColor);
				cellComments.setBorderWidthLeft(verticalBorderWidth);

				tableMeeting.addCell(cellComments);

				/*
				 * Strut cell
				 */
				tableMeeting.addCell(createVerticalStrut(PDFTools.cmToPt(1.3f),
						2));

				/*
				 * Write attendees
				 */
				if (protMgmt.getAttendees(protocol).size() > 0) {
					PdfPCell cellAtt = new PdfPCell(new Phrase(
							_("The following attendees participated") + " ("
									+ protMgmt.getAttendees(protocol).size()
									+ " " + _("attendees") + "):",
							boldItalicFont));
					cellAtt.setColspan(2);
					cellAtt.setPadding(0);
					cellAtt.setBorderWidth(0);
					cellAtt.setPadding(padding);
					cellAtt.setPaddingBottom(PDFTools.cmToPt(0.8f));

					tableMeeting.addCell(cellAtt);
				}

				pdfDoc.add(tableMeeting);

				if (protMgmt.getAttendees(protocol).size() > 0) {
					writeAttendees(protocol, true, true, showSignatureFields);
				}

				/*
				 * If there isn't any finding, finish the export here.
				 */
				if (findMgmt.getNumberOfFindings(protocol) == 0) {
					return;
				} else if (findMgmt.getNumberOfFindings(protocol) == 1) {
					Finding find = findMgmt.getFindings(protocol).get(0);

					if (find.getDescription().trim().equals("")
							&& find.getExternalReferences().size() == 0
							&& find.getReferences().size() == 0
							&& find.getAspects().size() == 0) {
						return;
					}
				}

				/*
				 * Write findings
				 */
				pdfDoc.newPage();

				PdfPTable tableFindIntro = new PdfPTable(1);
				tableFindIntro.setWidthPercentage(100);

				Phrase phraseFindIntro = new Phrase(
						_("The following findings were recorded by the participating reviewers")
								+ " ("
								+ findMgmt.getNumberOfFindings(protocol)
								+ " " + _("findings") + "): ", boldItalicFont);
				phraseFindIntro.setLeading(leading);

				PdfPCell cellFindIntro = new PdfPCell();
				cellFindIntro.addElement(phraseFindIntro);
				cellFindIntro.setBorderWidth(0);
				cellFindIntro.setPadding(0);
				cellFindIntro.setPaddingBottom(PDFTools.cmToPt(0.1f));

				tableFindIntro.addCell(cellFindIntro);

				pdfDoc.add(tableFindIntro);

				writeFindings(protocol, attachExtRefs);
			} catch (Exception e) {
				/*
				 * Not part of unit testing because this exception is only
				 * thrown if an internal error occurs.
				 */
				throw new ExportException(
						_("Cannot put the selected review meeting in the PDF document."));
			}
		}
	}

	/**
	 * Writes the given attendees to the protocol.
	 * 
	 * @param protocol
	 *            the protocol
	 * @param showAttendeesAspects
	 *            true, if the aspects of the reviewers should be part of the
	 *            protocol
	 * @param showAttendeesPrepTime
	 *            true, if the preparation time of the reviewers should be part
	 *            of the protocol
	 * @param showSignatureFields
	 *            ture, if the signature fields should be part of the protocol
	 * 
	 * @throws ExportException
	 *             If an error occurs while writing the attendees to the
	 *             protocol
	 */
	protected void writeAttendees(Protocol protocol,
			boolean showAttendeesAspects, boolean showAttendeesPrepTime,
			boolean showSignatureFields) throws ExportException {
		List<Attendee> atts;

		if (protocol != null) {
			atts = protMgmt.getAttendees(protocol);
		} else {
			atts = Application.getInstance().getAttendeeMgmt().getAttendees();
		}

		/*
		 * Sort the attendees by their role into different lists
		 */
		List<Attendee> reviewers = new ArrayList<Attendee>();
		List<Attendee> moderators = new ArrayList<Attendee>();
		List<Attendee> scribes = new ArrayList<Attendee>();
		List<Attendee> authors = new ArrayList<Attendee>();
		List<Attendee> customers = new ArrayList<Attendee>();
		List<Attendee> others = new ArrayList<Attendee>();

		for (Attendee att : atts) {
			switch (att.getRole()) {
			case AUTHOR:
				authors.add(att);
				break;
			case CUSTOMER:
				customers.add(att);
				break;
			case MODERATOR:
				moderators.add(att);
				break;
			case REVIEWER:
				reviewers.add(att);
				break;
			case SCRIBE:
				scribes.add(att);
				break;
			default:
				others.add(att);
				break;
			}
		}

		List<List<Attendee>> attendees = new ArrayList<List<Attendee>>();
		attendees.add(moderators);
		attendees.add(scribes);
		attendees.add(authors);
		attendees.add(customers);
		attendees.add(reviewers);
		attendees.add(others);

		/*
		 * Write attendees
		 */
		try {
			Font contactFont = new Font(BaseFont.createFont(BaseFont.HELVETICA,
					BaseFont.CP1252, BaseFont.EMBEDDED), 10);

			Font nameFont = new Font(
					BaseFont.createFont(BaseFont.HELVETICA_BOLD,
							BaseFont.CP1252, BaseFont.EMBEDDED), 10);

			Font roleFont = new Font(BaseFont.createFont(
					BaseFont.HELVETICA_OBLIQUE, BaseFont.CP1252,
					BaseFont.EMBEDDED), 10);

			Font aspectsFont = new Font(BaseFont.createFont(
					BaseFont.HELVETICA_OBLIQUE, BaseFont.CP1252,
					BaseFont.EMBEDDED), 8);

			Font aspectsTitleFont = new Font(BaseFont.createFont(
					BaseFont.HELVETICA_BOLDOBLIQUE, BaseFont.CP1252,
					BaseFont.EMBEDDED), 8);

			/*
			 * Build base table for all attendees
			 */
			PdfPTable tableAttendees = new PdfPTable(1);
			tableAttendees.setWidthPercentage(100);
			tableAttendees.setSplitRows(false);
			tableAttendees.getDefaultCell().setBorderWidth(0);
			tableAttendees.getDefaultCell().setPadding(0);

			boolean grayBackground = true;

			for (List<Attendee> attList : attendees) {
				for (Attendee att : attList) {
					/*
					 * Build table for one attendee
					 */
					PdfPTable tableAttendee = new PdfPTable(new float[] {
							0.80f, 0.20f });
					tableAttendee.setWidthPercentage(100);
					tableAttendee.getDefaultCell().setBorderWidth(0);
					tableAttendee.getDefaultCell().setPadding(0);

					PdfPCell cellAttendee = new PdfPCell();
					cellAttendee.setPadding(0);
					cellAttendee.setBorder(0);

					/*
					 * Name of the attendee
					 */
					PdfPCell cell = new PdfPCell();
					cell.setBorderWidth(0);
					cell.setPadding(padding * 0.4f);
					cell.setPaddingBottom(padding * 1.5f);
					cell.addElement(new Phrase(att.getName(), nameFont));
					cell.addElement(new Phrase(att.getContact(), contactFont));

					Phrase phraseStrut = new Phrase(" ");
					phraseStrut.setLeading(leading * 0.6f);

					/*
					 * Aspects of this attendee
					 */
					if (!attMgmt.getAspects(att).isEmpty()
							&& showAttendeesAspects) {
						String separator = "";

						cell.addElement(phraseStrut);

						cell.addElement(new Phrase(
								_("Assigned aspects:") + " ", aspectsTitleFont));

						Phrase phraseAspects = new Phrase();
						phraseAspects.setLeading(leading);
						phraseAspects.setFont(aspectsFont);

						for (Aspect asp : attMgmt.getAspects(att)) {
							phraseAspects.add(new Chunk(separator
									+ asp.getDirective() + " ("
									+ asp.getCategory() + ")", aspectsFont));

							separator = "  ·  ";
						}

						cell.addElement(phraseAspects);
					}

					/*
					 * Preparation time of the attendee
					 */
					Duration prepTime;
					if (protocol != null) {
						prepTime = protMgmt.getAttendeePrepTime(att, protocol);
					} else {
						prepTime = null;
					}

					if (prepTime != null && showAttendeesPrepTime) {
						cell.addElement(phraseStrut);

						cell.addElement(new Phrase(
								_("Preparation time:") + " ", aspectsTitleFont));

						Phrase phrasePrepTime = new Phrase();
						phrasePrepTime.setLeading(leading);
						phrasePrepTime.setFont(aspectsFont);

						String prep = "";
						String separator = "";

						if (prepTime.getDays() > 0) {
							prep = prep + prepTime.getDays() + " "
									+ _("Day(s)");

							separator = ", ";
						}

						if (prepTime.getHours() > 0) {
							prep = prep + separator + prepTime.getHours() + " "
									+ _("Hour(s)");

							separator = ", ";
						}

						if (prepTime.getMinutes() >= 0) {
							prep = prep + separator + prepTime.getMinutes()
									+ " " + _("Minute(s)");

							separator = ", ";
						}

						phrasePrepTime.add(new Chunk(prep, aspectsFont));

						cell.addElement(phrasePrepTime);
					}

					/*
					 * Signature field for the attendee
					 */
					if (showSignatureFields) {
						cell.addElement(phraseStrut);
						cell.addElement(phraseStrut);
						cell.addElement(phraseStrut);
						cell.addElement(phraseStrut);

						cell.addElement(new Phrase(
								"________________________________________",
								aspectsFont));

						cell.addElement(new Phrase(_("Date, Signature") + " ("
								+ att.getName() + ")", aspectsFont));
					}

					tableAttendee.addCell(cell);

					/*
					 * role of the attendee
					 */
					cell = new PdfPCell(new Phrase(_(att.getRole().toString()),
							roleFont));
					cell.setBorderWidth(0);
					cell.setPadding(padding * 0.4f);
					cell.setPaddingTop(padding * 1.1f);
					cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

					tableAttendee.addCell(cell);

					cellAttendee.addElement(tableAttendee);
					cellAttendee.setPadding(0);
					cellAttendee.setPaddingLeft(padding);
					cellAttendee.setPaddingRight(padding);

					if (grayBackground == true) {
						grayBackground = false;
						cellAttendee.setBackgroundColor(cellBackground);
					} else {
						grayBackground = true;
					}

					/*
					 * Add attendee to the list
					 */
					tableAttendees.addCell(cellAttendee);
				}
			}

			PdfPCell cellBottomLine = new PdfPCell();
			cellBottomLine.setPadding(0);
			cellBottomLine.setBorderWidth(0);
			cellBottomLine.setBorderWidthBottom(1);
			cellBottomLine.setBorderColor(cellBackground);

			tableAttendees.addCell(cellBottomLine);

			/*
			 * Add the attendee base table to the document
			 */
			pdfDoc.add(tableAttendees);
		} catch (Exception e) {
			/*
			 * Not part of unit testing because this exception is only thrown if
			 * an internal error occurs.
			 */
			throw new ExportException(
					_("Cannot put attendees into the PDF document."));
		}
	}

	/**
	 * Write the findings to the protocol.
	 * 
	 * @param protocol
	 *            the protocol
	 * @param attachExtRefs
	 *            true if the external references should be part of the protocol
	 * 
	 * @throws ExportException
	 *             If an error occurs while writing the findings to the protocol
	 */
	protected void writeFindings(Protocol protocol, boolean attachExtRefs)
			throws ExportException {
		try {
			/*
			 * Define fonts
			 */
			Font plainFontTitle = new Font(BaseFont.createFont(
					BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED), 9,
					Font.NORMAL, Color.WHITE);

			Font boldFontTitle = new Font(
					BaseFont.createFont(BaseFont.HELVETICA_BOLD,
							BaseFont.CP1252, BaseFont.EMBEDDED), 10,
					Font.NORMAL, Color.WHITE);

			Font plainFont = new Font(BaseFont.createFont(BaseFont.HELVETICA,
					BaseFont.CP1252, BaseFont.EMBEDDED), 10);

			Font boldFont = new Font(
					BaseFont.createFont(BaseFont.HELVETICA_BOLD,
							BaseFont.CP1252, BaseFont.EMBEDDED), 10);

			Font italicFont = new Font(BaseFont.createFont(
					BaseFont.HELVETICA_OBLIQUE, BaseFont.CP1252,
					BaseFont.EMBEDDED), 10);

			Font boldItalicFont = new Font(BaseFont.createFont(
					BaseFont.HELVETICA_BOLDOBLIQUE, BaseFont.CP1252,
					BaseFont.EMBEDDED), 10);

			/*
			 * Write findings
			 */
			PdfPTable tableBase = new PdfPTable(1);
			tableBase.setWidthPercentage(100);
			tableBase.setSplitRows(false);
			tableBase.getDefaultCell().setBorderWidth(0);
			tableBase.getDefaultCell().setPadding(0);

			for (Finding f : protocol.getFindings()) {
				tableBase
						.addCell(createVerticalStrut(PDFTools.cmToPt(0.7f), 1));
				PdfPCell cellFinding = new PdfPCell();
				cellFinding.setBorderColor(Color.GRAY);
				cellFinding.setBorderWidth(0.5f);

				PdfPTable tableTitle = new PdfPTable(3);
				tableTitle.setWidthPercentage(100);

				/*
				 * Print title of the finding
				 */
				Phrase phraseTitle = new Phrase(_("Finding") + " " + f.getId(),
						boldFontTitle);

				PdfPCell cellTitle = new PdfPCell(phraseTitle);
				cellTitle.setBackgroundColor(bgColorTitle);
				cellTitle.setBorderWidth(0);
				cellTitle.setPadding(padding);
				cellTitle.setPaddingBottom(padding * 1.5f);
				cellTitle.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);

				tableTitle.addCell(cellTitle);

				/*
				 * Print severity of the finding
				 */
				Phrase phraseSeverity = new Phrase(
						findMgmt.getLocalizedSeverity(f), plainFontTitle);

				PdfPCell cellSeverity = new PdfPCell(phraseSeverity);
				cellSeverity.setBackgroundColor(bgColorTitle);
				cellSeverity.setBorderWidth(0);
				cellSeverity.setPadding(padding);
				cellSeverity.setPaddingTop(padding * 1.1f);
				cellSeverity.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);

				tableTitle.addCell(cellSeverity);

				/*
				 * Print the meeting date and time of the finding
				 */
				String meetingDate = sdfDate.format(protocol.getDate()
						.getTime());

				PdfPCell cellMeeting = new PdfPCell(new Phrase(meetingDate,
						plainFontTitle));
				cellMeeting.setBackgroundColor(bgColorTitle);
				cellMeeting.setBorderWidth(0);
				cellMeeting.setPadding(padding);
				cellMeeting.setPaddingTop(padding * 1.1f);
				cellMeeting.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

				tableTitle.addCell(cellMeeting);

				/*
				 * Description
				 */
				Phrase phraseDesc = new Phrase(f.getDescription(), plainFont);
				phraseDesc.setLeading(leading);

				PdfPCell cellDesc = new PdfPCell();
				cellDesc.addElement(phraseDesc);
				cellDesc.setBorderWidth(0);
				cellDesc.setPadding(padding);
				cellDesc.setColspan(3);

				tableTitle.addCell(cellDesc);

				cellFinding.addElement(tableTitle);

				/*
				 * List point used for lists
				 */
				Phrase phraseListPoint = new Phrase("»", boldFont);
				phraseListPoint.setLeading(leading * 0.93f);

				PdfPCell cellListPoint = new PdfPCell();
				cellListPoint.addElement(phraseListPoint);
				cellListPoint.setBorderWidth(0);
				cellListPoint.setPadding(padding);
				cellListPoint.setPaddingLeft(padding * 2);

				/*
				 * Table of references
				 */
				if (f.getReferences().size() > 0
						|| (f.getExternalReferences().size() > 0 && attachExtRefs == true)) {
					PdfPTable tableRefs = new PdfPTable(new float[] { 0.04f,
							0.96f });
					tableRefs.setWidthPercentage(100);

					PdfPCell cellRefTitle = new PdfPCell(new Phrase(
							_("References:"), boldItalicFont));
					cellRefTitle.setBorderWidth(0);
					cellRefTitle.setPadding(padding);
					cellRefTitle.setPaddingTop(padding * 3);
					cellRefTitle.setPaddingBottom(0);
					cellRefTitle.setColspan(2);

					tableRefs.addCell(cellRefTitle);

					/*
					 * Textual references
					 */
					for (String ref : f.getReferences()) {
						Phrase phraseRef = new Phrase(ref, plainFont);
						phraseRef.setLeading(leading);

						PdfPCell cellRef = new PdfPCell();
						cellRef.addElement(phraseRef);
						cellRef.setBorderWidth(0);
						cellRef.setPadding(padding);

						tableRefs.addCell(cellListPoint);

						tableRefs.addCell(cellRef);
					}

					/*
					 * External file references
					 */
					if (attachExtRefs == true) {
						for (File ref : findMgmt.getExtReferences(f)) {
							Phrase phraseRef = new Phrase();
							phraseRef.add(new Chunk(ref.getName(), plainFont));
							phraseRef.add(new Chunk(" (" + _("File Attachment")
									+ ")", italicFont));
							phraseRef.setFont(plainFont);
							phraseRef.setLeading(leading);

							PdfPCell cellRef = new PdfPCell();
							cellRef.addElement(phraseRef);
							cellRef.setBorderWidth(0);
							cellRef.setPadding(padding);

							tableRefs.addCell(cellListPoint);

							cellRef.setCellEvent(new PDFCellEventExtRef(
									pdfWriter, ref));

							tableRefs.addCell(cellRef);
						}
					}

					cellFinding.addElement(tableRefs);
				}

				/*
				 * Table of aspects
				 */
				if (f.getAspects().size() > 0) {
					PdfPTable tableAspects = new PdfPTable(new float[] { 0.04f,
							0.96f });
					tableAspects.setWidthPercentage(100);

					PdfPCell cellAspTitle = new PdfPCell(new Phrase(
							_("Aspects:"), boldItalicFont));
					cellAspTitle.setBorderWidth(0);
					cellAspTitle.setPadding(padding);
					cellAspTitle.setPaddingTop(padding * 3);
					cellAspTitle.setPaddingBottom(0);
					cellAspTitle.setColspan(2);

					tableAspects.addCell(cellAspTitle);

					for (String asp : f.getAspects()) {
						Phrase phraseAsp = new Phrase(asp, plainFont);
						phraseAsp.setLeading(leading);

						PdfPCell cellAsp = new PdfPCell();
						cellAsp.addElement(phraseAsp);
						cellAsp.setBorderWidth(0);
						cellAsp.setPadding(padding);

						tableAspects.addCell(cellListPoint);

						tableAspects.addCell(cellAsp);
					}

					cellFinding.addElement(tableAspects);
				}

				/*
				 * Vertical strut at the end of the table
				 */
				PdfPTable tableStrut = new PdfPTable(1);
				tableStrut.setWidthPercentage(100);
				tableStrut.addCell(createVerticalStrut(padding, 1));

				cellFinding.addElement(tableStrut);

				tableBase.addCell(cellFinding);
			}

			pdfDoc.add(tableBase);
		} catch (Exception e) {
			/*
			 * Not part of unit testing because this exception is only thrown if
			 * an internal error occurs.
			 */
			throw new ExportException(
					_("Cannot put findings into the PDF document."));
		}
	}
}
