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

import neos.resi.app.model.schema.Aspect;

/**
 * This class implements a comparator for Resi aspects.
 */
public class AspectComparator implements Comparator<Aspect> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Aspect asp1, Aspect asp2) {
		int compCat = asp1.getCategory().compareTo(asp2.getCategory());
		int compDesc = asp1.getDescription().compareTo(asp2.getDescription());
		int compDir = asp1.getDirective().compareTo(asp2.getDirective());

		if (compDir != 0) {
			return compDir;
		}

		if (compDesc != 0) {
			return compDesc;
		}

		if (compCat != 0) {
			return compCat;
		}

		return 0;
	}

}
