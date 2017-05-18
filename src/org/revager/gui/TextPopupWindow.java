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

import static org.revager.app.model.Data.translate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.revager.app.model.Data;
import org.revager.gui.actions.popup.TextPopupWindowAction;
import org.revager.tools.GUITools;

/**
 * This class represents a general popup window for simple text input. It is
 * used at different places in the application.
 */
@SuppressWarnings("serial")
public class TextPopupWindow extends JDialog {

	public static enum ButtonClicked {
		OK, ABORT;
	};

	private ButtonClicked buttonClicked = null;

	private JTextComponent textInput = null;

	/**
	 * Instantiates a new text popup window.
	 * 
	 * @param parent
	 *            the parent
	 * @param titleText
	 *            the title text
	 * @param inputText
	 *            the input text
	 * @param multiLine
	 *            the multi line
	 */
	public TextPopupWindow(Window parent, String titleText, String inputText, boolean multiLine) {
		super(parent);

		setLayout(new BorderLayout());

		// setUndecorated(true);
		setResizable(false);
		setTitle(translate("RevAger"));

		setModal(true);

		JPanel panelBase = GUITools.newPopupBasePanel();

		JTextArea textTitle = GUITools.newPopupTitleArea(titleText);

		panelBase.add(textTitle, BorderLayout.NORTH);

		Dimension popupSize;

		if (multiLine == true) {
			textInput = new JTextArea();
			textInput.setMargin(new Insets(3, 3, 3, 3));
			((JTextArea) textInput).setLineWrap(true);
			((JTextArea) textInput).setWrapStyleWord(true);

			JScrollPane scrollPane = new JScrollPane(textInput);
			// scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scrollPane.setBorder(null);

			panelBase.add(scrollPane, BorderLayout.CENTER);

			popupSize = new Dimension(260, 200);
		} else {
			textInput = new JTextField();
			textInput.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));

			panelBase.add(textInput, BorderLayout.CENTER);

			popupSize = new Dimension(260, 140);
		}

		/*
		 * Text input general
		 */
		textInput.setText(inputText);
		textInput.setCaretPosition(0);
		textInput.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

		/*
		 * The buttons to abort and confirm the input
		 */
		JButton buttonAbort = GUITools.newImageButton();
		buttonAbort.setIcon(Data.getInstance().getIcon("buttonCancel_24x24_0.png"));
		buttonAbort.setRolloverIcon(Data.getInstance().getIcon("buttonCancel_24x24.png"));
		buttonAbort.setToolTipText(translate("Abort"));
		buttonAbort.addActionListener(new TextPopupWindowAction(this, ButtonClicked.ABORT));

		JButton buttonConfirm = GUITools.newImageButton();
		buttonConfirm.setIcon(Data.getInstance().getIcon("buttonOk_24x24_0.png"));
		buttonConfirm.setRolloverIcon(Data.getInstance().getIcon("buttonOk_24x24.png"));
		buttonConfirm.setToolTipText(translate("Confirm"));
		buttonConfirm.addActionListener(new TextPopupWindowAction(this, ButtonClicked.OK));

		JPanel panelButtons = new JPanel(new BorderLayout());
		panelButtons.setBackground(UI.POPUP_BACKGROUND);
		panelButtons.setBorder(BorderFactory.createLineBorder(panelButtons.getBackground(), 3));
		panelButtons.add(buttonAbort, BorderLayout.WEST);
		panelButtons.add(buttonConfirm, BorderLayout.EAST);

		/*
		 * Base panel
		 */
		panelBase.add(panelButtons, BorderLayout.SOUTH);

		add(panelBase, BorderLayout.CENTER);

		pack();

		/*
		 * Set size and location
		 */
		setMinimumSize(popupSize);
		setSize(popupSize);
		setPreferredSize(popupSize);

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

	/**
	 * Gets the input.
	 * 
	 * @return the input
	 */
	public String getInput() {
		return textInput.getText();
	}

}
