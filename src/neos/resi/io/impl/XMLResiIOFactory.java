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
package neos.resi.io.impl;

import neos.resi.io.ResiIO;
import neos.resi.io.ResiIOFactory;

/**
 * This class is part of the IO Provider which is designed as Abstract Factory.
 * It is an implementation of the {@link ResiIOFactory} that creates IO provider
 * objects of the type {@link XMLResiIO}.
 */
public class XMLResiIOFactory extends ResiIOFactory {

	/**
	 * Holds an instance of a {@link XMLResiIO} object.
	 */
	private ResiIO XMLIO = null;

	public XMLResiIOFactory() {
		super();
	}

	/**
	 * Provides access to an IO provider object. In this special case to an
	 * object of type {@link XMLResiIO}.
	 * 
	 * @see neos.resi.io.ResiIOFactory#getIOProvider()
	 */
	@Override
	public ResiIO getIOProvider() {
		if (XMLIO == null) {
			XMLIO = new XMLResiIO();
		}

		return XMLIO;
	}

}
