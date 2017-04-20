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
package org.revager.app.model;

/**
 * This is a dummy class for simulating the access to the disabled help.
 */
public class HelpData {
	
	private String[] dummyChapters = { "1", "2" };

	/**
	 * Get chapter IDs as an array.
	 * 
	 * @return array of String elements where each String is an idea of a
	 *         certain chapter
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the help chapters
	 */
	public String[] getChapters() throws DataException {
		return dummyChapters;
	}

	/**
	 * Get chapter number.
	 * 
	 * @param chapter
	 *            name of the chapter
	 * 
	 * @return number of the given chapter
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the number of the chapter
	 */
	public int getChapterNumber(String chapter) throws DataException {
		return dummyChapters.length;
	}

	/**
	 * Get the title of a certain chapter.
	 * 
	 * @param chapter
	 *            the help chapter from which the title should be returned
	 * 
	 * @return the title of the given chapter
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the title of the chapter
	 */
	public String getChapterTitle(String chapter) throws DataException {
		return "";
	}

	/**
	 * Returns the given chapter as HTML code.
	 * 
	 * @param chapter
	 *            the help chapter that should be returned
	 * 
	 * @return chapter text as HTML code
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the content of the chapter
	 */
	public String getChapterContent(String chapter) throws DataException {
		return "";
	}

}
