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


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.revager.app.Application;
import org.revager.app.ApplicationException;
import org.revager.app.ImportExportControl;
import org.revager.app.ResiFileFilter;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.schema.Aspects;
import org.revager.app.model.schema.Catalog;
import org.revager.app.model.schema.Meeting;
import org.revager.export.ExportException;
import org.revager.io.ResiIOException;
import org.revager.tools.FileTools;

/**
 * This class tests the ApplicationData class and its components.
 * 
 * @author Johannes Wettinger
 * @version 1.0
 */
public class ApplicationTest {

	private static String testdataDirectory = "org/revager/test/testdata/";
	private static String tempDirectory = System.getProperty("user.home")
			+ "/tmp-revager_/";

	private static String exampleReviewZIP = testdataDirectory
			+ "Review Example 1.rev";
	private static String exampleReviewXML = testdataDirectory
			+ "Resi Review Example.xml";
	private static String exampleCatalog = testdataDirectory
			+ "Resi Catalog Example.xml";
	private static String exampleAspects = testdataDirectory
			+ "Resi Aspects Example.xml";

	private static String outputXML = tempDirectory + "output.xml";
	private static String outputPDF = tempDirectory + "output.pdf";
	private static String outputCSV = tempDirectory + "output.csv";
	private static String outputZIP = tempDirectory + "output.zip";
	private static String outputDir = tempDirectory + "output";

	@BeforeClass
	public static void setUp() throws IOException, DataException {
		File tempDir = new File(tempDirectory);

		tempDir.mkdir();

		Data.getInstance().getAppData()
				.setCustomAppDataDirectory(tempDirectory);

		Data.getInstance().getAppData().initialize();
	}

	@Test(expected = ApplicationException.class)
	public void appExceptionWithoutMessage() throws ApplicationException {
		throw new ApplicationException();
	}

	@Test(expected = ApplicationException.class)
	public void appExceptionWithMessage() throws ApplicationException {
		throw new ApplicationException("Test-Meldung");
	}

	@Test
	public void createNewReview() {
		Application.getInstance().getApplicationCtl().newReview();
	}

	@Test
	public void loadAndStoreReview() throws ResiIOException, IOException,
			ApplicationException {
		Application.getInstance().getApplicationCtl().loadReview(
				exampleReviewXML);

		assertEquals("Review der Spezifikation XYZ", Data.getInstance()
				.getResiData().getReview().getName());

		Application.getInstance().getApplicationCtl().storeReview(outputXML);

		Application.getInstance().getApplicationCtl().newReview();

		/*
		 * Load ZIP review
		 */
		Application.getInstance().getApplicationCtl().loadReview(
				exampleReviewZIP);

		assertEquals("Review der Spezifikation XYZ", Data.getInstance()
				.getResiData().getReview().getName());

		Application.getInstance().getApplicationCtl()
				.storeReview(
						outputZIP
								+ "."
								+ Data.getInstance().getResource(
										"fileEndingReviewZIP"));
	}

	@Test(expected = ApplicationException.class)
	public void loadReviewFromAppData() throws ResiIOException, IOException,
			ApplicationException {
		Application.getInstance().getApplicationCtl().loadReview(
				Data.getInstance().getAppData().getAppDataPath() + "test.xml");
	}

	@Test(expected = ApplicationException.class)
	public void storeReviewToAppData() throws ResiIOException, IOException,
			ApplicationException {
		Application.getInstance().getApplicationCtl().storeReview(
				Data.getInstance().getAppData().getAppDataPath() + "test.xml");
	}

	@Test
	public void backupAndRestoreReview() throws ResiIOException, DataException {
		assertFalse(Application.getInstance().getApplicationCtl()
				.isReviewRestorable());

		Application.getInstance().getApplicationCtl().backupReview();

		assertTrue(Application.getInstance().getApplicationCtl()
				.isReviewRestorable());

		Application.getInstance().getApplicationCtl().restoreReview();
	}

	@Test
	public void importAndExportCatalogXML() throws ResiIOException {
		Catalog testCat = Application.getInstance().getImportExportCtl()
				.importCatalogXML(exampleCatalog);

		Application.getInstance().getImportExportCtl().exportCatalogXML(
				outputXML, testCat);
	}

	@Test
	public void importAndExportAspectsXML() throws ResiIOException {
		Aspects testAsp = Application.getInstance().getImportExportCtl()
				.importAspectsXML(exampleAspects);

		Application.getInstance().getImportExportCtl().exportAspectsXML(
				outputXML, testAsp);
	}

	@Test
	public void exportMeetingProtocolPDF() throws ResiIOException, IOException,
			ApplicationException, ExportException, DataException {
		Application.getInstance().getApplicationCtl().loadReview(
				exampleReviewZIP);

		Application.getInstance().getImportExportCtl()
				.exportMeetingProtocolPDF(
						outputPDF,
						Data.getInstance().getResiData().getReview()
								.getMeetings().get(0), true, true, true);
	}

	@Test
	public void exportReviewProtocolPDF() throws ExportException,
			DataException, ResiIOException, IOException, ApplicationException {
		Application.getInstance().getApplicationCtl().loadReview(
				exampleReviewZIP);

		Application.getInstance().getImportExportCtl().exportReviewProtocolPDF(
				outputPDF, true, true, true);
	}

	@Test
	public void exportMeetingFindingsCSV() throws ExportException,
			ApplicationException, DataException, ResiIOException, IOException {
		Application.getInstance().getApplicationCtl().loadReview(
				exampleReviewZIP);

		Application.getInstance().getImportExportCtl()
				.exportMeetingFindingsCSV(
						outputCSV,
						Data.getInstance().getAppData().getCSVProfile("Trac"),
						Data.getInstance().getResiData().getReview()
								.getMeetings().get(0), null, "Hans Moddi");
	}

	@Test(expected = ApplicationException.class)
	public void exportMeetingFindingsCSVEmpty() throws ExportException,
			ApplicationException, DataException, ResiIOException, IOException {
		Application.getInstance().getApplicationCtl().loadReview(
				exampleReviewZIP);

		Meeting meet = Data.getInstance().getResiData().getReview()
				.getMeetings().get(0);
		meet.setProtocol(null);

		Application.getInstance().getImportExportCtl()
				.exportMeetingFindingsCSV(outputCSV,
						Data.getInstance().getAppData().getCSVProfile("Trac"),
						meet, null, "Hans Moddi");
	}

	@Test
	public void exportReviewFindingsCSV() throws ResiIOException, IOException,
			ApplicationException, ExportException, DataException {
		Application.getInstance().getApplicationCtl().loadReview(
				exampleReviewZIP);

		Application.getInstance().getImportExportCtl().exportReviewFindingsCSV(
				outputCSV,
				Data.getInstance().getAppData().getCSVProfile("Trac"), null,
				"Hans Moddi");
	}

	@Test(expected = ApplicationException.class)
	public void exportReviewFindingsCSVEmpty() throws ResiIOException,
			IOException, ApplicationException, ExportException, DataException {
		Application.getInstance().getApplicationCtl().loadReview(
				exampleReviewZIP);

		for (Meeting m : Data.getInstance().getResiData().getReview()
				.getMeetings()) {
			m.setProtocol(null);
		}

		Application.getInstance().getImportExportCtl().exportReviewFindingsCSV(
				outputCSV,
				Data.getInstance().getAppData().getCSVProfile("Trac"), null,
				"Hans Moddi");
	}

	@Test
	public void exportInvitationsPDF() throws ExportException, DataException,
			ResiIOException, IOException, ApplicationException {
		Application.getInstance().getApplicationCtl().loadReview(
				exampleReviewZIP);

		Application.getInstance().getImportExportCtl().exportInvitations(
				outputDir,
				ImportExportControl.InvitationType.PDF,
				Data.getInstance().getResiData().getReview().getMeetings().get(
						0),
				Data.getInstance().getResiData().getReview().getAttendees()
						.get(0), true);
	}

	@Test
	public void exportInvitationsDir() throws ResiIOException, IOException,
			ApplicationException, ExportException, DataException {
		Application.getInstance().getApplicationCtl().loadReview(
				exampleReviewZIP);

		Application.getInstance().getImportExportCtl().exportInvitations(
				outputDir,
				ImportExportControl.InvitationType.DIRECTORY,
				Data.getInstance().getResiData().getReview().getMeetings().get(
						0),
				Data.getInstance().getResiData().getReview().getAttendees()
						.get(0), true);
	}

	@Test
	public void exportInvitationsZIP() throws ExportException, DataException,
			ResiIOException, IOException, ApplicationException {
		Application.getInstance().getApplicationCtl().loadReview(
				exampleReviewZIP);

		Application.getInstance().getImportExportCtl().exportInvitations(
				outputDir,
				ImportExportControl.InvitationType.ZIP,
				Data.getInstance().getResiData().getReview().getMeetings().get(
						0),
				Data.getInstance().getResiData().getReview().getAttendees()
						.get(0), true);
	}

	@Test
	public void testResiFileFilter() {
		File homeDir = new File(System.getProperty("user.home"));
		File directory = new File("/ein/Verzeichnis/");
		ResiFileFilter ff;

		ff = new ResiFileFilter(ResiFileFilter.TYPE_ALL);
		assertTrue(ff.accept(directory, "test.doc"));
		assertTrue(ff.accept(directory, "test"));
		assertTrue(ff.accept(directory, "test.blabla"));
		ff.getDescription();

		ff = new ResiFileFilter(ResiFileFilter.TYPE_ASPECTS);
		assertTrue(ff.accept(directory, "test."
				+ Data.getInstance().getResource("fileEndingAspects")));
		assertTrue(ff.accept(directory, "test.xml"));
		assertFalse(ff.accept(directory, "test.csv"));
		ff.getDescription();

		ff = new ResiFileFilter(ResiFileFilter.TYPE_CATALOG);
		assertTrue(ff.accept(directory, "test."
				+ Data.getInstance().getResource("fileEndingCatalog")));
		assertTrue(ff.accept(directory, "test.xml"));
		assertFalse(ff.accept(directory, "test.csv"));
		ff.getDescription();

		ff = new ResiFileFilter(ResiFileFilter.TYPE_CSV);
		assertTrue(ff.accept(directory, "test.csv"));
		assertTrue(ff.accept(directory, "test.txt"));
		assertFalse(ff.accept(directory, "test.xml"));
		ff.getDescription();

		ff = new ResiFileFilter(ResiFileFilter.TYPE_DIRECTORY);
		assertTrue(ff.accept(homeDir, ""));
		assertFalse(ff.accept(directory, "test.xml"));
		ff.getDescription();

		ff = new ResiFileFilter(ResiFileFilter.TYPE_PDF);
		assertTrue(ff.accept(directory, "test.pdf"));
		assertFalse(ff.accept(directory, "test.xml"));
		ff.getDescription();

		ff = new ResiFileFilter(ResiFileFilter.TYPE_ZIP);
		assertTrue(ff.accept(directory, "test.zip"));
		assertFalse(ff.accept(directory, "test.xml"));
		ff.getDescription();

		ff = new ResiFileFilter(ResiFileFilter.TYPE_REVIEW);
		assertTrue(ff.accept(directory, "test."
				+ Data.getInstance().getResource("fileEndingReviewXML")));
		assertTrue(ff.accept(directory, "test."
				+ Data.getInstance().getResource("fileEndingReviewZIP")));
		assertTrue(ff.accept(directory, "test.xml"));
		assertFalse(ff.accept(directory, "test.csv"));
		ff.getDescription();

		/*
		 * Invalid type accepts all
		 */
		ff = new ResiFileFilter(9999999);
		assertTrue(ff.accept(directory, "test.zip"));
		ff.getDescription();
	}

	@AfterClass
	public static void cleanUp() {
		FileTools.deleteDirectory(new File(Data.getInstance().getAppData()
				.getAppDataPath()));

		new File(outputPDF).delete();
		new File(outputCSV).delete();
		new File(outputZIP).delete();
		FileTools.deleteDirectory(new File(outputDir));

		FileTools.deleteDirectory(new File(tempDirectory));
	}

}
