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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.revager.app.Application;
import org.revager.app.model.Data;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.Protocol;
import org.revager.app.model.schema.Role;
import org.revager.gui.UI;
import org.revager.gui.actions.popup.AddAttToProtPopupWindowAction;
import org.revager.tools.GUITools;

/**
 * The Class AddAttToProtPopupWindow.
 */
@SuppressWarnings("serial")
public class AddAttToFLPopupWindow extends JDialog {

	private GridBagLayout gbl = new GridBagLayout();
	private JPanel inputPanel = new JPanel(gbl);
	private JComboBox roleBx;
	private JTextField nameTxtFld;
	private JTextArea contactTxtArea;
	private Role[] roles = Role.values();
	private Attendee selAtt;
	private JSpinner durMSpinner;
	private JSpinner durHSpinner;
	private JButton buttonConfirm;
	private JScrollPane scrllPn;

	/**
	 * Gets the name txt fld.
	 * 
	 * @return the name txt fld
	 */
	public JTextField getNameTxtFld() {
		return nameTxtFld;
	}

	/**
	 * Gets the scrll pn.
	 * 
	 * @return the scrll pn
	 */
	public JScrollPane getScrllPn() {
		return scrllPn;
	}

	/**
	 * Gets the sel att.
	 * 
	 * @return the sel att
	 */
	public Attendee getSelAtt() {
		return selAtt;
	}

	/**
	 * Gets the att name.
	 * 
	 * @return the att name
	 */
	public String getAttName() {
		return nameTxtFld.getText();
	}

	/**
	 * Gets the att contact.
	 * 
	 * @return the att contact
	 */
	public String getAttContact() {
		return contactTxtArea.getText();
	}

	/**
	 * Gets the att role.
	 * 
	 * @return the att role
	 */
	public Role getAttRole() {
		return roles[roleBx.getSelectedIndex()];
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
	 * Instantiates a new adds the att to prot popup window.
	 * 
	 * @param parent
	 *            the parent
	 * @param titleText
	 *            the title text
	 * @param editing
	 *            the editing
	 */
	public AddAttToFLPopupWindow(Window parent, String titleText,
			Boolean editing) {
		super(parent);

		toFront();

		setLayout(new BorderLayout());

		setUndecorated(true);

		setModal(true);

		JPanel panelBase = GUITools.newPopupBasePanel();

		JTextArea textTitle = GUITools.newPopupTitleArea(titleText);

		panelBase.add(textTitle, BorderLayout.NORTH);

		JLabel durLbl = new JLabel(_("Preparation time:"));
		JLabel roleLbl = new JLabel(_("Role:"));
		JLabel contactLbl = new JLabel(_("Contact information:"));
		JLabel nameLbl = new JLabel(_("Name:"));

		contactTxtArea = new JTextArea();
		nameTxtFld = new JTextField();

		durHSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
		durMSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));

		GUITools.formatSpinner(durHSpinner);
		GUITools.formatSpinner(durMSpinner);

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
				0.0, 5, 0, 0, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST);
		GUITools.addComponent(spinnerPanel, gbl, hoursLbl, 2, 0, 1, 1, 1.0, 0,
				5, 5, 0, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST);
		GUITools.addComponent(spinnerPanel, gbl, durMSpinner, 3, 0, 1, 1, 0.0,
				0.0, 5, 5, 0, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST);
		GUITools.addComponent(spinnerPanel, gbl, minLbl, 4, 0, 1, 1, 1.0, 0.0,
				5, 5, 0, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST);

		roleBx = new JComboBox();
		for (Role x : Role.values()) {
			String roleString = x.toString();
			roleBx.addItem(_(roleString));
		}

		scrllPn = GUITools.setIntoScrllPn(contactTxtArea);

		inputPanel.setBackground(Color.WHITE);

		GUITools.addComponent(inputPanel, gbl, nameLbl, 0, 0, 1, 1, 0, 0, 5,
				10, 0, 10, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(inputPanel, gbl, nameTxtFld, 0, 1, 1, 1, 1.0, 0,
				5, 10, 20, 10, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(inputPanel, gbl, roleLbl, 0, 2, 1, 1, 0, 0, 5,
				10, 0, 10, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(inputPanel, gbl, roleBx, 0, 3, 1, 1, 1.0, 0, 5,
				10, 20, 10, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(inputPanel, gbl, contactLbl, 0, 6, 1, 1, 1.0, 0,
				5, 10, 0, 10, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(inputPanel, gbl, scrllPn, 0, 7, 1, 2, 1.0, 1.0,
				5, 10, 20, 10, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);

		panelBase.add(inputPanel, BorderLayout.CENTER);

		/*
		 * adding duration to popup
		 */
		GUITools.addComponent(inputPanel, gbl, durLbl, 0, 4, 1, 1, 1.0, 0, 5,
				10, 0, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST);
		GUITools.addComponent(inputPanel, gbl, spinnerPanel, 0, 5, 1, 1, 1.0,
				0, 5, 10, 20, 10, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);

		Dimension popupSize = new Dimension(260, 400);

		if (editing) {
			Protocol prot = UI.getInstance().getProtocolFrame()
					.getCurrentProt();
			int selRow = UI.getInstance().getProtocolFrame()
					.getPresentAttTable().getSelectedRow();
			selAtt = Application.getInstance().getProtocolMgmt()
					.getAttendees(prot).get(selRow);

			nameTxtFld.setText(selAtt.getName());
			roleBx.setSelectedItem(_(selAtt.getRole().toString()));
			contactTxtArea.setText(selAtt.getContact());
			durHSpinner.setValue(Application.getInstance().getProtocolMgmt()
					.getAttendeePrepTime(selAtt, prot).getHours());
			durMSpinner.setValue(Application.getInstance().getProtocolMgmt()
					.getAttendeePrepTime(selAtt, prot).getMinutes());

		}

		/*
		 * The buttons to abort and confirm the input
		 */
		JButton buttonAbort = GUITools.newImageButton();
		buttonAbort.setIcon(Data.getInstance().getIcon(
				"buttonCancel_24x24_0.png"));
		buttonAbort.setRolloverIcon(Data.getInstance().getIcon(
				"buttonCancel_24x24.png"));
		buttonAbort.setToolTipText(_("Abort"));
		buttonAbort.addActionListener(new AddAttToProtPopupWindowAction(this,
				ButtonClicked.ABORT));

		buttonConfirm = GUITools.newImageButton();
		buttonConfirm.setIcon(Data.getInstance()
				.getIcon("buttonOk_24x24_0.png"));
		buttonConfirm.setRolloverIcon(Data.getInstance().getIcon(
				"buttonOk_24x24.png"));
		buttonConfirm.setToolTipText(_("Confirm"));
		buttonConfirm.addActionListener(new AddAttToProtPopupWindowAction(this,
				ButtonClicked.OK));

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

		pack();

		/*
		 * Set size and location
		 */
		setMinimumSize(popupSize);
		setSize(popupSize);
		setPreferredSize(popupSize);

		setAlwaysOnTop(true);
		toFront();

		GUITools.setLocationToCursorPos(this);
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
