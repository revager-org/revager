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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.revager.app.model.Data;
import org.revager.gui.actions.WarningAction;
import org.revager.tools.GUITools;


/**
 * The Class WarningDialog.
 */
@SuppressWarnings("serial")
public class WarningDialog extends JDialog {

	private String dontShowAgainString = Data.getInstance().getLocaleStr(
			"warningDialog.dontShowAgain");

	private JCheckBox dontShowAgainBx = new JCheckBox(dontShowAgainString);

	private GridBagLayout gbl = new GridBagLayout();

	private ButtonClicked buttonClicked = null;

	/**
	 * Checks if is dont show again.
	 * 
	 * @return true, if is dont show again
	 */
	public boolean isDontShowAgain() {
		return dontShowAgainBx.isSelected();
	}

	/**
	 * The Enum ButtonClicked.
	 */
	public static enum ButtonClicked {
		YES, NO;
	};

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

	/*
	 * 
	 * constructor
	 */
	/**
	 * Instantiates a new warning dialog.
	 * 
	 * @param parent
	 *            the parent
	 * @param message
	 *            the message
	 */
	public WarningDialog(Window parent, String message) {
		super(parent);
		setLayout(gbl);
		setModal(true);
		setTitle(Data.getInstance().getLocaleStr("warningDialog.title"));

		JLabel messageLabel = GUITools.getMessagePane(message);

		JButton canceleBttn = new JButton(Data.getInstance().getLocaleStr("no"));
		canceleBttn
				.addActionListener(new WarningAction(this, ButtonClicked.NO));
		JButton acceptBttn = new JButton(Data.getInstance().getLocaleStr("yes"));
		acceptBttn
				.addActionListener(new WarningAction(this, ButtonClicked.YES));

		GUITools.addComponent(this, gbl, messageLabel, 0, 0, 2, 1, 1.0, 0, 20,
				10, 0, 10, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, dontShowAgainBx, 0, 1, 2, 1, 1.0, 0,
				15, 10, 15, 10, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, acceptBttn, 0, 2, 1, 1, 1.0, 0, 5, 5,
				25, 5, GridBagConstraints.NONE, GridBagConstraints.CENTER);
		GUITools.addComponent(this, gbl, canceleBttn, 1, 2, 1, 1, 1.0, 0, 5, 5,
				25, 5, GridBagConstraints.NONE, GridBagConstraints.CENTER);

		pack();

		// setMinimumSize(new Dimension(350,210));
		setResizable(false);

		// calculating Position of HelpBrowserFrame
		int top = ((parent.getHeight() - getHeight()) / 2);
		int left = ((parent.getWidth() - getWidth()) / 2);

		// setting Position of HelpBrowserFrame
		setLocation(left, top);

	}

}
