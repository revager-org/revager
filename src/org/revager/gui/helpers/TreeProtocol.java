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

import static org.revager.app.model.Data._;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.revager.app.model.schema.Meeting;

/**
 * The Class TreeProtocol.
 */
public class TreeProtocol {

	private Meeting meeting;

	/**
	 * Instantiates a new tree protocol.
	 */
	public TreeProtocol() {
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
	 * Instantiates a new tree protocol.
	 * 
	 * @param meeting
	 *            the meeting
	 */
	public TreeProtocol(Meeting meeting) {
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
		try {
			DateFormat dfDateLong = DateFormat
					.getDateInstance(DateFormat.LONG);
			dfDateLong.setTimeZone(meeting.getProtocol().getDate()
					.getTimeZone());

			DateFormat dfTimeShort = DateFormat
					.getTimeInstance(DateFormat.SHORT);
			dfTimeShort.setTimeZone(meeting.getProtocol().getDate()
					.getTimeZone());

			String date = dfDateLong.format(meeting.getProtocol().getDate()
					.getTime());
			String start = dfTimeShort.format(meeting.getProtocol().getStart()
					.getTime());
			String end = dfTimeShort.format(meeting.getProtocol().getEnd()
					.getTime());
			String timezone = meeting.getProtocol().getEnd().getTimeZone()
					.getDisplayName();
			String location = meeting.getProtocol().getLocation();

			if (!location.trim().equals("")) {
				location = " | " + location;
			}

			String protFrom = _("Findings List of");
			String protocolName = protFrom + " " + date + " | " + start + " - "
					+ end + " (" + timezone + ")" + location;

			return protocolName;
		} catch (Exception e) {
			return new TreeMeeting(meeting).toString();
		}
	}

}
