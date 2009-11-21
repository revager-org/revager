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
package neos.resi.app;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import neos.resi.app.model.Data;
import neos.resi.app.model.DataException;
import neos.resi.app.model.appdata.AppCSVProfile;
import neos.resi.app.model.schema.Aspects;
import neos.resi.app.model.schema.Attendee;
import neos.resi.app.model.schema.Catalog;
import neos.resi.app.model.schema.Finding;
import neos.resi.app.model.schema.Meeting;
import neos.resi.export.CSVExporter;
import neos.resi.export.ExportException;
import neos.resi.export.FindingsCSVExporter;
import neos.resi.export.InvitationDirExporter;
import neos.resi.export.InvitationPDFExporter;
import neos.resi.export.InvitationZIPExporter;
import neos.resi.export.MeetingProtocolPDFExporter;
import neos.resi.export.ProtocolPDFExporter;
import neos.resi.export.ReviewProtocolPDFExporter;
import neos.resi.io.ResiIO;
import neos.resi.io.ResiIOException;
import neos.resi.io.ResiIOFactory;

/**
 * This class implements the control for import and export processes.
 */
public class ImportExportControl {

	/**
	 * The Enum InvitationType.
	 */
	public enum InvitationType {
		PDF, ZIP, DIRECTORY;
	}

	/**
	 * Instantiates the import & export control.
	 */
	ImportExportControl() {
		super();
	}

	/**
	 * The file ending for Aspects files.
	 */
	private static final String ENDING_ASPECTS = "."
			+ Data.getInstance().getResource("fileEndingAspects").toLowerCase();

	/**
	 * The file ending for Catalog files.
	 */
	private static final String ENDING_CATALOG = "."
			+ Data.getInstance().getResource("fileEndingCatalog").toLowerCase();

	/**
	 * The IO provider to load and store data in Resi XML format.
	 */
	private ResiIO io = ResiIOFactory.getInstance().getIOProvider();

	/**
	 * Import the given catalog as XML file.
	 * 
	 * @param filePath
	 *            the file path
	 * 
	 * @return the catalog
	 * 
	 * @throws ResiIOException
	 *             If an error occurs while importing the catalog
	 */
	public Catalog importCatalogXML(String filePath) throws ResiIOException {
		io.loadCatalog(filePath);

		return Data.getInstance().getResiData().getCatalog();
	}

	/**
	 * Export catalog xml.
	 * 
	 * @param filePath
	 *            the file path
	 * @param cat
	 *            the cat
	 * 
	 * @throws ResiIOException
	 *             the resi io exception
	 */
	public File exportCatalogXML(String filePath, Catalog cat)
			throws ResiIOException {
		if (!filePath.trim().equals("")
				&& !filePath.toLowerCase().trim().endsWith(ENDING_CATALOG)
				&& !filePath.toLowerCase().trim().endsWith(".xml")) {
			filePath = filePath + ENDING_CATALOG;
		}

		Data.getInstance().getResiData().setCatalog(cat);

		io.storeCatalog(filePath);

		return new File(filePath);
	}

	/**
	 * Imports the given aspects as XML file.
	 * 
	 * @param filePath
	 *            the file path
	 * 
	 * @return the aspects
	 * 
	 * @throws ResiIOException
	 *             If an error occurs while importing the aspects
	 */
	public Aspects importAspectsXML(String filePath) throws ResiIOException {
		io.loadAspects(filePath);

		return Data.getInstance().getResiData().getAspects();
	}

	/**
	 * Exports the given aspects to an XML file.
	 * 
	 * @param filePath
	 *            the file path
	 * @param asps
	 *            the aspects
	 * 
	 * @throws ResiIOException
	 *             If an error occurs while exporting
	 */
	public File exportAspectsXML(String filePath, Aspects asps)
			throws ResiIOException {
		if (!filePath.trim().equals("")
				&& !filePath.toLowerCase().trim().endsWith(ENDING_ASPECTS)
				&& !filePath.toLowerCase().trim().endsWith(".xml")) {
			filePath = filePath + ENDING_ASPECTS;
		}

		Data.getInstance().getResiData().setAspects(asps);

		io.storeAspects(filePath);

		return new File(filePath);
	}

	/**
	 * Exports a meeting protocol as PDF file.
	 * 
	 * @param filePath
	 *            the file path
	 * @param meeting
	 *            the meeting
	 * @param showSignFields
	 *            true if signature fields should be part of the protocol
	 * @param attachProdExtRefs
	 *            true if external product references (files) should be part of
	 *            the protocol
	 * @param attachFindExtRefs
	 *            true if external references of the findings should be part of
	 *            the protocol
	 * 
	 * @throws ExportException
	 *             If an error occurs while exporting the protocol
	 * @throws DataException
	 *             If an error occurs while getting the data for the protocol
	 */
	public File exportMeetingProtocolPDF(String filePath, Meeting meeting,
			boolean showSignFields, boolean attachProdExtRefs,
			boolean attachFindExtRefs) throws ExportException, DataException {
		if (!filePath.trim().equals("")
				&& !filePath.toLowerCase().trim().endsWith(".pdf")) {
			filePath = filePath + ".pdf";
		}

		ProtocolPDFExporter exporter = new MeetingProtocolPDFExporter(filePath,
				meeting, showSignFields, attachProdExtRefs, attachFindExtRefs);

		exporter.writeToFile();

		return new File(filePath);
	}

	/**
	 * Exports a review protocol as PDF file.
	 * 
	 * @param filePath
	 *            the file path
	 * @param showSignFields
	 *            true if signature fields should be part of the protocol
	 * @param attachProdExtRefs
	 *            true if external product references (files) should be part of
	 *            the protocol
	 * @param attachFindExtRefs
	 *            true if external references of the findings should be part of
	 *            the protocol
	 * 
	 * @throws ExportException
	 *             If an error occurs while exporting the protocol
	 * @throws DataException
	 *             If an error occurs while getting the data for the protocol
	 */
	public File exportReviewProtocolPDF(String filePath,
			boolean showSignFields, boolean attachProdExtRefs,
			boolean attachFindExtRefs) throws ExportException, DataException {
		if (!filePath.trim().equals("")
				&& !filePath.toLowerCase().trim().endsWith(".pdf")) {
			filePath = filePath + ".pdf";
		}

		ProtocolPDFExporter exporter = new ReviewProtocolPDFExporter(filePath,
				showSignFields, attachProdExtRefs, attachFindExtRefs);

		exporter.writeToFile();

		return new File(filePath);
	}

	/**
	 * Exports the findings of a meeting to a CSV file.
	 * 
	 * @param filePath
	 *            the file path
	 * @param csvProfile
	 *            the CSV profile
	 * @param meeting
	 *            the meeting
	 * @param severityMappings
	 *            the severity mappings
	 * @param reporter
	 *            the bug reporter
	 * 
	 * @throws ExportException
	 *             If an error occurs while exporting the findings
	 * @throws ApplicationException
	 *             If the given meeting has no protocol
	 */
	public File exportMeetingFindingsCSV(String filePath,
			AppCSVProfile csvProfile, Meeting meeting,
			Map<String, String> severityMappings, String reporter)
			throws ExportException, ApplicationException {
		if (!filePath.trim().equals("")
				&& !filePath.toLowerCase().trim().endsWith(".txt")
				&& !filePath.toLowerCase().trim().endsWith(".csv")) {
			filePath = filePath + ".csv";
		}

		if (meeting.getProtocol() != null) {
			CSVExporter exporter = new FindingsCSVExporter(csvProfile,
					severityMappings, meeting.getProtocol().getFindings(),
					reporter);

			exporter.writeToFile(filePath);
		} else {
			throw new ApplicationException(Data.getInstance().getLocaleStr(
					"message.csvNoFindingsExist"));
		}

		return new File(filePath);
	}

	/**
	 * Exports the findings of a review to a CSV file.
	 * 
	 * @param filePath
	 *            the file path
	 * @param csvProfile
	 *            the CSV profile
	 * @param severityMappings
	 *            the severity mappings
	 * @param reporter
	 *            the bug reporter
	 * 
	 * @throws ExportException
	 *             If an error occurs while exporting the findings
	 * @throws ApplicationException
	 *             If no protocol exists in the current review
	 */
	public File exportReviewFindingsCSV(String filePath,
			AppCSVProfile csvProfile, Map<String, String> severityMappings,
			String reporter) throws ExportException, ApplicationException {
		if (!filePath.trim().equals("")
				&& !filePath.toLowerCase().trim().endsWith(".txt")
				&& !filePath.toLowerCase().trim().endsWith(".csv")) {
			filePath = filePath + ".csv";
		}

		List<Finding> findings = new ArrayList<Finding>();

		for (Meeting m : Data.getInstance().getResiData().getReview()
				.getMeetings()) {
			if (m.getProtocol() != null) {
				findings.addAll(m.getProtocol().getFindings());
			}
		}

		if (findings.size() > 0) {
			CSVExporter exporter = new FindingsCSVExporter(csvProfile,
					severityMappings, findings, reporter);

			exporter.writeToFile(filePath);
		} else {
			throw new ApplicationException(Data.getInstance().getLocaleStr(
					"message.csvNoFindingsExist"));
		}

		return new File(filePath);
	}

	/**
	 * Exports the invitations for a review meeting.
	 * 
	 * @param dirPath
	 *            the dir path for storing the invitations
	 * @param type
	 *            the type of the invitations
	 * @param meeting
	 *            the meeting
	 * @param attendee
	 *            the attendee
	 * @param attachProdExtRefs
	 *            true if the external product references (files) should be part
	 *            of the invitation
	 * 
	 * @throws ExportException
	 *             If an error occurs while storing the invitations
	 * @throws DataException
	 *             If an error occurs while getting the data for the invitation
	 */
	public File exportInvitations(String dirPath, InvitationType type,
			Meeting meeting, Attendee attendee, boolean attachProdExtRefs)
			throws ExportException, DataException {
		List<Attendee> attendees = new ArrayList<Attendee>();
		attendees.add(attendee);

		exportInvitations(dirPath, type, meeting, attendees, attachProdExtRefs);

		return new File(dirPath);
	}

	/**
	 * Exports the invitations for a review meeting.
	 * 
	 * @param dirPath
	 *            the dir path for storing the invitations
	 * @param type
	 *            the type of the invitations
	 * @param meeting
	 *            the meeting
	 * @param attendees
	 *            the attendees
	 * @param attachProdExtRefs
	 *            true if the external product references (files) should be part
	 *            of the invitation
	 * 
	 * @throws ExportException
	 *             If an error occurs while storing the invitations
	 * @throws DataException
	 *             If an error occurs while getting the data for the invitation
	 */
	public File exportInvitations(String dirPath, InvitationType type,
			Meeting meeting, List<Attendee> attendees, boolean attachProdExtRefs)
			throws ExportException, DataException {
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		File directory = new File(dirPath);
		// FileTools.deleteDirectory(directory);
		directory.mkdir();

		String invitationPackName;
		String invitationPackPath;

		for (Attendee att : attendees) {
			invitationPackName = Data.getInstance().getLocaleStr(
					"export.invitationPackPrefix")
					+ " "
					+ sdf.format(new Date().getTime())
					+ " "
					+ att.getName()
					+ " ("
					+ Data.getInstance().getLocaleStr(
							"role." + att.getRole().toString().toLowerCase())
					+ ")";

			switch (type) {
			case PDF:
				invitationPackPath = new File(dirPath, invitationPackName
						+ ".pdf").getAbsolutePath();

				InvitationPDFExporter pdfExporter = new InvitationPDFExporter(
						invitationPackPath, meeting, att, attachProdExtRefs);

				pdfExporter.writeToFile();
				break;

			case ZIP:
				invitationPackPath = new File(dirPath, invitationPackName
						+ ".zip").getAbsolutePath();

				InvitationZIPExporter zipExporter = new InvitationZIPExporter(
						invitationPackPath, meeting, att, attachProdExtRefs);

				zipExporter.writeToFile();
				break;

			case DIRECTORY:
				invitationPackPath = new File(dirPath, invitationPackName)
						.getAbsolutePath();

				InvitationDirExporter dirExporter = new InvitationDirExporter(
						invitationPackPath, meeting, att, attachProdExtRefs);

				dirExporter.writeDir();
				break;

			default:
				break;
			}
		}

		return directory;
	}

}
