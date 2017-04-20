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

import java.text.MessageFormat;

import org.revager.tools.PDFTools;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

/**
 * This helper class is for writing the head and foot to every page of a PDF
 * document.
 */
public class PDFPageEventHelper extends PdfPageEventHelper {

	/**
	 * The head title.
	 */
	private String headTitle = null;

	/**
	 * The head logo path.
	 */
	private String headLogoPath = null;

	/**
	 * The foot text.
	 */
	private String footText = null;

	/**
	 * The template.
	 */
	private PdfTemplate template = null;

	/**
	 * The head base font.
	 */
	private BaseFont headBaseFont = null;

	/**
	 * The foot base font.
	 */
	private BaseFont footBaseFont = null;

	/**
	 * The head font size.
	 */
	private float headFontSize = 11;

	/**
	 * The foot font size.
	 */
	private float footFontSize = 9;

	/**
	 * Instantiates a new PDF page event helper.
	 * 
	 * @param headTitle
	 *            the head title
	 * @param headLogoPath
	 *            the head logo path
	 * @param footText
	 *            the foot text
	 */
	public PDFPageEventHelper(String headTitle, String headLogoPath,
			String footText) {
		if (headLogoPath == null || headLogoPath.equals("")) {
			headLogoPath = null;
		}

		if (footText == null || footText.equals("")) {
			footText = null;
		}

		this.headTitle = headTitle;
		this.headLogoPath = headLogoPath;
		this.footText = footText;
	}

	/**
	 * Sets the marks to the PDF document.
	 * 
	 * @param writer
	 *            the PDF writer
	 * @param document
	 *            the PDF document
	 */
	private void setMarks(PdfWriter writer, Document document) {
		PdfContentByte cb = writer.getDirectContent();
		float height = PDFTools.ptToCm(document.getPageSize().getHeight());

		cb.setLineWidth(0.0f);

		cb.moveTo(0.0f, PDFTools.cmToPt(height / 2.0f));
		cb.lineTo(PDFTools.cmToPt(0.3f), PDFTools.cmToPt(height / 2.0f));

		cb.moveTo(0.0f, PDFTools.cmToPt(height * 0.33f));
		cb.lineTo(PDFTools.cmToPt(0.3f), PDFTools.cmToPt(height * 0.33f));

		cb.moveTo(0.0f, PDFTools.cmToPt(height * 0.66f));
		cb.lineTo(PDFTools.cmToPt(0.3f), PDFTools.cmToPt(height * 0.66f));

		cb.stroke();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lowagie.text.pdf.PdfPageEventHelper#onOpenDocument(com.lowagie.text
	 * .pdf.PdfWriter, com.lowagie.text.Document)
	 */
	@Override
	public void onOpenDocument(PdfWriter writer, Document document) {
		template = writer.getDirectContent().createTemplate(100, 100);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lowagie.text.pdf.PdfPageEventHelper#onCloseDocument(com.lowagie.text
	 * .pdf.PdfWriter, com.lowagie.text.Document)
	 */
	@Override
	public void onCloseDocument(PdfWriter writer, Document document) {
		template.beginText();
		template.setFontAndSize(footBaseFont, footFontSize);
		template.setTextMatrix(0, 0);
		template.showText(Integer.toString(writer.getPageNumber() - 1));
		template.endText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lowagie.text.pdf.PdfPageEventHelper#onEndPage(com.lowagie.text.pdf
	 * .PdfWriter, com.lowagie.text.Document)
	 */
	@Override
	public void onEndPage(PdfWriter writer, Document document) {
		int columnNumber;

		try {
			Rectangle page = document.getPageSize();
			float pageWidth = page.getWidth() - document.leftMargin()
					- document.rightMargin();

			/*
			 * Write marks
			 */
			setMarks(writer, document);

			/*
			 * Define fonts
			 */
			headBaseFont = BaseFont.createFont(BaseFont.HELVETICA,
					BaseFont.CP1252, BaseFont.EMBEDDED);
			Font headFont = new Font(headBaseFont, headFontSize);

			footBaseFont = BaseFont.createFont(BaseFont.HELVETICA_OBLIQUE,
					BaseFont.CP1252, BaseFont.EMBEDDED);
			Font footFont = new Font(footBaseFont, footFontSize);

			/*
			 * Cell fill for space between head/foot and content
			 */
			PdfPCell cellFill = new PdfPCell();
			cellFill.setMinimumHeight(PDFTools.cmToPt(0.8f));
			cellFill.setBorderWidth(0);

			/*
			 * Write head
			 */
			if (headLogoPath != null) {
				columnNumber = 2;
			} else {
				columnNumber = 1;
			}

			PdfPTable head = new PdfPTable(columnNumber);

			Phrase phraseTitle = new Phrase(headTitle, headFont);

			PdfPCell cellTitle = new PdfPCell(phraseTitle);
			cellTitle.setHorizontalAlignment(Element.ALIGN_LEFT);
			cellTitle.setVerticalAlignment(Element.ALIGN_BOTTOM);
			cellTitle.setPaddingTop(0);
			cellTitle.setPaddingBottom(PDFTools.cmToPt(0.2f));
			cellTitle.setPaddingLeft(0);
			cellTitle.setPaddingRight(0);
			cellTitle.setBorderWidthTop(0);
			cellTitle.setBorderWidthBottom(0.5f);
			cellTitle.setBorderWidthLeft(0);
			cellTitle.setBorderWidthRight(0);

			head.addCell(cellTitle);

			if (headLogoPath != null) {
				Image headLogo = Image.getInstance(headLogoPath);
				headLogo.scaleToFit(PDFTools.cmToPt(5.0f),
						PDFTools.cmToPt(1.1f));

				PdfPCell cellLogo = new PdfPCell(headLogo);
				cellLogo.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cellLogo.setVerticalAlignment(Element.ALIGN_BOTTOM);
				cellLogo.setPaddingTop(0);
				cellLogo.setPaddingBottom(PDFTools.cmToPt(0.15f));
				cellLogo.setPaddingLeft(0);
				cellLogo.setPaddingRight(0);
				cellLogo.setBorderWidthTop(0);
				cellLogo.setBorderWidthBottom(0.5f);
				cellLogo.setBorderWidthLeft(0);
				cellLogo.setBorderWidthRight(0);

				head.addCell(cellLogo);

				head.addCell(cellFill);
			}

			head.addCell(cellFill);

			head.setTotalWidth(pageWidth);
			head.writeSelectedRows(
					0,
					-1,
					document.leftMargin(),
					page.getHeight() - document.topMargin()
							+ head.getTotalHeight(), writer.getDirectContent());

			/*
			 * Write foot
			 */
			if (footText == null) {
				footText = " ";
			}

			PdfPTable foot = new PdfPTable(1);

			foot.addCell(cellFill);

			PdfPCell cellFootText = new PdfPCell(new Phrase(footText, footFont));
			cellFootText.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cellFootText.setVerticalAlignment(Element.ALIGN_TOP);
			cellFootText.setPaddingTop(PDFTools.cmToPt(0.15f));
			cellFootText.setPaddingBottom(0);
			cellFootText.setPaddingLeft(0);
			cellFootText.setPaddingRight(0);
			cellFootText.setBorderWidthTop(0.5f);
			cellFootText.setBorderWidthBottom(0);
			cellFootText.setBorderWidthLeft(0);
			cellFootText.setBorderWidthRight(0);

			foot.addCell(cellFootText);

			/*
			 * Print page numbers
			 */
			PdfContentByte contentByte = writer.getDirectContent();
			contentByte.saveState();

			String text = MessageFormat.format(_("Page {0} of") + " ",
					writer.getPageNumber());

			float textSize = footBaseFont.getWidthPoint(text, footFontSize);
			float textBase = document.bottom() - PDFTools.cmToPt(1.26f);
			contentByte.beginText();
			contentByte.setFontAndSize(footBaseFont, footFontSize);

			float adjust;
			if (footText.trim().equals("")) {
				adjust = (pageWidth / 2) - (textSize / 2)
						- footBaseFont.getWidthPoint("0", footFontSize);
			} else {
				adjust = 0;
			}

			contentByte.setTextMatrix(document.left() + adjust, textBase);
			contentByte.showText(text);
			contentByte.endText();
			contentByte.addTemplate(template, document.left() + adjust
					+ textSize, textBase);

			contentByte.stroke();
			contentByte.restoreState();

			foot.setTotalWidth(pageWidth);
			foot.writeSelectedRows(0, -1, document.leftMargin(),
					document.bottomMargin(), writer.getDirectContent());
		} catch (Exception e) {
			/*
			 * Not part of unit testing because this exception is only thrown if
			 * an internal error occurs.
			 */
			throw new ExceptionConverter(e);
		}
	}

}
