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
package neos.resi.io;

/**
 * This interface is part of the IO Provider which is designed as Abstract
 * Factory. It defines the methods that an IO provider for Resi data must
 * implement.
 */
public interface ResiIO {

	/**
	 * Loads Review data from a file with the given name.
	 * 
	 * @param filePath
	 *            Path to file to load data from
	 * @throws ResiIOException
	 *             If loading fails
	 */
	public abstract void loadReview(String filePath) throws ResiIOException;

	/**
	 * Loads Catalog data from a file with the given name.
	 * 
	 * @param filePath
	 *            Path to file to load data from
	 * @throws ResiIOException
	 *             If loading fails
	 */
	public abstract void loadCatalog(String filePath) throws ResiIOException;

	/**
	 * Loads Aspects data from a file with the given name.
	 * 
	 * @param filePath
	 *            Path to file to load data from
	 * @throws ResiIOException
	 *             If loading fails
	 */
	public abstract void loadAspects(String filePath) throws ResiIOException;

	/**
	 * Loads review as backup from a file with the given name.
	 * 
	 * @throws ResiIOException
	 *             If loading fails
	 */
	public abstract void loadReviewBackup() throws ResiIOException;

	/**
	 * Stores Review data to a file with the given name.
	 * 
	 * @param filePath
	 *            Path to file to store data to
	 * @throws ResiIOException
	 *             If storing fails
	 */
	public abstract void storeReview(String filePath) throws ResiIOException;

	/**
	 * Stores Catalog data to a file with the given name.
	 * 
	 * @param filePath
	 *            Path to file to store data to
	 * @throws ResiIOException
	 *             If storing fails
	 */
	public abstract void storeCatalog(String filePath) throws ResiIOException;

	/**
	 * Stores Aspects data to a file with the given name.
	 * 
	 * @param filePath
	 *            Path to file to store data to
	 * @throws ResiIOException
	 *             If storing fails
	 */
	public abstract void storeAspects(String filePath) throws ResiIOException;

	/**
	 * Stores review as backup from a file with the given name.
	 * 
	 * @throws ResiIOException
	 *             If storing fails
	 */
	public abstract void storeReviewBackup() throws ResiIOException;

}
