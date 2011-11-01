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
package org.revager.gui;

import static org.revager.app.model.Data._;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.border.MatteBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeSelectionModel;

import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.gui.actions.ActionRegistry;
import org.revager.gui.actions.help.ResetHelpAction;
import org.revager.gui.actions.help.SearchHelpAction;
import org.revager.tools.GUITools;

/**
 * This class represents the help browser frame.
 */
@SuppressWarnings("serial")
public class HelpBrowserFrame extends AbstractFrame {

	private static JEditorPane bodyPane = null;

	private static JTextField srchTxtFld = null;

	private static JTree tree = null;

	private static String helpStartTitle = null;

	private static String helpStartContent = null;

	private static JScrollPane scrollP = null;

	private static JScrollPane scrollPPanel = new JScrollPane();

	private static JSplitPane horizontalSplit = null;

	/* CSS rules */
	private String cssRule = "body { font-family: Verdana, Arial, sans-serif; font-size: 12pt; margin-left: 20px; margin-right: 20px; margin-bottom: 20px; } \n"
			+ "h1 {font-size: 20pt; padding: 0; margin-top: 35px; margin-bottom: 0; }";

	/**
	 * Instantiates a new help browser frame.
	 */
	public HelpBrowserFrame() {
		super();

		// setAlwaysOnTop(true);

		setIcon(Data.getInstance().getIcon("helpBrowser_50x50.png"));

		setStatusBarVisible(false);

		setTitle(_("RevAger Online Help"));

		getContentPane().setLayout(new BorderLayout());

		GridBagLayout gblTop = new GridBagLayout();
		JPanel panelTop = new JPanel(gblTop);
		panelTop.setBackground(Color.WHITE);
		panelTop.setBorder(new MatteBorder(10, 7, 10, 7, Color.WHITE));

		srchTxtFld = new JTextField();
		srchTxtFld.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				ActionRegistry.getInstance()
						.get(SearchHelpAction.class.getName())
						.actionPerformed(null);
			}
		});
		srchTxtFld.setToolTipText(_("Filter/Search - Enter search item ..."));
		srchTxtFld.setColumns(30);
		srchTxtFld.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		srchTxtFld.setBackground(new Color(254, 255, 193));

		JButton reset = GUITools.newImageButton();
		reset.setIcon(Data.getInstance().getIcon("undo_22x22_0.png"));
		reset.setRolloverIcon(Data.getInstance().getIcon("undo_22x22.png"));
		reset.setToolTipText(_("Reset filter"));
		reset.addActionListener(ActionRegistry.getInstance().get(
				ResetHelpAction.class.getName()));

		JTextArea descText = new JTextArea(
				_("Please enter a search item into the input field to filter the contents:"));
		descText.setEditable(false);
		descText.setFont(FONT_TEXT);
		descText.setForeground(Color.DARK_GRAY);
		descText.setSelectionColor(Color.WHITE);

		GUITools.addComponent(panelTop, gblTop, descText, 0, 0, 1, 1, 1.0, 0,
				0, 3, 8, 3, GridBagConstraints.BOTH, GridBagConstraints.WEST);
		GUITools.addComponent(panelTop, gblTop, srchTxtFld, 0, 1, 1, 1, 1.0, 0,
				0, 3, 0, 6, GridBagConstraints.BOTH, GridBagConstraints.WEST);
		GUITools.addComponent(panelTop, gblTop, reset, 1, 1, 1, 1, 0, 0, 0, 0,
				0, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST);

		addTopComponent(panelTop);

		try {
			helpStartTitle = Data.getInstance().getHelpData()
					.getChapterTitle("start");
			helpStartContent = Data.getInstance().getHelpData()
					.getChapterContent("start");
		} catch (DataException startMissingError) {
			JOptionPane.showMessageDialog(UI.getInstance().getMainFrame(),
					_("Cannot load the start page."), _("Error occured"),
					JOptionPane.ERROR_MESSAGE);
		}

		tree = new JTree(getStandardRoot());
		tree.setModel(new DefaultTreeModel(getStandardRoot()));
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setBorder(null);

		/* Disallow collapsing the tree */
		tree.addTreeWillExpandListener(new TreeWillExpandListener() {
			@Override
			public void treeWillExpand(TreeExpansionEvent e) {
			}

			@Override
			public void treeWillCollapse(TreeExpansionEvent e)
					throws ExpandVetoException {
				throw new ExpandVetoException(e, "you can't collapse this tree");
			}
		});

		JPanel panel = new JPanel(new BorderLayout());

		// initializing start site of the online help
		resetBodyPane();

		scrollP = new JScrollPane(tree);

		scrollP.setMinimumSize(new Dimension(0, 500));
		scrollP.setPreferredSize(new Dimension(150, 500));
		scrollPPanel.setMinimumSize(new Dimension(0, 500));
		scrollPPanel.setPreferredSize(new Dimension(300, 500));
		panel.add(scrollPPanel, BorderLayout.CENTER);

		horizontalSplit = new JSplitPane(1, true, scrollP, panel);
		horizontalSplit.setDividerSize(7);
		horizontalSplit.setDividerLocation(220);
		horizontalSplit.setOneTouchExpandable(true);
		horizontalSplit.setBorder(null);

		getContentPane().setLayout(new BorderLayout());
		add(horizontalSplit, BorderLayout.CENTER);

		pack();

		/*
		 * choosing one chapter or sub-chapter will activate following
		 * TreeSelectionListener, which attributes the content to the title of
		 * this chapter.
		 */
		TreeSelectionListener treeSelected = new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent arg0) {
				String path;
				if (arg0.getNewLeadSelectionPath() != null) {
					path = arg0.getNewLeadSelectionPath()
							.getLastPathComponent().toString();
				} else {
					path = helpStartTitle;
				}

				String titleHtml = "<H1>" + path + "</H1>";

				String[] helpChapters = Data.getInstance()
						.getResource("helpChapters").split(",");

				for (int nodeCnt = 0; nodeCnt < helpChapters.length; nodeCnt++) {
					try {
						if (path.equals(Data.getInstance().getHelpData()
								.getChapterTitle(helpChapters[nodeCnt]))) {
							if (!srchTxtFld.getText().trim().equals("")
									& nodeCnt != 0) {
								String searchString = HelpBrowserFrame
										.getSrchTxtFld().getText();
								bodyPane.setText(SearchHelpAction
										.getLocalString(
												searchString,
												titleHtml
														+ Data.getInstance()
																.getHelpData()
																.getChapterContent(
																		helpChapters[nodeCnt])));
							} else {
								bodyPane.setText(titleHtml
										+ Data.getInstance()
												.getHelpData()
												.getChapterContent(
														helpChapters[nodeCnt]));
							}
						} else {
							if (path.equals("helpTitle")) {
								bodyPane.setText(titleHtml
										+ Data.getInstance().getHelpData()
												.getChapterContent("start"));
							}
						}
					} catch (DataException e) {
						System.err
								.println("Error: can't read chapterContent in HelpBrowserFrame");
					}

				}

				scrollPPanel.getViewport().setViewPosition(new Point(0, 0));
				bodyPane.setCaretPosition(0);
			}
		};

		tree.getSelectionModel().addTreeSelectionListener(treeSelected);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		setSize(screenSize.width / 2, screenSize.height - 150);

		setLocationToCenter();

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Window#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean vis) {
		super.setVisible(vis);

		toFront();

		srchTxtFld.requestFocus();
	}

	/**
	 * Reset body pane.
	 */
	private void resetBodyPane() {
		bodyPane = new JEditorPane();

		/*
		 * Set properties of EditorPane
		 */
		bodyPane.setEditable(false);
		bodyPane.setBorder(null);
		bodyPane.setContentType("text/html");

		((HTMLDocument) bodyPane.getDocument()).getStyleSheet()
				.addRule(cssRule);

		// base definition is not functional for images in a jar file
		// ((HTMLDocument)
		// bodyPane.getDocument()).setBase(getClass().getResource(Data.getInstance().getResource("path.helpDocBase")));

		/* Handle links */
		bodyPane.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent ev) {
				if (ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					JEditorPane src = (JEditorPane) ev.getSource();

					if (ev.getDescription().startsWith("#")) {
						src.scrollToReference(ev.getDescription().substring(1));
					} else if (ev.getDescription().startsWith("?")) {
						String[] link = ev.getDescription().substring(1)
								.split("#");

						setHelpChapter(link[0]);

						if (link.length > 1) {
							// ODOT JOJO Funktioniert leider nicht :-(
							// resetBodyPane();
							// setHelpChapter(link[0]);
							setHelpChapterAnchor(link[1]);
							// GUITools.executeSwingWorker(new
							// LoadHelpWorker(link[0], link[1]));
						}
					}
				}
			}
		});

		// initializing start site of the online help
		setHelpChapter("start");

		scrollPPanel.setViewportView(bodyPane);

		scrollPPanel.repaint();
		scrollPPanel.revalidate();
	}

	/**
	 * Sets the error page.
	 */
	private void setErrorPage() {
		bodyPane.setText("<H1>" + _("Error") + "</H1>" + "<P>"
				+ _("Cannot find help page.") + "</P>");
	}

	/**
	 * Sets the help chapter.
	 * 
	 * @param helpChapter
	 *            the new help chapter
	 */
	private void setHelpChapter(String helpChapter) {
		// ODOT
		// updateBodyPane();
		ActionRegistry.getInstance().get(ResetHelpAction.class.getName())
				.actionPerformed(null);

		try {
			tree.setSelectionRow(Data.getInstance().getHelpData()
					.getChapterNumber(helpChapter));
		} catch (DataException e) {
			tree.setSelectionRow(-1);
			setErrorPage();
		}

		bodyPane.repaint();
		bodyPane.revalidate();

		bodyPane.setCaretPosition(0);
	}

	/**
	 * Sets the help chapter anchor.
	 * 
	 * @param helpChapterAnchor
	 *            the new help chapter anchor
	 */
	private void setHelpChapterAnchor(String helpChapterAnchor) {
		// bodyPane.setCaretPosition(0);
		for (int i = 0; i < 4; i++) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				/*
				 * do nothing
				 */
			}

			bodyPane.scrollToReference(helpChapterAnchor);
		}

		// bodyPane.scrollRectToVisible(new Rectangle(0, 0));
		// bodyPane.scrollToReference(helpChapterAnchor);

		bodyPane.repaint();
		// bodyPane.revalidate();
	}

	/**
	 * Show help.
	 */
	public void showHelp() {
		setVisible(true);
		toFront();
	}

	/**
	 * Show help.
	 * 
	 * @param chapter
	 *            the chapter
	 */
	public void showHelp(String chapter) {
		setVisible(true);
		toFront();
		setHelpChapter(chapter);
	}

	/**
	 * Show help.
	 * 
	 * @param chapter
	 *            the chapter
	 * @param anchor
	 *            the anchor
	 */
	public void showHelp(String chapter, String anchor) {
		setVisible(true);
		toFront();
		setHelpChapter(chapter);
		setHelpChapterAnchor(anchor);

		// try {
		// setHelpChapterAnchor(anchor);
		// } catch (Exception e) {
		// resetBodyPane();
		// showHelp(chapter, anchor);
		// }
	}

	/**
	 * Gets the srch txt fld.
	 * 
	 * @return the srch txt fld
	 */
	public static JTextField getSrchTxtFld() {
		return srchTxtFld;
	}

	/**
	 * Gets the body pane.
	 * 
	 * @return the body pane
	 */
	public static JEditorPane getBodyPane() {
		return bodyPane;
	}

	/**
	 * Sets the tree.
	 * 
	 * @param root
	 *            the new tree
	 */
	synchronized public static void setTree(DefaultMutableTreeNode root) {
		tree.setModel(new DefaultTreeModel(root));
	}

	/**
	 * Gets the tree.
	 * 
	 * @return the tree
	 */
	public static JTree getTree() {
		return tree;
	}

	/**
	 * Gets the help start title.
	 * 
	 * @return the help start title
	 */
	public static String getHelpStartTitle() {
		return helpStartTitle;
	}

	/**
	 * Gets the help start content.
	 * 
	 * @return the help start content
	 */
	public static String getHelpStartContent() {
		return helpStartContent;
	}

	/**
	 * Gets the standard root.
	 * 
	 * @return the standard root
	 */
	public static DefaultMutableTreeNode getStandardRoot() {

		DefaultMutableTreeNode root = new DefaultMutableTreeNode(helpStartTitle);
		String[] chapters = null;
		String helpTitle = null;
		try {
			chapters = Data.getInstance().getHelpData().getChapters();
		} catch (DataException e) {
			System.err
					.println("Error: no Chapters available in HelpBrowserFrame");

		}

		// for-loop starting at 1 because 1-element is start.html

		for (int nodeCnt = 1; nodeCnt < chapters.length; nodeCnt++) {
			try {
				helpTitle = Data.getInstance().getHelpData()
						.getChapterTitle(chapters[nodeCnt]);
			} catch (DataException e) {
				System.err
						.println("Error: can't read ChapterTitel in HelpBrowserFrame");
			}

			DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(helpTitle);
			root.add(dmtn);

		}
		return root;
	}

}