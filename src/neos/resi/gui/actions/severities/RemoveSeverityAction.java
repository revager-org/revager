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
package neos.resi.gui.actions.severities;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import neos.resi.app.Application;
import neos.resi.app.SeverityManagement;
import neos.resi.app.model.Data;
import neos.resi.gui.UI;
import neos.resi.gui.dialogs.WarningDialog;
import neos.resi.gui.dialogs.WarningDialog.ButtonClicked;

/**
 * The Class RemoveSeverityAction.
 */
@SuppressWarnings("serial")
public class RemoveSeverityAction extends AbstractAction {

	private String message = Data.getInstance().getLocaleStr(
			"warningDialog.sev.message");

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {

		boolean dontShowAgain = UI.getInstance().getManageSeveritiesDialog()
				.isDontShowAgain();

		/*
		 * showing warning dialog when check box was never set
		 */
		if (!dontShowAgain) {
			WarningDialog remSevDialog = new WarningDialog(UI.getInstance()
					.getManageSeveritiesDialog(), message);

			remSevDialog.setVisible(true);

			if (remSevDialog.getButtonClicked() == ButtonClicked.YES) {
				removeSev();
			}

			UI.getInstance().getManageSeveritiesDialog().setDontShowAgain(
					remSevDialog.isDontShowAgain());
		} else if (UI.getInstance().getManageSeveritiesDialog()
				.isDontShowAgain()) {
			/*
			 * else delete selected severity
			 */

			removeSev();
		}

		UI.getInstance().getManageSeveritiesDialog().updateButtons();
	}

	/*
	 * methode for removing selected sev
	 */
	/**
	 * Removes the sev.
	 */
	private void removeSev() {
		SeverityManagement sevMan = Application.getInstance().getSeverityMgmt();

		int selectedRow = UI.getInstance().getManageSeveritiesDialog()
				.getSeverityTbl().getSelectedRow();

		sevMan.removeSeverity(sevMan.getSeverities().get(selectedRow));

		UI.getInstance().getManageSeveritiesDialog().getStm()
				.fireTableDataChanged();
	}

}
