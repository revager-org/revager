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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.revager.app.model.ApplicationData;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.appdata.AppSettingKey;
import org.revager.gui.UI;
import org.revager.gui.actions.popup.LanguagePopupWindowAction;
import org.revager.gui.helpers.HLink;
import org.revager.gui.helpers.LinkGroup;
import org.revager.tools.GUITools;

/**
 * The Class LanguagePopupWindow.
 * 
 * @author D.Casciato
 * 
 */
@SuppressWarnings("serial")
public class LanguagePopupWindow extends JDialog {

	/**
	 * The Enum ButtonClicked.
	 */
	public static enum ButtonClicked {
		OK, ABORT;
	};

	private ApplicationData appData = Data.getInstance().getAppData();

	private ButtonClicked buttonClicked = null;

	private GridBagLayout gbl = new GridBagLayout();
	/*
	 * Panels
	 */
	private JPanel inputPnl = new JPanel(gbl);
	private JPanel panelBase = GUITools.newPopupBasePanel();

	/*
	 * Strings
	 */
	private String germanStrng = Data.getInstance().getLocaleStr(
			"popup.language.german");
	private String englishStrng = Data.getInstance().getLocaleStr(
			"popup.language.english");

	/*
	 * ImageIcons
	 */
	private ImageIcon germanIcon = Data.getInstance().getIcon(
			"german_31x20_0.png");
	private ImageIcon germanRolloverIcon = Data.getInstance().getIcon(
			"german_31x20.png");
	private ImageIcon englishIcon = Data.getInstance().getIcon(
			"english_31x20_0.png");
	private ImageIcon englishRolloverIcon = Data.getInstance().getIcon(
			"english_31x20.png");
	private String currentLang;

	/*
	 * Links and LinkGroup
	 */
	private LinkGroup languageGrp = new LinkGroup();
	private HLink germanHLnk = new HLink(germanStrng, germanIcon,
			germanRolloverIcon, languageGrp);
	private HLink englishHLnk = new HLink(englishStrng, englishIcon,
			englishRolloverIcon, languageGrp);

	/**
	 * Returns the selected language.
	 * 
	 * @return
	 */
	public String getSelectedLanguage() {
		if (languageGrp.getSelectedLinkText().contains(
				Data.getInstance().getLocaleStr("popup.language.german")))
			return "de";
		else
			return "en";
	}

	/**
	 * Constructor
	 * 
	 * @param parent
	 * @param titleText
	 */
	public LanguagePopupWindow(Window parent, String titleText) {
		super(parent);

		setLayout(new BorderLayout());

		setUndecorated(true);

		setModal(true);

		JTextArea textTitle = GUITools.newPopupTitleArea(titleText);

		panelBase.add(textTitle, BorderLayout.NORTH);

		try {
			currentLang = appData.getSetting(AppSettingKey.APP_LANGUAGE);
		} catch (DataException e) {
			currentLang = null;
		}
		languageGrp.addLink(germanHLnk);
		languageGrp.addLink(englishHLnk);

		if (currentLang.equals("de"))
			languageGrp.selectLink(germanHLnk);
		else if (currentLang.equals("en"))
			languageGrp.selectLink(englishHLnk);

		GUITools.addComponent(inputPnl, gbl, germanHLnk, 0, 0, 1, 1, 1.0, 1.0,
				10, 10, 0, 10, GridBagConstraints.NONE,
				GridBagConstraints.CENTER);
		GUITools.addComponent(inputPnl, gbl, englishHLnk, 0, 1, 1, 1, 1.0, 1.0,
				10, 10, 10, 10, GridBagConstraints.NONE,
				GridBagConstraints.CENTER);

		panelBase.add(inputPnl, BorderLayout.CENTER);

		/*
		 * The buttons to abort and confirm the input
		 */
		JButton buttonAbort = GUITools.newImageButton();
		buttonAbort.setIcon(Data.getInstance().getIcon(
				"buttonCancel_24x24_0.png"));
		buttonAbort.setRolloverIcon(Data.getInstance().getIcon(
				"buttonCancel_24x24.png"));
		buttonAbort.setToolTipText(Data.getInstance().getLocaleStr("abort"));
		buttonAbort.addActionListener(new LanguagePopupWindowAction(this,
				ButtonClicked.ABORT));

		JButton buttonConfirm = GUITools.newImageButton();
		buttonConfirm.setIcon(Data.getInstance()
				.getIcon("buttonOk_24x24_0.png"));
		buttonConfirm.setRolloverIcon(Data.getInstance().getIcon(
				"buttonOk_24x24.png"));
		buttonConfirm
				.setToolTipText(Data.getInstance().getLocaleStr("confirm"));
		buttonConfirm.addActionListener(new LanguagePopupWindowAction(this,
				ButtonClicked.OK));

		JPanel panelButtons = new JPanel(new BorderLayout());
		panelButtons.setBackground(UI.POPUP_BACKGROUND);
		panelButtons.setBorder(BorderFactory.createLineBorder(panelButtons
				.getBackground(), 3));
		panelButtons.add(buttonAbort, BorderLayout.WEST);
		panelButtons.add(buttonConfirm, BorderLayout.EAST);

		/*
		 * Base panel
		 */
		panelBase.add(panelButtons, BorderLayout.SOUTH);

		add(panelBase, BorderLayout.CENTER);

		/*
		 * Set size and location
		 */
		Dimension popupSize = new Dimension(130, 170);

		setMinimumSize(popupSize);
		setSize(popupSize);
		setPreferredSize(popupSize);

		pack();

		setAlwaysOnTop(true);
		toFront();

		GUITools.setLocationToCursorPos(this);
	}

	/**
	 * Gets the button clicked.
	 * 
	 * @return the buttonClicked
	 */
	public ButtonClicked getButtonClicked() {
		return buttonClicked;
	}

	/**
	 * Sets the button clicked.
	 * 
	 * @param buttonClicked
	 *            the buttonClicked to set
	 */
	public void setButtonClicked(ButtonClicked buttonClicked) {
		this.buttonClicked = buttonClicked;
	}

}
