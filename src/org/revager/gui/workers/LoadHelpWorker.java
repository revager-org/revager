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
package org.revager.gui.workers;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.revager.gui.UI;

/**
 * Worker to load the help at the given chapter and anchor
 */
public class LoadHelpWorker extends SwingWorker<Void, Void> {

	/**
	 * The help chapter.
	 */
	private String helpChapter = null;

	/**
	 * The help chapter anchor.
	 */
	private String helpChapterAnchor = null;

	/**
	 * Instantiates a new load help worker.
	 */
	public LoadHelpWorker() {
		super();
	}

	/**
	 * Instantiates a new load help worker.
	 * 
	 * @param helpChapter
	 *            the help chapter
	 * @param helpChapterAnchor
	 *            the help chapter anchor
	 */
	public LoadHelpWorker(String helpChapter, String helpChapterAnchor) {
		this();
		this.helpChapter = helpChapter;
		this.helpChapterAnchor = helpChapterAnchor;
	}

	/**
	 * Instantiates a new load help worker.
	 * 
	 * @param helpChapter
	 *            the help chapter
	 */
	public LoadHelpWorker(String helpChapter) {
		this(helpChapter, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					if (helpChapter != null && helpChapterAnchor != null) {
						UI.getInstance().getHelpBrowserFrame()
								.showHelp(helpChapter, helpChapterAnchor);
					} else if (helpChapter != null && helpChapterAnchor == null) {
						UI.getInstance().getHelpBrowserFrame()
								.showHelp(helpChapter);
					} else {
						UI.getInstance().getHelpBrowserFrame().showHelp();
					}
				} catch (Exception e) {
					UI.getInstance().getHelpBrowserFrame().setVisible(false);
					UI.getInstance().getHelpBrowserFrame().showHelp();
				}
			}
		});

		return null;
	}
}
