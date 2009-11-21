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

import neos.resi.app.comparators.AspectComparator;
import neos.resi.app.comparators.AttendeeComparator;
import neos.resi.app.comparators.MeetingComparator;
import neos.resi.app.comparators.ProtocolComparator;

/**
 * This class is the interface of the Application component.
 */
public class Application {

	/**
	 * Holds an instance of this class.
	 */
	private static Application instance = null;

	/**
	 * Provides access to an instance of this class.
	 * 
	 * @return instance of this class
	 */
	public static Application getInstance() {
		if (instance == null) {
			instance = new Application();
		}

		return instance;
	}

	/**
	 * Instantiates a new application.
	 */
	private Application() {
		super();

		/*
		 * Initialize instances of the management and control classes
		 */
		applicationCtl = new ApplicationControl();
		importExportCtl = new ImportExportControl();
		aspectMgmt = new AspectManagement();
		attendeeMgmt = new AttendeeManagement();
		findingMgmt = new FindingManagement();
		meetingMgmt = new MeetingManagement();
		protocolMgmt = new ProtocolManagement();
		reviewMgmt = new ReviewManagement();
		severityMgmt = new SeverityManagement();

		/*
		 * Initialize instances of the comparator classes
		 */
		aspectComp = new AspectComparator();
		attendeeComp = new AttendeeComparator();
		meetingComp = new MeetingComparator();
		protocolComp = new ProtocolComparator();
	}

	/**
	 * The application control.
	 */
	private ApplicationControl applicationCtl;

	/**
	 * The import export control.
	 */
	private ImportExportControl importExportCtl;

	/**
	 * The aspect management.
	 */
	private AspectManagement aspectMgmt;

	/**
	 * The attendee management.
	 */
	private AttendeeManagement attendeeMgmt;

	/**
	 * The finding management.
	 */
	private FindingManagement findingMgmt;

	/**
	 * The meeting management.
	 */
	private MeetingManagement meetingMgmt;

	/**
	 * The protocol management.
	 */
	private ProtocolManagement protocolMgmt;

	/**
	 * The review management.
	 */
	private ReviewManagement reviewMgmt;

	/**
	 * The severity management.
	 */
	private SeverityManagement severityMgmt;

	/**
	 * The aspect comparator.
	 */
	private AspectComparator aspectComp;

	/**
	 * The attendee comparator.
	 */
	private AttendeeComparator attendeeComp;

	/**
	 * The meeting comparator.
	 */
	private MeetingComparator meetingComp;

	/**
	 * The protocol comparator.
	 */
	private ProtocolComparator protocolComp;

	/**
	 * Returns the instance of the ApplicationControl class.
	 * 
	 * @return instance of the ApplicationControl class
	 */
	public ApplicationControl getApplicationCtl() {
		return applicationCtl;
	}

	/**
	 * Returns the instance of the ImportExportControl class.
	 * 
	 * @return instance of the ImportExportControl class
	 */
	public ImportExportControl getImportExportCtl() {
		return importExportCtl;
	}

	/**
	 * Returns the instance of the AspectManagement class.
	 * 
	 * @return instance of the AspectManagement class
	 */
	public AspectManagement getAspectMgmt() {
		return aspectMgmt;
	}

	/**
	 * Returns the instance of the AttendeeManagement class.
	 * 
	 * @return instance of the AttendeeManagement class
	 */
	public AttendeeManagement getAttendeeMgmt() {
		return attendeeMgmt;
	}

	/**
	 * Returns the instance of the FindingManagement class.
	 * 
	 * @return instance of the FindingManagement class
	 */
	public FindingManagement getFindingMgmt() {
		return findingMgmt;
	}

	/**
	 * Returns the instance of the MeetingManagement class.
	 * 
	 * @return instance of the MeetingManagement class
	 */
	public MeetingManagement getMeetingMgmt() {
		return meetingMgmt;
	}

	/**
	 * Returns the instance of the ProtocolManagement class.
	 * 
	 * @return instance of the ProtocolManagement class
	 */
	public ProtocolManagement getProtocolMgmt() {
		return protocolMgmt;
	}

	/**
	 * Returns the instance of the ReviewManagement class.
	 * 
	 * @return instance of the ReviewManagement class
	 */
	public ReviewManagement getReviewMgmt() {
		return reviewMgmt;
	}

	/**
	 * Returns the instance of the SeverityManagement class.
	 * 
	 * @return instance of the SeverityManagement class
	 */
	public SeverityManagement getSeverityMgmt() {
		return severityMgmt;
	}

	/**
	 * Returns the instance of the AspectComparator class.
	 * 
	 * @return instance of the AspectComparator class
	 */
	public AspectComparator getAspectComp() {
		return aspectComp;
	}

	/**
	 * Returns the instance of the AttendeeComparator class.
	 * 
	 * @return instance of the AttendeeComparator class
	 */
	public AttendeeComparator getAttendeeComp() {
		return attendeeComp;
	}

	/**
	 * Returns the instance of the MeetingComparator class.
	 * 
	 * @return instance of the MeetingComparator class
	 */
	public MeetingComparator getMeetingComp() {
		return meetingComp;
	}

	/**
	 * Returns the instance of the ProtocolComparator class.
	 * 
	 * @return instance of the ProtocolComparator class
	 */
	public ProtocolComparator getProtocolComp() {
		return protocolComp;
	}

}
