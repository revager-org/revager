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
package neos.resi.test;

import static org.junit.Assert.*;

import java.io.File;

import neos.resi.app.model.Data;
import neos.resi.app.model.DataException;
import neos.resi.app.model.ResiData;
import neos.resi.app.model.schema.Aspects;
import neos.resi.app.model.schema.Catalog;
import neos.resi.app.model.schema.Review;
import neos.resi.io.ResiIO;
import neos.resi.io.ResiIOException;
import neos.resi.io.ResiIOFactory;
import neos.resi.tools.FileTools;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class tests the IO provider of Resi.
 * 
 * @author Johannes Wettinger
 * @version 1.0
 */
public class ResiIOTest {

	private static ResiIO io;

	private static ResiData resiData = Data.getInstance().getResiData();

	private static String testDirectory = "neos/resi/test/testdata/";

	private static String outputXML = testDirectory + "output.xml";

	private static String reviewExample = testDirectory
			+ "Resi Review Example.xml";
	private static String reviewExampleIncomplete = testDirectory
			+ "Resi Review Example incomplete.xml";
	private static String reviewExampleIncorrect = testDirectory
			+ "Resi Review Example incorrect.xml";

	private static String catalogExample = testDirectory
			+ "Resi Catalog Example.xml";
	private static String catalogExampleIncorrect = testDirectory
			+ "Resi Catalog Example incorrect.xml";

	private static String aspectsExample = testDirectory
			+ "Resi Aspects Example.xml";
	private static String aspectsExampleIncorrect = testDirectory
			+ "Resi Aspects Example incorrect.xml";

	@BeforeClass
	public static void setUp() throws DataException {
		Data.getInstance().getAppData()
				.setCustomAppDataDirectory(testDirectory);

		Data.getInstance().getAppData().resetDatabase();

		Data.getInstance().getAppData().initialize();
	}

	@Test
	public void loadIOProvider() {
		io = ResiIOFactory.getInstance().getIOProvider();
	}

	@Test
	public void loadCompleteReview() throws ResiIOException {
		io.loadReview(reviewExample);

		/*
		 * Check some values of the example
		 */
		assertEquals("Review der Spezifikation XYZ", resiData.getReview()
				.getName());
		assertEquals(
				"Es wird die Spezifikation XYZ des Projekts ABC begutachtet.",
				resiData.getReview().getDescription());
		assertEquals("Spezifikation XYZ", resiData.getReview().getProduct()
				.getName());

		/*
		 * Check if path is ok
		 */
		assertEquals(reviewExample, resiData.getReviewPath());
	}

	@Test
	public void storeCompleteReview() throws ResiIOException {
		io.loadReview(reviewExample);

		resiData.getReview().setName("Test Output");

		io.storeReview(outputXML);

		/*
		 * Check if storing the review was ok
		 */
		io.loadReview(outputXML);

		assertEquals("Test Output", resiData.getReview().getName());
		assertEquals(
				"Es wird die Spezifikation XYZ des Projekts ABC begutachtet.",
				resiData.getReview().getDescription());
		assertEquals("Spezifikation XYZ", resiData.getReview().getProduct()
				.getName());
	}

	@Test(expected = ResiIOException.class)
	public void loadIncompleteReview() throws ResiIOException {
		io.loadReview(reviewExampleIncomplete);
	}

	@Test(expected = ResiIOException.class)
	public void storeIncompleteReview() throws ResiIOException {
		io.loadReview(reviewExample);

		resiData.getReview().setName(null);

		io.storeReview(outputXML);
	}

	@Test(expected = ResiIOException.class)
	public void loadIncorrectReview() throws ResiIOException {
		io.loadReview(reviewExampleIncorrect);
	}

	@Test(expected = ResiIOException.class)
	public void loadReviewAsBackupWithoutBackupedBefore()
			throws ResiIOException {
		io.loadReviewBackup();
	}

	@Test
	public void storeAndLoadCompleteReviewAsBackup() throws ResiIOException {
		io.loadReview(reviewExample);

		io.storeReviewBackup();

		/*
		 * Reset ResiData model
		 */
		resiData.setReview(null);
		resiData.setReviewPath(null);

		io.loadReviewBackup();

		assertEquals(reviewExample, resiData.getReviewPath());
		assertEquals("Review der Spezifikation XYZ", resiData.getReview()
				.getName());
		assertEquals("Spezifikation XYZ", resiData.getReview().getProduct()
				.getName());
	}

	@Test
	public void storeAndLoadIncompleteReviewAsBackup() throws ResiIOException {
		io.loadReview(reviewExample);

		resiData.getReview().setName(null);

		io.storeReviewBackup();

		/*
		 * Reset ResiData model
		 */
		resiData.setReview(null);
		resiData.setReviewPath(null);

		io.loadReviewBackup();

		assertEquals(reviewExample, resiData.getReviewPath());
		assertEquals(null, resiData.getReview().getName());
		assertEquals("Spezifikation XYZ", resiData.getReview().getProduct()
				.getName());
	}

	@Test
	public void storeAndLoadNearlyEmptyReviewAsBackup() throws ResiIOException {
		/*
		 * Reset ResiData model
		 */
		resiData.setReview(new Review());
		resiData.setReviewPath(null);

		resiData.getReview().setName("Fast leeres Review");

		io.storeReviewBackup();

		/*
		 * Reset ResiData model
		 */
		resiData.setReview(null);
		resiData.setReviewPath(null);

		io.loadReviewBackup();

		assertEquals("", resiData.getReviewPath());
		assertEquals("Fast leeres Review", resiData.getReview().getName());
	}

	@Test
	public void loadCatalog() throws ResiIOException {
		io.loadCatalog(catalogExample);

		assertEquals(catalogExample, resiData.getCatalogPath());
		assertEquals("Rules of Software Project Management", resiData
				.getCatalog().getDescription());
	}

	@Test(expected = ResiIOException.class)
	public void loadIncorrectCatalog() throws ResiIOException {
		io.loadCatalog(catalogExampleIncorrect);
	}

	@Test
	public void modifyAndStoreCatalog() throws ResiIOException {
		/*
		 * Reset ResiData model
		 */
		resiData.setCatalog(null);
		resiData.setCatalogPath(null);

		io.loadCatalog(catalogExample);

		resiData.getCatalog().setDescription(
				"Einige Regeln für das Projektmanagement im Softwarebereich");

		assertEquals(
				"Einige Regeln für das Projektmanagement im Softwarebereich",
				resiData.getCatalog().getDescription());

		io.storeCatalog(outputXML);
	}

	@Test(expected = ResiIOException.class)
	public void storeIncorrectCatalog() throws ResiIOException {
		resiData.setCatalog(new Catalog());

		io.storeCatalog(outputXML);
	}

	@Test
	public void loadAspects() throws ResiIOException {
		io.loadAspects(aspectsExample);

		assertEquals(aspectsExample, resiData.getAspectsPath());
		assertEquals("Grafische Oberfläche", resiData.getAspects().getAspects()
				.get(0).getCategory());
	}

	@Test(expected = ResiIOException.class)
	public void loadIncorrectAspects() throws ResiIOException {
		io.loadAspects(aspectsExampleIncorrect);
	}

	@Test
	public void modifyAndStoreAspects() throws ResiIOException {
		/*
		 * Reset ResiData model
		 */
		resiData.setAspects(null);
		resiData.setAspectsPath(null);

		io.loadAspects(aspectsExample);

		resiData.getAspects().getAspects().get(0).setDirective(
				"Ist alles on Ordnung?");

		assertEquals("Ist alles on Ordnung?", resiData.getAspects()
				.getAspects().get(0).getDirective());

		io.storeAspects(outputXML);
	}

	@Test(expected = ResiIOException.class)
	public void storeIncorrectAspects() throws ResiIOException {
		resiData.setAspects(new Aspects());

		io.storeAspects(outputXML);
	}

	@Test(expected = ResiIOException.class)
	public void throwExceptionWithoutMessage() throws ResiIOException {
		throw new ResiIOException();
	}

	@AfterClass
	public static void cleanUp() {
		FileTools.deleteDirectory(new File(Data.getInstance().getAppData()
				.getAppDataPath()));

		new File(outputXML).delete();
	}

}
