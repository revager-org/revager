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
package org.revager.gui.actions.assistant;

import static org.revager.app.model.Data._;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.revager.Main;
import org.revager.app.model.ApplicationData;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.appdata.AppSettingKey;
import org.revager.gui.UI;
import org.revager.gui.dialogs.assistant.LanguagePopupWindow;
import org.revager.tools.GUITools;

/**
 * The Class GoToAddAttPnlAction. Calling this action will open a pop-up to
 * select a language. After confirming you'll need to restart the application to
 * activate the language changes.
 * 
 * @author D.Casciato
 * 
 */
@SuppressWarnings("serial")
public class SelectLanguageAction extends AbstractAction {

	private LanguagePopupWindow popup;
	private ApplicationData appData = Data.getInstance().getAppData();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ev) {

		if (UI.getInstance().getAssistantDialog().isVisible())
			popup = new LanguagePopupWindow(UI.getInstance().getAssistantDialog(), _("Select language"));
		popup.setVisible(true);

		if (popup.getButtonClicked() == LanguagePopupWindow.ButtonClicked.OK) {
			try {
				appData.setSetting(AppSettingKey.APP_LANGUAGE, popup.getSelectedLanguage());

				int option = JOptionPane.showConfirmDialog(UI.getInstance().getAssistantDialog(),
						GUITools.getMessagePane(
								_("You have to restart the application in order finalize the change of language. Restart now?")),
						_("Question"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (option == JOptionPane.YES_OPTION) {
					Main.restartApplication();
				}
			} catch (DataException e) {
				JOptionPane.showMessageDialog(UI.getInstance().getAssistantDialog(),
						GUITools.getMessagePane(e.getMessage()), _("Error"), JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
