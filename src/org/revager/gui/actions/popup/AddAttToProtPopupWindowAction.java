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
package org.revager.gui.actions.popup;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.revager.gui.UI;
import org.revager.gui.findings_list.AddAttToFLPopupWindow;
import org.revager.gui.findings_list.AddAttToFLPopupWindow.ButtonClicked;

/**
 * The Class AddAttToProtPopupWindowAction.
 */
@SuppressWarnings("serial")
public class AddAttToProtPopupWindowAction extends AbstractAction {

	private AddAttToFLPopupWindow popupWindow = null;

	private ButtonClicked buttonClick = null;

	/**
	 * Instantiates a new adds the att to prot popup window action.
	 * 
	 * @param popupWin
	 *            the popup win
	 * @param buttonClk
	 *            the button clk
	 */
	public AddAttToProtPopupWindowAction(AddAttToFLPopupWindow popupWin, ButtonClicked buttonClk) {
		this.popupWindow = popupWin;
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
		boolean isMissing = false;

		popupWindow.getNameTxtFld().setBorder(UI.STANDARD_BORDER_INLINE);
		popupWindow.getScrllPn().setBorder(UI.STANDARD_BORDER);

		popupWindow.commitSpinnerValues();

		if (popupWindow.getAttName().trim().equals("")) {
			popupWindow.getNameTxtFld().setBorder(UI.MARKED_BORDER_INLINE);
			isMissing = true;
		}

		if (!isMissing && buttonClick == ButtonClicked.OK || buttonClick == ButtonClicked.ABORT) {
			popupWindow.setButtonClicked(buttonClick);
			popupWindow.setVisible(false);
		}
	}

}
