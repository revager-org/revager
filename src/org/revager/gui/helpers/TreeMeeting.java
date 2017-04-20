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
package org.revager.gui.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.revager.app.model.schema.Meeting;

/**
 * The Class TreeMeeting.
 */
public class TreeMeeting {

	private Meeting meeting;

	/**
	 * Instantiates a new tree meeting.
	 */
	public TreeMeeting() {
		super();

		this.meeting = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object meet) {
		return meeting.equals(meet);
	}

	/**
	 * Instantiates a new tree meeting.
	 * 
	 * @param meeting
	 *            the meeting
	 */
	public TreeMeeting(Meeting meeting) {
		super();

		this.meeting = meeting;
	}

	/**
	 * Gets the meeting.
	 * 
	 * @return the meeting
	 */
	public Meeting getMeeting() {
		return meeting;
	}

	/**
	 * Sets the meeting.
	 * 
	 * @param meeting
	 *            the new meeting
	 */
	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		DateFormat dfDateLong = DateFormat
				.getDateInstance(DateFormat.LONG);
		dfDateLong.setTimeZone(meeting.getPlannedDate().getTimeZone());

		DateFormat dfTimeShort = DateFormat
				.getTimeInstance(DateFormat.SHORT);
		dfTimeShort.setTimeZone(meeting.getPlannedDate().getTimeZone());

		String date = dfDateLong.format(meeting.getPlannedDate().getTime());
		String start = dfTimeShort.format(meeting.getPlannedStart().getTime());
		String end = dfTimeShort.format(meeting.getPlannedEnd().getTime());
		String timezone = meeting.getPlannedEnd().getTimeZone()
				.getDisplayName();
		String location = meeting.getPlannedLocation();

		if (!location.trim().equals("")) {
			location = " | " + location;
		}
		
		String output = date + " | " + start + " - " + end + " (" + timezone
				+ ")" + location;

		return output;
	}

}
