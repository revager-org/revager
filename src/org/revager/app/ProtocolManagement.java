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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.datatype.Duration;

import org.revager.app.model.Data;
import org.revager.app.model.ResiData;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.AttendeeReference;
import org.revager.app.model.schema.Finding;
import org.revager.app.model.schema.Meeting;
import org.revager.app.model.schema.Protocol;


/**
 * This class manages the protocols.
 */
public class ProtocolManagement {

	/**
	 * Instantiates the protocol management.
	 */
	ProtocolManagement() {
		super();
	}

	/**
	 * The resi data for accessing the current review.
	 */
	private ResiData resiData = Data.getInstance().getResiData();

	/**
	 * Returns the protocol of the meeting.
	 * 
	 * @param meet
	 *            the meeting
	 * 
	 * @return the protocol of the meeting
	 */
	public Protocol getProtocol(Meeting meet) {
		return meet.getProtocol();
	}

	/**
	 * Returns the list of protocols with findings.
	 * 
	 * @return list of protocols
	 */
	public List<Protocol> getProtocolsWithFindings() {
		List<Protocol> protocols = new ArrayList<Protocol>();

		for (Meeting m : Application.getInstance().getMeetingMgmt()
				.getMeetings()) {
			if (m.getProtocol() != null
					&& !m.getProtocol().getFindings().isEmpty()) {
				protocols.add(m.getProtocol());
			}
		}

		return protocols;
	}

	/**
	 * Gets the meeting.
	 * 
	 * @param prot
	 *            the protocol
	 * 
	 * @return the meeting
	 */
	public Meeting getMeeting(Protocol prot) {
		MeetingManagement meetMgmt = Application.getInstance().getMeetingMgmt();

		for (Meeting m : meetMgmt.getMeetings()) {
			if (m.getProtocol() != null && m.getProtocol() == prot) {
				return m;
			}
		}

		return null;
	}

	/**
	 * Sets the protocol.
	 * 
	 * @param date
	 *            the date
	 * @param start
	 *            the start time
	 * @param end
	 *            the end time
	 * @param location
	 *            the location
	 * @param meet
	 *            the meeting
	 * 
	 * @return the protocol
	 */
	public Protocol setProtocol(Calendar date, Calendar start, Calendar end,
			String location, Meeting meet) {
		Protocol prot = new Protocol();

		prot.setDate(date);
		prot.setStart(start);
		prot.setEnd(end);
		prot.setLocation(location);

		setProtocol(prot, meet);

		return prot;
	}

	/**
	 * Adds the protocol to the given meeting.
	 * 
	 * @param prot
	 *            the protocol
	 * @param meet
	 *            the meeting
	 */
	public void setProtocol(Protocol prot, Meeting meet) {
		meet.setProtocol(prot);

		resiData.fireDataChanged();
	}

	/**
	 * Removes the protocol from the given meeting.
	 * 
	 * @param meet
	 *            the meeting
	 */
	public void clearProtocol(Meeting meet) {
		meet.setProtocol(null);

		resiData.fireDataChanged();
	}

	/**
	 * Returns the list of attendees of the given protocol.
	 * 
	 * @param prot
	 *            the protocol
	 * 
	 * @return the attendees
	 */
	public List<Attendee> getAttendees(Protocol prot) {
		List<Attendee> attendees = new ArrayList<Attendee>();

		for (AttendeeReference ar : prot.getAttendeeReferences()) {
			Attendee att = Application.getInstance().getAttendeeMgmt()
					.getAttendee(Integer.parseInt(ar.getAttendee()));

			attendees.add(att);
		}

		return attendees;
	}

	/**
	 * Checks if the given attendee exists.
	 * 
	 * @param att
	 *            the attendee
	 * @param prot
	 *            the protocol
	 * 
	 * @return true, if checks if the given attendee exists
	 */
	public boolean isAttendee(Attendee att, Protocol prot) {
		for (AttendeeReference ar : prot.getAttendeeReferences()) {
			if (ar.getAttendee().equals(att.getId())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Adds an attendee to the list of attendees of the given protocol.
	 * 
	 * @param att
	 *            the attendee
	 * @param prep
	 *            the preparation time
	 * @param prot
	 *            the protocol
	 */
	public void addAttendee(Attendee att, Duration prep, Protocol prot) {
		if (!isAttendee(att, prot)) {
			AttendeeReference attRef = new AttendeeReference();
			attRef.setAttendee(att.getId());
			attRef.setPreparationTime(prep);

			prot.getAttendeeReferences().add(attRef);

			resiData.fireDataChanged();
		}
	}

	/**
	 * Removes an attendee from the list of attendees of the given protocol.
	 * 
	 * @param att
	 *            the attendee
	 * @param prot
	 *            the protocol
	 */
	public void removeAttendee(Attendee att, Protocol prot) {
		int i = 0;

		List<AttendeeReference> attRefs = prot.getAttendeeReferences();

		while (i < attRefs.size()) {
			if (attRefs.get(i).getAttendee().equals(att.getId())) {
				prot.getAttendeeReferences().remove(attRefs.get(i));
				i--;

				resiData.fireDataChanged();
			}

			i++;
		}
	}

	/**
	 * Gets the attendee's preparation time.
	 * 
	 * @param att
	 *            the attendee
	 * @param prot
	 *            the protocol
	 * 
	 * @return the attendee prep time
	 */
	public Duration getAttendeePrepTime(Attendee att, Protocol prot) {
		Duration dur = null;

		for (AttendeeReference ar : prot.getAttendeeReferences()) {
			if (ar.getAttendee().equals(att.getId())) {
				dur = ar.getPreparationTime();
			}
		}

		return dur;
	}

	/**
	 * Sets the attendee's preparation time.
	 * 
	 * @param dur
	 *            the preparation time
	 * @param att
	 *            the attendee
	 * @param prot
	 *            the protocol
	 */
	public void setAttendeePrepTime(Duration dur, Attendee att, Protocol prot) {
		for (AttendeeReference ar : prot.getAttendeeReferences()) {
			if (ar.getAttendee().equals(att.getId())) {
				ar.setPreparationTime(dur);

				resiData.fireDataChanged();
			}
		}
	}

	/**
	 * Sets the protocol comment.
	 * 
	 * @param comm
	 *            the comment
	 * @param prot
	 *            the protocol
	 */
	public void setProtocolComment(String comm, Protocol prot) {
		prot.setComments(comm);

		resiData.fireDataChanged();
	}

	/**
	 * Gets the protocol comment.
	 * 
	 * @param prot
	 *            the prot
	 * 
	 * @return the protocol comment
	 */
	public String getProtocolComment(Protocol prot) {
		return prot.getComments();
	}

	/**
	 * Checks if the protocol is complete.
	 * 
	 * @param prot
	 *            the protocol
	 * 
	 * @return true, if the given protocol is complete
	 */
	public Boolean isProtocolComplete(Protocol prot) {
		ReviewManagement reviewMgmt = Application.getInstance().getReviewMgmt();
		FindingManagement findMgmt = Application.getInstance().getFindingMgmt();

		boolean allFindComp = true;

		for (Finding find : findMgmt.getFindings(prot)) {
			if (find.getDescription() == null
					|| find.getDescription().trim().equals(""))
				allFindComp = false;
		}

		if (prot.getDate() != null && !prot.getLocation().trim().equals("")
				&& prot.getAttendeeReferences().size() != 0
				&& !reviewMgmt.getImpression().trim().equals("") && allFindComp) {
			return true;
		} else {
			return false;
		}
	}

}
