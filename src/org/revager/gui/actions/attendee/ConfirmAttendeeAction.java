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
package org.revager.gui.actions.attendee;

import static org.revager.app.model.Data._;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.revager.app.Application;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.appdata.AppAttendee;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.Role;
import org.revager.gui.UI;
import org.revager.gui.dialogs.AttendeeDialog;
import org.revager.tools.GUITools;

/**
 * The Class ConfirmAttendeeAction.
 */
@SuppressWarnings("serial")
public class ConfirmAttendeeAction extends AbstractAction {

	private Role[] roles = Role.values();
	private String attContact;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ev) {
		AttendeeDialog attDialog = UI.getInstance().getAttendeeDialog();

		JTextField nameTxtFld = attDialog.getNameTxtFld();
		JScrollPane contactScrllPn = attDialog.getContactScrllPn();

		nameTxtFld.setBorder(UI.STANDARD_BORDER_INLINE);
		contactScrllPn.setBorder(UI.STANDARD_BORDER);

		String attName = nameTxtFld.getText();
		if (attDialog.getContactTxtArea().getText() != null)
			attContact = attDialog.getContactTxtArea().getText();
		else
			attContact = "";

		Role attRole = roles[attDialog.getRoleBox().getSelectedIndex()];

		boolean nameMissing = false;

		String message = "";

		if (attName.trim().equals("")) {
			nameMissing = true;
		}

		if (nameMissing) {
			message = _("Please enter the name of the attendee.");

			attDialog.setMessage(message);
			nameTxtFld.setBorder(UI.MARKED_BORDER_INLINE);
		} else {
			AppAttendee currAppAtt = attDialog.getCurrentAppAttendee();
			Attendee currAtt = attDialog.getCurrentAttendee();

			/*
			 * Update the app attendee in the database
			 */
			try {
				if (currAppAtt == null) {
					currAppAtt = Data.getInstance().getAppData().getAttendee(attName, attContact);

					if (currAppAtt == null) {
						currAppAtt = Data.getInstance().getAppData().newAttendee(attName, attContact);
					}
				} else {
					currAppAtt.setNameAndContact(attName, attContact);
				}

				for (String str : currAppAtt.getStrengths()) {
					currAppAtt.removeStrength(str);
				}

				for (String str : attDialog.getStrengthList()) {
					currAppAtt.addStrength(str);
				}
			} catch (DataException e) {
				JOptionPane.showMessageDialog(attDialog, GUITools.getMessagePane(e.getMessage()), _("Error"),
						JOptionPane.ERROR_MESSAGE);
			}

			/*
			 * update the review attendee
			 */
			Attendee newAtt = new Attendee();

			newAtt.setName(attName);
			newAtt.setContact(attContact);
			newAtt.setRole(attRole);

			if (currAtt == null) {
				if (!Application.getInstance().getAttendeeMgmt().isAttendee(newAtt)) {
					Application.getInstance().getAttendeeMgmt().addAttendee(attName, attContact, attRole, null);
				} else {
					attDialog.setMessage(_(
							"There is an attendee with the given information already existing. Please change the name, the contact information or the role of the attendee you would like to add."));

					return;
				}
			} else {
				newAtt.setAspects(currAtt.getAspects());

				if (Application.getInstance().getAttendeeComp().compare(currAtt, newAtt) != 0) {
					if (!Application.getInstance().getAttendeeMgmt().editAttendee(currAtt, newAtt)) {
						attDialog.setMessage(_(
								"There is an attendee with the given information already existing. Please change the name, the contact information or the role of the attendee you would like to add."));

						return;
					}
				}
			}

			attDialog.setVisible(false);

			UI.getInstance().getMainFrame().updateAttendeesTable(false);
			UI.getInstance().getMainFrame().updateButtons();

			UI.getInstance().getAspectsManagerFrame().updateViews();

			if (attDialog.isCalledByAspectsManager()) {
				attDialog.setCalledByAspectsManager(false);

				UI.getInstance().getAspectsManagerFrame().setVisible(true);
			}
		}
	}

}
