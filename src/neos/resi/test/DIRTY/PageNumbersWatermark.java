/**
 * 
 */
package neos.resi.test.DIRTY;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;

import neos.resi.tools.PDFTools;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Demonstrates the use of templates to add Watermarks and Pagenumbers.
 */
public class PageNumbersWatermark extends PdfPageEventHelper {
	/** The headertable. */
	public PdfPTable table;
	/** A template that will hold the total number of pages. */
	public PdfTemplate tpl;
	/** The font that will be used. */
	public BaseFont helv;

	/**
	 * Generates a document with a header containing Page x of y and with a
	 * Watermark on every page.
	 * 
	 * @param args
	 *            no arguments needed
	 */
	public static void main(String args[]) {
		try {
			Document doc = new Document(PageSize.A4, 50, 50, 100, 72);
			PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(
					"/home/jojo/TEST_pageNumbersWatermark.pdf"));
			writer.setPageEvent(new PageNumbersWatermark());
			doc.open();
			
			String text = "some padding text ";
			for (int k = 0; k < 10; ++k)
				text += text;
			Paragraph p = new Paragraph(text);
			p.setAlignment(Element.ALIGN_JUSTIFIED);
			doc.add(p);

			doc.close();
			
			Desktop.getDesktop().open(new File("/home/jojo/TEST_pageNumbersWatermark.pdf"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see com.lowagie.text.pdf.PdfPageEventHelper#onOpenDocument(com.lowagie.text.pdf.PdfWriter,
	 *      com.lowagie.text.Document)
	 */
	public void onOpenDocument(PdfWriter writer, Document document) {
		try {
			// initialization of the template
			tpl = writer.getDirectContent().createTemplate(100, 100);
			
			// initialization of the font
			helv = BaseFont.createFont("Helvetica", BaseFont.WINANSI, false);
		} catch (Exception e) {
			throw new ExceptionConverter(e);
		}
	}

	/**
	 * @see com.lowagie.text.pdf.PdfPageEventHelper#onEndPage(com.lowagie.text.pdf.PdfWriter,
	 *      com.lowagie.text.Document)
	 */
	public void onEndPage(PdfWriter writer, Document document) {
		PdfContentByte cb = writer.getDirectContent();
		cb.saveState();
		// compose the footer
		String text = "Page " + writer.getPageNumber() + " of ";
		float textSize = helv.getWidthPoint(text, 12);
		float textBase = document.bottom() - 20;
		cb.beginText();
		cb.setFontAndSize(helv, 12);
		// for odd pagenumbers, show the footer at the left

		cb.setTextMatrix(document.left(), textBase);
		cb.showText(text);
		cb.endText();
		cb.addTemplate(tpl, document.left() + textSize, textBase);
	}

	/**
	 * @see com.lowagie.text.pdf.PdfPageEventHelper#onCloseDocument(com.lowagie.text.pdf.PdfWriter,
	 *      com.lowagie.text.Document)
	 */
	public void onCloseDocument(PdfWriter writer, Document document) {
		tpl.beginText();
		tpl.setFontAndSize(helv, 12);
		tpl.setTextMatrix(0, 0);
		tpl.showText(Integer.toString(writer.getPageNumber() - 1));
		tpl.endText();
	}
}
