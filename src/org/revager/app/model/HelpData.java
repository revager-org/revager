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

import static org.revager.app.model.Data._;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * This class provides access to the help data.
 */
public class HelpData {

	/**
	 * Absolute path of help data for the images.
	 */
	private String helpPath = "";

	/**
	 * Array with the help chapter ids.
	 */
	private String[] helpChapters = null;

	/**
	 * Array with the titles of the help chapters.
	 */
	private String[] helpChaptersTitle = null;

	/**
	 * Array with HTML content of the help chapters.
	 */
	private String[] helpChaptersContent = null;

	/**
	 * HTML head for the help chapters.
	 */
	private String helpHead = null;

	/**
	 * HTML foot for the help chapters.
	 */
	private String helpFoot = null;

	/**
	 * This method is internally used to read a HTML file (by URL).
	 * 
	 * @param resource
	 *            the resource
	 * 
	 * @return content of file as String
	 * 
	 * @throws IOException
	 *             if an error occurs while reading the file
	 */
	private String readHtmlFile(URL resource) throws IOException {
		BufferedReader fileReader = null;
		InputStream htmlFile = null;

		String htmlLine = null;
		StringBuffer htmlStrBfr = null;

		htmlFile = (InputStream) resource.openStream();

		fileReader = new BufferedReader(
				new InputStreamReader(htmlFile, "UTF-8"));

		htmlStrBfr = new StringBuffer();

		while ((htmlLine = fileReader.readLine()) != null) {
			htmlStrBfr.append(htmlLine + "\n");
		}

		fileReader.close();
		htmlFile.close();

		String htmlStr = htmlStrBfr.toString();

		/*
		 * Customizing path of images
		 */
		htmlStr = Pattern
				.compile("<img \\p{Blank}*src=\"", Pattern.CASE_INSENSITIVE)
				.matcher(htmlStr).replaceAll("<img src=\"" + helpPath);

		return htmlStr;
	}

	/**
	 * This method is internally used to load the chapters, its titles and its
	 * content into the arrays.
	 * 
	 * @throws DataException
	 *             If there is an error while loading the help data
	 */
	private void readInChapters() throws DataException {
		String htmlStr = null;

		/*
		 * Create absolute path of help data to display the images
		 */
		String currentDirectory = new File(HelpData.class.getProtectionDomain()
				.getCodeSource().getLocation().getPath()).getAbsolutePath()
				.replace("\\", "/");

		if (!currentDirectory.startsWith("/")) {
			/*
			 * Only part of unit testing if you run the unit tests under
			 * Windows; there the starting slash is missing.
			 */
			currentDirectory = "/" + currentDirectory;
		}

		helpPath = "file:" + currentDirectory
				+ Data.getInstance().getResource("path.helpDocBase");

		if (currentDirectory.toLowerCase().endsWith(".jar")
				|| currentDirectory.toLowerCase().endsWith(".exe")) {
			/*
			 * Not part of unit testing because the correctness of the showed
			 * images is checked in the system test. Therefore the application
			 * has to be run from a Jar file.
			 */
			helpPath = "jar:file:" + currentDirectory + "!"
					+ Data.getInstance().getResource("path.helpDocBase");
		}

		try {
			/*
			 * Read head and foot
			 */
			helpHead = readHtmlFile(getClass().getResource(
					Data.getInstance().getResource("path.help") + "head.html"));

			helpFoot = readHtmlFile(getClass().getResource(
					Data.getInstance().getResource("path.help") + "foot.html"));
		} catch (Exception e) {
			/*
			 * Not part of unit testing because this exception only is thrown if
			 * an internal error occurs which cannot be provoked.
			 */
			throw new DataException(_("Cannot load help information."));
		}

		/*
		 * Initialize chapter arrays
		 */
		helpChapters = Data.getInstance().getResource("helpChapters")
				.split(",");
		helpChaptersTitle = new String[helpChapters.length];
		helpChaptersContent = new String[helpChapters.length];

		/*
		 * Read in chapter titles and content
		 */
		for (int i = 0; i < helpChapters.length; i++) {

			try {
				htmlStr = readHtmlFile(getClass().getResource(
						Data.getInstance().getResource("path.help")
								+ Data.getInstance().getLocale().getLanguage()
								+ "/" + helpChapters[i] + ".html"));

				helpChaptersContent[i] = helpHead
						+ htmlStr.substring(htmlStr.indexOf("-->") + 3)
						+ helpFoot;

				helpChaptersTitle[i] = htmlStr.substring(
						htmlStr.indexOf("<!--#TITLE") + 10,
						htmlStr.indexOf("-->")).trim();

				if (helpChaptersTitle[i].isEmpty()) {
					/*
					 * Not part of unit testing because normally you don't have
					 * any chapters without a title.
					 */
					helpChaptersTitle[i] = _("<no title>");
				}
			} catch (Exception e) {
				/*
				 * Not part of unit testing because this exception only is
				 * thrown if an internal error occurs which cannot be provoked.
				 */
				throw new DataException(_("Cannot load help information."));
			}

		}
	}

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
		if (helpChapters == null) {
			readInChapters();
		}

		return helpChapters.clone();
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
		if (helpChapters == null) {
			readInChapters();
		}

		int chapterNumber = Integer.MAX_VALUE;

		for (int i = 0; helpChapters.length > i; i++) {
			if (helpChapters[i].equals(chapter)) {
				chapterNumber = i;

				break;
			}
		}

		if (chapterNumber == Integer.MAX_VALUE) {
			/*
			 * Not part of unit testing because normally you have not more than
			 * 10-20 chapters. This exception is only thrown if you have more
			 * than Integer.MAX_VALUE chapters.
			 */
			throw new DataException();
		}

		return chapterNumber;
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
		if (helpChapters == null) {
			readInChapters();
		}

		String title = null;

		for (int i = 0; i < helpChapters.length; i++) {
			if (helpChapters[i].equals(chapter)) {
				title = helpChaptersTitle[i];
			}
		}

		if (title == null) {
			throw new DataException(_("Cannot find requested help chapter.")
					+ " [CHAPTER = " + chapter + "]");
		}

		return title;
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
		if (helpChapters == null) {
			readInChapters();
		}

		String htmlCode = null;

		for (int i = 0; i < helpChapters.length; i++) {
			if (helpChapters[i].equals(chapter)) {
				htmlCode = helpChaptersContent[i];
			}
		}

		if (htmlCode == null) {
			throw new DataException(_("Cannot find requested help chapter.")
					+ " [CHAPTER = " + chapter + "]");
		}

		return htmlCode;
	}

}
