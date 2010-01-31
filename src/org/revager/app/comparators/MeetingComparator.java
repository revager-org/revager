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
package org.revager.app.comparators;

import java.util.Comparator;

import org.revager.app.model.schema.Meeting;


/**
 * This class implements a comparator for Resi meetings.
 */
public class MeetingComparator implements Comparator<Meeting> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Meeting meet1, Meeting meet2) {
		if (meet1.getPlannedDate().equals(meet2.getPlannedDate())) {
			return meet1.getPlannedStart().compareTo(meet2.getPlannedStart());
		} else {
			return meet1.getPlannedDate().compareTo(meet2.getPlannedDate());
		}
	}

}
