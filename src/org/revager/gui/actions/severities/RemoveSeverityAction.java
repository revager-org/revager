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
package org.revager.gui.actions.severities;

import static org.revager.app.model.Data._;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.revager.app.Application;
import org.revager.app.SeverityManagement;
import org.revager.gui.UI;
import org.revager.gui.dialogs.WarningDialog;
import org.revager.gui.dialogs.WarningDialog.ButtonClicked;

/**
 * The Class RemoveSeverityAction.
 */
@SuppressWarnings("serial")
public class RemoveSeverityAction extends AbstractAction {

	private String message = _(
			"If you remove a severity, this will affect the whole review. Removed severities will be replaced by the next higher one. Would you really like to remove the selected severity?");

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {

		boolean dontShowAgain = UI.getInstance().getManageSeveritiesDialog().isDontShowAgain();

		/*
		 * showing warning dialog when check box was never set
		 */
		if (!dontShowAgain) {
			WarningDialog remSevDialog = new WarningDialog(UI.getInstance().getManageSeveritiesDialog(), message);

			remSevDialog.setVisible(true);

			if (remSevDialog.getButtonClicked() == ButtonClicked.YES) {
				removeSev();
			}

			UI.getInstance().getManageSeveritiesDialog().setDontShowAgain(remSevDialog.isDontShowAgain());
		} else if (UI.getInstance().getManageSeveritiesDialog().isDontShowAgain()) {
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

		int selectedRow = UI.getInstance().getManageSeveritiesDialog().getSeverityTbl().getSelectedRow();

		sevMan.removeSeverity(sevMan.getSeverities().get(selectedRow));

		UI.getInstance().getManageSeveritiesDialog().getStm().fireTableDataChanged();
	}

}
