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

import static org.revager.app.model.Data._;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.xml.datatype.DatatypeFactory;

import org.revager.app.Application;
import org.revager.app.AttendeeManagement;
import org.revager.app.FindingManagement;
import org.revager.app.MeetingManagement;
import org.revager.app.ProtocolManagement;
import org.revager.app.SeverityManagement;
import org.revager.app.model.Data;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.Finding;
import org.revager.app.model.schema.Meeting;
import org.revager.app.model.schema.Protocol;
import org.revager.gui.MainFrame;
import org.revager.gui.UI;
import org.revager.gui.findings_list.FindingsListFrame;
import org.revager.tools.GUITools;

/**
 * The Class OpenProtocolFrameAction.
 */
@SuppressWarnings("serial")
public class OpenFindingsListAction extends AbstractAction {

	private FindingManagement findingMgmt = Application.getInstance()
			.getFindingMgmt();
	private SeverityManagement sevMgmt = Application.getInstance()
			.getSeverityMgmt();
	private ProtocolManagement protMgmt = Application.getInstance()
			.getProtocolMgmt();
	private MeetingManagement meetMgmt = Application.getInstance()
			.getMeetingMgmt();
	private AttendeeManagement attMgmt = Application.getInstance()
			.getAttendeeMgmt();

	/**
	 * Instantiates a new open protocol frame action.
	 */
	public OpenFindingsListAction() {
		super();

		putValue(SMALL_ICON, Data.getInstance().getIcon("menuProt_16x16.png"));
		putValue(NAME, _("Open/Create Findings List"));
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
	public void actionPerformed(ActionEvent e) {
		GUITools.executeSwingWorker(new OpenProtocolFrameWorker());
	}
	
	public void performActionDirectly() {
		try {
			MainFrame mainFrame = UI.getInstance().getMainFrame();
			FindingsListFrame protFrame = UI.getInstance()
					.getProtocolFrame();

			mainFrame.switchToProgressMode();

			Protocol currentProt = null;
			Meeting currentMeet = null;

			if (mainFrame.getSelectedProtocol() != null) {
				currentProt = mainFrame.getSelectedProtocol();
				currentMeet = protMgmt.getMeeting(currentProt);
			} else if (mainFrame.getSelectedMeeting() != null) {
				currentProt = mainFrame.getSelectedMeeting().getProtocol();
				currentMeet = mainFrame.getSelectedMeeting();
			} else {
				/*
				 * Create a new meeting
				 */
				Calendar currentTime = new GregorianCalendar();

				int year = currentTime.get(Calendar.YEAR);
				int month = currentTime.get(Calendar.MONTH);
				int dayOfMonth = currentTime.get(Calendar.DAY_OF_MONTH);
				int hourOfDay = currentTime.get(Calendar.HOUR_OF_DAY) + 2;
				int minute = currentTime.get(Calendar.MINUTE);
				int second = currentTime.get(Calendar.SECOND);

				currentMeet = meetMgmt.addMeeting(currentTime, currentTime,
						new GregorianCalendar(year, month, dayOfMonth,
								hourOfDay, minute, second), "");
			}

			if (currentProt == null) {
				currentProt = new Protocol();
				currentProt.setDate(currentMeet.getPlannedDate());
				currentProt.setLocation(currentMeet.getPlannedLocation());
				currentProt.setStart(currentMeet.getPlannedStart());
				currentProt.setEnd(currentMeet.getPlannedEnd());
				currentProt.setComments("");

				Finding newFind = new Finding();
				newFind.setSeverity(sevMgmt.getSeverities().get(0));

				findingMgmt.addFinding(newFind, currentProt);

				/*
				 * If there's exactly one attendee it is very likely that an
				 * instant review has been started.
				 */
				List<Attendee> attendees = attMgmt.getAttendees();

				if (attendees.size() == 1) {
					protMgmt.addAttendee(attendees.get(0), DatatypeFactory
							.newInstance().newDuration(0), currentProt);
				}
			}

			protMgmt.setProtocol(currentProt, currentMeet);

			protFrame.resetClock();
			protFrame.setMeeting(currentMeet);
			protFrame.setVisible(true);

			mainFrame.updateMeetingsTree();
			mainFrame.switchToEditMode();
		} catch (Exception e) {
			UI.getInstance().getMainFrame().switchToEditMode();
			
			e.printStackTrace();
		}
	}

	private class OpenProtocolFrameWorker extends SwingWorker<Void, Void> {
		@Override
		protected Void doInBackground() throws Exception {
			performActionDirectly();
			
			return null;
		}
	}

}
