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
import java.util.Date;
import java.util.List;

import org.revager.app.Application;
import org.revager.app.AspectManagement;
import org.revager.app.AttendeeManagement;
import org.revager.app.MeetingManagement;
import org.revager.app.model.ApplicationData;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.ResiData;
import org.revager.app.model.appdata.AppSettingKey;
import org.revager.app.model.schema.Aspect;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.Meeting;
import org.revager.app.model.schema.Role;
import org.revager.tools.PDFTools;

import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

/**
 * This class implements the functionality to export review meeting invitations
 * as PDF files.
 */
public class InvitationPDFExporter extends PDFExporter {

	/**
	 * Reference to application data.
	 */
	private static ApplicationData appData = Data.getInstance().getAppData();

	/**
	 * Reference to Resi data to access the current review.
	 */
	private static ResiData resiData = Data.getInstance().getResiData();

	/**
	 * Reference to attendee management.
	 */
	private static AttendeeManagement attMgmt = Application.getInstance().getAttendeeMgmt();

	/**
	 * Reference to meeting management.
	 */
	private static MeetingManagement meetMgmt = Application.getInstance().getMeetingMgmt();

	/**
	 * Reference to meeting management.
	 */
	private static AspectManagement aspMgmt = Application.getInstance().getAspectMgmt();

	/**
	 * The padding used for tables.
	 */
	private float padding = PDFTools.cmToPt(0.2f);

	/**
	 * The line leading used for phrases.
	 */
	private float leading = PDFTools.cmToPt(0.45f);

	/**
	 * The cell background for tables.
	 */
	private Color cellBackground = new Color(235, 235, 235);

	/**
	 * The date formatter for dates.
	 */
	private DateFormat sdfDate = SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG);

	/**
	 * The date formatter for times.
	 */
	private DateFormat sdfTime = new SimpleDateFormat(_("HH:mm"));

	/**
	 * The meeting.
	 */
	private Meeting meeting = null;

	/**
	 * The attendee.
	 */
	private Attendee attendee = null;

	/**
	 * True, if the external product references shpuld be part of the
	 * invitation.
	 */
	private boolean attachProdExtRefs = true;

	/**
	 * Helper method to get the title of the invitation.
	 */
	private static String getReviewTitle() {
		String title = resiData.getReview().getName();

		if (title.trim().equals("")) {
			title = _("Review Meeting");
		}

		return title;
	}

	/**
	 * Instantiates a new invitation pdf exporter.
	 * 
	 * @param filePath
	 *            the path to store the invitation file
	 * @param meeting
	 *            the meeting
	 * @param attendee
	 *            the attendee
	 * @param attachProdExtRefs
	 *            true, if the external product references should be part of the
	 *            invitation
	 * 
	 * @throws ExportException
	 *             If an error occurs while exporting the invitation
	 * @throws DataException
	 *             If an error occurs while getting the data to create the
	 *             invitation
	 */
	public InvitationPDFExporter(String filePath, Meeting meeting, Attendee attendee, boolean attachProdExtRefs)
			throws ExportException, DataException {
		super(filePath, _("Invitation") + " · " + getReviewTitle(),
				appData.getSetting(AppSettingKey.PDF_INVITATION_LOGO),
				appData.getSetting(AppSettingKey.PDF_INVITATION_FOOT_TEXT));

		this.meeting = meeting;
		this.attendee = attendee;
		this.attachProdExtRefs = attachProdExtRefs;

		sdfDate.setTimeZone(meeting.getPlannedDate().getTimeZone());
		sdfTime.setTimeZone(meeting.getPlannedDate().getTimeZone());
	}

	/**
	 * Write the title page of the PDF document.
	 * 
	 * @throws ExportException
	 *             If an error occurs while creating the title page
	 */
	private void writeTitlePage() throws ExportException {
		try {
			Font plainFontTitle = new Font(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED),
					12);

			Font boldFontTitle = new Font(
					BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED), 12);

			Font boldItalicFontTitle = new Font(
					BaseFont.createFont(BaseFont.HELVETICA_BOLDOBLIQUE, BaseFont.CP1252, BaseFont.EMBEDDED), 12);

			Font plainFont = new Font(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED), 10);

			Font boldFont = new Font(BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED),
					10);

			Font boldItalicFont = new Font(
					BaseFont.createFont(BaseFont.HELVETICA_BOLDOBLIQUE, BaseFont.CP1252, BaseFont.EMBEDDED), 10);

			Font italicFont = new Font(
					BaseFont.createFont(BaseFont.HELVETICA_OBLIQUE, BaseFont.CP1252, BaseFont.EMBEDDED), 10);

			Font italicFontSmall = new Font(
					BaseFont.createFont(BaseFont.HELVETICA_OBLIQUE, BaseFont.CP1252, BaseFont.EMBEDDED), 9);

			Phrase phraseStrut = new Phrase(" ");
			phraseStrut.setLeading(leading);

			/*
			 * date and time of the meeting
			 */
			String meetingDate = sdfDate.format(meeting.getPlannedDate().getTime());

			String meetingTime = sdfTime.format(meeting.getPlannedStart().getTime()) + " - "
					+ sdfTime.format(meeting.getPlannedEnd().getTime()) + " ["
					+ meeting.getPlannedEnd().getTimeZone().getDisplayName() + "]";
			;

			/*
			 * Base table
			 */
			PdfPTable table = new PdfPTable(new float[] { 0.04f, 0.96f });
			table.setWidthPercentage(100);
			table.setSplitRows(false);
			table.getDefaultCell().setBorderWidth(0);
			table.getDefaultCell().setPadding(0);

			/*
			 * recipient of the invitation
			 */
			PdfPCell cell = new PdfPCell();
			cell.setBorder(0);
			cell.setColspan(2);
			cell.setPadding(padding);

			cell.addElement(phraseStrut);

			PdfPTable tableRecipient = new PdfPTable(2);
			tableRecipient.setWidthPercentage(100);
			tableRecipient.setSplitRows(false);
			tableRecipient.getDefaultCell().setBorderWidth(0);
			tableRecipient.getDefaultCell().setPadding(0);

			PdfPCell cellRecipient = new PdfPCell();
			cellRecipient.setBorder(0);
			cellRecipient.setPadding(0);
			cellRecipient.addElement(new Phrase(_("To:"), plainFontTitle));
			cellRecipient.addElement(new Phrase(attendee.getName(), boldFontTitle));

			tableRecipient.addCell(cellRecipient);

			PdfPCell cellDate = new PdfPCell(new Phrase(sdfDate.format(new Date().getTime()), plainFont));
			cellDate.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cellDate.setBorder(0);
			cellDate.setPadding(0);
			cellDate.setPaddingTop(7);

			tableRecipient.addCell(cellDate);

			cell.addElement(tableRecipient);

			cell.addElement(phraseStrut);
			cell.addElement(phraseStrut);
			cell.addElement(phraseStrut);

			/*
			 * subject
			 */
			cell.addElement(new Phrase(_("Invitation for the Meeting on") + " " + meetingDate, boldItalicFontTitle));

			cell.addElement(phraseStrut);

			/*
			 * invitation text
			 */
			Phrase phrase = new Phrase(Data.getInstance().getAppData().getSetting(AppSettingKey.PDF_INVITATION_TEXT),
					plainFont);
			phrase.setLeading(leading);

			cell.addElement(phrase);

			cell.addElement(phraseStrut);

			table.addCell(cell);

			/*
			 * meeting date, time and location
			 */
			cell = new PdfPCell(new Phrase(meetingDate, boldItalicFont));
			cell.setBorder(0);
			cell.setColspan(2);
			cell.setPadding(padding);
			cell.setPaddingBottom(0);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell(cell);

			cell = new PdfPCell(new Phrase(meetingTime, italicFont));
			cell.setBorder(0);
			cell.setColspan(2);
			cell.setPadding(padding);
			cell.setPaddingBottom(0);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell(cell);

			if (!meeting.getPlannedLocation().equals("")) {
				cell = new PdfPCell(new Phrase(_("Location") + ": " + meeting.getPlannedLocation(), italicFont));
				cell.setBorder(0);
				cell.setColspan(2);
				cell.setPadding(padding);
				cell.setPaddingBottom(0);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);

				table.addCell(cell);
			}

			/*
			 * role; review title and description
			 */
			cell = new PdfPCell();
			cell.setBorder(0);
			cell.setColspan(2);
			cell.setPadding(padding);

			cell.addElement(phraseStrut);
			cell.addElement(phraseStrut);

			phrase = new Phrase();
			phrase.setLeading(leading);
			phrase.add(new Chunk(MessageFormat.format(_("You are invited as {0} to this review ({1})."),
					_(attendee.getRole().toString()), getReviewTitle()), boldFont));

			cell.addElement(phrase);

			cell.addElement(phraseStrut);

			cell.addElement(new Phrase(resiData.getReview().getDescription(), italicFont));

			cell.addElement(phraseStrut);

			/*
			 * Predecessor meeting
			 */
			if (meetMgmt.getPredecessorMeeting(meeting) != null) {
				String preMeetingDate = sdfDate
						.format(meetMgmt.getPredecessorMeeting(meeting).getProtocol().getDate().getTime());

				cell.addElement(new Phrase(
						MessageFormat.format(_("This meeting ties up to the review meeting of {0}."), preMeetingDate),
						plainFont));

				cell.addElement(phraseStrut);
			}

			/*
			 * If there is a product name defined
			 */
			if (!Data.getInstance().getResiData().getReview().getProduct().getName().trim().equals("")) {
				/*
				 * the product of this review
				 */
				cell.addElement(new Phrase(_("The following product will be reviewed:"), plainFont));
				cell.addElement(phraseStrut);

				table.addCell(cell);

				/*
				 * Write name and version of the reviewed product
				 */
				Phrase phrName = new Phrase(Data.getInstance().getResiData().getReview().getProduct().getName(),
						boldItalicFont);
				phrName.setLeading(leading);

				PdfPCell cellName = new PdfPCell(phrName);
				cellName.setColspan(2);
				cellName.setBorderWidth(0);
				cellName.setPadding(padding);
				cellName.setPaddingBottom(0);
				cellName.setHorizontalAlignment(Element.ALIGN_CENTER);

				table.addCell(cellName);

				/*
				 * If there is a product version defined
				 */
				if (!Data.getInstance().getResiData().getReview().getProduct().getVersion().trim().equals("")) {
					Phrase phrVersion = new Phrase(
							_("Product Version") + ": "
									+ Data.getInstance().getResiData().getReview().getProduct().getVersion(),
							italicFont);
					phrVersion.setLeading(leading);

					PdfPCell cellVersion = new PdfPCell(phrVersion);
					cellVersion.setColspan(2);
					cellVersion.setBorderWidth(0);
					cellVersion.setPadding(padding);
					cellVersion.setPaddingBottom(0);
					cellVersion.setHorizontalAlignment(Element.ALIGN_CENTER);

					table.addCell(cellVersion);
				}

				table.addCell(createVerticalStrut(PDFTools.cmToPt(0.7f), 2));
			} else {
				table.addCell(cell);
			}

			boolean showBottomStrut = false;

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
			 * Textual references
			 */
			for (String ref : Application.getInstance().getReviewMgmt().getProductReferences()) {
				Phrase phraseRef = new Phrase(ref, italicFontSmall);
				phraseRef.setLeading(leading);

				PdfPCell cellRef = new PdfPCell();
				cellRef.addElement(phraseRef);
				cellRef.setBorderWidth(0);
				cellRef.setPadding(padding);
				cellRef.setPaddingBottom(0);

				table.addCell(cellListPoint);

				table.addCell(cellRef);

				showBottomStrut = true;
			}

			/*
			 * External file references
			 */
			for (File ref : Application.getInstance().getReviewMgmt().getExtProdReferences()) {
				Phrase phraseRef = new Phrase();
				phraseRef.add(new Chunk(ref.getName(), italicFontSmall));
				phraseRef.add(new Chunk(" (" + _("File Attachment") + ")", italicFontSmall));
				phraseRef.setFont(plainFont);
				phraseRef.setLeading(leading);

				PdfPCell cellRef = new PdfPCell();
				cellRef.addElement(phraseRef);
				cellRef.setBorderWidth(0);
				cellRef.setPadding(padding);
				cellRef.setPaddingBottom(0);

				table.addCell(cellListPoint);

				if (attachProdExtRefs) {
					cellRef.setCellEvent(new PDFCellEventExtRef(pdfWriter, ref));
				}

				table.addCell(cellRef);

				showBottomStrut = true;
			}

			if (showBottomStrut) {
				table.addCell(createVerticalStrut(PDFTools.cmToPt(0.4f), 2));
			}

			/*
			 * "please prepare" for the reviwers; on questions ask moderators
			 */
			cell = new PdfPCell();
			cell.setBorder(0);
			cell.setColspan(2);
			cell.setPadding(padding);

			phrase = new Phrase();
			phrase.setLeading(leading);

			if (attendee.getAspects() != null) {
				phrase.add(new Chunk(
						_("Please prepare for the review meeting by checking the product for the aspects which are associated to you.")
								+ " ",
						plainFont));
			}

			List<Attendee> moderators = new ArrayList<Attendee>();
			for (Attendee a : Application.getInstance().getAttendeeMgmt().getAttendees()) {
				if (a.getRole() == Role.MODERATOR) {
					moderators.add(a);
				}
			}

			if (moderators.size() == 1 && attendee.getRole() != Role.MODERATOR) {
				phrase.add(new Chunk(
						_("Please do not hesitate to contact the review moderator if you have any questions:") + " ",
						plainFont));
			} else if (moderators.size() > 1 && attendee.getRole() != Role.MODERATOR) {
				phrase.add(new Chunk(
						_("Please do not hesitate to contact one of the review moderators if you have any questions:")
								+ " ",
						plainFont));
			}

			cell.addElement(phrase);

			Phrase phraseStrutSmall = new Phrase(" ");
			phraseStrutSmall.setLeading(leading / 2);

			if (moderators.size() > 0 && attendee.getRole() != Role.MODERATOR) {
				for (Attendee mod : moderators) {
					cell.addElement(phraseStrutSmall);
					cell.addElement(new Phrase(mod.getName(), boldFont));
					cell.addElement(new Phrase(mod.getContact(), italicFont));
				}
			}

			table.addCell(cell);

			pdfDoc.add(table);
		} catch (Exception e) {
			/*
			 * Not part of unit testing because this exception is only thrown if
			 * an internal error occurs.
			 */
			throw new ExportException(_("Cannot generate front page of the PDF document."));
		}
	}

	/**
	 * Write the aspects.
	 * 
	 * @throws ExportException
	 *             If an error occurs while writing the aspects
	 */
	private void writeAspects() throws ExportException {
		/*
		 * If the role of the attendee is not reviewer show all aspects
		 */
		List<Aspect> aspects = null;

		if (attendee.getRole() == Role.REVIEWER) {
			aspects = attMgmt.getAspects(attendee);
		} else {
			aspects = aspMgmt.getAspects();
		}

		if (aspects.size() == 0) {
			return;
		}

		try {
			Font descriptionFont = new Font(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED),
					10);

			Font directiveFont = new Font(
					BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED), 10);

			Font categoryFont = new Font(
					BaseFont.createFont(BaseFont.HELVETICA_OBLIQUE, BaseFont.CP1252, BaseFont.EMBEDDED), 10);

			Font reviewerFont = new Font(
					BaseFont.createFont(BaseFont.HELVETICA_OBLIQUE, BaseFont.CP1252, BaseFont.EMBEDDED), 9);

			/*
			 * Build base table for all attendees
			 */
			PdfPTable tableAspects = new PdfPTable(1);
			tableAspects.setWidthPercentage(100);
			tableAspects.setSplitRows(false);
			tableAspects.getDefaultCell().setBorderWidth(0);
			tableAspects.getDefaultCell().setPadding(0);

			boolean grayBackground = true;

			for (Aspect asp : aspects) {
				/*
				 * Build table for one aspect
				 */
				PdfPTable tableAspect = new PdfPTable(new float[] { 0.70f, 0.30f });
				tableAspect.setWidthPercentage(100);
				tableAspect.getDefaultCell().setBorderWidth(0);
				tableAspect.getDefaultCell().setPadding(0);

				PdfPCell cellAspect = new PdfPCell();
				cellAspect.setPadding(0);
				cellAspect.setBorder(0);

				Phrase phraseStrut = new Phrase(" ");
				phraseStrut.setLeading(leading * 0.4f);

				/*
				 * directive and description of the aspect
				 */
				PdfPCell cell = new PdfPCell();
				cell.setBorderWidth(0);
				cell.setPadding(padding * 0.4f);
				cell.setPaddingBottom(padding * 1.5f);
				cell.addElement(new Phrase(asp.getDirective(), directiveFont));
				cell.addElement(phraseStrut);
				cell.addElement(new Phrase(asp.getDescription(), descriptionFont));

				/*
				 * the reviewers of this aspect (moderator only)
				 */
				if (attendee.getRole() == Role.MODERATOR) {
					cell.addElement(phraseStrut);
					cell.addElement(phraseStrut);

					String separator = "";

					Phrase phraseReviewers = new Phrase();
					phraseReviewers.setLeading(leading);
					phraseReviewers.setFont(reviewerFont);

					for (Attendee att : attMgmt.getAttendees()) {
						if (attMgmt.hasAspect(asp, att)) {
							phraseReviewers.add(new Chunk(separator + att.getName(), reviewerFont));

							separator = "  ·  ";
						}
					}

					cell.addElement(phraseReviewers);
				}

				tableAspect.addCell(cell);

				/*
				 * category of the aspect
				 */
				cell = new PdfPCell(new Phrase(asp.getCategory(), categoryFont));
				cell.setBorderWidth(0);
				cell.setPadding(padding * 0.4f);
				cell.setPaddingTop(padding * 1.1f);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);

				tableAspect.addCell(cell);

				cellAspect.addElement(tableAspect);
				cellAspect.setPadding(0);
				cellAspect.setPaddingLeft(padding);
				cellAspect.setPaddingRight(padding);

				if (grayBackground == true) {
					grayBackground = false;
					cellAspect.setBackgroundColor(cellBackground);
				} else {
					grayBackground = true;
				}

				/*
				 * Add aspect to the list
				 */
				tableAspects.addCell(cellAspect);
			}

			PdfPCell cellBottomLine = new PdfPCell();
			cellBottomLine.setPadding(0);
			cellBottomLine.setBorderWidth(0);
			cellBottomLine.setBorderWidthBottom(1);
			cellBottomLine.setBorderColor(cellBackground);

			tableAspects.addCell(cellBottomLine);

			/*
			 * Add the attendee base table to the document
			 */
			pdfDoc.add(tableAspects);
		} catch (Exception e) {
			/*
			 * Not part of unit testing because this exception is only thrown if
			 * an internal error occurs.
			 */
			throw new ExportException(_("Cannot put aspects into the PDF document."));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.revager.export.PDFExporter#writeContent()
	 */
	@Override
	protected void writeContent() throws ExportException {
		try {
			writeTitlePage();

			Font italicFont = new Font(
					BaseFont.createFont(BaseFont.HELVETICA_OBLIQUE, BaseFont.CP1252, BaseFont.EMBEDDED), 10);

			PdfPTable tableAspIntro = new PdfPTable(1);
			tableAspIntro.setWidthPercentage(100);

			Phrase phraseAspIntro;

			PdfPCell cellAspIntro = new PdfPCell();
			cellAspIntro.setBorderWidth(0);
			cellAspIntro.setPadding(0);
			cellAspIntro.setPaddingBottom(PDFTools.cmToPt(0.8f));

			if (attendee.getAspects() != null) {
				pdfDoc.newPage();

				phraseAspIntro = new Phrase(
						_("Please prepare for the review meeting by checking the product for the following aspects step by step:"),
						italicFont);

				cellAspIntro.addElement(phraseAspIntro);

				tableAspIntro.addCell(cellAspIntro);

				pdfDoc.add(tableAspIntro);
			} else if (attendee.getRole() != Role.REVIEWER) {
				pdfDoc.newPage();

				phraseAspIntro = new Phrase(_("The product will be checked for the following aspects:"), italicFont);

				cellAspIntro.addElement(phraseAspIntro);

				tableAspIntro.addCell(cellAspIntro);

				pdfDoc.add(tableAspIntro);
			}

			writeAspects();
		} catch (Exception e) {
			/*
			 * Not part of unit testing because this exception is only thrown if
			 * an internal error occurs.
			 */
			throw new ExportException(_("Cannot create invitation for the review meeting.") + "\n\n" + e.getMessage());
		}
	}

}
