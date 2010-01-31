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
package org.revager.gui.dialogs;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.revager.app.Application;
import org.revager.app.MeetingManagement;
import org.revager.app.model.Data;
import org.revager.app.model.schema.Meeting;
import org.revager.gui.AbstractDialog;
import org.revager.gui.UI;
import org.revager.gui.actions.ActionRegistry;
import org.revager.gui.actions.DatePickerAction;
import org.revager.gui.actions.meeting.ConfirmMeetingAction;
import org.revager.gui.helpers.ObservingTextField;
import org.revager.gui.helpers.TreeMeeting;
import org.revager.gui.models.RotateSpinnerNumberModel;
import org.revager.tools.GUITools;


/**
 * The Class MeetingDialog.
 */
@SuppressWarnings("serial")
public class MeetingDialog extends AbstractDialog {

	private MeetingManagement meetingMgmt = Application.getInstance()
			.getMeetingMgmt();

	private Meeting currentMeeting = null;

	private ObservingTextField dateTxtFld;

	private JSpinner beginMSpinner = new JSpinner(new RotateSpinnerNumberModel(
			00, 00, 45, 15));
	private JSpinner beginHSpinner = new JSpinner(new RotateSpinnerNumberModel(
			00, 00, 23, 1));
	private JSpinner endMSpinner = new JSpinner(new RotateSpinnerNumberModel(
			00, 00, 45, 15));
	private JSpinner endHSpinner = new JSpinner(new RotateSpinnerNumberModel(
			00, 00, 23, 1));

	private JTextField locationTxtFld;
	private JCheckBox canceled;
	private JTextArea canceledTxtArea;

	/**
	 * Gets the location txt fld.
	 * 
	 * @return the location txt fld
	 */
	public JTextField getLocationTxtFld() {
		return locationTxtFld;
	}

	/**
	 * Sets the location txt fld.
	 * 
	 * @param locationTxtFld
	 *            the new location txt fld
	 */
	public void setLocationTxtFld(JTextField locationTxtFld) {
		this.locationTxtFld = locationTxtFld;
	}

	/**
	 * Gets the begin.
	 * 
	 * @return the begin
	 */
	public Calendar getBegin() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, Integer.parseInt(beginMSpinner.getValue()
				.toString()));
		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(beginHSpinner.getValue()
				.toString()));
		return cal;
	}

	/**
	 * Gets the end.
	 * 
	 * @return the end
	 */
	public Calendar getEnd() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, Integer.parseInt(endMSpinner.getValue()
				.toString()));
		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endHSpinner.getValue()
				.toString()));
		return cal;
	}

	/**
	 * Gets the h begin.
	 * 
	 * @return the h begin
	 */
	public int getHBegin() {
		return Integer.parseInt(beginMSpinner.getValue().toString());
	}

	/**
	 * Gets the m end.
	 * 
	 * @return the m end
	 */
	public int getMEnd() {
		return Integer.parseInt(beginMSpinner.getValue().toString());
	}

	/**
	 * Gets the h end.
	 * 
	 * @return the h end
	 */
	public int getHEnd() {
		return Integer.parseInt(beginMSpinner.getValue().toString());
	}

	/**
	 * Gets the canceled.
	 * 
	 * @return the canceled
	 */
	public String getCanceled() {
		if (canceled.isSelected()) {
			return canceledTxtArea.getText();
		} else {
			return "";
		}
	}

	/**
	 * Sets the canceled.
	 * 
	 * @param canceled
	 *            the new canceled
	 */
	public void setCanceled(String canceled) {
		canceledTxtArea.setText(canceled);
	}

	/**
	 * Gets the location txt.
	 * 
	 * @return the location txt
	 */
	public String getLocationTxt() {
		return locationTxtFld.getText();
	}

	/**
	 * Sets the location txt.
	 * 
	 * @param locationTxt
	 *            the new location txt
	 */
	public void setLocationTxt(String locationTxt) {
		this.locationTxtFld.setText(locationTxt);
	}

	/**
	 * Gets the date txt fld.
	 * 
	 * @return the date txt fld
	 */
	public ObservingTextField getDateTxtFld() {
		return dateTxtFld;
	}

	/**
	 * Checks if is new meeting.
	 * 
	 * @return true, if is new meeting
	 */
	public boolean isNewMeeting() {
		if (currentMeeting == null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets the current meeting.
	 * 
	 * @return the current meeting
	 */
	public Meeting getCurrentMeeting() {
		return this.currentMeeting;
	}

	/**
	 * Sets the current meeting.
	 * 
	 * @param meet
	 *            the new current meeting
	 */
	public void setCurrentMeeting(Meeting meet) {
		this.currentMeeting = meet;

		locationTxtFld.setBorder(UI.STANDARD_BORDER_INLINE);

		if (currentMeeting == null) {
			setTitle(Data.getInstance().getLocaleStr("addMeeting.title"));
			setDescription(Data.getInstance().getLocaleStr(
					"addMeeting.description"));
			setIcon(Data.getInstance().getIcon("addMeeting_50x50.png"));
			setHelpChapter("meetings_management", "1");

			DateFormat dateF = SimpleDateFormat
					.getDateInstance(DateFormat.LONG);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_WEEK, 7);
			Date startDate = cal.getTime();
			cal.add(Calendar.HOUR, 0);

			dateTxtFld.setText(dateF.format(startDate));
			beginMSpinner.setValue(0);
			beginHSpinner.setValue(cal.get(Calendar.HOUR_OF_DAY));
			endMSpinner.setValue(0);
			cal.add(Calendar.HOUR, 2);
			endHSpinner.setValue(cal.get(Calendar.HOUR_OF_DAY));
			locationTxtFld.setText("");
			canceled.setEnabled(false);
			canceled.setSelected(false);
			canceledTxtArea.setText(Data.getInstance().getLocaleStr(
					"editMeeting.cause"));

			canceled.setEnabled(false);
			canceled.setSelected(false);
			canceledTxtArea.setText(Data.getInstance().getLocaleStr(
					"editMeeting.cause"));
		} else {
			setTitle(Data.getInstance().getLocaleStr("editMeeting.title"));
			setDescription(Data.getInstance().getLocaleStr(
					"editMeeting.description"));
			setIcon(Data.getInstance().getIcon("editMeeting_50x50.png"));
			setHelpChapter("meetings_management", "2");
			canceled.setEnabled(true);

			Meeting editMeet = null;
			TreePath path = UI.getInstance().getMainFrame().getMeetingsTree()
					.getSelectionPath();
			Object obj = ((DefaultMutableTreeNode) path.getLastPathComponent())
					.getUserObject();
			editMeet = ((TreeMeeting) obj).getMeeting();
			DateFormat dateF = SimpleDateFormat
					.getDateInstance(DateFormat.LONG);

			dateTxtFld.setText(dateF
					.format(editMeet.getPlannedDate().getTime()));
			beginMSpinner.setValue(editMeet.getPlannedStart().get(
					Calendar.MINUTE));
			beginHSpinner.setValue(editMeet.getPlannedStart().get(
					Calendar.HOUR_OF_DAY));
			endMSpinner.setValue(editMeet.getPlannedEnd().get(Calendar.MINUTE));
			endHSpinner.setValue(editMeet.getPlannedEnd().get(
					Calendar.HOUR_OF_DAY));
			locationTxtFld.setText(editMeet.getPlannedLocation());

			canceled.setEnabled(true);
			canceled.setSelected(meetingMgmt.isMeetingCanceled(editMeet));
			canceledTxtArea.setText(editMeet.getCanceled());
		}

		/*
		 * Correct the leading zero's
		 */
		if ((Integer) beginMSpinner.getValue() == 0) {
			((NumberEditor) beginMSpinner.getEditor()).getTextField().setText(
					"00");
		}

		if ((Integer) beginHSpinner.getValue() == 0) {
			((NumberEditor) beginHSpinner.getEditor()).getTextField().setText(
					"00");
		}

		if ((Integer) endMSpinner.getValue() == 0) {
			((NumberEditor) endMSpinner.getEditor()).getTextField().setText(
					"00");
		}

		if ((Integer) endHSpinner.getValue() == 0) {
			((NumberEditor) endHSpinner.getEditor()).getTextField().setText(
					"00");
		}
	}

	/*
	 * 
	 * constructor for meetingDialog
	 */
	/**
	 * Instantiates a new meeting dialog.
	 * 
	 * @param parent
	 *            the parent
	 */
	public MeetingDialog(Frame parent) {
		super(parent);

		setTitle(Data.getInstance().getLocaleStr("editMeeting.title"));
		setDescription(Data.getInstance().getLocaleStr(
				"editMeeting.description"));
		setIcon(Data.getInstance().getIcon("editMeeting_64x64.png"));

		GridBagLayout gbl = new GridBagLayout();
		Container c = getContentPane();
		c.setLayout(gbl);

		/*
		 * 
		 * creating spinners
		 */
		GUITools.formatSpinner(endHSpinner);
		GUITools.formatSpinner(endMSpinner);
		GUITools.formatSpinner(beginHSpinner);
		GUITools.formatSpinner(beginMSpinner);

		JLabel beginLbl = new JLabel(Data.getInstance().getLocaleStr(
				"editMeeting.period"));

		JPanel spinnerPanel = new JPanel(gbl);
		JLabel tillLabel = new JLabel(Data.getInstance().getLocaleStr(
				"editMeeting.period.till"));
		JLabel clockLabel = new JLabel(Data.getInstance().getLocaleStr(
				"editMeeting.period.clock"));

		GUITools.addComponent(spinnerPanel, gbl, beginHSpinner, 0, 0, 1, 1, 0,
				0, 0, 0, 0, 0, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(spinnerPanel, gbl, new JLabel(":"), 1, 0, 1, 1,
				0, 0, 0, 5, 0, 0, GridBagConstraints.NONE,
				GridBagConstraints.CENTER);
		GUITools.addComponent(spinnerPanel, gbl, beginMSpinner, 2, 0, 1, 1, 0,
				0, 0, 5, 0, 0, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(spinnerPanel, gbl, tillLabel, 3, 0, 1, 1, 1.0, 0,
				0, 20, 0, 20, GridBagConstraints.NONE,
				GridBagConstraints.CENTER);
		GUITools.addComponent(spinnerPanel, gbl, endHSpinner, 4, 0, 1, 1, 0, 0,
				0, 0, 0, 0, GridBagConstraints.NONE,
				GridBagConstraints.NORTHEAST);
		GUITools.addComponent(spinnerPanel, gbl, new JLabel(":"), 5, 0, 1, 1,
				0, 0, 0, 5, 0, 0, GridBagConstraints.NONE,
				GridBagConstraints.CENTER);
		GUITools.addComponent(spinnerPanel, gbl, endMSpinner, 6, 0, 1, 1, 0, 0,
				0, 5, 0, 0, GridBagConstraints.NONE,
				GridBagConstraints.NORTHEAST);
		GUITools.addComponent(spinnerPanel, gbl, clockLabel, 7, 0, 1, 1, 1.0,
				0, 0, 20, 0, 0, GridBagConstraints.NONE,
				GridBagConstraints.CENTER);

		/*
		 * 
		 * creating elements for location, and canceled infos
		 */
		JLabel locationLbl = new JLabel(Data.getInstance().getLocaleStr(
				"editMeeting.location"));
		locationTxtFld = new JTextField();

		canceledTxtArea = new JTextArea(Data.getInstance().getLocaleStr(
				"editMeeting.cause"));
		canceledTxtArea.setEnabled(false);

		JScrollPane canceledScrllPn = GUITools.setIntoScrllPn(canceledTxtArea);
		canceled = new JCheckBox(Data.getInstance().getLocaleStr(
				"editMeeting.canceled"));
		canceled.setHorizontalTextPosition(SwingConstants.LEFT);
		canceled.setMargin(new Insets(0, 0, 0, 0));
		canceled.setBorder(new EmptyBorder(0, 0, 0, 0));
		canceled.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		canceled.setFocusable(false);
		canceled.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					canceledTxtArea.setEnabled(true);
				} else {
					canceledTxtArea.setEnabled(false);
				}
			}
		});

		/*
		 * 
		 * creating elements for the date of the meeting
		 */
		JLabel dateLbl = new JLabel(Data.getInstance().getLocaleStr(
				"editMeeting.date"));
		dateTxtFld = new ObservingTextField();
		dateTxtFld.setFocusable(false);
		dateTxtFld.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		dateTxtFld.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ActionRegistry.getInstance().get(
						DatePickerAction.class.getName()).actionPerformed(null);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
		});

		JButton datePicker = GUITools.newImageButton();
		datePicker.setToolTipText(Data.getInstance().getLocaleStr(
				"datePicker.tooltip"));
		datePicker
				.setIcon(Data.getInstance().getIcon("datePicker_25x25_0.png"));
		datePicker.setRolloverIcon(Data.getInstance().getIcon(
				"datePicker_25x25.png"));
		datePicker.addActionListener(ActionRegistry.getInstance().get(
				DatePickerAction.class.getName()));

		/*
		 * 
		 * adding all created elements to the dialog
		 */
		GUITools.addComponent(c, gbl, dateLbl, 0, 0, 1, 1, 0, 0, 10, 5, 0, 5,
				GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(c, gbl, dateTxtFld, 1, 0, 1, 1, 1.0, 0, 10, 5, 0,
				5, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(c, gbl, datePicker, 2, 0, 1, 1, 0, 0, 10, 5, 0,
				5, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(c, gbl, beginLbl, 0, 1, 1, 1, 0, 0, 10, 5, 0, 5,
				GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(c, gbl, spinnerPanel, 1, 1, 2, 1, 1.0, 0, 10, 5,
				0, 5, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(c, gbl, locationLbl, 0, 2, 1, 1, 0, 0, 15, 5, 0,
				5, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(c, gbl, locationTxtFld, 1, 2, 2, 2, 1.0, 0, 15,
				5, 0, 5, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(c, gbl, canceled, 0, 3, 1, 1, 0, 0, 25, 5, 0, 5,
				GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(c, gbl, canceledScrllPn, 1, 3, 2, 2, 1.0, 1.0,
				25, 5, 0, 5, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);

		/*
		 * buttons for accepting and canceling
		 */
		JButton cancel = new JButton(Data.getInstance().getLocaleStr("abort"),
				Data.getInstance().getIcon("buttonCancel_16x16.png"));
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		addButton(cancel);

		JButton confirm = new JButton(Data.getInstance()
				.getLocaleStr("confirm"), Data.getInstance().getIcon(
				"buttonOk_16x16.png"));
		confirm.addActionListener(ActionRegistry.getInstance().get(
				ConfirmMeetingAction.class.getName()));
		addButton(confirm);

		setLocationToCenter();

		pack();
	}

}
