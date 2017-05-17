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
package org.revager.app;

import static org.revager.app.model.Data.translate;

import java.io.File;
import java.io.FilenameFilter;

import javax.swing.filechooser.FileFilter;

import org.revager.app.model.Data;

/**
 * This class filters files by a given type.
 */
public class ResiFileFilter extends FileFilter implements FilenameFilter {

	/**
	 * All files.
	 */
	public static final int TYPE_ALL = 1;

	/**
	 * Directories only.
	 */
	public static final int TYPE_DIRECTORY = 2;

	/**
	 * Review files only.
	 */
	public static final int TYPE_REVIEW = 3;

	/**
	 * Catalog files only.
	 */
	public static final int TYPE_CATALOG = 4;

	/**
	 * Aspects files only.
	 */
	public static final int TYPE_ASPECTS = 5;

	/**
	 * PDF files only.
	 */
	public static final int TYPE_PDF = 6;

	/**
	 * CSV file only.
	 */
	public static final int TYPE_CSV = 7;

	/**
	 * ZIP files only.
	 */
	public static final int TYPE_ZIP = 8;

	/**
	 * Image files only.
	 */
	public static final int TYPE_IMAGES = 9;

	/**
	 * File ending for review files in the XML format.
	 */
	private final String ENDING_REVIEW_XML = "." + Data.getInstance().getResource("fileEndingReviewXML").toLowerCase();

	/**
	 * File ending for review files in the ZIP format.
	 */
	private final String ENDING_REVIEW_ZIP = "." + Data.getInstance().getResource("fileEndingReviewZIP").toLowerCase();

	/**
	 * File ending for catalog files.
	 */
	private final String ENDING_CATALOG = "." + Data.getInstance().getResource("fileEndingCatalog").toLowerCase();

	/**
	 * File ending for aspects files.
	 */
	private final String ENDING_ASPECTS = "." + Data.getInstance().getResource("fileEndingAspects").toLowerCase();

	/**
	 * The current type.
	 */
	private int type = TYPE_ALL;

	/**
	 * Instantiates a new file filter.
	 * 
	 * @param type
	 *            the filter type (one of the static final constants of this
	 *            class)
	 */
	public ResiFileFilter(int type) {
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File path) {
		boolean returnValue = true;
		String fileName = path.getName().toLowerCase();

		switch (type) {
		case TYPE_ALL:
			returnValue = true;
			break;

		case TYPE_DIRECTORY:
			returnValue = path.isDirectory();
			break;

		case TYPE_REVIEW:
			returnValue = path.isDirectory() || fileName.endsWith(ENDING_REVIEW_ZIP)
					|| fileName.endsWith(ENDING_REVIEW_XML) || fileName.endsWith(".xml");
			break;

		case TYPE_CATALOG:
			returnValue = path.isDirectory() || fileName.endsWith(ENDING_CATALOG) || fileName.endsWith(".xml");
			break;

		case TYPE_ASPECTS:
			returnValue = path.isDirectory() || fileName.endsWith(ENDING_ASPECTS) || fileName.endsWith(".xml");
			break;

		case TYPE_PDF:
			returnValue = path.isDirectory() || fileName.endsWith(".pdf");
			break;

		case TYPE_CSV:
			returnValue = path.isDirectory() || fileName.endsWith(".csv") || fileName.endsWith(".txt");
			break;

		case TYPE_ZIP:
			returnValue = path.isDirectory() || fileName.endsWith(".zip");
			break;

		case TYPE_IMAGES:
			returnValue = path.isDirectory() || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")
					|| fileName.endsWith(".png") || fileName.endsWith(".gif");
			break;

		default:
			returnValue = true;
			break;
		}

		return returnValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	@Override
	public boolean accept(File dir, String name) {
		File f = new File(dir + name);

		return !f.isHidden() && accept(f);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription() {
		String description = "";

		switch (type) {
		case TYPE_ALL:
			description = translate("All files");
			break;

		case TYPE_DIRECTORY:
			description = translate("Directory");
			break;

		case TYPE_REVIEW:
			description = translate("Review files") + " (*" + ENDING_REVIEW_ZIP + " *" + ENDING_REVIEW_XML + " *.xml)";
			break;

		case TYPE_CATALOG:
			description = translate("Catalog files") + " (*" + ENDING_CATALOG + " *.xml)";
			break;

		case TYPE_ASPECTS:
			description = translate("Aspect files") + " (*" + ENDING_ASPECTS + " *.xml)";
			break;

		case TYPE_PDF:
			description = translate("PDF files") + " (*.pdf)";
			break;

		case TYPE_CSV:
			description = translate("CSV files") + " (*.csv *.txt)";
			break;

		case TYPE_ZIP:
			description = translate("ZIP files") + " (*.zip)";
			break;

		case TYPE_IMAGES:
			description = translate("Image files") + " (*.jpg *.jpeg *.gif *.png)";
			break;

		default:
			description = translate("All files");
			break;
		}

		return description;
	}

}
