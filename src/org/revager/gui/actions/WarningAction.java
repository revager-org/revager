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
package org.revager.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.revager.gui.dialogs.WarningDialog;
import org.revager.gui.dialogs.WarningDialog.ButtonClicked;

/**
 * The Class WarningAction.
 */
@SuppressWarnings("serial")
public class WarningAction extends AbstractAction {

	private WarningDialog warningDialog = null;

	private ButtonClicked buttonClick = null;

	/**
	 * Instantiates a new warning action.
	 * 
	 * @param warningDialog
	 *            the warning dialog
	 * @param buttonClk
	 *            the button clk
	 */
	public WarningAction(WarningDialog warningDialog, ButtonClicked buttonClk) {
		super();

		this.warningDialog = warningDialog;
		this.buttonClick = buttonClk;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		warningDialog.setButtonClicked(buttonClick);
		warningDialog.setVisible(false);
	}

}
