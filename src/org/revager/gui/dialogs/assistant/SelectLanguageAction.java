package org.revager.gui.dialogs.assistant;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.revager.app.model.ApplicationData;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.appdata.AppSettingKey;
import org.revager.gui.UI;

public class SelectLanguageAction extends AbstractAction{
	
	private LanguagePopupWindow popup;
	private ApplicationData appData = Data.getInstance().getAppData();
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		

			if(UI.getInstance().getAssistantDialog().isVisible())
				popup = new LanguagePopupWindow(UI.getInstance()
						.getAssistantDialog(), Data.getInstance().getLocaleStr(
						"assistantDialog.selectLanguage"));
			popup.setVisible(true);

			if (popup.getButtonClicked() == LanguagePopupWindow.ButtonClicked.OK) {
				try {
					appData.setSetting(AppSettingKey.APP_LANGUAGE, popup.getSelectedLanguage());
					UI.getInstance().getAssistantDialog().setMessage(Data.getInstance()
							.getLocaleStr("settingsDialog.general.langHintRestart"));
				} catch (DataException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}


