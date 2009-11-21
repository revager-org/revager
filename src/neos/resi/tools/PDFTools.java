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
package neos.resi.tools;

/**
 * This class provides some useful tools to support the creation of PDF files.
 */
public class PDFTools {

	/**
	 * Converts a length in centimeters to a length in points. It is assumed
	 * that the PDF is rendered with 72 dpi. This method is copied from an iText
	 * tutorial of Markus Knauss (licensed under the GPL).
	 * 
	 * @param cm
	 *            to convert to pt
	 * 
	 * @return cm length in pt
	 */
	public static float cmToPt(float cm) {
		return ((cm / 2.54f) * 72.0f);
	}

	/**
	 * Converts a length given in pt to cm. The conversion assumes that 72 dpi
	 * are used for rendering. This method is copied from an iText tutorial of
	 * Markus Knauss (licensed under the GPL).
	 * 
	 * @param pt
	 *            to convert to cm
	 * 
	 * @return pt length in cm
	 */
	public static float ptToCm(float pt) {
		// 72 pt = 1 inch = 2.54 cm
		return ((pt / 72.0f) * 2.54f);
	}

}
