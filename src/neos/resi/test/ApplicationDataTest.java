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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import neos.resi.app.model.ApplicationData;
import neos.resi.app.model.Data;
import neos.resi.app.model.DataException;
import neos.resi.app.model.appdata.AppAspect;
import neos.resi.app.model.appdata.AppAttendee;
import neos.resi.app.model.appdata.AppCSVColumnName;
import neos.resi.app.model.appdata.AppCSVProfile;
import neos.resi.app.model.appdata.AppCatalog;
import neos.resi.app.model.appdata.AppSettingKey;
import neos.resi.app.model.appdata.AppSettingValue;
import neos.resi.app.model.schema.Aspect;
import neos.resi.app.model.schema.Attendee;
import neos.resi.tools.FileTools;

import org.junit.AfterClass;
import org.junit.Test;

/**
 * This class tests the ApplicationData class and its components.
 * 
 * @author Johannes Wettinger
 * @version 1.0
 */
public class ApplicationDataTest {

	private static ApplicationData appData;

	@Test
	public void resetAndInitializeDatabase() throws SQLException, DataException {
		appData = Data.getInstance().getAppData();

		assertEquals("", appData.getAppDataPath());

		appData.setCustomAppDataDirectory("neos/resi/test/");
		appData.setCustomAppDataDirectory("neos/resi/test");
		assertEquals("neos/resi/test/", appData.getCustomAppDataDirectory());

		appData.initialize();

		assertTrue(appData.getAppDataPath().startsWith("neos/resi/test/"));

		appData.resetDatabase();

		appData.initialize();
	}

	@Test
	public void tryToNotifyObservers() {
		appData.fireDataChanged();
	}

	@Test
	public void getUnsetAppSetting() throws DataException {
		String settingStr = appData
				.getSetting(AppSettingKey.APP_LAST_REVIEW_PATH);
		assertNull(settingStr);

		AppSettingValue settingVal = appData
				.getSettingValue(AppSettingKey.APP_LAST_REVIEW_PATH);
		assertNull(settingVal);
	}

	@Test
	public void setUnsetAppSetting() throws DataException {
		appData.setSetting(AppSettingKey.PDF_INVITATION_FOOT_TEXT,
				"some foot text...");
	}

	@Test
	public void getAndSetAppSettings() throws DataException {
		/*
		 * Test AppSettingValue
		 */
		appData.setSettingValue(AppSettingKey.APP_DO_AUTO_SAVE,
				AppSettingValue.FALSE);
		AppSettingValue setting1 = appData
				.getSettingValue(AppSettingKey.APP_DO_AUTO_SAVE);
		assertEquals(AppSettingValue.FALSE, setting1);

		/*
		 * Test String
		 */
		appData.setSetting(AppSettingKey.PDF_INVITATION_TEXT, "Hallo Welt");
		String setting2String = appData
				.getSetting(AppSettingKey.PDF_INVITATION_TEXT);
		assertEquals("Hallo Welt", setting2String);

		/*
		 * Test combination
		 */
		AppSettingValue setting2Value = appData
				.getSettingValue(AppSettingKey.PDF_INVITATION_TEXT);
		assertEquals(AppSettingValue.INVALID_VALUE, setting2Value);
	}

	@Test
	public void addAndRemoveLastReviews() throws DataException {
		String review1 = "/home/jojo/Review_no1.rev";
		String review2 = "/home/tester/review_2.rev";
		String review3 = "/tmp/Review_temp.rev";

		/*
		 * Add reviews
		 */
		appData.addLastReview(review1);
		appData.addLastReview(review2);
		appData.addLastReview(review3);
		appData.addLastReview(review1);

		Iterator<String> iter = appData.getLastReviews().iterator();

		assertEquals(review1, iter.next());
		assertEquals(review3, iter.next());
		assertEquals(review2, iter.next());

		/*
		 * Remove review
		 */
		appData.removeLastReview(review3);

		iter = appData.getLastReviews().iterator();

		assertEquals(review1, iter.next());
		assertEquals(review2, iter.next());
	}

	@Test
	public void getEmptyCatalogList() throws DataException {
		assertTrue(appData.getCatalogs().isEmpty());
		assertEquals(0, appData.getNumberOfCatalogs());
	}

	@Test
	public void removeAllCatalogData() throws DataException {
		for (AppCatalog catalog : appData.getCatalogs()) {
			for (String category : catalog.getCategories()) {
				for (AppAspect aspect : catalog.getAspects(category)) {
					catalog.removeAspect(aspect);
				}
			}

			appData.removeCatalog(catalog);
		}

		assertTrue(appData.getCatalogs().isEmpty());
		assertEquals(0, appData.getNumberOfCatalogs());
	}

	@Test
	public void getAndSetNameOfCatalog() throws DataException {
		AppCatalog cat = appData.newCatalog("Testkatalog Nr. 1");
		cat.setName("Neuer Name");

		assertEquals("Neuer Name", cat.getName());
		assertEquals("Neuer Name", appData.getCatalog("Neuer Name").getName());
	}

	@Test(expected = DataException.class)
	public void setExistingNameOfCatalog() throws DataException {
		removeAllCatalogData();

		AppCatalog cat1 = appData.newCatalog("Testkatalog Nr. 1");
		AppCatalog cat2 = appData.newCatalog("Testkatalog Nr. 2");

		cat1.setName(cat2.getName());
	}

	@Test
	public void addAndRemoveCatalogs() throws DataException {
		removeAllCatalogData();

		String catalog1 = "Testkatalog Nr. 1";
		String catalog2 = "Weiterer Testkatalog";
		String catalog3 = "Testkatalog der dritte";
		String catalog4 = "Unbekannter Katalog";

		/*
		 * Add catalogs
		 */
		appData.newCatalog(catalog1);
		appData.newCatalog(catalog2);
		appData.newCatalog(catalog3);

		assertEquals(3, appData.getNumberOfCatalogs());

		/*
		 * Search for an unknown catalog
		 */
		assertNull(appData.getCatalog(catalog4));

		/*
		 * Test if catalogs are in the list
		 */
		Iterator<AppCatalog> iter = appData.getCatalogs().iterator();

		assertEquals(catalog1, iter.next().getName());
		assertEquals(catalog2, iter.next().getName());
		assertEquals(catalog3, iter.next().getName());

		/*
		 * Remove catalog
		 */
		appData.removeCatalog(catalog2);

		assertNull(appData.getCatalog(catalog2));
		assertEquals(2, appData.getNumberOfCatalogs());

		iter = appData.getCatalogs().iterator();

		assertEquals(catalog1, iter.next().getName());
		assertEquals(catalog3, iter.next().getName());

		/*
		 * Get sorting positions of catalogs
		 */
		assertEquals(1, appData.getFirstSortPosOfCatalogs());
		assertEquals(3, appData.getLastSortPosOfCatalogs());
	}

	@Test
	public void pushCatalogs() throws DataException {
		removeAllCatalogData();

		AppCatalog catalog1 = appData.newCatalog("Testkatalog Nr. 1");
		AppCatalog catalog2 = appData.newCatalog("Weiterer Katalog");
		AppCatalog catalog3 = appData.newCatalog("Testkatalog der dritte");
		AppCatalog catalog4 = appData.newCatalog("Letzter Katalog");

		/*
		 * Test the order of catalogs
		 */
		Iterator<AppCatalog> iter = appData.getCatalogs().iterator();

		assertEquals(catalog1, iter.next());
		assertEquals(catalog2, iter.next());
		assertEquals(catalog3, iter.next());
		assertEquals(catalog4, iter.next());

		/*
		 * Test the filter for getting catalogs
		 */
		iter = appData.getCatalogs("testkatalo").iterator();

		assertEquals(catalog1, iter.next());
		assertEquals(catalog3, iter.next());

		/*
		 * Push some catalogs
		 */
		catalog4.pushUp();
		catalog1.pushDown();

		/*
		 * Test the order of catalogs
		 */
		iter = appData.getCatalogs().iterator();

		assertEquals(catalog2, iter.next());
		assertEquals(catalog1, iter.next());
		assertEquals(catalog4, iter.next());
		assertEquals(catalog3, iter.next());

		/*
		 * Push some catalogs
		 */
		catalog1.pushBottom();
		catalog3.pushTop();

		/*
		 * Test the order of catalogs
		 */
		iter = appData.getCatalogs().iterator();

		assertEquals(catalog3, iter.next());
		assertEquals(catalog2, iter.next());
		assertEquals(catalog4, iter.next());
		assertEquals(catalog1, iter.next());
	}

	@Test(expected = DataException.class)
	public void setExistingNameOfCategory() throws DataException {
		removeAllCatalogData();

		AppCatalog catalog = appData.newCatalog("Testkatalog Nr. 1");

		catalog.newAspect("Direktive", "Beschreibung", "Eine Kategorie");
		catalog.newAspect("Direktive", "Beschreibung", "Andere Kategorie");

		catalog.editCategory("Eine Kategorie", "Andere Kategorie");
	}

	@Test
	public void modifyAndManageCatalog() throws DataException {
		removeAllCatalogData();

		AppCatalog catalog = appData.newCatalog("Testkatalog Nr. 1");

		/*
		 * Add aspects to the catalog
		 */
		AppAspect aspect1 = catalog
				.newAspect(
						"Positionierung der Buttons prüfen",
						"Vor allem die Andordnung der Buttons soll auf Usability geprüft werden.",
						"GUI");
		AppAspect aspect2 = catalog
				.newAspect(
						"Lesbarkeit der Schrift prüfen",
						"Die Schriftgröße, Schriftart sowie die Schriftfarbe sollen überprüft werden.",
						"GUI");
		AppAspect aspect3 = catalog.newAspect("Tue dies und das",
				"Beschreibung, was zu tun ist.", "Neue Kategorie");
		AppAspect aspect4 = catalog
				.newAspect(
						"Skalierbarkeit der Fenster prüfen",
						"Es soll überprüft werden, ob die Größe der Fenster gut skalierbar sind.",
						"GUI");
		AppAspect aspect5 = catalog.newAspect("Direktive", "Beschreibung",
				"Weitere Kategorie");
		AppAspect aspect6 = catalog.newAspect("Direktive", "Beschreibung",
				"Noch eine Kategorie");

		/*
		 * Test the toString() methods
		 */
		assertEquals("Testkatalog Nr. 1", catalog.toString());
		assertEquals("Skalierbarkeit der Fenster prüfen", aspect4.toString());

		/*
		 * Check number of categories and number of aspects
		 */
		assertEquals(4, catalog.getNumberOfCategories());
		assertEquals(6, catalog.getNumberOfAspects());
		assertEquals(3, catalog.getNumberOfAspects("GUI"));
		assertEquals(1, catalog.getNumberOfAspects("Neue Kategorie"));
		assertEquals(1, catalog.getNumberOfAspects("Weitere Kategorie"));
		assertEquals(1, catalog.getNumberOfAspects("Noch eine Kategorie"));

		/*
		 * Try to get filtered categories and aspects
		 */
		assertEquals(3, catalog.getCategories("Kategorie").size());
		assertEquals(1, catalog.getCategories("GUI").size());
		assertEquals(0, catalog.getCategories("Gibts nicht!").size());

		assertEquals(3, catalog.getAspects("GUI", "prüfen").size());
		assertEquals(1, catalog.getAspects("GUI", "Skalierbarkeit").size());
		assertEquals(0, catalog.getAspects("GUI",
				"Sowas gibts einfach nicht...").size());

		/*
		 * Test if the categories exist and their order
		 */
		assertTrue(catalog.isCategory("GUI"));
		assertTrue(catalog.isCategory("Noch eine Kategorie"));
		assertFalse(catalog.isCategory("Unbekannte Kategorie"));

		Iterator<String> iterCategories = catalog.getCategories().iterator();
		assertEquals("GUI", iterCategories.next());
		assertEquals("Neue Kategorie", iterCategories.next());
		assertEquals("Weitere Kategorie", iterCategories.next());
		assertEquals("Noch eine Kategorie", iterCategories.next());

		catalog.pushUpCategory("Weitere Kategorie");
		catalog.pushDownCategory("Noch eine Kategorie");
		catalog.pushDownCategory("Neue Kategorie");

		iterCategories = catalog.getCategories().iterator();
		assertEquals("GUI", iterCategories.next());
		assertEquals("Weitere Kategorie", iterCategories.next());
		assertEquals("Noch eine Kategorie", iterCategories.next());
		assertEquals("Neue Kategorie", iterCategories.next());

		catalog.pushTopCategory("Noch eine Kategorie");
		catalog.pushBottomCategory("GUI");

		iterCategories = catalog.getCategories().iterator();
		assertEquals("Noch eine Kategorie", iterCategories.next());
		assertEquals("Weitere Kategorie", iterCategories.next());
		assertEquals("Neue Kategorie", iterCategories.next());
		assertEquals("GUI", iterCategories.next());

		/*
		 * Test editCategory method
		 */
		catalog.editCategory("Weitere Kategorie", "Eine andere Kategorie");

		Iterator<AppAspect> iterAspects = catalog.getAspects(
				"Eine andere Kategorie").iterator();
		assertEquals(aspect5, iterAspects.next());

		/*
		 * Remove aspect and check if the category is also deleted
		 */
		catalog.removeAspect(aspect6);

		assertEquals(0, catalog.getNumberOfAspects("Noch eine Kategorie"));
		assertFalse(catalog.isCategory("Noch eine Kategorie"));

		/*
		 * Test if aspects are in the catalog (category GUI)
		 */
		iterAspects = catalog.getAspects("GUI").iterator();
		assertEquals(aspect1, iterAspects.next());
		assertEquals(aspect2, iterAspects.next());
		assertEquals(aspect4, iterAspects.next());

		/*
		 * Move aspect into an existing category
		 */
		aspect5.setCategory("GUI");

		iterAspects = catalog.getAspects("GUI").iterator();
		assertEquals(aspect1, iterAspects.next());
		assertEquals(aspect2, iterAspects.next());
		assertEquals(aspect4, iterAspects.next());
		assertEquals(aspect5, iterAspects.next());

		/*
		 * Move aspect into a new category
		 */
		aspect5.setCategory("Bewertung");

		iterAspects = catalog.getAspects("Bewertung").iterator();
		assertEquals(aspect5, iterAspects.next());

		/*
		 * Modify aspect's properties
		 */
		aspect5.setDirective("Eine neue Direktive");
		aspect5.setDescription("Eine neue Beschreibung");

		assertEquals("Eine neue Direktive", aspect5.getDirective());
		assertEquals("Eine neue Beschreibung", aspect5.getDescription());

		/*
		 * Test the getAspect method
		 */
		assertEquals(aspect3, catalog.getAspect(aspect3.getId()));
		assertEquals(aspect5, catalog.getAspect(aspect5.getId()));
	}

	@Test
	public void pushAspects() throws DataException {
		removeAllCatalogData();

		AppCatalog catalog = appData.newCatalog("Testkatalog");

		AppAspect aspect1 = catalog.newAspect("Direktive 1", "Beschreibung",
				"Eine Kategorie");
		AppAspect aspect2 = catalog.newAspect("Direktive 2", "Beschreibung",
				"Eine Kategorie");
		AppAspect aspect3 = catalog.newAspect("Direktive 3", "Beschreibung",
				"Eine Kategorie");
		AppAspect aspect4 = catalog.newAspect("Direktive 4", "Beschreibung",
				"Eine Kategorie");
		AppAspect aspect5 = catalog.newAspect("Direktive 5", "Beschreibung",
				"Eine Kategorie");

		/*
		 * Original order
		 */
		Iterator<AppAspect> iterAspects = catalog.getAspects("Eine Kategorie")
				.iterator();
		assertEquals(aspect1, iterAspects.next());
		assertEquals(aspect2, iterAspects.next());
		assertEquals(aspect3, iterAspects.next());
		assertEquals(aspect4, iterAspects.next());
		assertEquals(aspect5, iterAspects.next());

		/*
		 * Push aspects
		 */
		aspect2.pushUp();
		aspect3.pushDown();

		iterAspects = catalog.getAspects("Eine Kategorie").iterator();
		assertEquals(aspect2, iterAspects.next());
		assertEquals(aspect1, iterAspects.next());
		assertEquals(aspect4, iterAspects.next());
		assertEquals(aspect3, iterAspects.next());
		assertEquals(aspect5, iterAspects.next());

		/*
		 * Push aspects
		 */
		aspect5.pushBottom();
		aspect2.pushBottom();
		aspect3.pushTop();

		iterAspects = catalog.getAspects("Eine Kategorie").iterator();
		assertEquals(aspect3, iterAspects.next());
		assertEquals(aspect1, iterAspects.next());
		assertEquals(aspect4, iterAspects.next());
		assertEquals(aspect5, iterAspects.next());
		assertEquals(aspect2, iterAspects.next());
	}

	@Test
	public void getStringRepOfEmptyAspect() throws DataException {
		removeAllCatalogData();

		AppCatalog catalog = appData.newCatalog("Testkatalog");

		AppAspect asp = new AppAspect(catalog, 5);

		assertEquals("", asp.toString());
	}

	@Test
	public void convertToResiAspect() throws DataException {
		removeAllCatalogData();

		AppCatalog catalog = appData.newCatalog("Testkatalog");

		AppAspect appAsp = catalog.newAspect("Test-Direktive",
				"Test-Beschreibung", "Test-Kategorie");

		Aspect resiAsp = appAsp.getAsResiAspect();

		assertEquals("Test-Direktive", resiAsp.getDirective());
		assertEquals("Test-Beschreibung", resiAsp.getDescription());
		assertEquals("Test-Kategorie", resiAsp.getCategory());
	}

	@Test
	public void getEmptyAttendeeList() throws DataException {
		assertTrue(appData.getAttendees().isEmpty());
		assertEquals(0, appData.getNumberOfAttendees());
	}

	@Test
	public void removeAllAttendeeData() throws DataException {
		for (AppAttendee att : appData.getAttendees()) {
			appData.removeAttendee(att);
		}

		assertTrue(appData.getAttendees().isEmpty());
		assertEquals(0, appData.getNumberOfAttendees());
	}

	@Test
	public void getAndSetNameOfAttendee() throws DataException {
		AppAttendee att = appData.newAttendee("Franz Faller", "Kontakt...");

		assertEquals("Franz Faller", att.getName());

		att.setName("Heinz Herbert");
		att.setContact("Neuer Kontakt");

		assertEquals("Heinz Herbert", att.getName());

		assertEquals("Heinz Herbert", appData.getAttendee("Heinz Herbert",
				"Neuer Kontakt").getName());
	}

	@Test(expected = DataException.class)
	public void setExistingNameOfAttendee() throws DataException {
		removeAllAttendeeData();

		AppAttendee att1 = appData.newAttendee("Franz Faller", "Kontakt...");
		AppAttendee att2 = appData.newAttendee("Heinz Herbert", "Kontakt...");

		att1.setNameAndContact(att2.getName(), att2.getContact());
	}

	@Test
	public void addAndRemoveAttendees() throws DataException {
		removeAllAttendeeData();

		/*
		 * Add attendees
		 */
		AppAttendee att1 = appData.newAttendee("Franz Faller", "Kontakt...");
		AppAttendee att2 = appData.newAttendee("Heinz Herbert", "Kontakt...");
		AppAttendee att3 = appData.newAttendee("Albert Einstein", "Kontakt...");
		AppAttendee att4 = appData.newAttendee("Klaus Kohl", "Kontakt...");
		AppAttendee att5 = appData.newAttendee("Berta Brecht", "Kontakt...");

		assertEquals(5, appData.getNumberOfAttendees());

		/*
		 * Search for an unknown attendee
		 */
		assertNull(appData.getCatalog("Mr. X"));

		/*
		 * Test if attendees are in the list in the right order
		 */
		Iterator<AppAttendee> iter = appData.getAttendees().iterator();

		assertEquals(att3, iter.next());
		assertEquals(att5, iter.next());
		assertEquals(att1, iter.next());
		assertEquals(att2, iter.next());
		assertEquals(att4, iter.next());

		/*
		 * Test to filter the attendees
		 */
		iter = appData.getAttendees("ErT").iterator();

		assertEquals(att3, iter.next());
		assertEquals(att5, iter.next());
		assertEquals(att2, iter.next());

		/*
		 * Remove attendees
		 */
		appData.removeAttendee(att3);
		appData.removeAttendee(att1.getName(), att1.getContact());

		assertNull(appData.getAttendee(att3.getName(), att3.getContact()));
		assertNull(appData.getAttendee(att1.getName(), att1.getContact()));
		assertEquals(3, appData.getNumberOfAttendees());

		iter = appData.getAttendees().iterator();

		assertEquals(att5, iter.next());
		assertEquals(att2, iter.next());
		assertEquals(att4, iter.next());
	}

	@Test
	public void modifyAttendee() throws DataException {
		removeAllAttendeeData();

		AppAttendee attendee = appData.newAttendee("Franz Faller",
				"Kontakt-Infos des Teilnehmers usw usw usw");

		assertTrue(attendee.getStrengths().isEmpty());

		/*
		 * Test the toString() method
		 */
		assertEquals(attendee.getName() + " ("
				+ attendee.getContact().substring(0, 20) + "...)", attendee
				.toString());

		/*
		 * Modify contact
		 */
		attendee.setContact("Tel. 0711-123456, eMail: franz.faller@company.de");
		assertEquals("Tel. 0711-123456, eMail: franz.faller@company.de",
				attendee.getContact());

		/*
		 * Test to filter the attendees by contact
		 */
		Iterator<AppAttendee> iterAttendees = appData
				.getAttendees("Company.de").iterator();

		assertEquals(attendee, iterAttendees.next());

		/*
		 * Add strengths to attendee
		 */
		attendee.addStrength("GUI");
		attendee.addStrength("User Interface");
		attendee.addStrength("Business-Logik");
		attendee.addStrength("Usability");

		Iterator<String> iterStrengths = attendee.getStrengths().iterator();

		assertEquals("Business-Logik", iterStrengths.next());
		assertEquals("GUI", iterStrengths.next());
		assertEquals("Usability", iterStrengths.next());
		assertEquals("User Interface", iterStrengths.next());

		/*
		 * Remove strengths
		 */
		attendee.removeStrength("Usability");
		attendee.removeStrength("Business-Logik");

		iterStrengths = attendee.getStrengths().iterator();

		assertEquals("GUI", iterStrengths.next());
		assertEquals("User Interface", iterStrengths.next());

		/*
		 * Try to add already existing strength
		 */
		assertTrue(attendee.isStrength("GUI"));
		attendee.addStrength("GUI");

		iterStrengths = attendee.getStrengths().iterator();

		assertEquals("GUI", iterStrengths.next());
		assertEquals("User Interface", iterStrengths.next());

		/*
		 * Try to remove non-existing strength
		 */
		assertFalse(attendee.isStrength("Unbekannte Kategorie"));
		attendee.removeStrength("Unbekannte Kategorie");

		iterStrengths = attendee.getStrengths().iterator();

		assertEquals("GUI", iterStrengths.next());
		assertEquals("User Interface", iterStrengths.next());
	}

	@Test
	public void convertToResiAttendee() throws DataException {
		removeAllAttendeeData();

		AppAttendee attendee = appData.newAttendee("Ein Teilnehmer",
				"Kontakt...");
		attendee.setContact("Kontaktadresse des Teilnehmers");

		Attendee resiAtt = attendee.getAsResiAttendee();

		assertEquals("Ein Teilnehmer", resiAtt.getName());
		assertEquals("Kontaktadresse des Teilnehmers", resiAtt.getContact());
	}

	@Test
	public void removeAllCSVProfileData() throws DataException {
		for (AppCSVProfile prof : appData.getCSVProfiles()) {
			appData.removeCSVProfile(prof);
		}

		assertTrue(appData.getCSVProfiles().isEmpty());
		assertEquals(0, appData.getNumberOfCSVProfiles());
	}

	@Test
	public void getAndSetNameOfCSVProfile() throws DataException {
		AppCSVProfile prof = appData.newCSVProfile("Trac");

		assertEquals("Trac", prof.getName());

		prof.setName("Trac Bugtracker");

		assertEquals("Trac Bugtracker", prof.getName());

		assertEquals("Trac Bugtracker", appData
				.getCSVProfile("Trac Bugtracker").getName());
	}

	@Test(expected = DataException.class)
	public void setExistingNameOfCSVProfile() throws DataException {
		removeAllCSVProfileData();

		AppCSVProfile prof1 = appData.newCSVProfile("Trac");
		AppCSVProfile prof2 = appData.newCSVProfile("Bugzilla");

		prof1.setName(prof2.getName());
	}

	@Test
	public void addAndRemoveCSVProfiles() throws DataException {
		removeAllCSVProfileData();

		/*
		 * Add profiles
		 */
		AppCSVProfile prof1 = appData.newCSVProfile("Trac Bug-Tracker");
		AppCSVProfile prof2 = appData.newCSVProfile("Bugzilla");
		AppCSVProfile prof3 = appData.newCSVProfile("Another Tracker");
		AppCSVProfile prof4 = appData.newCSVProfile("H2O Bug-Tracker");

		assertEquals(4, appData.getNumberOfCSVProfiles());

		/*
		 * Search for an unknown profile
		 */
		assertNull(appData.getCatalog("Unknown Tracker"));

		/*
		 * Test if profiles are in the list in the right order
		 */
		Iterator<AppCSVProfile> iter = appData.getCSVProfiles().iterator();

		assertEquals(prof3, iter.next());
		assertEquals(prof2, iter.next());
		assertEquals(prof4, iter.next());
		assertEquals(prof1, iter.next());

		/*
		 * Test to filter the profiles
		 */
		iter = appData.getCSVProfiles("racker").iterator();

		assertEquals(prof3, iter.next());
		assertEquals(prof4, iter.next());

		/*
		 * Remove profiles
		 */
		appData.removeCSVProfile(prof3);
		appData.removeCSVProfile(prof4.getName());

		assertNull(appData.getCSVProfile(prof3.getName()));
		assertNull(appData.getCSVProfile(prof4.getName()));
		assertEquals(2, appData.getNumberOfCSVProfiles());

		iter = appData.getCSVProfiles().iterator();

		assertEquals(prof2, iter.next());
		assertEquals(prof1, iter.next());
	}

	@Test
	public void modifyCSVProfile() throws DataException {
		removeAllCSVProfileData();

		AppCSVProfile prof = appData.newCSVProfile("Trac Bug-Tracker");

		/*
		 * Test the toString() method
		 */
		assertEquals("Trac Bug-Tracker", prof.toString());

		/*
		 * Get standard order of columns
		 */
		Iterator<AppCSVColumnName> iter = prof.getColumnOrder().iterator();
		assertEquals(AppCSVColumnName.DESCRIPTION, iter.next());
		assertEquals(AppCSVColumnName.REFERENCE, iter.next());
		assertEquals(AppCSVColumnName.SEVERITY, iter.next());
		assertEquals(AppCSVColumnName.REPORTER, iter.next());

		/*
		 * Change order of columns
		 */
		List<AppCSVColumnName> colOrd = new ArrayList<AppCSVColumnName>();
		colOrd.add(AppCSVColumnName.REPORTER);
		colOrd.add(AppCSVColumnName.SEVERITY);
		colOrd.add(AppCSVColumnName.DESCRIPTION);
		colOrd.add(AppCSVColumnName.REFERENCE);

		prof.setColumnOrder(colOrd);

		iter = prof.getColumnOrder().iterator();
		assertEquals(AppCSVColumnName.REPORTER, iter.next());
		assertEquals(AppCSVColumnName.SEVERITY, iter.next());
		assertEquals(AppCSVColumnName.DESCRIPTION, iter.next());
		assertEquals(AppCSVColumnName.REFERENCE, iter.next());

		/*
		 * Get and set property colsInFirstLine
		 */
		prof.isColsInFirstLine();

		prof.setColsInFirstLine(true);
		assertTrue(prof.isColsInFirstLine());

		prof.setColsInFirstLine(false);
		assertFalse(prof.isColsInFirstLine());

		/*
		 * Get and set property encapsulateContent
		 */
		prof.isEncapsulateContent();

		prof.setEncapsulateContent(false);
		assertFalse(prof.isEncapsulateContent());

		prof.setEncapsulateContent(true);
		assertTrue(prof.isEncapsulateContent());

		/*
		 * Get and set column mappings
		 */
		prof.setColumnMapping(AppCSVColumnName.DESCRIPTION, "beschreibung");
		assertEquals("beschreibung", prof
				.getColumnMapping(AppCSVColumnName.DESCRIPTION));

		prof.setColumnMapping(AppCSVColumnName.REFERENCE, "link");
		assertEquals("link", prof.getColumnMapping(AppCSVColumnName.REFERENCE));

		prof.setColumnMapping(AppCSVColumnName.REPORTER, "melder");
		assertEquals("melder", prof.getColumnMapping(AppCSVColumnName.REPORTER));

		prof.removeColumnMapping(AppCSVColumnName.REFERENCE);
		assertEquals(prof.getColumnMapping(AppCSVColumnName.REFERENCE), prof
				.getColumnMapping(AppCSVColumnName.REFERENCE));
		assertEquals("melder", prof.getColumnMapping(AppCSVColumnName.REPORTER));
		assertEquals("beschreibung", prof
				.getColumnMapping(AppCSVColumnName.DESCRIPTION));

		/*
		 * Try to overwrite an alread defined column mapping
		 */
		prof.setColumnMapping(AppCSVColumnName.DESCRIPTION, "summary");
		assertEquals("summary", prof
				.getColumnMapping(AppCSVColumnName.DESCRIPTION));

		/*
		 * Get and set severity mappings
		 */
		assertTrue(prof.getValidSeverityMappings().isEmpty());

		List<String> sevMaps = new ArrayList<String>();
		sevMaps.add("critical");
		sevMaps.add("major");
		sevMaps.add("minor");
		sevMaps.add("good");
		sevMaps.add("minor"); // duplicate! (has to be ignored)

		prof.setValidSeverityMappings(sevMaps);
		assertEquals(4, prof.getValidSeverityMappings().size());

		Iterator<String> iterSevMaps = prof.getValidSeverityMappings()
				.iterator();
		assertEquals("critical", iterSevMaps.next());
		assertEquals("major", iterSevMaps.next());
		assertEquals("minor", iterSevMaps.next());
		assertEquals("good", iterSevMaps.next());

		/*
		 * Pushing severity mappings
		 */
		prof.pushUpSeverityMapping("good");
		prof.pushDownSeverityMapping("major");
		prof.pushUpSeverityMapping("critical");

		iterSevMaps = prof.getValidSeverityMappings().iterator();
		assertEquals("critical", iterSevMaps.next());
		assertEquals("good", iterSevMaps.next());
		assertEquals("major", iterSevMaps.next());
		assertEquals("minor", iterSevMaps.next());

		/*
		 * Check if severity mappings are valid
		 */
		assertTrue(prof.isValidSeverityMapping("critical"));
		assertFalse(prof.isValidSeverityMapping("very good"));

		/*
		 * Reset severity mappings
		 */
		prof.setValidSeverityMappings(null);
		assertEquals(0, prof.getValidSeverityMappings().size());
	}

	@Test(expected = DataException.class)
	public void setIncorrectColOrderCSVProfile() throws DataException {
		removeAllCSVProfileData();

		AppCSVProfile prof = appData.newCSVProfile("Trac");

		/*
		 * AppCSVColumnName.REFERENCE is missing
		 */
		List<AppCSVColumnName> colOrd = new ArrayList<AppCSVColumnName>();
		colOrd.add(AppCSVColumnName.REPORTER);
		colOrd.add(AppCSVColumnName.SEVERITY);
		colOrd.add(AppCSVColumnName.DESCRIPTION);

		prof.setColumnOrder(colOrd);
	}

	@AfterClass
	public static void deleteDatabase() {
		FileTools.deleteDirectory(new File(Data.getInstance().getAppData()
				.getAppDataPath()));
	}

}
