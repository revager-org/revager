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

import java.io.FileOutputStream;

import org.revager.app.model.Data;
import org.revager.tools.PDFTools;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfWriter;

/**
 * This class is the base for all PDF exporter classes. It implements the basic
 * functionality.
 */
public abstract class PDFExporter {

	/**
	 * The pdf writer.
	 */
	protected PdfWriter pdfWriter = null;

	/**
	 * The pdf document.
	 */
	protected Document pdfDoc = null;

	/**
	 * The page width.
	 */
	protected float pageWidth;

	/**
	 * The page height.
	 */
	protected float pageHeight;

	/**
	 * Instantiates a new PDF exporter.
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
	 *             If an error occurs while instantiating the PDF exporter class
	 */
	protected PDFExporter(String filePath, String headTitle, String headLogoPath, String footText)
			throws ExportException {
		pdfDoc = new Document(PageSize.A4, PDFTools.cmToPt(2), PDFTools.cmToPt(2), PDFTools.cmToPt(3.5f),
				PDFTools.cmToPt(3.2f));

		try {
			pdfWriter = PdfWriter.getInstance(pdfDoc, new FileOutputStream(filePath));
		} catch (Exception e) {
			/*
			 * Not part of unit testing because this exception is only thrown if
			 * an internal error occurs.
			 */
			throw new ExportException(_(
					"Cannot store PDF file. Either you don't have write permissions or the PDF file cannot be created."));
		}

		pdfWriter.setPageEvent(new PDFPageEventHelper(headTitle, headLogoPath, footText));

		pdfDoc.addAuthor(Data.getInstance().getResource("appName"));
		pdfDoc.addCreator(Data.getInstance().getResource("appName"));
		pdfDoc.addCreationDate();

		pdfDoc.addTitle(headTitle);
		pdfDoc.addSubject(headTitle);

		Rectangle page = pdfDoc.getPageSize();
		pageWidth = page.getWidth() - pdfDoc.leftMargin() - pdfDoc.rightMargin();
		pageHeight = page.getHeight() - pdfDoc.topMargin() - pdfDoc.bottomMargin();
	}

	/**
	 * Creates an empty PdfPTable cell with a defined height and colspan.
	 * 
	 * @param height
	 *            the height of the created cell
	 * @param colspan
	 *            the colspan
	 * 
	 * @return Empty cell with defined minimum height
	 */
	protected PdfPCell createVerticalStrut(float height, int colspan) {
		PdfPCell fill = new PdfPCell();

		fill.setColspan(colspan);
		fill.setMinimumHeight(height);
		fill.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);

		return fill;
	}

	/**
	 * Writes the content of the PDF document.
	 * 
	 * @throws ExportException
	 *             If an error occurs while writing the content.
	 */
	protected abstract void writeContent() throws ExportException;

	/**
	 * Writes the PDF document to a file.
	 * 
	 * @throws ExportException
	 *             If an error occurs while writing the document to file.
	 */
	public void writeToFile() throws ExportException {
		pdfDoc.open();

		writeContent();

		pdfDoc.close();
	}

}
