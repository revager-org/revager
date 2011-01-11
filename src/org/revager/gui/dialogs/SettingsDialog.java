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
package org.revager.gui.dialogs;

import static org.revager.app.model.Data._;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.revager.app.ResiFileFilter;
import org.revager.app.model.ApplicationData;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.appdata.AppSettingKey;
import org.revager.app.model.appdata.AppSettingValue;
import org.revager.gui.AbstractDialog;
import org.revager.gui.UI;
import org.revager.gui.actions.ActionRegistry;
import org.revager.gui.actions.ExitAction;
import org.revager.gui.helpers.FileChooser;
import org.revager.gui.workers.SettingsWorker;
import org.revager.gui.workers.SettingsWorker.Mode;
import org.revager.tools.FileTools;
import org.revager.tools.GUITools;

/**
 * The Class SettingsDialog.
 */
@SuppressWarnings("serial")
public class SettingsDialog extends AbstractDialog {

	private ApplicationData appData = Data.getInstance().getAppData();

	private JTabbedPane tabbedPane = new JTabbedPane();

	private JPanel panelGeneral = new JPanel();
	private JPanel panelPDF = new JPanel();

	private JSpinner spinnerAutoSave = null;
	private JSpinner spinnerProtWarn = null;

	private JCheckBox checkboxAutoSave = null;
	private JCheckBox checkboxCheckUpdates = null;
	private JCheckBox checkboxShowHints = null;
	private JCheckBox checkboxProtWarn = null;
	private JCheckBox checkboxHighlightFields = null;

	private JComboBox boxLanguage = null;

	private JLabel labelImageProtocol = null;
	private String logoPathProtocol = null;
	private JTextField textFootProtocol = null;

	private JLabel labelImageInvitation = null;
	private String logoPathInvitation = null;
	private JTextField textFootInvitation = null;
	private JTextArea textInvitation = null;

	private boolean langChangeHintAlreadyDisplayed = false;

	/**
	 * Instantiates a new settings dialog.
	 * 
	 * @param parent
	 *            the parent
	 */
	public SettingsDialog(Frame parent) {
		super(parent);

		setTitle(_("Application Settings"));
		setIcon(Data.getInstance().getIcon("settingsDialog_64x64.png"));
		setHelpChapter("options");

		createTabsContent();

		/*
		 * Content area as tabbed pane
		 */
		tabbedPane.setTabPlacement(JTabbedPane.TOP);
		tabbedPane.setBorder(null);
		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateDialog();
			}
		});

		panelGeneral.setOpaque(true);
		tabbedPane.addTab(_("General"),
				Data.getInstance().getIcon("tabGeneral_24x24.png"),
				panelGeneral);

		panelPDF.setOpaque(true);
		tabbedPane.addTab(_("PDF Export"),
				Data.getInstance().getIcon("tabPDF_24x24.png"), panelPDF);

		getContentPane().setLayout(new BorderLayout());

		add(tabbedPane, BorderLayout.CENTER);

		/*
		 * buttons
		 */
		JButton buttonConfirm = new JButton(_("Confirm"), Data.getInstance()
				.getIcon("buttonOk_16x16.png"));
		buttonConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GUITools.executeSwingWorker(new SettingsWorker(
						Mode.STORE_APPDATA));
				setVisible(false);
			}
		});

		JButton buttonCancel = new JButton(_("Abort"), Data.getInstance()
				.getIcon("buttonCancel_16x16.png"));
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		addButton(buttonCancel);
		addButton(buttonConfirm);

		buttonConfirm.requestFocus();

		/*
		 * Set window properties
		 */
		setMinimumSize(new Dimension(650, 650));
		setPreferredSize(new Dimension(650, 650));

		setLocationToCenter();

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		pack();
	}

	/**
	 * Creates the tabs content.
	 */
	private void createTabsContent() {
		/*
		 * *********** TAB GENERAL ***********
		 */
		GridBagLayout gblGeneral = new GridBagLayout();
		panelGeneral.setLayout(gblGeneral);

		/*
		 * Auto save
		 */
		String autoSaveText = _("Save review every {0} minutes automatically.");
		String[] textParts = autoSaveText.split("\\{0\\}");

		String textPart1 = "";
		String textPart2 = "";

		try {
			textPart1 = textParts[0].trim();
			textPart2 = textParts[1].trim();
		} catch (Exception e) {
		}

		checkboxAutoSave = new JCheckBox(textPart1);
		checkboxAutoSave.setFocusPainted(false);
		checkboxAutoSave.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				spinnerAutoSave.setEnabled(checkboxAutoSave.isSelected());
			}
		});

		spinnerAutoSave = new JSpinner(new SpinnerNumberModel(5, 2, 99, 1));

		GridBagLayout gblAutoSave = new GridBagLayout();
		JPanel panelAutoSave = new JPanel(gblAutoSave);

		GUITools.addComponent(panelAutoSave, gblAutoSave, checkboxAutoSave, 0,
				0, 1, 1, 0.0, 0.0, 5, 20, 5, 0, GridBagConstraints.NONE,
				GridBagConstraints.WEST);
		GUITools.addComponent(panelAutoSave, gblAutoSave, spinnerAutoSave, 1,
				0, 1, 1, 0.0, 0.0, 5, 0, 5, 0, GridBagConstraints.NONE,
				GridBagConstraints.WEST);
		GUITools.addComponent(panelAutoSave, gblAutoSave,
				new JLabel(textPart2), 2, 0, 1, 1, 0.0, 0.0, 5, 5, 5, 5,
				GridBagConstraints.NONE, GridBagConstraints.WEST);

		/*
		 * Check updates
		 */
		checkboxCheckUpdates = new JCheckBox(
				_("Check if a new version of RevAger is available during application start."));
		checkboxCheckUpdates.setFocusPainted(false);

		GridBagLayout gblCheckUpdates = new GridBagLayout();
		JPanel panelCheckUpdates = new JPanel(gblCheckUpdates);

		GUITools.addComponent(panelCheckUpdates, gblCheckUpdates,
				checkboxCheckUpdates, 0, 0, 1, 1, 0.0, 0.0, 5, 20, 5, 0,
				GridBagConstraints.NONE, GridBagConstraints.WEST);

		/*
		 * Show hints by default
		 */
		checkboxShowHints = new JCheckBox(
				_("Show hints in dialogs by default."));
		checkboxShowHints.setFocusPainted(false);

		GridBagLayout gblShowHints = new GridBagLayout();
		JPanel panelShowHints = new JPanel(gblShowHints);

		GUITools.addComponent(panelShowHints, gblShowHints, checkboxShowHints,
				0, 0, 1, 1, 0.0, 0.0, 5, 20, 5, 0, GridBagConstraints.NONE,
				GridBagConstraints.WEST);

		/*
		 * Highlight fields
		 */
		checkboxHighlightFields = new JCheckBox(
				_("Highlight obligatory input fields."));
		checkboxHighlightFields.setFocusPainted(false);

		GridBagLayout gblHighlightFields = new GridBagLayout();
		JPanel panelHighlightFields = new JPanel(gblHighlightFields);

		GUITools.addComponent(panelHighlightFields, gblHighlightFields,
				checkboxHighlightFields, 0, 0, 1, 1, 0.0, 0.0, 5, 20, 5, 0,
				GridBagConstraints.NONE, GridBagConstraints.WEST);

		/*
		 * Protocol warning
		 */
		String warnText = _("Show warning while creating a findings list after {0} minutes.");
		String[] warnParts = warnText.split("\\{0\\}");

		String warnPart1 = "";
		String warnPart2 = "";

		try {
			warnPart1 = warnParts[0].trim();
			warnPart2 = warnParts[1].trim();
		} catch (Exception e) {
		}

		checkboxProtWarn = new JCheckBox(warnPart1);
		checkboxProtWarn.setFocusPainted(false);
		checkboxProtWarn.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				spinnerProtWarn.setEnabled(checkboxProtWarn.isSelected());
			}
		});

		spinnerProtWarn = new JSpinner(new SpinnerNumberModel(15, 15, 990, 15));

		GridBagLayout gblProtWarn = new GridBagLayout();
		JPanel panelProtWarn = new JPanel(gblProtWarn);

		GUITools.addComponent(panelProtWarn, gblProtWarn, checkboxProtWarn, 0,
				0, 1, 1, 0.0, 0.0, 5, 20, 5, 0, GridBagConstraints.NONE,
				GridBagConstraints.WEST);
		GUITools.addComponent(panelProtWarn, gblProtWarn, spinnerProtWarn, 1,
				0, 1, 1, 0.0, 0.0, 5, 0, 5, 0, GridBagConstraints.NONE,
				GridBagConstraints.WEST);
		GUITools.addComponent(panelProtWarn, gblProtWarn,
				new JLabel(warnPart2), 2, 0, 1, 1, 0.0, 0.0, 5, 5, 5, 5,
				GridBagConstraints.NONE, GridBagConstraints.WEST);

		/*
		 * Select language
		 */
		boxLanguage = new JComboBox();

		for (String langId : Data.getInstance().getLanguages().keySet()) {
			boxLanguage.addItem(new Language(Data.getInstance().getLanguages()
					.get(langId), langId));
		}
		// boxLanguage.addItem(new Language("Deutsch", "de"));
		// boxLanguage.addItem(new Language("English", "en"));

		boxLanguage.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ev) {
				String currentLang;

				try {
					currentLang = appData
							.getSetting(AppSettingKey.APP_LANGUAGE);
				} catch (DataException e) {
					currentLang = null;
				}

				String selectedLang = ((Language) boxLanguage.getSelectedItem())
						.getLangCode();

				if (!currentLang.equals(selectedLang)
						&& !langChangeHintAlreadyDisplayed) {
					int option = JOptionPane.showConfirmDialog(
							UI.getInstance().getSettingsDialog(),
							GUITools.getMessagePane(_("You have to restart the application in order finalize the change of language. Restart now?")),
							_("Question"), JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);

					langChangeHintAlreadyDisplayed = true;

					if (option == JOptionPane.YES_OPTION) {
						GUITools.executeSwingWorker(new SettingsWorker(
								Mode.STORE_APPDATA));

						setVisible(false);

						ExitAction exitAction = ((ExitAction) ActionRegistry
								.getInstance().get(ExitAction.class.getName()));

						exitAction.setRestartAgain(true);
						exitAction.actionPerformed(null);
					}
				}
			}
		});

		GridBagLayout gblLanguage = new GridBagLayout();
		JPanel panelLanguage = new JPanel(gblLanguage);

		GUITools.addComponent(panelLanguage, gblLanguage, new JLabel(
				_("Language:")), 0, 0, 1, 1, 0.0, 0.0, 5, 42, 5, 5,
				GridBagConstraints.NONE, GridBagConstraints.WEST);
		GUITools.addComponent(panelLanguage, gblLanguage, boxLanguage, 1, 0, 1,
				1, 0.0, 0.0, 5, 5, 5, 5, GridBagConstraints.NONE,
				GridBagConstraints.WEST);

		/*
		 * Add the panels to general tab
		 */
		GUITools.addComponent(panelGeneral, gblGeneral, panelAutoSave, 0, 0, 1,
				1, 1.0, 1.0, 5, 5, 5, 5, GridBagConstraints.NONE,
				GridBagConstraints.WEST);
		GUITools.addComponent(panelGeneral, gblGeneral, panelCheckUpdates, 0,
				1, 1, 1, 1.0, 1.0, 5, 5, 5, 5, GridBagConstraints.NONE,
				GridBagConstraints.WEST);
		GUITools.addComponent(panelGeneral, gblGeneral, panelShowHints, 0, 2,
				1, 1, 1.0, 1.0, 5, 5, 5, 5, GridBagConstraints.NONE,
				GridBagConstraints.WEST);
		GUITools.addComponent(panelGeneral, gblGeneral, panelHighlightFields,
				0, 3, 1, 1, 1.0, 1.0, 5, 5, 5, 5, GridBagConstraints.NONE,
				GridBagConstraints.WEST);
		GUITools.addComponent(panelGeneral, gblGeneral, panelProtWarn, 0, 4, 1,
				1, 1.0, 1.0, 5, 5, 5, 5, GridBagConstraints.NONE,
				GridBagConstraints.WEST);
		GUITools.addComponent(panelGeneral, gblGeneral, panelLanguage, 0, 5, 1,
				1, 1.0, 1.0, 5, 5, 5, 5, GridBagConstraints.NONE,
				GridBagConstraints.WEST);

		/*
		 * ************** TAB PDF EXPORT **************
		 */
		GridBagLayout gblPdfExport = new GridBagLayout();
		panelPDF.setLayout(gblPdfExport);

		/*
		 * Panel PDF protocol
		 */
		GridBagLayout gblPdfProtocol = new GridBagLayout();
		JPanel panelPdfProtocol = new JPanel(gblPdfProtocol);
		panelPdfProtocol.setBorder(BorderFactory
				.createTitledBorder(_("Findings list as PDF file")));

		labelImageProtocol = new JLabel();
		labelImageProtocol.setOpaque(true);
		labelImageProtocol.setBackground(Color.WHITE);
		labelImageProtocol.setBorder(new MatteBorder(1, 1, 1, 1, Color.GRAY));

		JButton buttonBrowseProt = GUITools.newImageButton(Data.getInstance()
				.getIcon("buttonBrowse_22x22_0.png"), Data.getInstance()
				.getIcon("buttonBrowse_22x22.png"));
		buttonBrowseProt.setToolTipText(_("Choose an Image..."));
		buttonBrowseProt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (UI.getInstance()
						.getFileChooser()
						.showDialog(UI.getInstance().getSettingsDialog(),
								FileChooser.MODE_OPEN_FILE,
								ResiFileFilter.TYPE_IMAGES) == FileChooser.SELECTED_APPROVE) {
					logoPathProtocol = UI.getInstance().getFileChooser()
							.getFile().getAbsolutePath();

					GUITools.executeSwingWorker(new SettingsWorker(
							Mode.UPDATE_LOGO_VIEWS));
				}
			}
		});

		JButton buttonClearProt = GUITools.newImageButton(Data.getInstance()
				.getIcon("clear_22x22_0.png"),
				Data.getInstance().getIcon("clear_22x22.png"));
		buttonClearProt.setToolTipText(_("Remove Image"));
		buttonClearProt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				logoPathProtocol = null;

				GUITools.executeSwingWorker(new SettingsWorker(
						Mode.UPDATE_LOGO_VIEWS));
			}
		});

		GUITools.addComponent(panelPdfProtocol, gblPdfProtocol, new JLabel(
				_("Displayed image:")), 0, 0, 1, 1, 0.0, 1.0, 5, 5, 5, 5,
				GridBagConstraints.NONE, GridBagConstraints.WEST);
		GUITools.addComponent(panelPdfProtocol, gblPdfProtocol,
				buttonClearProt, 1, 0, 1, 1, 0.0, 1.0, 5, 5, 5, 5,
				GridBagConstraints.NONE, GridBagConstraints.WEST);
		GUITools.addComponent(panelPdfProtocol, gblPdfProtocol,
				buttonBrowseProt, 2, 0, 1, 1, 0.0, 1.0, 5, 5, 5, 5,
				GridBagConstraints.NONE, GridBagConstraints.WEST);
		GUITools.addComponent(panelPdfProtocol, gblPdfProtocol,
				labelImageProtocol, 3, 0, 1, 1, 1.0, 1.0, 0, 5, 0, 6,
				GridBagConstraints.NONE, GridBagConstraints.EAST);

		textFootProtocol = new JTextField();

		GUITools.addComponent(panelPdfProtocol, gblPdfProtocol, new JLabel(
				_("Displayed foot text:")), 0, 1, 4, 1, 0.0, 0.0, 5, 5, 0, 5,
				GridBagConstraints.NONE, GridBagConstraints.WEST);
		GUITools.addComponent(panelPdfProtocol, gblPdfProtocol,
				textFootProtocol, 0, 2, 4, 1, 1.0, 0.0, 5, 5, 5, 5,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);

		/*
		 * Panel PDF invitation
		 */
		GridBagLayout gblPdfInvitation = new GridBagLayout();
		JPanel panelPdfInvitation = new JPanel(gblPdfInvitation);
		panelPdfInvitation.setBorder(BorderFactory
				.createTitledBorder(_("Invitations as PDF file:")));

		labelImageInvitation = new JLabel();
		labelImageInvitation.setOpaque(true);
		labelImageInvitation.setBackground(Color.WHITE);
		labelImageInvitation.setBorder(new MatteBorder(1, 1, 1, 1, Color.GRAY));

		JButton buttonBrowseInv = GUITools.newImageButton(Data.getInstance()
				.getIcon("buttonBrowse_22x22_0.png"), Data.getInstance()
				.getIcon("buttonBrowse_22x22.png"));
		buttonBrowseInv.setToolTipText(_("Choose an Image..."));
		buttonBrowseInv.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (UI.getInstance()
						.getFileChooser()
						.showDialog(UI.getInstance().getSettingsDialog(),
								FileChooser.MODE_OPEN_FILE,
								ResiFileFilter.TYPE_IMAGES) == FileChooser.SELECTED_APPROVE) {
					logoPathInvitation = UI.getInstance().getFileChooser()
							.getFile().getAbsolutePath();

					GUITools.executeSwingWorker(new SettingsWorker(
							Mode.UPDATE_LOGO_VIEWS));
				}
			}
		});

		JButton buttonClearInv = GUITools.newImageButton(Data.getInstance()
				.getIcon("clear_22x22_0.png"),
				Data.getInstance().getIcon("clear_22x22.png"));
		buttonClearInv.setToolTipText(_("Remove Image"));
		buttonClearInv.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				logoPathInvitation = null;

				GUITools.executeSwingWorker(new SettingsWorker(
						Mode.UPDATE_LOGO_VIEWS));
			}
		});

		GUITools.addComponent(panelPdfInvitation, gblPdfInvitation, new JLabel(
				_("Displayed image:")), 0, 0, 1, 1, 0.0, 1.0, 5, 5, 5, 5,
				GridBagConstraints.NONE, GridBagConstraints.WEST);
		GUITools.addComponent(panelPdfInvitation, gblPdfInvitation,
				buttonClearInv, 1, 0, 1, 1, 0.0, 1.0, 5, 5, 5, 5,
				GridBagConstraints.NONE, GridBagConstraints.WEST);
		GUITools.addComponent(panelPdfInvitation, gblPdfInvitation,
				buttonBrowseInv, 2, 0, 1, 1, 0.0, 1.0, 5, 5, 5, 5,
				GridBagConstraints.NONE, GridBagConstraints.WEST);
		GUITools.addComponent(panelPdfInvitation, gblPdfInvitation,
				labelImageInvitation, 3, 0, 1, 1, 1.0, 1.0, 0, 5, 0, 6,
				GridBagConstraints.NONE, GridBagConstraints.EAST);

		textInvitation = new JTextArea();
		textInvitation.setLineWrap(true);
		textInvitation.setWrapStyleWord(true);
		textInvitation.setRows(3);

		GUITools.addComponent(panelPdfInvitation, gblPdfInvitation, new JLabel(
				_("Text of invitations:")), 0, 1, 4, 1, 0.0, 0.0, 5, 5, 0, 5,
				GridBagConstraints.NONE, GridBagConstraints.WEST);
		GUITools.addComponent(panelPdfInvitation, gblPdfInvitation,
				GUITools.setIntoScrllPn(textInvitation), 0, 2, 4, 1, 1.0, 0.0,
				5, 5, 5, 5, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST);

		textFootInvitation = new JTextField();

		GUITools.addComponent(panelPdfInvitation, gblPdfInvitation, new JLabel(
				_("Displayed foot text:")), 0, 3, 4, 1, 0.0, 0.0, 5, 5, 0, 5,
				GridBagConstraints.NONE, GridBagConstraints.WEST);
		GUITools.addComponent(panelPdfInvitation, gblPdfInvitation,
				textFootInvitation, 0, 4, 4, 1, 1.0, 0.0, 5, 5, 5, 5,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);

		/*
		 * Add the panels to PDF export tab
		 */
		GUITools.addComponent(panelPDF, gblPdfExport, panelPdfProtocol, 0, 0,
				1, 1, 1.0, 1.0, 10, 10, 10, 10, GridBagConstraints.BOTH,
				GridBagConstraints.WEST);
		GUITools.addComponent(panelPDF, gblPdfExport, panelPdfInvitation, 0, 1,
				1, 1, 1.0, 1.0, 10, 10, 10, 10, GridBagConstraints.BOTH,
				GridBagConstraints.WEST);
	}

	/**
	 * Switch to tab general.
	 */
	public void switchToTabGeneral() {
		tabbedPane.setSelectedComponent(panelGeneral);
	}

	/**
	 * Switch to tab pdf export.
	 */
	public void switchToTabPDFExport() {
		tabbedPane.setSelectedComponent(panelPDF);
	}

	/**
	 * This method updates the dialog elements.
	 */
	public void updateDialog() {
		/*
		 * Dialog description
		 */
		if (tabbedPane.getSelectedComponent() == panelGeneral) {
			setDescription(_("Here you can set some application options. They will be saved in the database independently of any particular review."));
		} else if (tabbedPane.getSelectedComponent() == panelPDF) {
			setDescription(_("Here you can set some options concerning the PDF export. They will be saved in the database independently of any particular review."));
		} else {
			setDescription("");
		}
	}

	/**
	 * This method updates the data in the app data model.
	 */
	public void updateAppData() {
		/*
		 * GENERAL
		 */
		try {
			if (checkboxAutoSave.isSelected()) {
				appData.setSettingValue(AppSettingKey.APP_DO_AUTO_SAVE,
						AppSettingValue.TRUE);

				appData.setSetting(AppSettingKey.APP_AUTO_SAVE_INTERVAL,
						Integer.toString((Integer) spinnerAutoSave.getValue()));
			} else {
				appData.setSettingValue(AppSettingKey.APP_DO_AUTO_SAVE,
						AppSettingValue.FALSE);
			}

			if (checkboxCheckUpdates.isSelected()) {
				appData.setSettingValue(AppSettingKey.APP_CHECK_VERSION,
						AppSettingValue.TRUE);
			} else {
				appData.setSettingValue(AppSettingKey.APP_CHECK_VERSION,
						AppSettingValue.FALSE);
			}

			if (checkboxShowHints.isSelected()) {
				appData.setSettingValue(AppSettingKey.APP_SHOW_HINTS,
						AppSettingValue.TRUE);
			} else {
				appData.setSettingValue(AppSettingKey.APP_SHOW_HINTS,
						AppSettingValue.FALSE);
			}

			if (checkboxHighlightFields.isSelected()) {
				appData.setSettingValue(AppSettingKey.APP_HIGHLIGHT_FIELDS,
						AppSettingValue.TRUE);
			} else {
				appData.setSettingValue(AppSettingKey.APP_HIGHLIGHT_FIELDS,
						AppSettingValue.FALSE);
			}

			if (checkboxProtWarn.isSelected()) {
				appData.setSettingValue(
						AppSettingKey.APP_SHOW_PROTOCOL_WARNING,
						AppSettingValue.TRUE);

				appData.setSetting(AppSettingKey.APP_PROTOCOL_WARNING_TIME,
						Integer.toString((Integer) spinnerProtWarn.getValue()));
			} else {
				appData.setSettingValue(
						AppSettingKey.APP_SHOW_PROTOCOL_WARNING,
						AppSettingValue.FALSE);
			}

			String language = ((Language) boxLanguage.getSelectedItem())
					.getLangCode();
			appData.setSetting(AppSettingKey.APP_LANGUAGE, language);
		} catch (DataException e) {
			JOptionPane.showMessageDialog(null,
					GUITools.getMessagePane(e.getMessage()), _("Error"),
					JOptionPane.ERROR_MESSAGE);
		}

		/*
		 * PDF EXPORT
		 */
		try {
			/* logo protocol */
			if (logoPathProtocol != null) {
				File source = new File(logoPathProtocol);
				File target = new File(Data.getInstance().getAppData()
						.getAppDataPath()
						+ Data.getInstance().getResource("logoPdfProtName"));

				if (!source.equals(target)) {
					FileTools.copyFile(source, target);

					logoPathProtocol = Data.getInstance().getAppData()
							.getAppDataPath()
							+ Data.getInstance().getResource("logoPdfProtName");

					appData.setSetting(AppSettingKey.PDF_PROTOCOL_LOGO,
							logoPathProtocol);
				}
			} else {
				appData.setSetting(AppSettingKey.PDF_PROTOCOL_LOGO, "");
			}

			/* logo invitation */
			if (logoPathInvitation != null) {
				File source = new File(logoPathInvitation);
				File target = new File(Data.getInstance().getAppData()
						.getAppDataPath()
						+ Data.getInstance().getResource("logoPdfInvName"));

				if (!source.equals(target)) {
					FileTools.copyFile(source, target);

					logoPathInvitation = Data.getInstance().getAppData()
							.getAppDataPath()
							+ Data.getInstance().getResource("logoPdfInvName");

					appData.setSetting(AppSettingKey.PDF_INVITATION_LOGO,
							logoPathInvitation);
				}
			} else {
				appData.setSetting(AppSettingKey.PDF_INVITATION_LOGO, "");
			}

			appData.setSetting(AppSettingKey.PDF_PROTOCOL_FOOT_TEXT,
					textFootProtocol.getText());
			appData.setSetting(AppSettingKey.PDF_INVITATION_FOOT_TEXT,
					textFootInvitation.getText());
			appData.setSetting(AppSettingKey.PDF_INVITATION_TEXT,
					textInvitation.getText());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					GUITools.getMessagePane(e.getMessage()), _("Error"),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Update dialog data.
	 */
	public void updateDialogData() {
		/*
		 * GENERAL
		 */
		try {
			if (appData.getSettingValue(AppSettingKey.APP_DO_AUTO_SAVE) == AppSettingValue.TRUE) {
				checkboxAutoSave.setSelected(true);
			} else {
				checkboxAutoSave.setSelected(false);
				spinnerAutoSave.setEnabled(false);
			}

			if (appData.getSetting(AppSettingKey.APP_AUTO_SAVE_INTERVAL) != null) {
				spinnerAutoSave.setValue(Integer.parseInt(appData
						.getSetting(AppSettingKey.APP_AUTO_SAVE_INTERVAL)));
			} else {
				spinnerAutoSave.setValue(5);
			}

			if (appData.getSettingValue(AppSettingKey.APP_CHECK_VERSION) == AppSettingValue.TRUE) {
				checkboxCheckUpdates.setSelected(true);
			} else {
				checkboxCheckUpdates.setSelected(false);
			}

			if (appData.getSettingValue(AppSettingKey.APP_SHOW_HINTS) == AppSettingValue.TRUE) {
				checkboxShowHints.setSelected(true);
			} else {
				checkboxShowHints.setSelected(false);
			}

			if (appData.getSettingValue(AppSettingKey.APP_HIGHLIGHT_FIELDS) == AppSettingValue.TRUE) {
				checkboxHighlightFields.setSelected(true);
			} else {
				checkboxHighlightFields.setSelected(false);
			}

			if (appData
					.getSettingValue(AppSettingKey.APP_SHOW_PROTOCOL_WARNING) == AppSettingValue.TRUE) {
				checkboxProtWarn.setSelected(true);
			} else {
				checkboxProtWarn.setSelected(false);
				spinnerProtWarn.setEnabled(false);
			}

			if (appData.getSetting(AppSettingKey.APP_PROTOCOL_WARNING_TIME) != null) {
				spinnerProtWarn.setValue(Integer.parseInt(appData
						.getSetting(AppSettingKey.APP_PROTOCOL_WARNING_TIME)));
			} else {
				spinnerProtWarn.setValue(15);
			}

			String language = appData.getSetting(AppSettingKey.APP_LANGUAGE);
			int langCount = boxLanguage.getItemCount();
			for (int i = 0; i < langCount; i++) {
				if (((Language) boxLanguage.getItemAt(i)).getLangCode().equals(
						language)) {
					boxLanguage.setSelectedIndex(i);
				}
			}
		} catch (DataException e) {
			JOptionPane.showMessageDialog(null,
					GUITools.getMessagePane(e.getMessage()), _("Error"),
					JOptionPane.ERROR_MESSAGE);
		}

		/*
		 * PDF EXPORT
		 */
		try {
			/* protocol logo */
			if (appData.getSetting(AppSettingKey.PDF_PROTOCOL_LOGO) != null
					&& !appData.getSetting(AppSettingKey.PDF_PROTOCOL_LOGO)
							.equals("")) {
				logoPathProtocol = appData
						.getSetting(AppSettingKey.PDF_PROTOCOL_LOGO);
			}

			/* invitation logo */
			if (appData.getSetting(AppSettingKey.PDF_INVITATION_LOGO) != null
					&& !appData.getSetting(AppSettingKey.PDF_INVITATION_LOGO)
							.equals("")) {
				logoPathInvitation = appData
						.getSetting(AppSettingKey.PDF_INVITATION_LOGO);
			}

			updateLogoViews();

			/* protocol foot text */
			if (appData.getSetting(AppSettingKey.PDF_PROTOCOL_FOOT_TEXT) != null) {
				textFootProtocol.setText(appData
						.getSetting(AppSettingKey.PDF_PROTOCOL_FOOT_TEXT));
			} else {
				textFootProtocol.setText("");
			}

			/* invitation foot text */
			if (appData.getSetting(AppSettingKey.PDF_INVITATION_FOOT_TEXT) != null) {
				textFootInvitation.setText(appData
						.getSetting(AppSettingKey.PDF_INVITATION_FOOT_TEXT));
			} else {
				textFootInvitation.setText("");
			}

			/* invitation text */
			if (appData.getSetting(AppSettingKey.PDF_INVITATION_TEXT) != null) {
				textInvitation.setText(appData
						.getSetting(AppSettingKey.PDF_INVITATION_TEXT));
			} else {
				textInvitation.setText("");
			}
		} catch (DataException e) {
			JOptionPane.showMessageDialog(null,
					GUITools.getMessagePane(e.getMessage()), _("Error"),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.revager.gui.AbstractDialog#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean vis) {
		if (vis) {
			GUITools.executeSwingWorker(new SettingsWorker(Mode.LOAD_APPDATA));
		}

		super.setVisible(vis);
	}

	/**
	 * Update logo views.
	 */
	public void updateLogoViews() {
		labelImageProtocol.setIcon(Data.getInstance().getIcon(
				"noImageProgress_30x30.gif"));
		labelImageInvitation.setIcon(Data.getInstance().getIcon(
				"noImageProgress_30x30.gif"));

		/* protocol logo */
		ImageIcon icon = Data.getInstance().getIcon("noImage_30x30.png");

		if (logoPathProtocol != null) {
			icon = new ImageIcon(logoPathProtocol);
		}

		Image image = icon.getImage().getScaledInstance(-1, 30,
				Image.SCALE_SMOOTH);
		labelImageProtocol.setIcon(new ImageIcon(image));

		/* invitation logo */
		icon = Data.getInstance().getIcon("noImage_30x30.png");

		if (logoPathInvitation != null) {
			icon = new ImageIcon(logoPathInvitation);
		}

		image = icon.getImage().getScaledInstance(-1, 30, Image.SCALE_SMOOTH);
		labelImageInvitation.setIcon(new ImageIcon(image));
	}

	private class Language {
		private String langDescription = null;
		private String langCode = null;

		public Language(String description, String langCode) {
			super();

			this.langDescription = description;
			this.langCode = langCode;
		}

		@Override
		public String toString() {
			return this.langDescription;
		}

		public String getLangCode() {
			return this.langCode;
		}
	}

}
