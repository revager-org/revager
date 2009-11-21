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
package neos.resi.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import neos.resi.app.ResiFileFilter;
import neos.resi.app.model.Data;
import neos.resi.app.model.DataException;
import neos.resi.gui.AbstractDialog;
import neos.resi.gui.UI;
import neos.resi.gui.actions.ActionRegistry;
import neos.resi.gui.actions.ExitAction;
import neos.resi.gui.actions.InitializeMainFrameAction;
import neos.resi.gui.actions.OpenModeratorModeAction;
import neos.resi.gui.actions.OpenScribeModeAction;
import neos.resi.gui.helpers.FileChooser;
import neos.resi.tools.GUITools;

/**
 * The Class AssistantDialog.
 */
@SuppressWarnings("serial")
public class AssistantDialog extends AbstractDialog {

	/**
	 * The Enum Selection.
	 */
	public enum Selection {
		NEW_REVIEW, LOAD_REVIEW, MANAGE_ASPECTS;
	}

	private Selection selected = Selection.NEW_REVIEW;

	/**
	 * Gets the selected.
	 * 
	 * @return the selected
	 */
	public Selection getSelected() {
		return selected;
	}

	private GridBagLayout gbl = new GridBagLayout();
	private JPanel basePanel = new JPanel(gbl);

	private JButton buttonExit = new JButton(Data.getInstance().getLocaleStr(
			"closeApplication"));
	private JButton buttonBack = new JButton(Data.getInstance().getLocaleStr(
			"back"));
	private JButton buttonConfirm = new JButton(Data.getInstance()
			.getLocaleStr("confirm"));

	private JList listLastRevs;

	private JTextField pathTxtFld = new JTextField();

	private final ImageIcon ICON_BROWSE = Data.getInstance().getIcon(
			"buttonBrowse_22x22_0.png");
	private final ImageIcon ICON_BROWSE_ROLLOVER = Data.getInstance().getIcon(
			"buttonBrowse_22x22.png");
	private JButton buttonBrowse = GUITools.newImageButton(ICON_BROWSE,
			ICON_BROWSE_ROLLOVER);

	/**
	 * Gets the path.
	 * 
	 * @return the path
	 */
	public String getPath() {
		return pathTxtFld.getText();
	}

	/**
	 * Sets the path.
	 * 
	 * @param path
	 *            the new path
	 */
	public void setPath(String path) {
		pathTxtFld.setText(path);
	}

	/**
	 * Sets the select mode.
	 */
	public void setSelectMode() {
		setDescription(Data.getInstance().getLocaleStr(
				"assistantDialog.selectModeDescription"));

		basePanel.removeAll();

		basePanel.setLayout(gbl);

		buttonConfirm.setEnabled(false);
		buttonBack.setEnabled(false);

		JButton moderator = GUITools.newImageButton();
		moderator
				.setIcon(Data.getInstance().getIcon("moderator_128x128_0.png"));
		moderator.setRolloverIcon(Data.getInstance().getIcon(
				"moderator_128x128.png"));
		moderator.addActionListener(ActionRegistry.getInstance().get(
				OpenModeratorModeAction.class.getName()));

		JButton scribe = GUITools.newImageButton();
		scribe.setIcon(Data.getInstance().getIcon("scribe_128x128_0.png"));
		scribe
				.setRolloverIcon(Data.getInstance().getIcon(
						"scribe_128x128.png"));
		scribe.addActionListener(ActionRegistry.getInstance().get(
				OpenScribeModeAction.class.getName()));

		JButton instantReview = GUITools.newImageButton(Data.getInstance().getIcon("instantReview_128x128_0.png"),
				Data.getInstance().getIcon("instantReview_128x128.png"));
		instantReview.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Data.getInstance().setMode("instant");
				UI.getInstance().getAssistantDialog().setSelectReview();
			}});
		instantReview.setEnabled(true);
		
		JLabel scribeLabel = new JLabel(Data.getInstance().getLocaleStr(
				"mode.scribe"));
		JLabel moderatorLabel = new JLabel(Data.getInstance().getLocaleStr(
				"mode.moderator"));
		JLabel instantRevLabel = new JLabel(Data.getInstance().getLocaleStr(
		"mode.instant"));

		buttonExit.addActionListener(ActionRegistry.getInstance().get(
				ExitAction.class.getName()));

		GUITools.addComponent(basePanel, gbl, moderator,      0, 0, 1, 1, 1.0, 1.0, 0, 20, 0, 20, GridBagConstraints.NONE, GridBagConstraints.SOUTH);
		GUITools.addComponent(basePanel, gbl, moderatorLabel, 0, 1, 1, 1, 1.0, 1.0, 0, 20, 0, 20, GridBagConstraints.NONE, GridBagConstraints.NORTH);
		GUITools.addComponent(basePanel, gbl, instantReview,  1, 0, 1, 1, 1.0, 1.0, 0, 20, 0, 20, GridBagConstraints.NONE, GridBagConstraints.SOUTH);
		GUITools.addComponent(basePanel, gbl, instantRevLabel,1, 1, 1, 1, 1.0, 1.0, 0, 20, 0, 20, GridBagConstraints.NONE, GridBagConstraints.NORTH);
		GUITools.addComponent(basePanel, gbl, scribe,         2, 0, 1, 1, 1.0, 1.0, 0, 20, 0, 20, GridBagConstraints.NONE, GridBagConstraints.SOUTH);
		GUITools.addComponent(basePanel, gbl, scribeLabel,    2, 1, 1, 1, 1.0, 1.0, 0, 20, 0, 20, GridBagConstraints.NONE, GridBagConstraints.NORTH);

		buttonConfirm.requestFocusInWindow();

		basePanel.repaint();

		pack();
	}

	/**
	 * Sets the select review.
	 */
	public void setSelectReview() {
		setDescription(Data.getInstance().getLocaleStr(
				"assistantDialog.selectReviewDescription"));

		basePanel.removeAll();

		buttonBack.setEnabled(true);
		buttonConfirm.setEnabled(true);

		final JLabel labelSelectReview = new JLabel(Data.getInstance()
				.getLocaleStr("assistantDialog.selectReview")
				+ ":");
		final JLabel labelLastReviews = new JLabel(Data.getInstance()
				.getLocaleStr("assistantDialog.lastReviews")
				+ ":");

		pathTxtFld.setBorder(UI.STANDARD_BORDER_INLINE);

		/*
		 * List of last reviews
		 */
		listLastRevs = new JList();
		listLastRevs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listLastRevs.setSelectedIndex(0);
		listLastRevs.setListData(getLastReviews());
		listLastRevs.setBorder(UI.STANDARD_BORDER);
		listLastRevs.setFont(listLastRevs.getFont().deriveFont(Font.PLAIN));
		listLastRevs.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				List<String> lastRevs;
				try {
					lastRevs = Data.getInstance().getAppData().getLastReviews();
					if (listLastRevs.getSelectedIndex() > -1) {
						String selectedReview = lastRevs.get(listLastRevs
								.getSelectedIndex());

						if (!pathTxtFld.getText().equals(selectedReview)) {
							pathTxtFld.setText(selectedReview);
						}
					}
				} catch (DataException exc) {
					JOptionPane.showMessageDialog(null, GUITools
							.getMessagePane(exc.getMessage()), Data
							.getInstance().getLocaleStr("error"),
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		ButtonGroup choice = new ButtonGroup();

		final JRadioButton newReview = new JRadioButton(Data.getInstance()
				.getLocaleStr("menu.file.newReview"), true);
		choice.add(newReview);

		final JRadioButton openReview = new JRadioButton(Data.getInstance()
				.getLocaleStr("menu.file.openReview"));
		choice.add(openReview);

		final JRadioButton manageAspects = new JRadioButton(Data.getInstance()
				.getLocaleStr("assistantDialog.manageCatalogs"));
		choice.add(manageAspects);

		openReview.setEnabled(Data.getInstance().getModeParam(
				"ableToOpenReview"));
		openReview.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (openReview.isSelected()) {
					pathTxtFld.setEnabled(true);
					buttonBrowse.setEnabled(true);
					listLastRevs.setEnabled(true);
					selected = Selection.LOAD_REVIEW;
					labelSelectReview.setEnabled(true);
					labelLastReviews.setEnabled(true);
				}
			}
		});

		newReview.setEnabled(Data.getInstance().getModeParam(
				"ableToCreateNewReview"));
		newReview.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (newReview.isSelected()) {
					pathTxtFld.setEnabled(false);
					buttonBrowse.setEnabled(false);
					listLastRevs.setEnabled(false);
					selected = Selection.NEW_REVIEW;
					labelSelectReview.setEnabled(false);
					labelLastReviews.setEnabled(false);
				}
			}
		});

		manageAspects.setEnabled(Data.getInstance().getModeParam(
				"ableToUseAspectsManager"));
		manageAspects.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (manageAspects.isSelected()) {
					pathTxtFld.setEnabled(false);
					buttonBrowse.setEnabled(false);
					listLastRevs.setEnabled(false);
					selected = Selection.MANAGE_ASPECTS;
					labelSelectReview.setEnabled(false);
					labelLastReviews.setEnabled(false);
				}
			}
		});

		/*
		 * Set start selection
		 */
		if (Data.getInstance().getModeParam("ableToCreateNewReview")) {
			newReview.setSelected(true);
			selected = Selection.NEW_REVIEW;
		} else if (Data.getInstance().getModeParam("ableToOpenReview")) {
			openReview.setSelected(true);
			selected = Selection.LOAD_REVIEW;
		}

		pathTxtFld.setEnabled(openReview.isSelected());
		buttonBrowse.setEnabled(openReview.isSelected());
		listLastRevs.setEnabled(openReview.isSelected());
		labelSelectReview.setEnabled(openReview.isSelected());
		labelLastReviews.setEnabled(openReview.isSelected());

		// container,layout, component,gx,gy,gwidth,gheight,weightx,weighty, t,
		// l, b, r, a, i
		GUITools.addComponent(basePanel, gbl, manageAspects, 0, 0, 1, 1, 0, 0,
				0, 0, 0, 0, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(basePanel, gbl, newReview, 0, 1, 1, 1, 0, 0, 10,
				0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(basePanel, gbl, openReview, 0, 2, 1, 1, 0, 0, 10,
				0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(basePanel, gbl, labelSelectReview, 0, 3, 1, 1, 0,
				0, 0, 20, 0, 0, GridBagConstraints.NONE,
				GridBagConstraints.WEST);
		GUITools.addComponent(basePanel, gbl, pathTxtFld, 1, 3, 2, 1, 1.0, 0.3,
				20, 0, 20, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST);
		GUITools.addComponent(basePanel, gbl, buttonBrowse, 3, 3, 1, 1, 0, 0,
				0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
		GUITools.addComponent(basePanel, gbl, labelLastReviews, 0, 4, 1, 1,
				0.0, 0.0, 0, 20, 0, 0, GridBagConstraints.NONE,
				GridBagConstraints.WEST);
		GUITools.addComponent(basePanel, gbl, listLastRevs, 2, 4, 2, 2, 1.0,
				1.0, 0, 0, 0, 0, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);

		buttonConfirm.requestFocusInWindow();

		basePanel.repaint();

		pack();
	}

	/**
	 * Instantiates a new assistant dialog.
	 * 
	 * @param parent
	 *            the parent
	 */
	public AssistantDialog(Frame parent) {
		super(parent);

		setTitle(Data.getInstance().getLocaleStr("assistantDialog.title"));
		setIcon(Data.getInstance().getIcon("assistantDialog_64x64.png"));

		addButton(buttonExit);
		addButton(buttonBack);
		addButton(buttonConfirm);

		getContentPane().setLayout(new BorderLayout());
		add(basePanel, BorderLayout.CENTER);

		pathTxtFld.setColumns(13);
		pathTxtFld.setText("");
		pathTxtFld.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		pathTxtFld.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				listLastRevs.setListData(getLastReviews());
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
		});

		MatteBorder padding = new MatteBorder(0, 10, 0, 0, getContentPane()
				.getBackground());
		buttonBrowse.setBorder(padding);

		buttonExit.setIcon(Data.getInstance().getIcon("buttonExit_16x16.png"));
		buttonBack.setIcon(Data.getInstance().getIcon("buttonBack_16x16.png"));
		buttonConfirm.setIcon(Data.getInstance().getIcon("buttonOk_16x16.png"));

		buttonBrowse.setToolTipText(Data.getInstance().getLocaleStr(
				"assistantDialog.browseToolTip"));
		buttonBrowse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileChooser fileChooser = UI.getInstance().getFileChooser();

				if (fileChooser.showDialog(UI.getInstance()
						.getAssistantDialog(), FileChooser.MODE_OPEN_FILE,
						ResiFileFilter.TYPE_REVIEW) == FileChooser.SELECTED_APPROVE) {
					String reviewPath = fileChooser.getFile().getAbsolutePath();

					UI.getInstance().getAssistantDialog().setPath(reviewPath);

					listLastRevs.setListData(getLastReviews());
				}
			}
		});

		buttonBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				UI.getInstance().getAssistantDialog().setSelectMode();
			}
		});

		buttonConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (UI.getInstance().getAssistantDialog().getSelected() == AssistantDialog.Selection.LOAD_REVIEW
						&& UI.getInstance().getAssistantDialog().getPath()
								.equals("")) {
					JOptionPane.showMessageDialog(UI.getInstance()
							.getAssistantDialog(), GUITools.getMessagePane(Data
							.getInstance().getLocaleStr(
									"message.selectReviewToLoad")), Data
							.getInstance().getLocaleStr("error"),
							JOptionPane.ERROR_MESSAGE);

					return;
				}

				ActionRegistry.getInstance().get(
						InitializeMainFrameAction.class.getName())
						.actionPerformed(e);
			}
		});

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		setSelectMode();

		setMinimumSize(new Dimension(550, 500));

		pack();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see neos.resi.gui.AbstractDialog#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean vis) {
		if (vis) {
			setLocationToCenter();
		}

		super.setVisible(vis);
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
