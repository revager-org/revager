package org.revager.gui.dialogs.assistant;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.revager.gui.dialogs.assistant.LanguagePopupWindow.ButtonClicked;



@SuppressWarnings("serial")
public class LanguagePopupWindowAction extends AbstractAction {

	private LanguagePopupWindow popupWindow = null;

	private ButtonClicked buttonClick = null;


	public LanguagePopupWindowAction(
			LanguagePopupWindow popupWin, ButtonClicked buttonClk) {
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
		popupWindow.setButtonClicked(buttonClick);
		popupWindow.setVisible(false);
	}

}
