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
package neos.resi.gui.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import neos.resi.app.model.ApplicationData;
import neos.resi.app.model.Data;
import neos.resi.app.model.DataException;
import neos.resi.app.model.appdata.AppSettingKey;
import neos.resi.app.model.schema.Meeting;
import neos.resi.gui.UI;

/**
 * The Class TreeMeeting.
 */
public class TreeMeeting {

	private ApplicationData appData = Data.getInstance().getAppData();
	
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
	public String toString() {
		String date = SimpleDateFormat.getDateInstance(DateFormat.LONG).format(
				meeting.getPlannedDate().getTime());
		String start = SimpleDateFormat.getTimeInstance(DateFormat.SHORT)
				.format(meeting.getPlannedStart().getTime());
		String end = SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(
				meeting.getPlannedEnd().getTime());
		String location = meeting.getPlannedLocation();

		String clock = Data.getInstance().getLocaleStr("tree.clock");
		String output=null;
		try {

			if(appData.getSetting(AppSettingKey.APP_LANGUAGE).equals("en")){
				output=date + ", " + start + "-" + end + ", " + location;	
			}else{
				output=date + ", " + start + "-" + end + " " + clock + ", " + location;	
			}
		} catch (DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

}
