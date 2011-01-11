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

import static org.revager.app.model.Data._;

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
public class OpenReviewPanel extends AbstractDialogPanel {

	private GridBagLayout gbl2 = new GridBagLayout();

	/*
	 * Strings
	 */
	private String anotherRevStrng = _("Select another review...");
	private String noRevsStrng = _("No reviews available");
	private String firstRevStrng;
	private String secondRevStrng;
	private String thirdRevStrng;
	private String fourthRevStrng;

	/*
	 * Image Icons
	 */
	private ImageIcon reviewIcon = Data.getInstance().getIcon(
			"review_40x40_0.png");
	private ImageIcon reviewRolloverIcon = Data.getInstance().getIcon(
			"review_40x40.png");
	private ImageIcon browseIcon = Data.getInstance().getIcon(
			"open_40x40_0.png");
	private ImageIcon browseRolloverIcon = Data.getInstance().getIcon(
			"open_40x40.png");

	/*
	 * Links and LinkGroup
	 */
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
				
				UI.getInstance().getMainFrame().setAssistantMode(false);
				
				GUITools.executeSwingWorker(new LoadReviewWorker(revPath));
			} catch (DataException exc) {
				// TODO Auto-generated catch block
				exc.printStackTrace();
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
				GUITools.executeSwingWorker(new LoadReviewWorker(reviewPath));
			}
		}
	};

	/**
	 * Returns the group of the recent reviews.
	 * 
	 * @return
	 */
	public LinkGroup getLastRevsGrp() {
		return lastRevsGrp;
	}

	/**
	 * Returns the vector of the recent reviews.
	 * 
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

		try {
			firstRevStrng = lastRevsVector.get(0);
			firstReviewLnk = new HLink(firstRevStrng, reviewIcon,
					reviewRolloverIcon, lastRevsGrp);
			lastRevsGrp.addLink(firstReviewLnk);
			lastRevsGrp.selectLink(firstReviewLnk);
			GUITools.addComponent(this, gbl2, firstReviewLnk, 2, 0, 1, 1, 1.0,
					0.0, 0, 20, 0, 0, GridBagConstraints.HORIZONTAL,
					GridBagConstraints.NORTHWEST);

		} catch (Exception e) {
			noRevsLbl.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
			GUITools.addComponent(this, gbl2, noRevsLbl, 2, 0, 1, 1, 1.0, 0.0,
					15, 20, 0, 0, GridBagConstraints.HORIZONTAL,
					GridBagConstraints.NORTHWEST);
		}
		try {
			secondRevStrng = lastRevsVector.get(1);
			secondReviewLnk = new HLink(secondRevStrng, reviewIcon,
					reviewRolloverIcon, lastRevsGrp);
			lastRevsGrp.addLink(secondReviewLnk);
			GUITools.addComponent(this, gbl2, secondReviewLnk, 2, 1, 1, 1, 1.0,
					0.0, 0, 20, 0, 0, GridBagConstraints.HORIZONTAL,
					GridBagConstraints.NORTHWEST);

		} catch (Exception e) {

		}
		try {
			thirdRevStrng = lastRevsVector.get(2);
			thirdReviewLnk = new HLink(thirdRevStrng, reviewIcon,
					reviewRolloverIcon, lastRevsGrp);
			lastRevsGrp.addLink(thirdReviewLnk);
			GUITools.addComponent(this, gbl2, thirdReviewLnk, 2, 2, 1, 1, 1.0,
					0.0, 0, 20, 0, 0, GridBagConstraints.HORIZONTAL,
					GridBagConstraints.NORTHWEST);

		} catch (Exception e) {

		}

		try {
			fourthRevStrng = lastRevsVector.get(3);
			fourthReviewLnk = new HLink(fourthRevStrng, reviewIcon,
					reviewRolloverIcon, lastRevsGrp);
			lastRevsGrp.addLink(fourthReviewLnk);
			GUITools.addComponent(this, gbl2, fourthReviewLnk, 2, 3, 1, 1, 1.0,
					0.0, 0, 20, 0, 0, GridBagConstraints.HORIZONTAL,
					GridBagConstraints.NORTHWEST);

		} catch (Exception e) {

		}
		anotherReviewLnk = new HLink(anotherRevStrng, browseIcon,
				browseRolloverIcon, null);
		anotherReviewLnk.setUnderlined(true);
		anotherReviewLnk.addActionListener(openAnotherRev);
		GUITools.addComponent(this, gbl2, anotherReviewLnk, 2, 4, 1, 1, 1.0,
				1.0, 30, 20, 0, 0, GridBagConstraints.HORIZONTAL,
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
			JOptionPane.showMessageDialog(null,
					GUITools.getMessagePane(exc.getMessage()), _("Error"),
					JOptionPane.ERROR_MESSAGE);
		}

		return vecLastReviews;
	}

}
