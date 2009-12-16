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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import neos.resi.app.Application;
import neos.resi.app.ResiFileFilter;
import neos.resi.app.model.Data;
import neos.resi.app.model.DataException;
import neos.resi.app.model.appdata.AppAttendee;
import neos.resi.app.model.schema.Attendee;
import neos.resi.app.model.schema.Role;
import neos.resi.gui.AbstractDialog;
import neos.resi.gui.StrengthPopupWindow;
import neos.resi.gui.UI;
import neos.resi.gui.StrengthPopupWindow.ButtonClicked;
import neos.resi.gui.actions.ActionRegistry;
import neos.resi.gui.actions.ExitAction;
import neos.resi.gui.actions.InitializeMainFrameAction;
import neos.resi.gui.actions.OpenModeratorModeAction;
import neos.resi.gui.actions.OpenScribeModeAction;
import neos.resi.gui.actions.attendee.SelectAttOutOfDirAction;
import neos.resi.gui.helpers.FileChooser;
import neos.resi.gui.models.StrengthTableModel;
import neos.resi.gui.workers.LoadStdCatalogsWorker;
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
	private Level dialogLevel;
	private JLabel nameLabel;
	private JLabel contactLabel;
	private JLabel roleLabel;
	private JLabel strengthLabel;
	private JTextField nameTxtFld;
	private JTextArea contactTxtArea;
	private JComboBox roleCmbBx;
	private JTable strengthTbl;

	private JButton directoryBttn;
	private JButton addStrength = null;
	private JButton removeStrength = null;

	private StrengthTableModel stm = null;
	private List<String> strengthList = null;
	private JScrollPane contactScrllPn;
	private JPanel buttonPanel = null;

	private AppAttendee currentAppAttendee;
	//private Attendee currentAttendee;

	public enum Level {
		LEVEL1, LEVEL2, LEVEL3
	};

	private boolean nameMissing;

	/**
	 * Returns if name is missing or not
	 * 
	 * @return missing
	 */
	public boolean isNameMissing() {
		return nameMissing;
	}

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
		dialogLevel = Level.LEVEL1;

		setDescription(Data.getInstance().getLocaleStr(
				"assistantDialog.selectModeDescription"));

		basePanel.removeAll();

		basePanel.setLayout(gbl);

		buttonConfirm.setEnabled(false);
		buttonConfirm.setIcon(Data.getInstance().getIcon("buttonOk_16x16.png"));
		buttonConfirm.setText(Data.getInstance().getLocaleStr("confirm"));

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

		JButton instantReview = GUITools.newImageButton(Data.getInstance()
				.getIcon("instantReview_128x128_0.png"), Data.getInstance()
				.getIcon("instantReview_128x128.png"));
		instantReview.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Data.getInstance().setMode("instant");
				UI.getInstance().getAssistantDialog().setSelectReview();
			}
		});
		instantReview.setEnabled(true);

		JLabel scribeLabel = new JLabel(Data.getInstance().getLocaleStr(
				"mode.scribe"));
		JLabel moderatorLabel = new JLabel(Data.getInstance().getLocaleStr(
				"mode.moderator"));
		JLabel instantRevLabel = new JLabel(Data.getInstance().getLocaleStr(
				"mode.instant"));

		buttonExit.addActionListener(ActionRegistry.getInstance().get(
				ExitAction.class.getName()));

		GUITools
				.addComponent(basePanel, gbl, moderator, 0, 0, 1, 1, 1.0, 1.0,
						0, 20, 0, 20, GridBagConstraints.NONE,
						GridBagConstraints.SOUTH);
		GUITools.addComponent(basePanel, gbl, moderatorLabel, 0, 1, 1, 1, 1.0,
				1.0, 0, 20, 0, 20, GridBagConstraints.NONE,
				GridBagConstraints.NORTH);
		GUITools.addComponent(basePanel, gbl, instantReview, 1, 0, 1, 1, 1.0,
				1.0, 0, 20, 0, 20, GridBagConstraints.NONE,
				GridBagConstraints.SOUTH);
		GUITools.addComponent(basePanel, gbl, instantRevLabel, 1, 1, 1, 1, 1.0,
				1.0, 0, 20, 0, 20, GridBagConstraints.NONE,
				GridBagConstraints.NORTH);
		GUITools.addComponent(basePanel, gbl, scribe, 2, 0, 1, 1, 1.0, 1.0, 0,
				20, 0, 20, GridBagConstraints.NONE, GridBagConstraints.SOUTH);
		GUITools.addComponent(basePanel, gbl, scribeLabel, 2, 1, 1, 1, 1.0,
				1.0, 0, 20, 0, 20, GridBagConstraints.NONE,
				GridBagConstraints.NORTH);

		buttonConfirm.requestFocusInWindow();

		basePanel.repaint();

		pack();
	}

	/**
	 * Sets the select review.
	 */
	public void setSelectReview() {

		dialogLevel = Level.LEVEL2;

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
	 * Changes to level3 view of the dialog
	 */
	public void setAddAttToInstRev() {
		dialogLevel = Level.LEVEL3;

		setDescription(Data.getInstance().getLocaleStr(
				"addYourself.description"));

		basePanel.removeAll();

		buttonBack.setEnabled(true);
		buttonConfirm.setEnabled(true);

		nameLabel = new JLabel(Data.getInstance().getLocaleStr("attendee.name"));
		contactLabel = new JLabel(Data.getInstance().getLocaleStr(
				"attendee.contact"));
		roleLabel = new JLabel(Data.getInstance().getLocaleStr("attendee.role"));
		strengthLabel = new JLabel(Data.getInstance().getLocaleStr(
				"attendee.priorities"));

		nameTxtFld = new JTextField();
		contactTxtArea = new JTextArea();
		contactTxtArea.addFocusListener(focusListener);
		contactScrllPn = GUITools.setIntoScrllPn(contactTxtArea);
		roleCmbBx = new JComboBox();
		roleCmbBx.addFocusListener(focusListener);

		for (Role x : Role.values()) {
			String roleString = "role.".concat(x.value());
			roleCmbBx.addItem(Data.getInstance().getLocaleStr(roleString));
		}

		roleCmbBx.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ev) {
				updateStrengthTable();
			}
		});

		// roleCmbBx.setSelectedItem(Data.getInstance().getLocaleStr(
		// "role." + Role.REVIEWER.toString().toLowerCase()));

		strengthTbl = GUITools.newStandardTable(null, false);

		directoryBttn = GUITools.newImageButton(Data.getInstance().getIcon(
				"directory_25x25_0.png"), Data.getInstance().getIcon(
				"directory_25x25.png"));
		directoryBttn.setToolTipText(Data.getInstance().getLocaleStr(
				"attendee.directory"));
		directoryBttn.addActionListener(ActionRegistry.getInstance().get(
				SelectAttOutOfDirAction.class.getName()));
		buttonPanel = new JPanel(new GridLayout(3, 1));

		strengthTbl.addFocusListener(focusListener);
		strengthTbl.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				updateStrengthButtons();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
		});

		addStrength = GUITools.newImageButton();
		addStrength.setIcon(Data.getInstance().getIcon("add_25x25_0.png"));
		addStrength
				.setRolloverIcon(Data.getInstance().getIcon("add_25x25.png"));
		addStrength.setToolTipText(Data.getInstance().getLocaleStr(
				"attendeeDialog.addStrength"));

		addStrength.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				final String title = Data.getInstance().getLocaleStr(
						"popup.addStrength.title");

				SwingWorker<Void, Void> showPopupWorker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						StrengthPopupWindow popup = new StrengthPopupWindow(UI
								.getInstance().getAssistantDialog(), title);

						/*
						 * Import the standard catalogs, if no catalogs exist in
						 * the database
						 */
						try {
							if (Data.getInstance().getAppData()
									.getNumberOfCatalogs() == 0) {
								switchToProgressMode(Data
										.getInstance()
										.getLocaleStr("status.importingCatalog"));

								LoadStdCatalogsWorker catalogWorker = new LoadStdCatalogsWorker();

								catalogWorker.execute();

								while (!catalogWorker.isDone()
										&& !catalogWorker.isCancelled()) {
									Thread.sleep(500);
								}

								switchToEditMode();
							}
						} catch (Exception exc) {
							/*
							 * do nothing
							 */
						}

						/*
						 * Show the popup
						 */
						popup.setVisible(true);

						if (popup.getButtonClicked() == ButtonClicked.OK) {
							for (String cat : popup.getSelCateList()) {
								if (!strengthList.contains(cat)) {
									strengthList.add(cat);
								}
							}

							stm.fireTableDataChanged();

							updateStrengthButtons();
						}

						return null;
					}
				};

				showPopupWorker.execute();
			}
		});

		buttonPanel.add(addStrength);

		removeStrength = GUITools.newImageButton();
		removeStrength
				.setIcon(Data.getInstance().getIcon("remove_25x25_0.png"));
		removeStrength.setRolloverIcon(Data.getInstance().getIcon(
				"remove_25x25.png"));
		removeStrength.setToolTipText(Data.getInstance().getLocaleStr(
				"attendeeDialog.remStrength"));
		removeStrength.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				int selRow = strengthTbl.getSelectedRow();

				String str = (String) stm.getValueAt(selRow, 1);

				strengthList.remove(str);

				stm.fireTableDataChanged();

				updateStrengthButtons();
			}
		});

		buttonPanel.add(removeStrength);

		JScrollPane strScrllPn = GUITools.setIntoScrollPane(strengthTbl);

		GUITools.addComponent(basePanel, gbl, nameLabel, 0, 0, 1, 1, 0, 0, 0,
				20, 0, 20, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(basePanel, gbl, nameTxtFld, 1, 0, 3, 1, 1.0, 0,
				0, 20, 0, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(basePanel, gbl, directoryBttn, 4, 0, 1, 1, 0, 0,
				0, 5, 0, 20, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(basePanel, gbl, contactLabel, 0, 1, 1, 1, 0, 0,
				5, 20, 0, 20, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(basePanel, gbl, contactScrllPn, 1, 1, 3, 3, 1.0,
				0.5, 5, 20, 0, 0, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(basePanel, gbl, roleLabel, 0, 4, 1, 1, 0, 0, 10,
				20, 0, 20, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(basePanel, gbl, roleCmbBx, 1, 4, 3, 1, 1.0, 0,
				10, 20, 0, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(basePanel, gbl, strengthLabel, 0, 5, 1, 1, 0, 0,
				17, 20, 0, 20, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(basePanel, gbl, strScrllPn, 1, 5, 3, 2, 1.0, 0.5,
				15, 20, 0, 0, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(basePanel, gbl, buttonPanel, 4, 5, 1, 2, 0, 0,
				17, 5, 0, 0, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);

		setCurrentAttendee(null);

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

		dialogLevel = Level.LEVEL1;
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
				if (dialogLevel == Level.LEVEL2) {
					UI.getInstance().getAssistantDialog().setSelectMode();
					dialogLevel = Level.LEVEL1;
				} else if (dialogLevel == Level.LEVEL3) {
					UI.getInstance().getAssistantDialog().setSelectReview();
					dialogLevel = Level.LEVEL2;
				}
			}
		});

		buttonConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// No review path
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

				// Set AddAttendee screen
				if (UI.getInstance().getAssistantDialog().getSelected() == AssistantDialog.Selection.NEW_REVIEW
						&& Data.getInstance().getMode().equals("instant")
						&& dialogLevel == Level.LEVEL2) {
					setAddAttToInstRev();

					dialogLevel = Level.LEVEL3;

					return;
				}

				// Do some checks and updates on level 3
				if (dialogLevel == Level.LEVEL3) {
					

					if (!nameTxtFld.getText().trim().equals("")) {
						ActionRegistry.getInstance().get(
								InitializeMainFrameAction.class.getName())
								.actionPerformed(e);
						updateInstantAtt();
					}else{
						String message="";
						message = Data.getInstance().getLocaleStr(
						"attendeeDialog.message.noName");

						setMessage(message);
						nameTxtFld.setBorder(UI.MARKED_BORDER_INLINE);
					}

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

	private FocusListener focusListener = new FocusListener() {
		@Override
		public void focusGained(FocusEvent e) {
			if (e.getSource() != strengthTbl) {
				if (strengthTbl.getRowCount() > 0) {
					strengthTbl.removeRowSelectionInterval(0, strengthTbl
							.getRowCount() - 1);

					updateStrengthButtons();
				}
			}
		}

		@Override
		public void focusLost(FocusEvent e) {
		}
	};

	/**
	 * Update strength buttons.
	 */
	private void updateStrengthButtons() {
		if (strengthTbl.getSelectedRow() != -1 && strengthTbl.isEnabled()) {
			removeStrength.setEnabled(true);
		} else {
			removeStrength.setEnabled(false);
		}
	}

	/**
	 * Sets the current app attendee.
	 * 
	 * @param appAtt
	 *            the new current app attendee
	 */
	public void setCurrentAppAttendee(AppAttendee appAtt) {
		this.currentAppAttendee = appAtt;

		nameTxtFld.setBorder(UI.STANDARD_BORDER_INLINE);
		contactScrllPn.setBorder(UI.STANDARD_BORDER);

		nameTxtFld.setText(currentAppAttendee.getName());

		try {
			contactTxtArea.setText(currentAppAttendee.getContact());
		} catch (DataException e) {
			JOptionPane.showMessageDialog(this, GUITools.getMessagePane(e
					.getMessage()), Data.getInstance().getLocaleStr("error"),
					JOptionPane.ERROR_MESSAGE);
		}

		updateStrengthTable();
	}

	/**
	 * Sets the current attendee.
	 * 
	 * @param att
	 *            the new current attendee
	 */
	public void setCurrentAttendee(Attendee att) {
		//currentAttendee = att;
		currentAppAttendee = null;

		nameTxtFld.setText(null);
		contactTxtArea.setText(null);
		roleCmbBx.setSelectedItem(Data.getInstance().getLocaleStr(
				"role." + Role.REVIEWER.toString().toLowerCase()));

		updateStrengthTable();
	}

	/**
	 * Update strength table.
	 */
	private void updateStrengthTable() {

		try {
			strengthList = currentAppAttendee.getStrengths();
		} catch (Exception e) {
			strengthList = new ArrayList<String>();
		}

		if (stm == null) {
			stm = new StrengthTableModel();
			strengthTbl.setModel(stm);
		}

		stm.fireTableDataChanged();

		/*
		 * View of strengths
		 */
		boolean enable = false;

		if (((String) roleCmbBx.getSelectedItem()).equals(Data.getInstance()
				.getLocaleStr("role.reviewer"))) {
			enable = true;
		}

		addStrength.setEnabled(enable);
		removeStrength.setEnabled(false);
		strengthTbl.setEnabled(enable);
		strengthLabel.setEnabled(enable);

		if (enable) {
			strengthTbl.setForeground(Color.BLACK);
		} else {
			strengthTbl.setForeground(Color.GRAY);
		}
	}

	/**
	 * Gets the strength list.
	 * 
	 * @return the strengthList
	 */
	public List<String> getStrengthList() {
		if (strengthList == null) {
			strengthList = new ArrayList<String>();
		}

		return strengthList;
	}

	/**
	 * Adds instant reviewer
	 * 
	 */
	public void updateInstantAtt() {

		Role[] roles = Role.values();
		String attContact;

		nameTxtFld.setBorder(UI.STANDARD_BORDER_INLINE);
		contactScrllPn.setBorder(UI.STANDARD_BORDER);

		String attName = nameTxtFld.getText();
		if (contactTxtArea.getText() != null)
			attContact = contactTxtArea.getText();
		else
			attContact = "";

		Role attRole = roles[roleCmbBx.getSelectedIndex()];

		nameMissing = false;



			/*
			 * Update the app attendee in the database
			 */
			try {
				if (currentAppAttendee == null) {
					currentAppAttendee = Data.getInstance().getAppData()
							.getAttendee(attName, attContact);

					if (currentAppAttendee == null) {
						currentAppAttendee = Data.getInstance().getAppData()
								.newAttendee(attName, attContact);
					}
				} else {
					currentAppAttendee.setNameAndContact(attName, attContact);
				}

				for (String str : currentAppAttendee.getStrengths()) {
					currentAppAttendee.removeStrength(str);
				}

				for (String str : strengthList) {
					currentAppAttendee.addStrength(str);
				}
			} catch (DataException e1) {
				JOptionPane.showMessageDialog(UI.getInstance()
						.getAssistantDialog(), GUITools.getMessagePane(e1
						.getMessage()), Data.getInstance()
						.getLocaleStr("error"), JOptionPane.ERROR_MESSAGE);
			}

			/*
			 * update the review attendee
			 */
			Attendee newAtt = new Attendee();

			newAtt.setName(attName);
			newAtt.setContact(attContact);
			newAtt.setRole(attRole);

			Application.getInstance().getAttendeeMgmt().addAttendee(attName,
					attContact, attRole, null);

			setVisible(false);

			UI.getInstance().getAspectsManagerFrame().updateViews();
		

	}

	/**
	 * Gets the dialog level
	 * 
	 * @return dialogLevel
	 */
	public Level getLevel() {

		return dialogLevel;
	}

}
