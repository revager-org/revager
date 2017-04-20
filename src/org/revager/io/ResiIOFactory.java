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
package org.revager.io;

import org.revager.io.impl.XMLResiIOFactory;

/**
 * This class is part of the IO Provider which is designed as Abstract Factory.
 * It provides access to an IO provider that can be used to load and store Resi
 * data. The {@link ResiIO} interface specifies the methods that must be
 * implemented by an IO provider.
 */
public abstract class ResiIOFactory {

	/**
	 * Holds an instance of this class.
	 */
	private static ResiIOFactory theInstance = null;

	/**
	 * Provides access to an instance of an implementation of this class.
	 * 
	 * @return instance of an implementation of this class.
	 */
	public static ResiIOFactory getInstance() {
		if (theInstance == null) {
			theInstance = new XMLResiIOFactory();
		}

		return theInstance;
	}

	/**
	 * Provides access to an IO provider for loading and saving Resi data. An IO
	 * provider implements the {@link ResiIO} interface.
	 * 
	 * @return IO provider
	 */
	public abstract ResiIO getIOProvider();

}
