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

import static org.revager.app.model.Data._;

import java.awt.BorderLayout;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;

import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.gui.AbstractDialog;
import org.revager.gui.UI;

/**
 * Worker to load the embedded help for the dialogs in this software.
 */
public class LoadEmbeddedHelpWorker extends SwingWorker<String, Void> {

	/**
	 * The help panel.
	 */
	protected JPanel panelHelp = null;

	/**
	 * The help chapter.
	 */
	protected String helpChapter = null;

	/**
	 * The help chapter anchor.
	 */
	protected String helpChapterAnchor = null;

	/**
	 * Instantiates the load embedded help worker.
	 * 
	 * @param panelHelp
	 *            the help panel
	 * @param helpChapter
	 *            the help chapter
	 * @param helpChapterAnchor
	 *            the help chapter anchor
	 */
	public LoadEmbeddedHelpWorker(JPanel panelHelp, String helpChapter, String helpChapterAnchor) {
		this.panelHelp = panelHelp;
		this.helpChapter = helpChapter;
		this.helpChapterAnchor = helpChapterAnchor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected String doInBackground() throws Exception {
		String htmlString = null;
		JEditorPane helpPane = new JEditorPane();

		/*
		 * Preparations for displaying the help
		 */
		String cssRules = "body { background-color:" + AbstractDialog.HELP_BACKGROUND
				+ "; font-family: Verdana, Arial, sans-serif; font-size: 12pt; margin-left: 10px; margin-right: 10px; margin-bottom: 10px; } \n"
				+ "h1 {font-size: 20pt; padding: 0; margin-top: 20px; margin-bottom: 0; }";

		helpPane.setEditable(false);
		helpPane.setContentType("text/html");
		helpPane.setBorder(null);

		((HTMLDocument) helpPane.getDocument()).getStyleSheet().addRule(cssRules);
		// ((HTMLDocument)
		// helpPane.getDocument()).setBase(getClass().getResource(Data.getInstance().getResource("path.helpDocBase")));

		helpPane.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent ev) {
				if (ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					JEditorPane src = (JEditorPane) ev.getSource();

					if (ev.getDescription().startsWith("#")) {
						src.scrollToReference(ev.getDescription().substring(1));
					} else if (ev.getDescription().startsWith("?")) {
						String[] link = ev.getDescription().substring(1).split("#");

						if (link.length > 1) {
							UI.getInstance().getHelpBrowserFrame().showHelp(link[0], link[1]);
						} else {
							UI.getInstance().getHelpBrowserFrame().showHelp(link[0]);
						}
					}
				}
			}
		});

		/*
		 * Load help content
		 */
		try {
			htmlString = "<h1>" + Data.getInstance().getHelpData().getChapterTitle(this.helpChapter) + "</h1>";
			htmlString = htmlString + Data.getInstance().getHelpData().getChapterContent(this.helpChapter);
		} catch (DataException exc) {
			htmlString = "<h1>" + _("Cannot load help information.") + "</h1>";
		}

		helpPane.setText(htmlString);

		/*
		 * Configure scroll pane
		 */
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
		scrollPane.getViewport().setView(helpPane);

		/*
		 * Some properties for the help pane
		 */
		helpPane.setCaretPosition(0);

		panelHelp.removeAll();
		panelHelp.add(scrollPane, BorderLayout.CENTER);

		panelHelp.revalidate();

		if (helpChapterAnchor != null) {
			// helpPane.scrollRectToVisible(new Rectangle(0, 0));
			for (int i = 0; i < 4; i++) {
				Thread.sleep(100);
				helpPane.scrollToReference(helpChapterAnchor);
			}

			helpPane.repaint();
			helpPane.revalidate();
		}

		return htmlString;
	}

}
