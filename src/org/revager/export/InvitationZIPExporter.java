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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.revager.app.Application;
import org.revager.app.ReviewManagement;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.Meeting;
import org.revager.tools.FileTools;

/**
 * This class implements the functionality to export review meeting invitations
 * as ZIP archive.
 */
public class InvitationZIPExporter {

	/**
	 * Reference to review management.
	 */
	private ReviewManagement revMgmt = Application.getInstance()
			.getReviewMgmt();

	/**
	 * The work directory to store temporary files.
	 */
	private static final File WORK_DIRECTORY = new File(Data.getInstance()
			.getAppData().getAppDataPath()
			+ Data.getInstance().getResource("workDirectoryName"));

	/**
	 * The review info document.
	 */
	private static final File REVIEW_INFO_DOC = new File(WORK_DIRECTORY,
			_("Review_Information.pdf"));

	/**
	 * The meeting.
	 */
	private Meeting meeting = null;

	/**
	 * The attendee.
	 */
	private Attendee attendee = null;

	/**
	 * The file path.
	 */
	private String filePath = null;

	/**
	 * The files to add to the ZIP archive.
	 */
	private List<File> files = new ArrayList<File>();

	/**
	 * Instantiates a new invitation zip exporter.
	 * 
	 * @param filePath
	 *            the file path
	 * @param meeting
	 *            the meeting
	 * @param attendee
	 *            the attendee
	 * @param attachProdExtRefs
	 *            true, if the external product references should be part of the
	 *            invitation
	 */
	public InvitationZIPExporter(String filePath, Meeting meeting,
			Attendee attendee, boolean attachProdExtRefs) {
		super();

		this.meeting = meeting;
		this.attendee = attendee;
		this.filePath = filePath;

		/*
		 * Recreate the work directory
		 */
		if (WORK_DIRECTORY.exists()) {
			FileTools.deleteDirectory(WORK_DIRECTORY);
		}

		WORK_DIRECTORY.mkdir();

		/*
		 * List of external product references
		 */
		if (attachProdExtRefs) {
			for (File ref : revMgmt.getExtProdReferences()) {
				files.add(ref);
			}
		}
	}

	/**
	 * Write the invitation to file.
	 * 
	 * @throws ExportException
	 *             If an error occurs while exporting the invitation
	 * @throws DataException
	 *             If an error occurs while getting the data to create the
	 *             invitation
	 */
	public void writeToFile() throws ExportException, DataException {
		new InvitationPDFExporter(REVIEW_INFO_DOC.getAbsolutePath(), meeting,
				attendee, false).writeToFile();

		files.add(new File(REVIEW_INFO_DOC.getAbsolutePath()));

		try {
			FileTools.writeToZip(files, new File(filePath), false, true);
		} catch (IOException e) {
			/*
			 * Not part of unit testing because this exception is only thrown if
			 * an internal error occurs.
			 */
			throw new ExportException(_("Cannot store invitation as ZIP file."));
		}
	}
}
