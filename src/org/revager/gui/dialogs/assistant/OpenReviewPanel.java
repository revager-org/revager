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
package org.revager.gui.dialogs.assistant;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.revager.app.ResiFileFilter;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.gui.AbstractDialog;
import org.revager.gui.AbstractDialogPanel;
import org.revager.gui.UI;
import org.revager.gui.helpers.FileChooser;
import org.revager.gui.helpers.HLink;
import org.revager.gui.helpers.LinkGroup;
import org.revager.gui.workers.LoadReviewWorker;
import org.revager.tools.GUITools;

/**
 * The class OpenReviewPanel.
 * 
 * @author D.Casciato
 *
 */
@SuppressWarnings("serial")
public class OpenReviewPanel extends AbstractDialogPanel{

	private GridBagLayout gbl2 = new GridBagLayout();
	

	/*
	 * Strings
	 */
	private String moderatorStrng = Data.getInstance().getLocaleStr(
			"mode.moderator");
	private String scribeStrng = Data.getInstance().getLocaleStr(
			"mode.scribeOrSingle");
	private String anotherRevStrng = Data.getInstance().getLocaleStr(
			"assistantDialog.selectAnotherRev");
	private String noRevsStrng = Data.getInstance().getLocaleStr(
			"assistantDialog.noRevs");;
	private String firstRevStrng;
	private String secondRevStrng;
	private String thirdRevStrng;
	private String fourthRevStrng;

	/*
	 * ImageIcons
	 */
	private ImageIcon smallModeratorIcon = Data.getInstance().getIcon(
			"moderator_50x50_0.png");
	private ImageIcon smallModeratorRolloverIcon = Data.getInstance().getIcon(
			"moderator_50x50.png");
	private ImageIcon scribeIcon = Data.getInstance().getIcon(
			"scribe_50x50_0.png");
	private ImageIcon scribeRolloverIcon = Data.getInstance().getIcon(
			"scribe_50x50.png");
	private ImageIcon reviewIcon = Data.getInstance().getIcon(
			"review_40x40_0.png");
	private ImageIcon reviewRolloverIcon = Data.getInstance().getIcon(
			"review_40x40.png");
	private ImageIcon browseIcon = Data.getInstance().getIcon(
			"open_40x40_0.png");
	private ImageIcon browseRolloverIcon = Data.getInstance().getIcon(
			"open_40x40.png");

	/*
	 * links and LinkGroups
	 */
	private LinkGroup modeGrp = new LinkGroup();
	private HLink moderatorLnk = new HLink(moderatorStrng, smallModeratorIcon,
			smallModeratorRolloverIcon, modeGrp);
	private HLink scribeSingleRevLnk = new HLink(scribeStrng, scribeIcon,
			scribeRolloverIcon, modeGrp);

	private LinkGroup lastRevsGrp = new LinkGroup();
	private HLink firstReviewLnk;
	private HLink secondReviewLnk;
	private HLink thirdReviewLnk;
	private HLink fourthReviewLnk;
	private HLink anotherReviewLnk;

	/*
	 * Others
	 */
	private JLabel noRevsLbl = new JLabel(noRevsStrng);

	private Vector<String> lastRevsVector = getLastReviews();

	/**
	 * Action to open an existing review.
	 */
	public ActionListener openExistRev = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			try {
				List<String> lastRevs = Data.getInstance().getAppData()
						.getLastReviews();
				int index = lastRevsGrp.getSelectedLinkIndex();
				String revPath = lastRevs.get(index);
				new LoadReviewWorker(revPath).execute();
			} catch (DataException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	};

	/**
	 * Action to open another review. 
	 */
	private ActionListener openAnotherRev = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			FileChooser fileChooser = UI.getInstance().getFileChooser();

			if (fileChooser.showDialog(UI.getInstance().getAssistantDialog(),
					FileChooser.MODE_OPEN_FILE, ResiFileFilter.TYPE_REVIEW) == FileChooser.SELECTED_APPROVE) {
				String reviewPath = fileChooser.getFile().getAbsolutePath();
				new LoadReviewWorker(reviewPath).execute();
			}
		}
	};
	
	/**
	 * Returns the group of the recent reviews.
	 * @return
	 */
	public LinkGroup getLastRevsGrp() {
		return lastRevsGrp;
	}

	/**
	 * Returns the vector of the recent reviews. 
	 * @return
	 */
	public Vector<String> getLastRevsVector() {
		return lastRevsVector;
	}

	/**
	 * Constructor
	 * 
	 * @param parent
	 */
	public OpenReviewPanel(AbstractDialog parent) {
		super(parent);
		createOpenReviewPnl();
	}
	
	/**
	 * Method which creates and locates the component of this panel.
	 */
	private void createOpenReviewPnl() {

		this.setLayout(gbl2);

		moderatorLnk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				UI.getInstance().getAssistantDialog().setLocalMode("moderator");
				Data.getInstance().setMode("moderator");
			}
		});

		scribeSingleRevLnk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				UI.getInstance().getAssistantDialog().setLocalMode("scribe");
				Data.getInstance().setMode("scribe");
			}
		});

		modeGrp.addLink(moderatorLnk);
		modeGrp.addLink(scribeSingleRevLnk);
		modeGrp.selectLink(moderatorLnk);

		GUITools.addComponent(this, gbl2, moderatorLnk, 0, 0, 1, 1,
				1.0, 0.0, 0, 20, 0, 84, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl2, scribeSingleRevLnk, 0, 1,
				1, 1, 1.0, 0.0, 0, 20, 0, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl2, new JSeparator(
				SwingConstants.VERTICAL), 1, 0, 1, 6, 0.0, 1.0, 0, 0, 0, 0,
				GridBagConstraints.VERTICAL, GridBagConstraints.NORTHWEST);

		try {
			firstRevStrng = lastRevsVector.get(0);
			firstReviewLnk = new HLink(firstRevStrng, reviewIcon,
					reviewRolloverIcon, lastRevsGrp);
			lastRevsGrp.addLink(firstReviewLnk);
			lastRevsGrp.selectLink(firstReviewLnk);
			GUITools.addComponent(this, gbl2, firstReviewLnk, 2, 0,
					1, 1, 1.0, 0.0, 0, 40, 0, 0, GridBagConstraints.HORIZONTAL,
					GridBagConstraints.NORTHWEST);

		} catch (Exception e) {
			noRevsLbl.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
			GUITools.addComponent(this, gbl2, noRevsLbl, 2, 0, 1, 1,
					1.0, 0.0, 15, 80, 0, 0, GridBagConstraints.HORIZONTAL,
					GridBagConstraints.NORTHWEST);
		}
		try {
			secondRevStrng = lastRevsVector.get(1);
			secondReviewLnk = new HLink(secondRevStrng, reviewIcon,
					reviewRolloverIcon, lastRevsGrp);
			lastRevsGrp.addLink(secondReviewLnk);
			GUITools.addComponent(this, gbl2, secondReviewLnk, 2, 1,
					1, 1, 1.0, 0.0, 0, 40, 0, 0, GridBagConstraints.HORIZONTAL,
					GridBagConstraints.NORTHWEST);

		} catch (Exception e) {

		}
		try {
			thirdRevStrng = lastRevsVector.get(2);
			thirdReviewLnk = new HLink(thirdRevStrng, reviewIcon,
					reviewRolloverIcon, lastRevsGrp);
			lastRevsGrp.addLink(thirdReviewLnk);
			GUITools.addComponent(this, gbl2, thirdReviewLnk, 2, 2,
					1, 1, 1.0, 0.0, 0, 40, 0, 0, GridBagConstraints.HORIZONTAL,
					GridBagConstraints.NORTHWEST);

		} catch (Exception e) {

		}

		try {
			fourthRevStrng = lastRevsVector.get(3);
			fourthReviewLnk = new HLink(fourthRevStrng, reviewIcon,
					reviewRolloverIcon, lastRevsGrp);
			lastRevsGrp.addLink(fourthReviewLnk);
			GUITools
					.addComponent(this, gbl2, fourthReviewLnk, 2, 3,
							1, 1, 1.0, 0.0, 10, 40, 0, 0,
							GridBagConstraints.HORIZONTAL,
							GridBagConstraints.NORTHWEST);

		} catch (Exception e) {

		}
		anotherReviewLnk = new HLink(anotherRevStrng, browseIcon,
				browseRolloverIcon, null);
		anotherReviewLnk.setUnderlined(true);
		anotherReviewLnk.addActionListener(openAnotherRev);
		GUITools.addComponent(this, gbl2, anotherReviewLnk, 2, 4, 1,
				1, 1.0, 1.0, 30, 30, 0, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.SOUTHWEST);

	}
	
	/**
	 * Gets the last reviews.
	 * 
	 * @return the last reviews
	 */
	private Vector<String> getLastReviews() {
		Vector<String> vecLastReviews = new Vector<String>();

		try {
			for (String rev : Data.getInstance().getAppData().getLastReviews()) {
				vecLastReviews.add(new File(rev).getName());
			}
		} catch (DataException exc) {
			JOptionPane.showMessageDialog(null, GUITools.getMessagePane(exc
					.getMessage()), Data.getInstance().getLocaleStr("error"),
					JOptionPane.ERROR_MESSAGE);
		}

		return vecLastReviews;
	}

}
