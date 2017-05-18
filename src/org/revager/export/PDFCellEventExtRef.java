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

import java.io.File;
import java.io.IOException;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * This helper class is for attaching files to a PDF document.
 */
public class PDFCellEventExtRef implements PdfPCellEvent {

	/**
	 * The PDF writer.
	 */
	private PdfWriter writer = null;

	/**
	 * The file to add to the document.
	 */
	private File file = null;

	/**
	 * Instantiates a new PDF cell event for external references (files).
	 * 
	 * @param writer
	 *            the PDF writer
	 * @param file
	 *            the file to add to the document
	 */
	public PDFCellEventExtRef(PdfWriter writer, File file) {
		this.writer = writer;
		this.file = file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lowagie.text.pdf.PdfPCellEvent#cellLayout(com.lowagie.text.pdf.
	 * PdfPCell , com.lowagie.text.Rectangle,
	 * com.lowagie.text.pdf.PdfContentByte[])
	 */
	@Override
	public void cellLayout(PdfPCell cell, Rectangle rect, PdfContentByte[] canvas) {
		PdfContentByte cb = canvas[PdfPTable.LINECANVAS];

		// cb.reset();

		Rectangle attachmentRect = new Rectangle(rect.getLeft() - 25, rect.getTop() - 25,
				rect.getRight() - rect.getWidth() - 40, rect.getTop() - 10);

		String fileDesc = file.getName() + " (" + translate("File Attachment") + ")";

		try {
			PdfAnnotation attachment = PdfAnnotation.createFileAttachment(writer, attachmentRect, fileDesc, null,
					file.getAbsolutePath(), file.getName());
			writer.addAnnotation(attachment);
		} catch (IOException e) {
			/*
			 * just do not add a reference if the file was not found or another
			 * error occured.
			 */
		}

		// cb.setColorStroke(new GrayColor(0.8f));
		// cb.roundRectangle(rect.getLeft() + 4, rect.getBottom(),
		// rect.getWidth() - 8, rect.getHeight() - 4, 4);

		cb.stroke();
	}

}
