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
package org.revager.gui.findings_list;

import static org.revager.app.model.Data._;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.revager.app.Application;
import org.revager.app.model.Data;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.Protocol;
import org.revager.gui.UI;
import org.revager.gui.actions.popup.AddResiAttToProtPopupWindowAction;
import org.revager.tools.GUITools;

/**
 * The Class AddResiAttToProtPopupWindow.
 */
@SuppressWarnings("serial")
public class AddResiAttToFLPopupWindow extends JDialog {

	private JComboBox attendeeBx;
	private GridBagLayout gbl = new GridBagLayout();
	private JPanel inputPanel = new JPanel(gbl);
	private JSpinner durMSpinner;
	private JSpinner durHSpinner;

	private Protocol protocol = null;
	private List<String> attendeeIds = new ArrayList<String>();

	/**
	 * Gets the attendee bx.
	 * 
	 * @return the attendee bx
	 */
	public JComboBox getAttendeeBx() {
		return attendeeBx;
	}

	/**
	 * Gets the duration.
	 * 
	 * @return the duration
	 */
	public Duration getDuration() {
		int hours = Integer.parseInt(durHSpinner.getValue().toString());
		int mins = Integer.parseInt(durMSpinner.getValue().toString());
		try {
			return DatatypeFactory.newInstance().newDurationDayTime(true, 0,
					hours, mins, 0);
		} catch (DatatypeConfigurationException e) {
			return null;
		}
	}

	/**
	 * The Enum ButtonClicked.
	 */
	public static enum ButtonClicked {
		OK, ABORT;
	};

	private ButtonClicked buttonClicked = null;

	/**
	 * Instantiates a new adds the resi att to prot popup window.
	 * 
	 * @param parent
	 *            the parent
	 * @param titleText
	 *            the title text
	 */
	public AddResiAttToFLPopupWindow(Window parent) {
		super(parent);

		if (parent instanceof FindingsListFrame) {
			this.protocol = ((FindingsListFrame) parent).getCurrentProt();
		}

		setLayout(new BorderLayout());

		// setUndecorated(true);
		setResizable(false);
		setTitle(_("RevAger"));

		setModal(true);

		JPanel panelBase = GUITools.newPopupBasePanel();

		JTextArea textTitle = GUITools
				.newPopupTitleArea(_("Please select the attendee you would like to add to the current meeting:"));

		attendeeBx = new JComboBox();
		createAttendeeBx();

		panelBase.add(textTitle, BorderLayout.NORTH);

		JLabel durLbl = new JLabel(_("Preparation time:"));

		durHSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
		durMSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));

		/*
		 * Hide border if the application runs on Mac OS X
		 */
		boolean hideBorder = UI.getInstance().getPlatform() == UI.Platform.MAC;

		GUITools.formatSpinner(durHSpinner, hideBorder);
		GUITools.formatSpinner(durMSpinner, hideBorder);

		if ((Integer) durHSpinner.getValue() == 0) {
			((NumberEditor) durHSpinner.getEditor()).getTextField().setText(
					"00");
		}

		if ((Integer) durMSpinner.getValue() == 0) {
			((NumberEditor) durMSpinner.getEditor()).getTextField().setText(
					"00");
		}

		JPanel spinnerPanel = new JPanel(gbl);
		spinnerPanel.setBackground(null);

		JLabel hoursLbl = new JLabel(_("Hour(s)"));
		JLabel minLbl = new JLabel(_("Minute(s)"));

		GUITools.addComponent(spinnerPanel, gbl, durHSpinner, 1, 0, 1, 1, 0.0,
				0, 5, 0, 0, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST);
		GUITools.addComponent(spinnerPanel, gbl, hoursLbl, 2, 0, 1, 1, 1.0, 0,
				5, 5, 0, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST);
		GUITools.addComponent(spinnerPanel, gbl, durMSpinner, 3, 0, 1, 1, 0.0,
				0, 5, 5, 0, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST);
		GUITools.addComponent(spinnerPanel, gbl, minLbl, 4, 0, 1, 1, 1.0, 0, 5,
				5, 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);

		inputPanel.setBackground(Color.WHITE);

		GUITools.addComponent(inputPanel, gbl, attendeeBx, 0, 0, 1, 1, 1.0, 0,
				5, 10, 20, 10, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(inputPanel, gbl, durLbl, 0, 1, 1, 1, 1.0, 0, 5,
				10, 0, 10, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(inputPanel, gbl, spinnerPanel, 0, 2, 1, 1, 1.0,
				0, 5, 10, 20, 10, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);

		panelBase.add(inputPanel, BorderLayout.CENTER);

		Dimension popupSize;

		popupSize = new Dimension(260, 230);

		/*
		 * The buttons to abort and confirm the input
		 */
		JButton buttonAbort = GUITools.newImageButton();
		buttonAbort.setIcon(Data.getInstance().getIcon(
				"buttonCancel_24x24_0.png"));
		buttonAbort.setRolloverIcon(Data.getInstance().getIcon(
				"buttonCancel_24x24.png"));
		buttonAbort.setToolTipText(_("Abort"));
		buttonAbort.addActionListener(new AddResiAttToProtPopupWindowAction(
				this, ButtonClicked.ABORT));

		JButton buttonConfirm = GUITools.newImageButton();
		buttonConfirm.setIcon(Data.getInstance()
				.getIcon("buttonOk_24x24_0.png"));
		buttonConfirm.setRolloverIcon(Data.getInstance().getIcon(
				"buttonOk_24x24.png"));
		buttonConfirm.setToolTipText(_("Confirm"));
		buttonConfirm.addActionListener(new AddResiAttToProtPopupWindowAction(
				this, ButtonClicked.OK));

		JPanel panelButtons = new JPanel(new BorderLayout());
		panelButtons.setBackground(UI.POPUP_BACKGROUND);
		panelButtons.setBorder(BorderFactory.createLineBorder(
				panelButtons.getBackground(), 3));
		panelButtons.add(buttonAbort, BorderLayout.WEST);
		panelButtons.add(buttonConfirm, BorderLayout.EAST);

		/*
		 * Base panel
		 */
		panelBase.add(panelButtons, BorderLayout.SOUTH);

		add(panelBase, BorderLayout.CENTER);

		/*
		 * Set size and location
		 */
		setMinimumSize(popupSize);
		setSize(popupSize);
		setPreferredSize(popupSize);

		pack();

		setAlwaysOnTop(true);
		toFront();

		GUITools.setLocationToCursorPos(this);
	}

	/**
	 * Creates the attendee bx.
	 */
	private void createAttendeeBx() {
		for (Attendee att : Application.getInstance().getAttendeeMgmt()
				.getAttendees()) {
			if (!Application.getInstance().getProtocolMgmt()
					.isAttendee(att, protocol)) {
				String role = _(att.getRole().toString());

				attendeeBx.addItem(att.getName() + " (" + role + ")");

				attendeeIds.add(att.getId());
			}
		}
	}

	/**
	 * @return the attendeeIds
	 */
	public List<String> getAttendeeIds() {
		return attendeeIds;
	}

	/**
	 * Gets the button clicked.
	 * 
	 * @return the buttonClicked
	 */
	public ButtonClicked getButtonClicked() {
		return buttonClicked;
	}

	/**
	 * Sets the button clicked.
	 * 
	 * @param buttonClicked
	 *            the buttonClicked to set
	 */
	public void setButtonClicked(ButtonClicked buttonClicked) {
		this.buttonClicked = buttonClicked;
	}

	public void commitSpinnerValues() {
		try {
			((NumberEditor) durHSpinner.getEditor()).getTextField()
					.commitEdit();
		} catch (ParseException e) {
			durHSpinner.setValue(0);
		}

		try {
			((NumberEditor) durMSpinner.getEditor()).getTextField()
					.commitEdit();
		} catch (ParseException e) {
			durMSpinner.setValue(0);
		}
	}

}
