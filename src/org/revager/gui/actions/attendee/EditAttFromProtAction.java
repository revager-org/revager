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

import static org.revager.app.model.Data.translate;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.xml.datatype.Duration;

import org.revager.app.Application;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.Protocol;
import org.revager.gui.UI;
import org.revager.gui.findings_list.AddAttToFLPopupWindow;

/**
 * The Class EditAttFromProtAction.
 */
@SuppressWarnings("serial")
public class EditAttFromProtAction extends AbstractAction {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		AddAttToFLPopupWindow popup = new AddAttToFLPopupWindow(UI.getInstance().getProtocolFrame(), translate("Edit Attendee"),
				true);
		popup.setVisible(true);

		if (popup.getButtonClicked() == AddAttToFLPopupWindow.ButtonClicked.OK) {

			Protocol prot = UI.getInstance().getProtocolFrame().getCurrentProt();

			int selRow = UI.getInstance().getProtocolFrame().getPresentAttTable().getSelectedRow();
			Attendee oldAtt = Application.getInstance().getProtocolMgmt().getAttendees(prot).get(selRow);
			Attendee localAtt = new Attendee();

			localAtt.setName(popup.getAttName());
			localAtt.setContact(popup.getAttContact());
			localAtt.setRole(popup.getAttRole());
			localAtt.setAspects(oldAtt.getAspects());

			Duration dur = popup.getDuration();
			Application.getInstance().getProtocolMgmt().setAttendeePrepTime(dur, oldAtt, prot);

			Application.getInstance().getAttendeeMgmt().editAttendee(oldAtt, localAtt);

			UI.getInstance().getProtocolFrame().getPatm().setProtocol(prot);
			UI.getInstance().getProtocolFrame().getPatm().fireTableDataChanged();
			UI.getInstance().getProtocolFrame().updateAttButtons();

		}

	}

}
