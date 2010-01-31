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

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.revager.app.comparators.ProtocolComparator;
import org.revager.app.model.Data;
import org.revager.app.model.ResiData;
import org.revager.app.model.schema.Meeting;


/**
 * This class manages the meetings.
 */
public class MeetingManagement {

	/**
	 * Instantiates the meeting management.
	 */
	MeetingManagement() {
		super();
	}

	/**
	 * Reference to the instance of the Resi data model.
	 */
	private ResiData resiData = Data.getInstance().getResiData();

	/**
	 * Returns the meetings of the review.
	 * 
	 * @return the meetings
	 */
	public List<Meeting> getMeetings() {
		return resiData.getReview().getMeetings();
	}

	/**
	 * Gets the predecessor meeting.
	 * 
	 * @param curMeet
	 *            the current meeting
	 * 
	 * @return the predecessor meeting
	 */
	public Meeting getPredecessorMeeting(Meeting curMeet) {
		ProtocolComparator comp = Application.getInstance().getProtocolComp();
		Meeting meet = null;

		for (int i = getMeetings().size() - 1; i >= 0; i--) {
			meet = getMeetings().get(i);

			if (meet.getCanceled() == null && meet.getProtocol() != null) {
				if (comp.compare(curMeet.getProtocol(), meet.getProtocol()) > 0) {
					return meet;
				}
			}
		}

		return null;
	}

	/**
	 * Adds the given meeting.
	 * 
	 * @param date
	 *            the date
	 * @param start
	 *            the start time
	 * @param end
	 *            the end time
	 * @param location
	 *            the location
	 * 
	 * @return the meeting
	 */
	public Meeting addMeeting(Calendar date, Calendar start, Calendar end,
			String location) {
		Meeting meeting = new Meeting();

		meeting.setPlannedDate(date);
		meeting.setPlannedStart(start);
		meeting.setPlannedEnd(end);
		meeting.setPlannedLocation(location);

		addMeeting(meeting);

		return meeting;
	}

	/**
	 * Adds the given meeting to the review.
	 * 
	 * @param meet
	 *            the meeting
	 */
	public void addMeeting(Meeting meet) {
		if (meet.getComments() == null) {
			meet.setComments("");
		}

		if (!getMeetings().contains(meet)) {
			getMeetings().add(meet);

			Collections.sort(getMeetings(), Application.getInstance()
					.getMeetingComp());

			resiData.fireDataChanged();
		}
	}

	/**
	 * Removes the given meeting from the review.
	 * 
	 * @param meet
	 *            the meeting
	 */
	public void removeMeeting(Meeting meet) {
		getMeetings().remove(meet);

		resiData.fireDataChanged();
	}

	/**
	 * Removes the given meeting from the review.
	 * 
	 * @param meetIndex
	 *            the index of the meeting
	 */
	public void removeMeeting(int meetIndex) {
		getMeetings().remove(meetIndex);

		resiData.fireDataChanged();
	}

	/**
	 * Replaces an existing Meeting by another one.
	 * 
	 * @param oldMeet
	 *            the old meeting
	 * @param newMeet
	 *            the new meeting
	 */
	public void editMeeting(Meeting oldMeet, Meeting newMeet) {
		if (getMeetings().contains(oldMeet)) {
			int index = getMeetings().indexOf(oldMeet);

			getMeetings().remove(oldMeet);
			getMeetings().add(index, newMeet);

			resiData.fireDataChanged();
		}
	}

	/**
	 * Sets the meeting comment.
	 * 
	 * @param comm
	 *            the comment
	 * @param meet
	 *            the meeting
	 */
	public void setMeetingComment(String comm, Meeting meet) {
		meet.setComments(comm);

		resiData.fireDataChanged();
	}

	/**
	 * Gets the meeting comment.
	 * 
	 * @param meet
	 *            the meeting
	 * 
	 * @return the meeting comment
	 */
	public String getMeetingComment(Meeting meet) {
		return meet.getComments();
	}

	/**
	 * Sets the reason for which the meeting is canceled.
	 * 
	 * @param cancel
	 *            the reason
	 * @param meet
	 *            the meeting
	 */
	public void setMeetingCanceled(String cancel, Meeting meet) {
		meet.setCanceled(cancel);

		resiData.fireDataChanged();
	}

	/**
	 * Checks if the meeting is canceled.
	 * 
	 * @param meet
	 *            the meeting
	 * 
	 * @return true, if the meeting is canceled
	 */
	public boolean isMeetingCanceled(Meeting meet) {
		if (meet.getCanceled() != null && !meet.getCanceled().trim().equals("")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets the reason for which the meeting canceled.
	 * 
	 * @param meet
	 *            the meeting
	 * 
	 * @return the reason for which the meeting canceled
	 */
	public String getMeetingCanceled(Meeting meet) {
		return meet.getCanceled();
	}

}
