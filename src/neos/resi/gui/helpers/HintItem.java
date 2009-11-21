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

/**
 * The Class HintItem.
 */
public class HintItem {

	public static final int ERROR = 1;
	public static final int WARNING = 2;
	public static final int OK = 3;
	public static final int INFO = 4;

	private int type = 4;

	private String text = null;

	private String helpChapter = null;

	private String helpChapterAnchor = null;

	/**
	 * Instantiates a new hint item.
	 * 
	 * @param text
	 *            the text
	 * @param type
	 *            the type
	 * @param helpChapter
	 *            the help chapter
	 * @param helpChapterAnchor
	 *            the help chapter anchor
	 */
	public HintItem(String text, int type, String helpChapter,
			String helpChapterAnchor) {
		super();

		this.text = text;
		this.type = type;
		this.helpChapter = helpChapter;
		this.helpChapterAnchor = helpChapterAnchor;
	}

	/**
	 * Instantiates a new hint item.
	 * 
	 * @param text
	 *            the text
	 * @param type
	 *            the type
	 * @param helpChapter
	 *            the help chapter
	 */
	public HintItem(String text, int type, String helpChapter) {
		this(text, type, helpChapter, null);
	}

	/**
	 * Instantiates a new hint item.
	 * 
	 * @param text
	 *            the text
	 * @param type
	 *            the type
	 */
	public HintItem(String text, int type) {
		this(text, type, null, null);
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * Gets the text.
	 * 
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text.
	 * 
	 * @param text
	 *            the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Gets the help chapter.
	 * 
	 * @return the helpChapter
	 */
	public String getHelpChapter() {
		return helpChapter;
	}

	/**
	 * Sets the help chapter.
	 * 
	 * @param helpChapter
	 *            the helpChapter to set
	 */
	public void setHelpChapter(String helpChapter) {
		this.helpChapter = helpChapter;
	}

	/**
	 * Gets the help chapter anchor.
	 * 
	 * @return the helpChapterAnchor
	 */
	public String getHelpChapterAnchor() {
		return helpChapterAnchor;
	}

	/**
	 * Sets the help chapter anchor.
	 * 
	 * @param helpChapterAnchor
	 *            the helpChapterAnchor to set
	 */
	public void setHelpChapterAnchor(String helpChapterAnchor) {
		this.helpChapterAnchor = helpChapterAnchor;
	}

}
