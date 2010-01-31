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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.revager.app.Application;
import org.revager.app.ApplicationException;
import org.revager.app.AspectManagement;
import org.revager.app.AttendeeManagement;
import org.revager.app.FindingManagement;
import org.revager.app.MeetingManagement;
import org.revager.app.ProtocolManagement;
import org.revager.app.ReviewManagement;
import org.revager.app.SeverityManagement;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.appdata.AppAttendee;
import org.revager.app.model.schema.Aspect;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.AttendeeReference;
import org.revager.app.model.schema.Finding;
import org.revager.app.model.schema.Meeting;
import org.revager.app.model.schema.Protocol;
import org.revager.app.model.schema.Review;
import org.revager.app.model.schema.Role;
import org.revager.io.ResiIOException;
import org.revager.tools.FileTools;

/**
 * This class tests the management classes in the package org.revager.app
 * 
 * @author Johannes Wettinger
 * @version 1.0
 */
public class ManagementTest {

	private static String testdataDirectory = "org/revager/test/testdata/";

	private static String tempDirectory = System.getProperty("user.home")
			+ "/tmp-revager_/";

	private static String exampleReview = testdataDirectory
			+ "Review Example 2.rev";
	private static String exampleReviewMod = testdataDirectory
			+ "Review Example 3.rev";

	private static File extRef1 = new File(testdataDirectory + "coffee.gif");
	private static File extRef2 = new File(testdataDirectory + "smiley.png");
	private static File extRef3 = new File(testdataDirectory + "tux.gif");
	private static File extRef3Mod = new File(testdataDirectory + "tux1.gif");
	private static File extRef4 = new File(testdataDirectory + "wallpaper");

	@BeforeClass
	public static void setUp() throws DataException {
		File tempDir = new File(tempDirectory);

		tempDir.mkdir();

		Data.getInstance().getAppData()
				.setCustomAppDataDirectory(tempDirectory);

		Data.getInstance().getAppData().initialize();
	}

	@Test
	public void manageAspects() throws ResiIOException, IOException,
			ApplicationException {
		ReviewManagement revMgmt = Application.getInstance().getReviewMgmt();
		AspectManagement aspMgmt = Application.getInstance().getAspectMgmt();

		Application.getInstance().getApplicationCtl().loadReview(exampleReview);

		/*
		 * Create new unknown aspect.
		 */
		Aspect unknownAsp = new Aspect();
		unknownAsp.setCategory("cat");
		unknownAsp.setDescription("desc");
		unknownAsp.setDirective("dir");

		/*
		 * Create new test aspects.
		 */
		Aspect testAsp1 = new Aspect();
		testAsp1.setCategory("CATEGORY");
		testAsp1.setDescription("DESCRIPTION");
		testAsp1.setDirective("DIRECTIVE");

		Aspect testAsp2 = new Aspect();
		testAsp2.setCategory("Bestimmte Kategorie");
		testAsp2.setDescription("Irgendeine Beschreibung");
		testAsp2.setDirective("Test-Direktive");

		Aspect testAsp3 = new Aspect();
		testAsp3.setCategory("Kategorie");
		testAsp3.setDescription("Beschreibung");
		testAsp3.setDirective("Direktive");

		/*
		 * Get aspects by id and compare them.
		 */
		for (Aspect a : aspMgmt.getAspects()) {
			assertEquals(a, aspMgmt.getAspect(Integer.parseInt(a.getId())));
			assertTrue(aspMgmt.isAspect(a));
		}

		assertFalse(aspMgmt.isAspect(unknownAsp));

		assertEquals(Data.getInstance().getResiData().getReview().getAspects()
				.size(), revMgmt.getNumberOfAspects());

		/*
		 * Add new aspects.
		 */
		Aspect asp1 = aspMgmt.addAspect(testAsp1);
		Aspect asp2 = aspMgmt.addAspect(testAsp2.getDirective(), testAsp2
				.getDescription(), testAsp2.getCategory());

		assertTrue(aspMgmt.isAspect(testAsp1));
		assertTrue(aspMgmt.isAspect(testAsp2));
		assertTrue(aspMgmt.isAspect(asp1));
		assertTrue(aspMgmt.isAspect(asp2));

		Data.getInstance().getResiData().getReview().getAttendees().get(0)
				.getAspects().getAspectIds().add(asp1.getId());

		/*
		 * Ensure that no duplicates can be added.
		 */
		int numOfAsp = revMgmt.getNumberOfAspects();

		aspMgmt.addAspect(testAsp1);

		assertEquals(numOfAsp, revMgmt.getNumberOfAspects());

		/*
		 * Remove existing aspect.
		 */
		numOfAsp = revMgmt.getNumberOfAspects();

		aspMgmt.removeAspect(asp1);

		assertEquals(numOfAsp - 1, revMgmt.getNumberOfAspects());

		/*
		 * Remove not existing aspect.
		 */
		numOfAsp = revMgmt.getNumberOfAspects();

		aspMgmt.removeAspect(unknownAsp);

		assertEquals(numOfAsp, revMgmt.getNumberOfAspects());

		/*
		 * Edit aspects.
		 */
		assertFalse(aspMgmt.editAspect(asp1, testAsp3));
		assertTrue(aspMgmt.editAspect(asp2, testAsp3));
		assertFalse(aspMgmt.editAspect(asp2, testAsp3));

		/*
		 * Push aspects.
		 */
		asp1 = aspMgmt.addAspect(testAsp1);

		assertFalse(aspMgmt.isTopAspect(asp1));
		assertTrue(aspMgmt.isBottomAspect(asp1));

		aspMgmt.pushTopAspect(asp1);

		assertTrue(aspMgmt.isTopAspect(asp1));
		assertFalse(aspMgmt.isBottomAspect(asp1));

		aspMgmt.pushDownAspect(asp1);

		assertFalse(aspMgmt.isTopAspect(asp1));
		assertFalse(aspMgmt.isBottomAspect(asp1));

		aspMgmt.pushBottomAspect(asp1);

		assertFalse(aspMgmt.isTopAspect(asp1));
		assertTrue(aspMgmt.isBottomAspect(asp1));

		aspMgmt.pushUpAspect(asp1);

		assertFalse(aspMgmt.isTopAspect(asp1));
		assertFalse(aspMgmt.isBottomAspect(asp1));
	}

	@Test
	public void manageAttendees() throws ResiIOException, IOException,
			ApplicationException, DataException {
		AttendeeManagement attMgmt = Application.getInstance()
				.getAttendeeMgmt();
		AspectManagement aspMgmt = Application.getInstance().getAspectMgmt();

		Application.getInstance().getApplicationCtl().loadReview(exampleReview);

		/*
		 * Create new unknown attendee.
		 */
		Attendee unknownAtt = new Attendee();
		unknownAtt.setName("Unbekannter Teilnehmer");
		unknownAtt.setContact("Kontakt...");
		unknownAtt.setRole(Role.REVIEWER);

		/*
		 * Create new test attendees.
		 */
		Attendee testAtt1 = new Attendee();
		testAtt1.setName("Teilnehmer 1");
		testAtt1.setContact("Kontaktdaten von Teilnehmer 1...");
		testAtt1.setRole(Role.REVIEWER);

		Attendee testAtt2 = new Attendee();
		testAtt2.setName("Teilnehmer 2");
		testAtt2.setContact("Kontaktdaten von Teilnehmer 2...");
		testAtt2.setRole(Role.REVIEWER);

		Attendee testAtt3 = new Attendee();
		testAtt3.setName("Teilnehmer 3");
		testAtt3.setContact("Kontaktdaten von Teilnehmer 3...");
		testAtt3.setRole(Role.MODERATOR);

		/*
		 * Create test aspects and add them to the review
		 */
		Aspect testAsp1 = new Aspect();
		testAsp1.setCategory("CATEGORY");
		testAsp1.setDescription("DESCRIPTION");
		testAsp1.setDirective("DIRECTIVE");

		Aspect testAsp2 = new Aspect();
		testAsp2.setCategory("Bestimmte Kategorie");
		testAsp2.setDescription("Irgendeine Beschreibung");
		testAsp2.setDirective("Test-Direktive");

		Aspect testAsp3 = new Aspect();
		testAsp3.setCategory("Bestimmte Kategorie 3");
		testAsp3.setDescription("Irgendeine Beschreibung 3");
		testAsp3.setDirective("Test-Direktive 3");

		Aspect testAsp3Identical = new Aspect();
		testAsp3Identical.setCategory(testAsp3.getCategory());
		testAsp3Identical.setDescription(testAsp3.getDescription());
		testAsp3Identical.setDirective(testAsp3.getDirective());

		Aspect asp1 = aspMgmt.addAspect(testAsp1);
		Aspect asp2 = aspMgmt.addAspect(testAsp2);

		/*
		 * Get attendees by id and compare them.
		 */
		for (Attendee a : attMgmt.getAttendees()) {
			assertEquals(a, attMgmt.getAttendee(Integer.parseInt(a.getId())));
			assertTrue(attMgmt.isAttendee(a));
		}

		assertFalse(attMgmt.isAttendee(unknownAtt));

		assertEquals(Data.getInstance().getResiData().getReview()
				.getAttendees().size(), attMgmt.getNumberOfAttendees());

		/*
		 * Add attendees
		 */
		AppAttendee appAtt = Data.getInstance().getAppData().newAttendee(
				testAtt1.getName(), testAtt1.getContact());

		Attendee att1 = attMgmt.addAttendee(appAtt, testAtt1.getRole(), null);

		List<Aspect> someAspects = new ArrayList<Aspect>();
		someAspects.add(asp1);
		someAspects.add(asp2);

		Attendee att2 = attMgmt.addAttendee(testAtt2.getName(), testAtt2
				.getContact(), testAtt2.getRole(), someAspects);

		assertTrue(attMgmt.isAttendee(testAtt1));
		assertTrue(attMgmt.isAttendee(testAtt2));
		assertTrue(attMgmt.isAttendee(att1));
		assertTrue(attMgmt.isAttendee(att2));

		AttendeeReference ar = new AttendeeReference();
		ar.setAttendee(att1.getId());
		Data.getInstance().getResiData().getReview().getMeetings().get(0)
				.getProtocol().getAttendeeReferences().add(ar);

		/*
		 * Ensure that no duplicates can be added
		 */
		int numOfAtt = attMgmt.getNumberOfAttendees();

		attMgmt.addAttendee(att1);

		assertEquals(numOfAtt, attMgmt.getNumberOfAttendees());

		/*
		 * Remove existing attendee.
		 */
		numOfAtt = attMgmt.getNumberOfAttendees();

		assertFalse(attMgmt.isAttendeeRemovable(att1));
		assertTrue(attMgmt.isAttendeeRemovable(att2));

		attMgmt.removeAttendee(att2);

		assertEquals(numOfAtt - 1, attMgmt.getNumberOfAttendees());

		/*
		 * Edit attendees.
		 */
		assertFalse(attMgmt.editAttendee(att2, testAtt3));
		assertTrue(attMgmt.editAttendee(att1, testAtt3));
		assertFalse(attMgmt.editAttendee(att1, testAtt3));

		/*
		 * Meetings of an attendee.
		 */
		att2 = attMgmt.addAttendee(testAtt2);
		assertEquals(1, attMgmt.getMeetings(att1).size());
		assertEquals(0, attMgmt.getMeetings(att2).size());

		/*
		 * Add and remove aspects.
		 */
		att1 = attMgmt.addAttendee(testAtt1);

		attMgmt.addAspect(testAsp3, att1);
		attMgmt.addAspect(testAsp3Identical, att1);

		attMgmt.addAspect(asp1, att1);

		assertTrue(attMgmt.hasAspect(asp1, att1));
		attMgmt.addAspect(asp2, att1);
		assertTrue(attMgmt.hasAspect(asp2, att1));

		assertEquals(3, attMgmt.getAspects(att1).size());
		assertEquals(3, attMgmt.getNumberOfAspects(att1));

		attMgmt.addAspect(asp1, att2);
		assertTrue(attMgmt.hasAspect(asp1, att2));

		assertEquals(1, attMgmt.getAspects(att2).size());
		assertEquals(1, attMgmt.getNumberOfAspects(att2));

		attMgmt.removeAspect(asp1, att2);

		assertEquals(0, attMgmt.getAspects(att2).size());
		assertEquals(0, attMgmt.getNumberOfAspects(att2));

		/*
		 * Push aspects of attendee.
		 */
		attMgmt.pushBottomAspect(att1, asp1);

		assertFalse(attMgmt.isTopAspect(att1, asp1));
		assertTrue(attMgmt.isBottomAspect(att1, asp1));

		attMgmt.pushTopAspect(att1, asp1);

		assertTrue(attMgmt.isTopAspect(att1, asp1));
		assertFalse(attMgmt.isBottomAspect(att1, asp1));

		attMgmt.pushDownAspect(att1, asp1);

		assertFalse(attMgmt.isTopAspect(att1, asp1));
		assertFalse(attMgmt.isBottomAspect(att1, asp1));

		attMgmt.pushBottomAspect(att1, asp1);

		assertFalse(attMgmt.isTopAspect(att1, asp1));
		assertTrue(attMgmt.isBottomAspect(att1, asp1));

		attMgmt.pushUpAspect(att1, asp1);

		assertFalse(attMgmt.isTopAspect(att1, asp1));
		assertFalse(attMgmt.isBottomAspect(att1, asp1));

		/*
		 * Get attendees with aspect
		 */
		for (Attendee a : attMgmt.getAttendeesWithAspect(asp1)) {
			assertTrue(attMgmt.hasAspect(asp1, a));
		}

		/*
		 * Update attendees in database
		 */
		attMgmt.updateAttendeesDirectory();
	}

	@Test
	public void manageFindings() throws ResiIOException, IOException,
			ApplicationException {
		FindingManagement findMgmt = Application.getInstance().getFindingMgmt();

		Application.getInstance().getApplicationCtl().loadReview(exampleReview);

		/*
		 * Aspects
		 */
		Aspect asp1 = Data.getInstance().getResiData().getReview().getAspects()
				.get(0);
		Aspect asp2 = Data.getInstance().getResiData().getReview().getAspects()
				.get(1);

		Protocol prot = Data.getInstance().getResiData().getReview()
				.getMeetings().get(0).getProtocol();

		assertEquals(3, findMgmt.getFindings(prot).size());
		assertEquals(3, findMgmt.getNumberOfFindings(prot));

		/*
		 * Add new findings
		 */
		Finding find1 = findMgmt.addFinding("Beschreibung des Befunds...",
				"Hauptfehler", prot);
		Finding find2 = findMgmt.addFinding("Beschreibung des Befunds...",
				"Nebenfehler", prot);

		/*
		 * Remove finding
		 */
		findMgmt.removeFinding(find1, prot);

		/*
		 * Edit finding
		 */
		find1 = findMgmt.addFinding("Beschreibung des Befunds...",
				"Hauptfehler", prot);
		findMgmt.editFinding(find1, find2, prot);

		/*
		 * Add references
		 */
		findMgmt.addReference("Fehler ist auf Seite 23", find2);
		findMgmt.addReference("...und auf S. 26", find2);
		findMgmt.addReference("noch eine Ref.", find2);

		assertEquals("Fehler ist auf Seite 23", findMgmt.getReferences(find2)
				.get(0));
		assertEquals("...und auf S. 26", findMgmt.getReferences(find2).get(1));
		assertEquals("noch eine Ref.", findMgmt.getReferences(find2).get(2));

		/*
		 * Remove reference
		 */
		findMgmt.removeReference("...und auf S. 26", find2);

		assertEquals("Fehler ist auf Seite 23", findMgmt.getReferences(find2)
				.get(0));
		assertEquals("noch eine Ref.", findMgmt.getReferences(find2).get(1));

		/*
		 * Add external references
		 */
		findMgmt.addExtReference(extRef1, find2);
		findMgmt.addExtReference(extRef2, find2);
		findMgmt.addExtReference(extRef3, find2);
		findMgmt.addExtReference(extRef3, find2);

		assertEquals(extRef1.getName(), findMgmt.getExtReferences(find2).get(0)
				.getName());
		assertEquals(extRef2.getName(), findMgmt.getExtReferences(find2).get(1)
				.getName());
		assertEquals(extRef3.getName(), findMgmt.getExtReferences(find2).get(2)
				.getName());
		assertEquals(extRef3Mod.getName(), findMgmt.getExtReferences(find2)
				.get(3).getName());

		/*
		 * Remove external reference
		 */
		findMgmt.removeExtReference(findMgmt.getExtReferences(find2).get(2),
				find2);

		assertEquals(extRef1.getName(), findMgmt.getExtReferences(find2).get(0)
				.getName());
		assertEquals(extRef2.getName(), findMgmt.getExtReferences(find2).get(1)
				.getName());
		assertEquals(extRef3Mod.getName(), findMgmt.getExtReferences(find2)
				.get(2).getName());

		/*
		 * Add aspects
		 */
		findMgmt.addAspect(asp1, find2);
		findMgmt.addAspect(asp2, find2);

		assertEquals(asp1.getDirective() + " (" + asp1.getCategory() + ")",
				findMgmt.getAspects(find2).get(0));
		assertEquals(asp2.getDirective() + " (" + asp2.getCategory() + ")",
				findMgmt.getAspects(find2).get(1));

		/*
		 * Remove aspect
		 */
		findMgmt.removeAspect(asp1.getDirective() + " (" + asp1.getCategory()
				+ ")", find2);

		assertEquals(asp2.getDirective() + " (" + asp2.getCategory() + ")",
				findMgmt.getAspects(find2).get(0));

		/*
		 * Push findings
		 */
		findMgmt.addFinding(find1, prot);

		assertFalse(findMgmt.isTopFinding(find1, prot));
		assertTrue(findMgmt.isBottomFinding(find1, prot));

		findMgmt.pushTopFinding(find1, prot);

		assertTrue(findMgmt.isTopFinding(find1, prot));
		assertFalse(findMgmt.isBottomFinding(find1, prot));

		findMgmt.pushDownFinding(find1, prot);

		assertFalse(findMgmt.isTopFinding(find1, prot));
		assertFalse(findMgmt.isBottomFinding(find1, prot));

		findMgmt.pushBottomFinding(find1, prot);

		assertFalse(findMgmt.isTopFinding(find1, prot));
		assertTrue(findMgmt.isBottomFinding(find1, prot));

		findMgmt.pushUpFinding(find1, prot);

		assertFalse(findMgmt.isTopFinding(find1, prot));
		assertFalse(findMgmt.isBottomFinding(find1, prot));
	}

	@Test
	public void manageMeetings() throws ResiIOException, IOException,
			ApplicationException {
		MeetingManagement meetMgmt = Application.getInstance().getMeetingMgmt();

		Application.getInstance().getApplicationCtl().loadReview(exampleReview);

		/*
		 * Meetings of this review
		 */
		Meeting meet1 = meetMgmt.getMeetings().get(0);
		Meeting meet2 = meetMgmt.getMeetings().get(1);
		Meeting meet3 = meetMgmt.getMeetings().get(2);

		/*
		 * Test getMeetings method
		 */
		assertEquals(Data.getInstance().getResiData().getReview().getMeetings()
				.size(), meetMgmt.getMeetings().size());

		int i = 0;

		for (Meeting m : meetMgmt.getMeetings()) {
			assertEquals(Data.getInstance().getResiData().getReview()
					.getMeetings().get(i), m);

			i++;
		}

		/*
		 * Test predecessor meeting method
		 */
		assertEquals(meet1, meetMgmt.getPredecessorMeeting(meet2));
		assertNull(meetMgmt.getPredecessorMeeting(meet1));

		/*
		 * Add meeting
		 */
		Calendar date = meet3.getPlannedDate();
		date.add(Calendar.DATE, 2);
		Calendar start = meet3.getPlannedStart();
		Calendar end = meet3.getPlannedEnd();

		Meeting newMeet = meetMgmt.addMeeting(date, start, end,
				"an einem sicheren Ort ;-)");

		/* try to add duplicate */
		meetMgmt.addMeeting(newMeet);

		assertEquals(newMeet, meetMgmt.getMeetings().get(3));
		assertEquals(4, meetMgmt.getMeetings().size());

		/*
		 * Remove meeting
		 */
		meetMgmt.removeMeeting(newMeet);
		assertEquals(3, meetMgmt.getMeetings().size());

		meetMgmt.removeMeeting(0);
		assertEquals(2, meetMgmt.getMeetings().size());

		/*
		 * Edit meeting
		 */
		newMeet = meetMgmt.addMeeting(date, start, end,
				"an einem sicheren Ort ;-)");

		meetMgmt.editMeeting(meet2, newMeet);

		assertEquals(3, meetMgmt.getMeetings().size());
	}

	@Test
	public void manageProtocols() throws ResiIOException, IOException,
			ApplicationException, DatatypeConfigurationException {
		ProtocolManagement protMgmt = Application.getInstance()
				.getProtocolMgmt();

		Application.getInstance().getApplicationCtl().loadReview(exampleReview);

		Meeting meet1 = Data.getInstance().getResiData().getReview()
				.getMeetings().get(0);
		Meeting meet2 = Data.getInstance().getResiData().getReview()
				.getMeetings().get(1);

		/*
		 * Set protocol
		 */
		Protocol prot1 = protMgmt.setProtocol(meet1.getPlannedDate(), meet1
				.getPlannedStart(), meet1.getPlannedEnd(), meet1
				.getPlannedLocation(), meet1);

		assertEquals(prot1, protMgmt.getProtocol(meet1));

		/*
		 * Clear protocol
		 */
		protMgmt.clearProtocol(meet1);

		assertNull(protMgmt.getProtocol(meet1));

		/*
		 * attendees of a protocol
		 */
		int i = 0;

		for (Attendee a : protMgmt.getAttendees(meet2.getProtocol())) {
			assertEquals(meet2.getProtocol().getAttendeeReferences().get(i)
					.getAttendee(), a.getId());

			assertTrue(protMgmt.isAttendee(a, meet2.getProtocol()));

			i++;
		}

		/*
		 * remove and add attendee
		 */
		Attendee att = protMgmt.getAttendees(meet2.getProtocol()).get(0);

		protMgmt.removeAttendee(att, meet2.getProtocol());

		protMgmt.addAttendee(att, DatatypeFactory.newInstance().newDuration(
				"PT1H15M"), meet2.getProtocol());

		/*
		 * get preparation time
		 */
		assertEquals(DatatypeFactory.newInstance().newDuration("PT1H15M"),
				protMgmt.getAttendeePrepTime(att, meet2.getProtocol()));
	}

	@Test
	public void manageReview() throws ResiIOException, IOException,
			ApplicationException {
		final String REVIEW_FILE = Data.getInstance().getResource(
				"reviewFileName");

		final String EXTREF_PREFIX = Data.getInstance().getResource(
				"extRefURIPrefix");

		ReviewManagement revMgmt = Application.getInstance().getReviewMgmt();
		Review rev = Data.getInstance().getResiData().getReview();

		Application.getInstance().getApplicationCtl().loadReview(
				exampleReviewMod);

		/*
		 * file name and URI of external references
		 */
		assertEquals("Datei mit Ümläute.gif", revMgmt.getExtRefFileName(revMgmt
				.getExtRefURI("Datei mit Ümläute.gif")));

		/*
		 * invalid URI
		 */
		assertEquals("\\\\invalid_uri://\\::\\/::,,:s", revMgmt
				.getExtRefFileName(EXTREF_PREFIX
						+ "\\\\invalid_uri://\\::\\/::,,:s"));

		/*
		 * existence of external references
		 */
		assertTrue(revMgmt.isExtRef(REVIEW_FILE));
		assertTrue(revMgmt.isExtRef("error.log"));
		assertTrue(revMgmt.isExtRef("dpkg.list"));
		assertFalse(revMgmt.isExtRef("datei_gibts_nicht.txt"));

		/*
		 * validate external references
		 */
		revMgmt.validateExtRefs();

		/*
		 * review name
		 */
		assertEquals(rev.getName(), revMgmt.getReviewName());

		revMgmt.setReviewName("Neuer Name für das Review");
		assertEquals("Neuer Name für das Review", revMgmt.getReviewName());

		/*
		 * review description
		 */
		assertEquals(rev.getDescription(), revMgmt.getReviewDescription());

		revMgmt.setReviewDescription("Neue Beschreibung für das Review");
		assertEquals("Neue Beschreibung für das Review", revMgmt
				.getReviewDescription());

		/*
		 * review comments
		 */
		assertEquals(rev.getComments(), revMgmt.getReviewComments());

		revMgmt.setReviewComments("Neue Kommentare für das Review");
		assertEquals("Neue Kommentare für das Review", revMgmt
				.getReviewComments());

		/*
		 * product name
		 */
		assertEquals(rev.getProduct().getName(), revMgmt.getProductName());

		revMgmt.setProductName("Neue Bezeichnung für das Produkt");
		assertEquals("Neue Bezeichnung für das Produkt", revMgmt
				.getProductName());

		/*
		 * product version
		 */
		assertEquals(rev.getProduct().getVersion(), revMgmt.getProductVersion());

		revMgmt.setProductVersion("Neue Version des Produkts");
		assertEquals("Neue Version des Produkts", revMgmt.getProductVersion());

		/*
		 * impression
		 */
		assertEquals(rev.getImpression(), revMgmt.getImpression());

		revMgmt.setImpression("Eindruck...");
		assertEquals("Eindruck...", revMgmt.getImpression());

		/*
		 * recommendation
		 */
		assertEquals(rev.getRecommendation(), revMgmt.getRecommendation());

		revMgmt.setRecommendation("Empfehlung...");
		assertEquals("Empfehlung...", revMgmt.getRecommendation());

		/*
		 * Remove all external references
		 */
		for (File ref : revMgmt.getExtProdReferences()) {
			revMgmt.removeExtProdReference(ref);
		}

		/*
		 * Manage product references
		 */
		for (String ref : revMgmt.getProductReferences()) {
			assertTrue(revMgmt.isProductReference(ref));

			if (revMgmt.isProductReferenceRemovable(ref)) {
				revMgmt.removeProductReference(ref);
			}
		}

		assertEquals(1, revMgmt.getProductReferences().size());
		assertFalse(revMgmt.isProductReference("nicht existierende Referenz"));

		String prodRef1 = "Referenz zum Prüfling";
		String prodRef2 = "Weitere Referenz zum Prüfling";

		revMgmt.addProductReference(prodRef1);

		assertTrue(revMgmt.isProductReference(prodRef1));

		revMgmt.editProductReference(prodRef1, prodRef2);

		assertFalse(revMgmt.isProductReference(prodRef1));
		assertTrue(revMgmt.isProductReference(prodRef2));

		/*
		 * Add some external product references and remove all product
		 * references
		 */
		revMgmt.addExtProdReference(extRef1);
		revMgmt.addExtProdReference(extRef2);
		revMgmt.addExtProdReference(extRef3);

		for (String ref : revMgmt.getProductReferences()) {
			revMgmt.removeProductReference(ref);
		}

		/*
		 * Manage external product references
		 */
		for (File ref : revMgmt.getExtProdReferences()) {
			if (revMgmt.isExtProdReferenceRemovable(ref)) {
				revMgmt.removeExtProdReference(ref);
			}
		}

		assertEquals(1, revMgmt.getExtProdReferences().size());

		revMgmt.addExtProdReference(extRef1);
		revMgmt.addExtProdReference(extRef2);
		revMgmt.addExtProdReference(extRef2);
		revMgmt.addExtProdReference(extRef4);
		revMgmt.addExtProdReference(extRef4);

		assertEquals(6, revMgmt.getExtProdReferences().size());

		/*
		 * Reload review and check numbers
		 */
		Application.getInstance().getApplicationCtl().loadReview(
				exampleReviewMod);

		assertEquals(2, revMgmt.getNumberOfAspects());
		assertEquals(3, revMgmt.getNumberOfSeverities());
		assertEquals(3, revMgmt.getNumberOfAttendees());
		assertEquals(9, revMgmt.getNumberOfFindings());
		assertEquals(3, revMgmt.getNumberOfMeetings());
		assertEquals(4, revMgmt.getNumberOfProdRefs());
	}

	@Test
	public void manageSeverities() throws ResiIOException, IOException,
			ApplicationException {
		SeverityManagement sevMgmt = Application.getInstance()
				.getSeverityMgmt();

		Application.getInstance().getApplicationCtl().loadReview(
				exampleReviewMod);

		/*
		 * validate the severites in the findings of the loaded review
		 */
		sevMgmt.validateSeverities();

		/*
		 * number of severities
		 */
		assertEquals(3, sevMgmt.getNumberOfSeverities());

		/*
		 * check existing and not existing severities
		 */
		assertTrue(sevMgmt.isSeverity("Hauptfehler"));
		assertFalse(sevMgmt.isSeverity("major"));

		/*
		 * check replacement severities
		 */
		assertEquals("Kritischer Fehler", sevMgmt
				.getReplaceSeverity("Hauptfehler"));
		assertEquals("Hauptfehler", sevMgmt.getReplaceSeverity("Nebenfehler"));

		/*
		 * edit severities
		 */
		sevMgmt.editSeverity("Hauptfehler", "Schwerer Fehler");
		sevMgmt.editSeverity("Nebenfehler", "Beiläufiger Fehler");

		assertTrue(sevMgmt.isSeverity("Schwerer Fehler"));
		assertTrue(sevMgmt.isSeverity("Beiläufiger Fehler"));
		assertFalse(sevMgmt.isSeverity("Hauptfehler"));
		assertFalse(sevMgmt.isSeverity("Nebenfehler"));

		/*
		 * try to remove all severities
		 */
		int i = 0;

		List<String> sevs = sevMgmt.getSeverities();

		while (i < sevs.size()) {
			if (sevMgmt.isSeverityRemovable(sevs.get(i))) {
				sevMgmt.removeSeverity(sevs.get(i));
				i--;
			}

			i++;
		}

		assertEquals(1, sevMgmt.getSeverities().size());

		/*
		 * add new severities
		 */
		sevMgmt.addSeverity("major");
		sevMgmt.addSeverity("minor");
		sevMgmt.addSeverity("critical");

		assertEquals(4, sevMgmt.getSeverities().size());

		/*
		 * Push severities
		 */
		assertFalse(sevMgmt.isTopSeverity("critical"));
		assertTrue(sevMgmt.isBottomSeverity("critical"));

		sevMgmt.pushTopSeverity("critical");

		assertTrue(sevMgmt.isTopSeverity("critical"));
		assertFalse(sevMgmt.isBottomSeverity("critical"));

		sevMgmt.pushDownSeverity("critical");

		assertFalse(sevMgmt.isTopSeverity("critical"));
		assertFalse(sevMgmt.isBottomSeverity("critical"));

		sevMgmt.pushBottomSeverity("critical");

		assertFalse(sevMgmt.isTopSeverity("critical"));
		assertTrue(sevMgmt.isBottomSeverity("critical"));

		sevMgmt.pushUpSeverity("critical");

		assertFalse(sevMgmt.isTopSeverity("critical"));
		assertFalse(sevMgmt.isBottomSeverity("critical"));
	}

	@AfterClass
	public static void cleanUp() {
		FileTools.deleteDirectory(new File(Data.getInstance().getAppData()
				.getAppDataPath()));

		FileTools.deleteDirectory(new File(tempDirectory));
	}

}
