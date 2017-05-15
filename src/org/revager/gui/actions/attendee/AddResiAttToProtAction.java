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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.xml.datatype.Duration;

import org.revager.app.Application;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.Protocol;
import org.revager.gui.UI;
import org.revager.gui.findings_list.AddResiAttToFLPopupWindow;

/**
 * The Class AddResiAttToProtAction.
 */
@SuppressWarnings("serial")
public class AddResiAttToProtAction extends AbstractAction {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		AddResiAttToFLPopupWindow popup = new AddResiAttToFLPopupWindow(UI.getInstance().getProtocolFrame());
		popup.setVisible(true);

		if (popup.getButtonClicked() == AddResiAttToFLPopupWindow.ButtonClicked.OK) {
			Attendee localAtt;
			Duration prep = popup.getDuration();
			Protocol prot = UI.getInstance().getProtocolFrame().getCurrentProt();

			String attId = popup.getAttendeeIds().get(popup.getAttendeeBx().getSelectedIndex());

			localAtt = Application.getInstance().getAttendeeMgmt().getAttendee(Integer.parseInt(attId));

			Application.getInstance().getProtocolMgmt().addAttendee(localAtt, prep, prot);
			UI.getInstance().getProtocolFrame().getPatm().setProtocol(prot);
			UI.getInstance().getProtocolFrame().getPatm().fireTableDataChanged();
			UI.getInstance().getProtocolFrame().updateAttButtons();

		}
	}

}
