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

import org.revager.app.model.ApplicationData;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.appdata.AppSettingKey;
import org.revager.app.model.schema.Meeting;


/**
 * The Class TreeProtocol.
 */
public class TreeProtocol {

	private ApplicationData appData = Data.getInstance().getAppData();
	
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
	public String toString() {
		String clock = Data.getInstance().getLocaleStr("tree.clock");
		try {
			String date = SimpleDateFormat.getDateInstance(DateFormat.LONG)
					.format(meeting.getProtocol().getDate().getTime());
			String start = SimpleDateFormat.getTimeInstance(DateFormat.SHORT)
					.format(meeting.getProtocol().getStart().getTime());
			String end = SimpleDateFormat.getTimeInstance(DateFormat.SHORT)
					.format(meeting.getProtocol().getEnd().getTime());
			String location = meeting.getProtocol().getLocation();

			String protFrom = Data.getInstance().getLocaleStr("tree.protFrom");
			String protocolName = protFrom + " " + date + ", " + start + "-"
					+ end + " " + clock + ", " + location;

			return protocolName;

		} catch (Exception e) {
			String date = SimpleDateFormat.getDateInstance(DateFormat.LONG)
					.format(meeting.getPlannedDate().getTime());
			String start = SimpleDateFormat.getTimeInstance(DateFormat.SHORT)
					.format(meeting.getPlannedStart().getTime());
			String end = SimpleDateFormat.getTimeInstance(DateFormat.SHORT)
					.format(meeting.getPlannedEnd().getTime());
			String location = meeting.getPlannedLocation();

			String output=null;
			try {
				System.out.println(appData.getSetting(AppSettingKey.APP_LANGUAGE));
				if(appData.getSetting(AppSettingKey.APP_LANGUAGE).equals("en")){
					output=date + ", " + start + "-" + end + ", " + location;	
				}else{
					output=date + ", " + start + "-" + end + " " + clock + ", " + location;	
				}
			} catch (DataException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return output;
		}
	}

}
