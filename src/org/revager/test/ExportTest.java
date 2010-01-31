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
package org.revager.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.revager.app.Application;
import org.revager.app.ApplicationException;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.appdata.AppCSVProfile;
import org.revager.app.model.appdata.AppSettingKey;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.Meeting;
import org.revager.app.model.schema.Role;
import org.revager.export.ExportException;
import org.revager.export.FindingsCSVExporter;
import org.revager.export.InvitationDirExporter;
import org.revager.export.InvitationPDFExporter;
import org.revager.export.InvitationZIPExporter;
import org.revager.export.MeetingProtocolPDFExporter;
import org.revager.export.ProtocolPDFExporter;
import org.revager.export.ReviewProtocolPDFExporter;
import org.revager.io.ResiIOException;
import org.revager.tools.FileTools;

/**
 * This class tests the Export component (classes in org.revager.export package).
 * 
 * @author Johannes Wettinger
 * @version 1.0
 */
public class ExportTest {

	private static String testdataDirectory = "org/revager/test/testdata/";

	private static String exampleReview = testdataDirectory
			+ "Review Example 1.rev";

	private static String outputPDF = testdataDirectory + "output.pdf";
	private static String outputCSV = testdataDirectory + "output.csv";
	private static String outputZIP = testdataDirectory + "output.zip";
	private static String outputDir = testdataDirectory + "output";

	private static Meeting meeting;
	private static Attendee reviewer;
	private static Attendee scribe;
	private static Attendee moderator;

	private static AppCSVProfile csvProfile;

	@BeforeClass
	public static void setUp() throws DataException, ResiIOException,
			IOException, ApplicationException {
		/*
		 * Set Application data and load example review
		 */
		Data.getInstance().getAppData().setCustomAppDataDirectory(
				testdataDirectory);

		Data.getInstance().getAppData().resetDatabase();

		Data.getInstance().getAppData().initialize();

		Application.getInstance().getApplicationCtl().loadReview(exampleReview);

		/*
		 * Set application settings
		 */
		Data.getInstance().getAppData().setSetting(
				AppSettingKey.PDF_PROTOCOL_FOOT_TEXT,
				"(c) 2009 by Universität Stuttgart, Abteilung SE");
		Data.getInstance().getAppData().setSetting(
				AppSettingKey.PDF_PROTOCOL_LOGO,
				"org/revager/test/testdata/Logo SE.jpg");

		Data.getInstance().getAppData().setSetting(
				AppSettingKey.PDF_INVITATION_FOOT_TEXT,
				"(c) 2009 by Universität Stuttgart, Abteilung SE");
		Data.getInstance().getAppData().setSetting(
				AppSettingKey.PDF_INVITATION_LOGO,
				"org/revager/test/testdata/Logo SE.jpg");

		/*
		 * Set test meeting and attendees
		 */
		meeting = Data.getInstance().getResiData().getReview().getMeetings()
				.get(2);
		reviewer = Data.getInstance().getResiData().getReview().getAttendees()
				.get(0);
		scribe = Data.getInstance().getResiData().getReview().getAttendees()
				.get(1);
		moderator = Data.getInstance().getResiData().getReview().getAttendees()
				.get(2);

		/*
		 * default csv profile
		 */
		csvProfile = Data.getInstance().getAppData().getCSVProfile("Trac");
	}

	@Test(expected = ExportException.class)
	public void exportExceptionWithoutMessage() throws ExportException {
		throw new ExportException();
	}

	@Test(expected = ExportException.class)
	public void exportExceptionWithMessage() throws ExportException {
		throw new ExportException("Test-Meldung");
	}

	@Test
	public void createReviewProtocols() throws ExportException, DataException {
		ProtocolPDFExporter exporter;

		exporter = new ReviewProtocolPDFExporter(outputPDF, true, true, true);
		exporter.writeToFile();

		exporter = new ReviewProtocolPDFExporter(outputPDF, true, true, false);
		exporter.writeToFile();

		exporter = new ReviewProtocolPDFExporter(outputPDF, true, false, true);
		exporter.writeToFile();

		exporter = new ReviewProtocolPDFExporter(outputPDF, true, false, false);
		exporter.writeToFile();

		exporter = new ReviewProtocolPDFExporter(outputPDF, false, true, true);
		exporter.writeToFile();

		exporter = new ReviewProtocolPDFExporter(outputPDF, false, true, false);
		exporter.writeToFile();

		exporter = new ReviewProtocolPDFExporter(outputPDF, false, false, true);
		exporter.writeToFile();

		exporter = new ReviewProtocolPDFExporter(outputPDF, false, false, false);
		exporter.writeToFile();

		/*
		 * Modify head and foot of the protocol pdf files: Delete logo and foot
		 * text.
		 * 
		 * Delete description, comments, recommendation and impression of the
		 * review.
		 * 
		 * Delete comments of meeting and protocol.
		 */
		Data.getInstance().getAppData().setSetting(
				AppSettingKey.PDF_PROTOCOL_FOOT_TEXT, "");
		Data.getInstance().getAppData().setSetting(
				AppSettingKey.PDF_PROTOCOL_LOGO, "");
		
		Data.getInstance().getResiData().getReview().setComments("");
		Data.getInstance().getResiData().getReview().setDescription("");
		Data.getInstance().getResiData().getReview().setRecommendation("");
		Data.getInstance().getResiData().getReview().setImpression("");
		
		meeting.setComments("");
		meeting.getProtocol().setComments("");
		
		exporter = new ReviewProtocolPDFExporter(outputPDF, true, true, true);
		exporter.writeToFile();
	}

	@Test
	public void createMeetingProtocols() throws ExportException, DataException {
		Meeting meeting = Data.getInstance().getResiData().getReview()
				.getMeetings().get(0);
		ProtocolPDFExporter exporter;

		exporter = new MeetingProtocolPDFExporter(outputPDF, meeting, true,
				true, true);
		exporter.writeToFile();

		exporter = new MeetingProtocolPDFExporter(outputPDF, meeting, true,
				true, false);
		exporter.writeToFile();

		exporter = new MeetingProtocolPDFExporter(outputPDF, meeting, true,
				false, true);
		exporter.writeToFile();

		exporter = new MeetingProtocolPDFExporter(outputPDF, meeting, true,
				false, false);
		exporter.writeToFile();

		exporter = new MeetingProtocolPDFExporter(outputPDF, meeting, false,
				true, true);
		exporter.writeToFile();

		exporter = new MeetingProtocolPDFExporter(outputPDF, meeting, false,
				true, false);
		exporter.writeToFile();

		exporter = new MeetingProtocolPDFExporter(outputPDF, meeting, false,
				false, true);
		exporter.writeToFile();

		exporter = new MeetingProtocolPDFExporter(outputPDF, meeting, false,
				false, false);
		exporter.writeToFile();
		
		/*
		 * Set meeting as planned.
		 */
		meeting.getProtocol().setDate(meeting.getPlannedDate());
		meeting.getProtocol().setStart(meeting.getPlannedStart());
		meeting.getProtocol().setEnd(meeting.getPlannedEnd());
		meeting.getProtocol().setLocation(meeting.getPlannedLocation());
		
		exporter = new MeetingProtocolPDFExporter(outputPDF, meeting, true,
				true, true);
		exporter.writeToFile();
	}

	@Test
	public void createInvitationsAsPDF() throws ExportException, DataException {
		InvitationPDFExporter exporter;

		exporter = new InvitationPDFExporter(outputPDF, meeting, reviewer, true);
		exporter.writeToFile();

		exporter = new InvitationPDFExporter(outputPDF, meeting, reviewer,
				false);
		exporter.writeToFile();

		exporter = new InvitationPDFExporter(outputPDF, meeting, scribe, true);
		exporter.writeToFile();

		exporter = new InvitationPDFExporter(outputPDF, meeting, scribe, false);
		exporter.writeToFile();

		exporter = new InvitationPDFExporter(outputPDF, meeting, moderator,
				true);
		exporter.writeToFile();

		exporter = new InvitationPDFExporter(outputPDF, meeting, moderator,
				false);
		exporter.writeToFile();

		/*
		 * Add a second moderator to the review and create an invitation
		 */
		Attendee secondMod = new Attendee();
		secondMod.setContact("irgendeine Kontaktadresse");
		secondMod.setName("Hans Moddi");
		secondMod.setRole(Role.MODERATOR);

		Application.getInstance().getAttendeeMgmt().addAttendee(secondMod);

		exporter = new InvitationPDFExporter(outputPDF, meeting, reviewer, true);
		exporter.writeToFile();
	}

	@Test
	public void createInvitationsAsZIP() throws ExportException, DataException {
		InvitationZIPExporter exporter;

		exporter = new InvitationZIPExporter(outputZIP, meeting, reviewer, true);
		exporter.writeToFile();

		exporter = new InvitationZIPExporter(outputZIP, meeting, reviewer,
				false);
		exporter.writeToFile();

		exporter = new InvitationZIPExporter(outputZIP, meeting, scribe, true);
		exporter.writeToFile();

		exporter = new InvitationZIPExporter(outputZIP, meeting, scribe, false);
		exporter.writeToFile();

		exporter = new InvitationZIPExporter(outputZIP, meeting, moderator,
				true);
		exporter.writeToFile();

		exporter = new InvitationZIPExporter(outputZIP, meeting, moderator,
				false);
		exporter.writeToFile();
	}

	@Test
	public void createInvitationsAsDir() throws ExportException, DataException {
		InvitationDirExporter exporter;

		exporter = new InvitationDirExporter(outputDir, meeting, reviewer, true);
		exporter.writeDir();

		exporter = new InvitationDirExporter(outputDir, meeting, reviewer,
				false);
		exporter.writeDir();

		exporter = new InvitationDirExporter(outputDir, meeting, scribe, true);
		exporter.writeDir();

		exporter = new InvitationDirExporter(outputDir, meeting, scribe, false);
		exporter.writeDir();

		exporter = new InvitationDirExporter(outputDir, meeting, moderator,
				true);
		exporter.writeDir();

		exporter = new InvitationDirExporter(outputDir, meeting, moderator,
				false);
		exporter.writeDir();
	}

	@Test
	public void createFindingsListAsCSV() throws DataException, ExportException {
		Map<String, String> sevMap = new HashMap<String, String>();
		sevMap.put("Hauptfehler", "major");

		FindingsCSVExporter exporter = new FindingsCSVExporter(csvProfile,
				null, meeting.getProtocol().getFindings(), "Hans Moddi");
		exporter.addSeverityMapping("Nebenfehler", "minor");
		exporter.addSeverityMapping("Hauptfehler", "major");

		exporter.writeToFile(outputCSV);

		assertEquals(4, exporter.getColumns());

		exporter.setColumns(-1);
		assertEquals(1, exporter.getColumns());
		exporter.setColumns(4);

		exporter.setSeparator(null);
		assertEquals(",", exporter.getSeparator());
		exporter.setSeparator(";");

		exporter.writeToFile(outputCSV);

		exporter.setEncapsulator(null);
		assertNull(exporter.getEncapsulator());
		exporter.setEncapsulator("'");
		assertEquals("'", exporter.getEncapsulator());

		exporter.writeToFile(outputCSV);

		exporter.setEncapsulator("\"");
		assertEquals("\"", exporter.getEncapsulator());

		exporter.writeToFile(outputCSV);

		/*
		 * Modify csv profile
		 */
		csvProfile.setEncapsulateContent(true);

		exporter = new FindingsCSVExporter(csvProfile, sevMap, meeting
				.getProtocol().getFindings(), "Hans Moddi");
	}

	@AfterClass
	public static void cleanUp() {
		FileTools.deleteDirectory(new File(Data.getInstance().getAppData()
				.getAppDataPath()));

		new File(outputPDF).delete();
		new File(outputCSV).delete();
		new File(outputZIP).delete();
		FileTools.deleteDirectory(new File(outputDir));
	}

}
