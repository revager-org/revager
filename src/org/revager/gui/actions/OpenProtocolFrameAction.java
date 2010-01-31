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
package org.revager.gui.actions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.revager.app.Application;
import org.revager.app.FindingManagement;
import org.revager.app.model.Data;
import org.revager.app.model.schema.Finding;
import org.revager.app.model.schema.Meeting;
import org.revager.app.model.schema.Protocol;
import org.revager.gui.UI;
import org.revager.gui.helpers.TreeMeeting;
import org.revager.gui.helpers.TreeProtocol;


/**
 * The Class OpenProtocolFrameAction.
 */
@SuppressWarnings("serial")
public class OpenProtocolFrameAction extends AbstractAction {

	private FindingManagement findingMgmt = Application.getInstance()
			.getFindingMgmt();

	/**
	 * Instantiates a new open protocol frame action.
	 */
	public OpenProtocolFrameAction() {
		super();

		putValue(SMALL_ICON, Data.getInstance().getIcon("menuProt_16x16.png"));
		putValue(NAME, Data.getInstance().getLocaleStr("menu.protocolMode"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Meeting editMeet;
		Protocol currentProt;

		TreePath path = UI.getInstance().getMainFrame().getMeetingsTree()
				.getSelectionPath();

		if (path.getPathCount() == 3)
			editMeet = ((TreeProtocol) ((DefaultMutableTreeNode) path
					.getLastPathComponent()).getUserObject()).getMeeting();
		else
			editMeet = ((TreeMeeting) ((DefaultMutableTreeNode) path
					.getLastPathComponent()).getUserObject()).getMeeting();

		currentProt = Application.getInstance().getProtocolMgmt().getProtocol(
				editMeet);
		if (currentProt == null) {
			currentProt = new Protocol();
			currentProt.setDate(editMeet.getPlannedDate());
			currentProt.setLocation(editMeet.getPlannedLocation());
			currentProt.setStart(editMeet.getPlannedStart());
			currentProt.setEnd(editMeet.getPlannedEnd());
			currentProt.setComments("");
			findingMgmt.addFinding(new Finding(), currentProt);
		}
		Application.getInstance().getProtocolMgmt().setProtocol(currentProt,
				editMeet);

		UI.getInstance().getProtocolFrame().resetClock();
		UI.getInstance().getProtocolFrame().setMeeting(editMeet);
		UI.getInstance().getProtocolFrame().setVisible(true);
		UI.getInstance().getMainFrame().updateMeetingsTree();

	}

}
