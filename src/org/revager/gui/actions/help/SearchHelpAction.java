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
package org.revager.gui.actions.help;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.tree.DefaultMutableTreeNode;

import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.gui.HelpBrowserFrame;

/**
 * The Class SearchHelpAction.
 */
@SuppressWarnings("serial")
public class SearchHelpAction extends AbstractAction {

	/**
	 * Instantiates a new search help action.
	 */
	public SearchHelpAction() {
		super();
	}

	/**
	 * Gets the local string.
	 * 
	 * @param searchString
	 *            the search string
	 * @param chapterContent
	 *            the chapter content
	 * 
	 * @return the local string
	 */
	public static String getLocalString(String searchString,
			String chapterContent) {

		String localString = "";

		if (chapterContent.indexOf("<") > 0) {
			String local = chapterContent.substring(0, chapterContent
					.indexOf("<"));

			String localLow = local.toLowerCase();
			String searchLow = searchString.toLowerCase();
			if (localLow.indexOf(searchLow) != -1) {
				String replacement = "";
				replacement = local.substring(0, localLow.indexOf(searchLow));
				replacement = replacement.concat(Data.getInstance()
						.getResource("helpHtmlMarkingBegin"));
				replacement = replacement.concat(local.substring(localLow
						.indexOf(searchLow), localLow.indexOf(searchLow)
						+ searchString.length()));
				replacement = replacement.concat(Data.getInstance()
						.getResource("helpHtmlMarkingEnd"));
				localString = localString.concat(replacement);
				chapterContent = chapterContent.substring(localLow
						.indexOf(searchLow)
						+ searchString.length());
			} else {
				localString = localString.concat(local);
				chapterContent = chapterContent.substring(chapterContent
						.indexOf("<"));
			}
		}
		if (chapterContent.indexOf("<") == 0) {
			localString = localString.concat(chapterContent.substring(0,
					chapterContent.indexOf(">") + 1));
			chapterContent = chapterContent.substring(chapterContent
					.indexOf(">") + 1);
		}
		if (chapterContent.indexOf("<") != -1) {
			localString = localString.concat(getLocalString(searchString,
					chapterContent));
		}

		return localString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent SearchHelp) {
		try {

			HelpBrowserFrame.getBodyPane().setText(
					"<H1>"
							+ Data.getInstance().getHelpData().getChapterTitle(
									"start")
							+ "</H1>"
							+ Data.getInstance().getHelpData()
									.getChapterContent("start"));

		} catch (DataException e) {
			/*
			 * do nothing
			 */
		}

		String[] chapters = null;
		String helpTitle = null;
		String helpChapter = null;

		try {
			chapters = Data.getInstance().getHelpData().getChapters();
		} catch (DataException e1) {
			chapters = null;
		}
		String searchString = HelpBrowserFrame.getSrchTxtFld().getText();

		DefaultMutableTreeNode root = new DefaultMutableTreeNode(
				HelpBrowserFrame.getHelpStartTitle());

		// for-loop starting at 1 because 1-element is start.html
		for (int nodeCnt = 1; nodeCnt < chapters.length; nodeCnt++) {

			boolean gefunden = false;
			// Suche in Bezeichnung

			try {
				helpTitle = Data.getInstance().getHelpData().getChapterTitle(
						chapters[nodeCnt]);
				helpChapter = Data.getInstance().getHelpData()
						.getChapterContent(chapters[nodeCnt]).toString();

			} catch (DataException ChapterTitelError) {
				System.out
						.println("Error: can't read ChapterTitel in HelpBrowserFrame");
			}

			for (int index = 0; index < (helpTitle.length()
					- searchString.length() + 1); index++) {

				if (searchString.equalsIgnoreCase((helpTitle.substring(index,
						index + searchString.length())))
						& gefunden == false) {
					DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(
							helpTitle);
					root.add(dmtn);
					gefunden = true;

				}

			}
			if (true) {
				for (int index = 0; index < (helpChapter.length()
						- searchString.length() + 1); index++) {
					if (searchString.equalsIgnoreCase(helpChapter.substring(
							index, index + searchString.length()))
							& gefunden == false) {
						DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(
								helpTitle);
						root.add(dmtn);
						gefunden = true;

					}
				}
			}

		}

		HelpBrowserFrame.setTree(root);

		for (int nodeCnt = 1; nodeCnt < chapters.length; nodeCnt++) {
			try {
				helpTitle = Data.getInstance().getHelpData().getChapterTitle(
						chapters[nodeCnt]);
			} catch (DataException ChapterTitelError) {
				System.out
						.println("Error: can't read ChapterTitel in HelpBrowserFrame");
			}
		}
	}

}
