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

import static org.revager.app.model.Data._;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.schema.Product;
import org.revager.app.model.schema.Review;
import org.revager.app.model.schema.Severities;
import org.revager.io.ResiIO;
import org.revager.io.ResiIOException;
import org.revager.io.ResiIOFactory;
import org.revager.tools.FileTools;

/**
 * The Class ApplicationControl.
 */
public class ApplicationControl {

	/**
	 * Instantiates a new application control.
	 */
	ApplicationControl() {
		super();
	}

	/**
	 * The IO provider to load and store data in Resi XML format.
	 */
	private ResiIO io = ResiIOFactory.getInstance().getIOProvider();

	/**
	 * The path to the current review file.
	 */
	private static final File REVIEW_FILE = new File(Data.getInstance()
			.getAppData().getAppDataPath()
			+ Data.getInstance().getResource("reviewFileName"));

	/**
	 * The path to the backup review file.
	 */
	private static final File REVIEW_BACKUP_FILE = new File(Data.getInstance()
			.getAppData().getAppDataPath()
			+ Data.getInstance().getResource("revBakFileName"));

	/**
	 * The path to the directory for external references.
	 */
	private static final File EXTREFS_DIRECTORY = new File(Data.getInstance()
			.getAppData().getAppDataPath()
			+ Data.getInstance().getResource("extRefsDirectoryName"));

	/**
	 * The file ending for reviews in ZIP format.
	 */
	private static final String ENDING_ZIP = "."
			+ Data.getInstance().getResource("fileEndingReviewZIP")
					.toLowerCase();

	/**
	 * The file ending for reviews in XML format.
	 */
	private static final String ENDING_XML = "."
			+ Data.getInstance().getResource("fileEndingReviewXML")
					.toLowerCase();

	/**
	 * The alternative file ending for reviews in XML format.
	 */
	private static final String ENDING_XML_ALT = ".xml";

	/**
	 * Clears the review data.
	 */
	public void clearReview() {
		/*
		 * Clean up review data
		 */
		if (REVIEW_FILE.exists()) {
			REVIEW_FILE.delete();
		}

		if (REVIEW_BACKUP_FILE.exists()) {
			REVIEW_BACKUP_FILE.delete();
		}

		if (EXTREFS_DIRECTORY.exists()) {
			FileTools.deleteDirectory(EXTREFS_DIRECTORY);
		}

		Data.getInstance().getResiData().clearReview();
		Data.getInstance().getResiData().setReview(new Review());

		EXTREFS_DIRECTORY.mkdir();

		/*
		 * Set standard severities
		 */
		Data.getInstance().getResiData().getReview()
				.setSeverities(new Severities());

		List<String> stdSev = Data.getStandardSeverities();
		Data.getInstance().getResiData().getReview().getSeverities()
				.getSeverities().addAll(stdSev);

		/*
		 * Set empty product
		 */
		Data.getInstance().getResiData().getReview().setProduct(new Product());
		Data.getInstance().getResiData().getReview().getProduct().setName("");
		Data.getInstance().getResiData().getReview().getProduct()
				.setVersion("");
		
		Application.getInstance().getReviewMgmt().addDummyProdReference();

		/*
		 * Set empty values
		 */
		Data.getInstance().getResiData().getReview().setName("");
		Data.getInstance().getResiData().getReview().setDescription("");
		Data.getInstance().getResiData().getReview().setComments("");
		Data.getInstance().getResiData().getReview().setRecommendation("");
		Data.getInstance().getResiData().getReview().setImpression("");

		Application.getInstance().getAspectMgmt().addDummyAspect();
		Application.getInstance().getAttendeeMgmt().addDummyAttendee();

		Data.getInstance().getResiData().fireDataChanged();
	}

	/**
	 * Clears the old review and creates a new one.
	 */
	public void newReview() {
		clearReview();
	}

	/**
	 * Store review.
	 * 
	 * @param filePath
	 *            the file path
	 * 
	 * @throws ResiIOException
	 *             the resi io exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ApplicationException
	 *             the application exception
	 */
	public void storeReview(String filePath) throws ResiIOException,
			IOException, ApplicationException {
		if (filePath.startsWith(Data.getInstance().getAppData()
				.getAppDataPath())) {
			throw new ApplicationException(
					_("It is not possible to store and open application data. Please choose another location."));
		}

		if (!filePath.trim().equals("")
				&& !filePath.toLowerCase().trim().endsWith(ENDING_ZIP)
				&& !filePath.toLowerCase().trim().endsWith(ENDING_XML)
				&& !filePath.toLowerCase().trim().endsWith(ENDING_XML_ALT)) {
			filePath = filePath + ENDING_ZIP;
		}

		if (filePath.toLowerCase().trim().endsWith(ENDING_ZIP)) {
			storeReviewAsZIP(filePath);
		} else {
			storeReviewAsXML(filePath);
		}

		Data.getInstance().getResiData().setReviewPath(filePath);

		Data.getInstance().getResiData().fireDataChanged();

		try {
			Data.getInstance().getAppData().addLastReview(filePath);
		} catch (DataException e) {
			/*
			 * do nothing
			 */
		}
	}

	/**
	 * Load review.
	 * 
	 * @param filePath
	 *            the file path
	 * 
	 * @throws ResiIOException
	 *             the resi io exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ApplicationException
	 *             the application exception
	 */
	public void loadReview(String filePath) throws ResiIOException,
			IOException, ApplicationException {
		if (filePath.startsWith(Data.getInstance().getAppData()
				.getAppDataPath())) {
			throw new ApplicationException(
					_("It is not possible to store and open application data. Please choose another location."));
		}

		if (filePath.toLowerCase().trim().endsWith(ENDING_ZIP)) {
			loadReviewFromZIP(filePath);
		} else {
			loadReviewFromXML(filePath);
		}

		Application.getInstance().getReviewMgmt().refactorReview();

		Application.getInstance().getReviewMgmt().addDummyProdReference();
		Application.getInstance().getAspectMgmt().addDummyAspect();
		Application.getInstance().getAttendeeMgmt().addDummyAttendee();

		Application.getInstance().getAttendeeMgmt().updateAttendeesDirectory();

		Data.getInstance().getResiData().setReviewPath(filePath);

		Data.getInstance().getResiData().fireDataChanged();

		try {
			Data.getInstance().getAppData().addLastReview(filePath);
		} catch (DataException e) {
			/*
			 * do nothing
			 */
		}
	}

	/**
	 * Store review as xml.
	 * 
	 * @param filePath
	 *            the file path
	 * 
	 * @throws ResiIOException
	 *             the resi io exception
	 */
	private void storeReviewAsXML(String filePath) throws ResiIOException {
		io.storeReview(filePath);
	}

	/**
	 * Store review as zip.
	 * 
	 * @param filePath
	 *            the file path
	 * 
	 * @throws ResiIOException
	 *             the resi io exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void storeReviewAsZIP(String filePath) throws ResiIOException,
			IOException {
		io.storeReview(REVIEW_FILE.getPath());

		List<File> reviewFiles = FileTools.getListOfFiles(EXTREFS_DIRECTORY);

		reviewFiles.add(REVIEW_FILE);

		FileTools.writeToZip(reviewFiles, new File(filePath), true, false);
	}

	/**
	 * Load review from xml.
	 * 
	 * @param filePath
	 *            the file path
	 * 
	 * @throws ResiIOException
	 *             the resi io exception
	 */
	private void loadReviewFromXML(String filePath) throws ResiIOException {
		clearReview();

		io.loadReview(filePath);
	}

	/**
	 * Load review from zip.
	 * 
	 * @param filePath
	 *            the file path
	 * 
	 * @throws ResiIOException
	 *             the resi io exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void loadReviewFromZIP(String filePath) throws ResiIOException,
			IOException {
		clearReview();

		FileTools.extractZipFile(new File(filePath), EXTREFS_DIRECTORY, true);

		File tempReviewFile = new File(EXTREFS_DIRECTORY.getPath() + "/"
				+ REVIEW_FILE.getName());

		FileTools.copyFile(tempReviewFile, REVIEW_FILE);

		tempReviewFile.delete();

		io.loadReview(REVIEW_FILE.getPath());
	}

	/**
	 * Backup review.
	 * 
	 * @throws ResiIOException
	 *             the resi io exception
	 * @throws DataException
	 *             the data exception
	 */
	public void backupReview() throws ResiIOException, DataException {
		io.storeReviewBackup();
	}

	/**
	 * Restore backuped review.
	 * 
	 * @throws ResiIOException
	 *             the resi io exception
	 * @throws DataException
	 *             the data exception
	 */
	public void restoreReview() throws ResiIOException, DataException {
		if (isReviewRestorable()) {
			Data.getInstance().getResiData().clearReview();

			io.loadReviewBackup();

			Application.getInstance().getReviewMgmt().refactorReview();

			Data.getInstance().getResiData().fireDataChanged();
		}
	}

	/**
	 * Checks if is review restorable.
	 * 
	 * @return true, if the review is restorable
	 */
	public boolean isReviewRestorable() {
		return REVIEW_BACKUP_FILE.exists();
	}

	/**
	 * Returns a suggestion for the file name of the current review.
	 * 
	 * @return a suggestion for the review file name
	 */
	public String getReviewNameSuggestion() {
		ReviewManagement revMgmt = Application.getInstance().getReviewMgmt();
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String reviewName = revMgmt.getReviewName().trim().replace(" ", "_");
		String productName = revMgmt.getProductName().trim().replace(" ", "_");
		String productVersion = revMgmt.getProductVersion().trim()
				.replace(" ", "_");

		String reviewFileName = sdf.format(new Date().getTime()) + "_";

		if (!reviewName.equals("")) {
			reviewFileName += reviewName;
		} else if (!productName.equals("")) {
			reviewFileName += _("Review") + "_" + productName;

			if (!productVersion.equals("")) {
				reviewFileName += "_" + productVersion;
			}
		} else {
			reviewFileName += _("Review");
		}

		return reviewFileName;
	}

}
