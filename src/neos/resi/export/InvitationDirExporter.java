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

import java.io.File;
import java.io.IOException;

import neos.resi.app.Application;
import neos.resi.app.ReviewManagement;
import neos.resi.app.model.Data;
import neos.resi.app.model.DataException;
import neos.resi.app.model.schema.Attendee;
import neos.resi.app.model.schema.Meeting;
import neos.resi.tools.FileTools;

/**
 * This class implements the functionality to export review meeting invitations
 * as directories.
 */
public class InvitationDirExporter {

	/**
	 * Reference to review management.
	 */
	private ReviewManagement revMgmt = Application.getInstance()
			.getReviewMgmt();

	/**
	 * The review info document.
	 */
	private File reviewInfoDoc = null;

	/**
	 * The meeting.
	 */
	private Meeting meeting = null;

	/**
	 * The attendee.
	 */
	private Attendee attendee = null;

	/**
	 * The directory to store the invitation in.
	 */
	private File directory = null;

	/**
	 * True, if external product references should be part of the invitation.
	 */
	private boolean attachProdExtRefs = true;

	/**
	 * Instantiates a new invitation dir exporter.
	 * 
	 * @param dirPath
	 *            the directory to store the invitation
	 * @param meeting
	 *            the meeting
	 * @param attendee
	 *            the attendee
	 * @param attachProdExtRefs
	 *            true, if external product references should be part of the
	 *            invitation
	 */
	public InvitationDirExporter(String dirPath, Meeting meeting,
			Attendee attendee, boolean attachProdExtRefs) {
		super();

		this.meeting = meeting;
		this.attendee = attendee;
		this.directory = new File(dirPath);
		this.attachProdExtRefs = attachProdExtRefs;

		this.reviewInfoDoc = new File(directory, Data.getInstance()
				.getLocaleStr("export.reviewInfoDocumentName"));
	}

	/**
	 * Store the invitation as directory.
	 * 
	 * @throws ExportException
	 *             If an error occurs while exporting the invitation
	 * @throws DataException
	 *             If an error occurs while getting the data to create the
	 *             invitation
	 */
	public void writeDir() throws ExportException, DataException {
		/*
		 * create directory
		 */
		if (directory.exists()) {
			FileTools.deleteDirectory(directory);
		}

		directory.mkdir();

		/*
		 * write review info file
		 */
		new InvitationPDFExporter(reviewInfoDoc.getAbsolutePath(), meeting,
				attendee, false).writeToFile();

		/*
		 * external product references
		 */
		if (attachProdExtRefs) {
			for (File ref : revMgmt.getExtProdReferences()) {
				try {
					FileTools.copyFile(ref, new File(directory, ref.getName()));
				} catch (IOException e) {
					/*
					 * Not part of unit testing because this exception is only
					 * thrown if an internal error occurs.
					 */
					throw new ExportException(Data.getInstance().getLocaleStr(
							"message.invitationDirWriteFailed"));
				}
			}
		}
	}

}
