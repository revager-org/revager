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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import org.revager.app.model.Data;
import org.revager.gui.UI;


@SuppressWarnings("serial")
/**
 * The class OpenAssistantAction.
 * 
 */
public class OpenAssistantAction extends AbstractAction{

	/**
	 * Opens the AssistantDialog.
	 */
	public OpenAssistantAction() {
		super();

		putValue(Action.SMALL_ICON, Data.getInstance().getIcon(
				"menuAssistant_16x16.png"));
		putValue(Action.NAME, Data.getInstance().getLocaleStr(
				"menu.showAssistant"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M, Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		UI.getInstance().getAssistantDialog().setCurrentPnl(
		UI.getInstance().getAssistantDialog().getFirstScreenPanel());
		UI.getInstance().getAssistantDialog().updateMessage();
		UI.getInstance().getAssistantDialog().updateContents();
		UI.getInstance().getAssistantDialog().updateWizardBttns();
		UI.getInstance().getAssistantDialog().setVisible(true);
	}

}