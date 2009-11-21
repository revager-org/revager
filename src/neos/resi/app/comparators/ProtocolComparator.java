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
package neos.resi.app.comparators;

import java.util.Comparator;

import neos.resi.app.model.schema.Protocol;

/**
 * This class implements a comparator for Resi protocols.
 */
public class ProtocolComparator implements Comparator<Protocol> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Protocol prot1, Protocol prot2) {
		if (prot1.getDate().equals(prot2.getDate())) {
			return prot1.getStart().compareTo(prot2.getStart());
		} else {
			return prot1.getDate().compareTo(prot2.getDate());
		}
	}

}
