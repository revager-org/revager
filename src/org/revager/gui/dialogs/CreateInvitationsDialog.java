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

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;

import org.revager.app.Application;
import org.revager.app.AttendeeManagement;
import org.revager.app.ResiFileFilter;
import org.revager.app.model.Data;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.Meeting;
import org.revager.gui.AbstractDialog;
import org.revager.gui.UI;
import org.revager.gui.helpers.ExtendedFlowLayout;
import org.revager.gui.helpers.FileChooser;
import org.revager.gui.helpers.TreeMeeting;
import org.revager.gui.workers.CreateInvitationsWorker;
import org.revager.tools.GUITools;

/**
 * The Class CreateInvitationsDialog.
 */
@SuppressWarnings("serial")
public class CreateInvitationsDialog extends AbstractDialog {
	private GridBagLayout gbl = new GridBagLayout();
	private Container c = getContentPane();
	private List<Attendee> selectedAttendees = new ArrayList<Attendee>();
	private List<JCheckBox> allAttendeesBx = new ArrayList<JCheckBox>();
	private List<JCheckBox> selectedBx = new ArrayList<JCheckBox>();
	private AttendeeManagement attMgmt = Application.getInstance()
			.getAttendeeMgmt();
	private JToggleButton selectAll;
	private JTextField pathTxtFld;

	private JCheckBox productBx;
	private JComboBox meetingsBx;
	private JRadioButton pdfRB;
	private JRadioButton zipRB;
	private JRadioButton dirRB;
	private JScrollPane scrllPn;

	/**
	 * Gets the selected attendees.
	 * 
	 * @return the selected attendees
	 */
	public List<Attendee> getSelectedAttendees() {
		return selectedAttendees;
	}

	/**
	 * Gets the selected meeting.
	 * 
	 * @return the selected meeting
	 */
	public Meeting getSelectedMeeting() {
		return ((TreeMeeting) meetingsBx.getSelectedItem()).getMeeting();
	}

	/**
	 * Checks if is prod selected.
	 * 
	 * @return true, if is prod selected
	 */
	public boolean isProdSelected() {
		return productBx.isSelected();
	}

	/**
	 * Checks if is pdf selected.
	 * 
	 * @return true, if is pdf selected
	 */
	public boolean isPdfSelected() {
		return pdfRB.isSelected();
	}

	/**
	 * Checks if is zip selected.
	 * 
	 * @return true, if is zip selected
	 */
	public boolean isZipSelected() {
		return zipRB.isSelected();
	}

	/**
	 * Checks if is dir selected.
	 * 
	 * @return true, if is dir selected
	 */
	public boolean isDirSelected() {
		return dirRB.isSelected();
	}

	/**
	 * Mark path txt field.
	 */
	public void markPathTxtField() {
		pathTxtFld.setBorder(UI.MARKED_BORDER_INLINE);
	}

	/**
	 * Mark att scroll pane.
	 */
	public void markAttScrollPane() {
		scrllPn.setBorder(UI.MARKED_BORDER);
	}

	/**
	 * Gets the selected path.
	 * 
	 * @return the selected path
	 */
	public String getSelectedPath() {
		return pathTxtFld.getText();
	}

	/**
	 * Unmark all comp.
	 */
	public void unmarkAllComp() {
		pathTxtFld.setBorder(UI.STANDARD_BORDER_INLINE);
		scrllPn.setBorder(UI.STANDARD_BORDER);
	}

	/**
	 * Clear invitations dialog.
	 */
	public void clearInvitationsDialog() {
		setMessage(null);
		c.removeAll();
		selectedAttendees.clear();
		selectedBx.clear();
		allAttendeesBx.clear();
		final JPanel attPanel = new JPanel();
		attPanel.setBackground(Color.WHITE);
		attPanel.setLayout(new ExtendedFlowLayout(FlowLayout.LEFT, 5, 5));

		for (int index = 0; index < attMgmt.getNumberOfAttendees(); index++) {
			final int indexL = index;
			JCheckBox attendee = new JCheckBox();
			attendee.setBackground(Color.WHITE);
			allAttendeesBx.add(attendee);
			String roleString = "role.".concat(attMgmt.getAttendees()
					.get(index).getRole().value());

			String attendeeName = attMgmt.getAttendees().get(index).getName()
					+ ", " + Data.getInstance().getLocaleStr(roleString);

			int endIndex = 23;
			if (attendeeName.length() - 1 < endIndex) {
				attendee.setText(attendeeName);
			} else {
				attendee.setText(attendeeName.substring(0, endIndex) + "...");
			}

			attendee.setToolTipText(attendeeName);
			attendee.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED
							&& !selectedAttendees.contains(attMgmt
									.getAttendees().get(indexL))) {

						selectedAttendees.add(attMgmt.getAttendees()
								.get(indexL));
						selectedBx.add(allAttendeesBx.get(indexL));

					} else {

						selectedAttendees.remove(attMgmt.getAttendees().get(
								indexL));
						selectedBx.remove(allAttendeesBx.get(indexL));

					}
					if (selectedBx.size() == allAttendeesBx.size()) {
						selectAll.setSelected(true);
						selectAll.repaint();
					} else if (selectedBx.size() == 0) {
						selectAll.setSelected(false);
						selectAll.repaint();

					}

				}
			});

			attPanel.add(attendee);
		}

		JLabel meetingLbl = new JLabel(Data.getInstance().getLocaleStr(
				"invitationsDialog.meeting"));

		meetingsBx = new JComboBox();
		for (Meeting meet : Application.getInstance().getMeetingMgmt()
				.getMeetings()) {
			TreeMeeting treeMeet = new TreeMeeting();
			treeMeet.setMeeting(meet);

			meetingsBx.addItem(treeMeet);
		}

		productBx = new JCheckBox();
		productBx.setText(Data.getInstance().getLocaleStr(
				"invitationsDialog.addProduct"));

		JPanel rbPanel = new JPanel(new GridLayout(3, 1));

		ButtonGroup buttonG = new ButtonGroup();
		pdfRB = new JRadioButton(Data.getInstance().getLocaleStr(
				"invitationsDialog.asPDF"), true);
		rbPanel.add(pdfRB);
		buttonG.add(pdfRB);

		zipRB = new JRadioButton(Data.getInstance().getLocaleStr(
				"invitationsDialog.asZIP"));
		rbPanel.add(zipRB);
		buttonG.add(zipRB);

		dirRB = new JRadioButton(Data.getInstance().getLocaleStr(
				"invitationsDialog.asDirectory"));
		rbPanel.add(dirRB);
		buttonG.add(dirRB);

		scrllPn = new JScrollPane(attPanel);
		scrllPn.getViewport().setBackground(Color.WHITE);
		scrllPn
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrllPn.setPreferredSize(new Dimension(230, 300));
		scrllPn.setMinimumSize(new Dimension(230, 300));

		JLabel attendeeLbl = new JLabel(Data.getInstance().getLocaleStr(
				"invitationsDialog.attendees"));

		selectAll = new JToggleButton(Data.getInstance().getLocaleStr(
				"invitationsDialog.selectNothing"));
		selectAll.setFocusable(false);
		selectAll.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {

					for (JCheckBox bx : allAttendeesBx)
						bx.setSelected(true);

					selectAll.setText(Data.getInstance().getLocaleStr(
							"invitationsDialog.selectAll"));
					selectedAttendees.clear();
					selectedBx.clear();

					for (int index = 0; index < attMgmt.getNumberOfAttendees(); index++) {
						selectedAttendees
								.add(attMgmt.getAttendees().get(index));
						selectedBx.add(allAttendeesBx.get(index));
					}
				} else {
					for (JCheckBox bx : allAttendeesBx)
						bx.setSelected(false);
					selectAll.setText(Data.getInstance().getLocaleStr(
							"invitationsDialog.selectNothing"));

					selectedAttendees.clear();
					selectedBx.clear();
				}
			}
		});

		JLabel directory = new JLabel(Data.getInstance().getLocaleStr(
				"invitationsDialog.directory"));
		pathTxtFld = new JTextField();
		JButton browse = GUITools.newImageButton(Data.getInstance().getIcon(
				"buttonBrowse_22x22_0.png"), Data.getInstance().getIcon(
				"buttonBrowse_22x22.png"));
		browse.setToolTipText(Data.getInstance().getLocaleStr(
				"invitationsDialog.browseToolTip"));
		browse.setMargin(new Insets(1, 1, 1, 1));
		browse.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				FileChooser fileChooser = UI.getInstance().getFileChooser();

				fileChooser.setFile(null);

				if (fileChooser.showDialog(UI.getInstance()
						.getCreateInvitationsDialog(),
						FileChooser.MODE_SELECT_DIRECTORY,
						ResiFileFilter.TYPE_DIRECTORY) == FileChooser.SELECTED_APPROVE) {
					String dirPath = fileChooser.getDir().getAbsolutePath();

					pathTxtFld.setText(dirPath);
				}
			}
		});

		GUITools.addComponent(c, gbl, meetingLbl, 0, 0, 1, 1, 0, 0, 20, 5, 0,
				5, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(c, gbl, meetingsBx, 0, 1, 3, 1, 1.0, 0, 5, 5, 0,
				5, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(c, gbl, attendeeLbl, 0, 2, 1, 1, 0, 0, 20, 5, 5,
				5, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(c, gbl, selectAll, 0, 3, 1, 1, 0, 0, 0, 5, 0, 5,
				GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(c, gbl, scrllPn, 0, 4, 1, 2, 0.0, 1.0, 5, 5, 0,
				5, GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(c, gbl, rbPanel, 1, 4, 1, 1, 0, 0, 10, 30, 0, 5,
				GridBagConstraints.VERTICAL, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(c, gbl, productBx, 1, 5, 1, 1, 0, 1.0, 20, 30, 0,
				5, GridBagConstraints.VERTICAL, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(c, gbl, directory, 0, 6, 1, 1, 0, 0, 20, 5, 5, 5,
				GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(c, gbl, pathTxtFld, 0, 7, 2, 1, 1.0, 0, 0, 5, 5,
				5, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(c, gbl, browse, 2, 7, 1, 1, 0, 0, 0, 5, 5, 5,
				GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);

	}

	/**
	 * Instantiates a new creates the invitations dialog.
	 * 
	 * @param parent
	 *            the parent
	 */
	public CreateInvitationsDialog(Frame parent) {
		super(parent);

		setMinimumSize(new Dimension(600, 600));
		setPreferredSize(new Dimension(600, 600));
		getContentPane().setPreferredSize(new Dimension(550, 600));

		pack();

		setTitle(Data.getInstance().getLocaleStr("invitationsDialog.title"));
		setDescription(Data.getInstance().getLocaleStr(
				"invitationsDialog.description"));
		setIcon(Data.getInstance().getIcon("createInvitations_64x64.png"));

		setHelpChapter("invitations_management", "1");
		c.setLayout(gbl);
		clearInvitationsDialog();

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
		confirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GUITools.executeSwingWorker(new CreateInvitationsWorker());
			}
		});

		addButton(confirm);

		setLocationToCenter();
	}

}
