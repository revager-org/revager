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
import org.revager.app.model.Data;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.Protocol;
import org.revager.gui.UI;
import org.revager.gui.protocol.AddAttToProtPopupWindow;


/**
 * The Class AddAttToProtAction.
 */
@SuppressWarnings("serial")
public class AddAttToProtAction extends AbstractAction {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		AddAttToProtPopupWindow popup = new AddAttToProtPopupWindow(UI
				.getInstance().getProtocolFrame(), Data.getInstance()
				.getLocaleStr("addAttendee.title"), false);
		popup.setVisible(true);

		if (popup.getButtonClicked() == AddAttToProtPopupWindow.ButtonClicked.OK) {
			Duration dur = popup.getDuration();

			Attendee localAtt = new Attendee();
			localAtt.setName(popup.getAttName());
			localAtt.setAspects(null);
			if(popup.getAttContact()!=null)
				localAtt.setContact(popup.getAttContact());
			else
				localAtt.setContact("");
			localAtt.setRole(popup.getAttRole());
			Protocol prot = UI.getInstance().getProtocolFrame()
					.getCurrentProt();
			Attendee newAtt = Application.getInstance().getAttendeeMgmt()
					.addAttendee(localAtt);
			Application.getInstance().getProtocolMgmt().addAttendee(newAtt,
					dur, prot);
			UI.getInstance().getProtocolFrame().getPatm().setProtocol(prot);
			UI.getInstance().getProtocolFrame().getPatm()
					.fireTableDataChanged();
			UI.getInstance().getProtocolFrame().updateAttButtons();

		}

	}

}
