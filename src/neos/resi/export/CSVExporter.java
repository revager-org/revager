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
package neos.resi.export;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * This abstract class is the base for all CSV exporter classes.
 */
public abstract class CSVExporter {

	/**
	 * The separator to separate the columns.
	 */
	private String separator = null;

	/**
	 * The encapsulator to protect the content of the cells.
	 */
	private String encapsulator = null;

	/**
	 * The number of columns.
	 */
	private int columns = 1;

	/**
	 * The CSV document: One list entry is a line in the CSV file; one line is
	 * represented by an array.
	 */
	protected List<String[]> csvDoc = new ArrayList<String[]>();

	/**
	 * The columns of the CSV file.
	 */
	protected String[] csvHead = null;

	/**
	 * Instantiates a new cSV exporter.
	 */
	protected CSVExporter() {
		super();

		setSeparator(",");
	}

	/**
	 * Write the content to the CSV document.
	 */
	protected abstract void writeContent();

	/**
	 * Creates a new line for the CSV file.
	 * 
	 * @return the String array
	 */
	protected String[] newLine() {
		return new String[columns];
	}

	/**
	 * Writes the CSV data to a file with the given path.
	 * 
	 * @param filePath
	 *            the file path
	 * 
	 * @throws ExportException
	 *             If an error occurs while exporting the CSV data to the file
	 */
	public void writeToFile(String filePath) throws ExportException {
		writeContent();

		try {
			OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream(filePath), "UTF-8");

			StringBuilder csvLine = new StringBuilder();

			List<String[]> csvContent = csvDoc;

			if (csvHead != null) {
				csvContent.add(0, csvHead);
			}

			for (String[] line : csvContent) {
				csvLine.delete(0, csvLine.length());
				String sep = "";

				for (String cell : line) {

					/*
					 * Replace encapsulator and separator chars in cell string
					 */
					csvLine.append(sep);

					if (encapsulator != null) {
						if (encapsulator.equals("'")) {
							cell = cell.replace(encapsulator, "\"");
						} else {
							cell = cell.replace(encapsulator, "'");
						}

						csvLine.append(encapsulator);
					} else {
						if (separator.equals(";")) {
							cell = cell.replace(separator, ",");
						} else {
							cell = cell.replace(separator, ";");
						}
					}

					csvLine.append(cell);

					if (encapsulator != null) {
						csvLine.append(encapsulator);
					}

					sep = separator;
				}

				writer.write(csvLine.toString());
				writer.write('\n');
			}

			writer.flush();

			writer.close();
		} catch (Exception e) {
			/*
			 * Not part of unit testing because this exception is only thrown if
			 * an internal error occurs.
			 */
			throw new ExportException(e.getMessage());
		}
	}

	/**
	 * Gets the current separator.
	 * 
	 * @return the separator
	 */
	public String getSeparator() {
		return separator;
	}

	/**
	 * Sets the separator.
	 * 
	 * @param separator
	 *            the separator to set
	 */
	public void setSeparator(String separator) {
		if (separator != null && !separator.equals("")) {
			this.separator = separator;
		} else {
			this.separator = ",";
		}
	}

	/**
	 * Gets the encapsulator.
	 * 
	 * @return the encapsulator
	 */
	public String getEncapsulator() {
		return encapsulator;
	}

	/**
	 * Sets the encapsulator.
	 * 
	 * @param encapsulator
	 *            the encapsulator to set
	 */
	public void setEncapsulator(String encapsulator) {
		if (encapsulator != null && !encapsulator.equals("")) {
			this.encapsulator = encapsulator;
		} else {
			this.encapsulator = null;
		}
	}

	/**
	 * Gets the number of columns.
	 * 
	 * @return the number of columns
	 */
	public int getColumns() {
		return columns;
	}

	/**
	 * Sets the number of columns.
	 * 
	 * @param columns
	 *            the number of columns to set
	 */
	public void setColumns(int columns) {
		if (columns > 1) {
			this.columns = columns;
		} else {
			this.columns = 1;
		}
	}

}
