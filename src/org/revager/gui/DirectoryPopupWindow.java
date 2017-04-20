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
package org.revager.gui;

import static org.revager.app.model.Data._;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.appdata.AppAttendee;
import org.revager.gui.actions.popup.DirectoryPopupWindowAction;
import org.revager.tools.GUITools;

/**
 * This class represents the popup window to choose an attendee from the
 * internal directory.
 */
@SuppressWarnings("serial")
public class DirectoryPopupWindow extends JDialog {

	private JComboBox attendeeBx;

	private JTextField filterTxtFld;

	private GridBagLayout gbl = new GridBagLayout();

	private List<AppAttendee> filteredAtt = new ArrayList<AppAttendee>();

	private JPanel inputPanel = new JPanel(gbl);

	private JLabel filterLbl;

	private JLabel attFilterLbl;

	private JButton deleteBttn;

	public JComboBox getAttendeeBx() {
		return attendeeBx;
	}

	public static enum ButtonClicked {
		OK, ABORT;
	};

	private ButtonClicked buttonClicked = null;

	/**
	 * Instantiates a new directory popup window.
	 * 
	 * @param parent
	 *            the parent
	 * @param titleText
	 *            the title text
	 */
	public DirectoryPopupWindow(Window parent, String titleText) {
		super(parent);

		setLayout(new BorderLayout());

		// setUndecorated(true);
		setResizable(false);
		setTitle(_("RevAger"));

		setModal(true);

		JPanel panelBase = GUITools.newPopupBasePanel();

		JTextArea textTitle = GUITools.newPopupTitleArea(titleText);

		attendeeBx = new JComboBox();
		createAttendeeBx();

		panelBase.add(textTitle, BorderLayout.NORTH);

		inputPanel.setBackground(Color.WHITE);

		filterLbl = new JLabel(_("Filter:"));
		attFilterLbl = new JLabel(_("Filtered attendees:"));
		deleteBttn = GUITools.newImageButton(
				Data.getInstance().getIcon("remove_25x25_0.png"), Data
						.getInstance().getIcon("remove_25x25.png"));
		deleteBttn.setToolTipText(_("Remove attendee from address book"));
		deleteBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					AppAttendee localAtt = (AppAttendee) attendeeBx
							.getSelectedItem();
					Data.getInstance()
							.getAppData()
							.removeAttendee(localAtt.getName(),
									localAtt.getContact());
					if (attendeeBx.getItemCount() > 1)
						attendeeBx.removeItem(attendeeBx.getSelectedItem());
					else
						attendeeBx = new JComboBox();
					clearPopup();

				} catch (DataException e1) {
				}
			}
		});
		filterTxtFld = new JTextField();
		filterTxtFld.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {

				attendeeBx = new JComboBox();

				try {
					filteredAtt = Data.getInstance().getAppData()
							.getAttendees(filterTxtFld.getText());
				} catch (DataException e) {
					filteredAtt = new ArrayList<AppAttendee>();
				}
				for (AppAttendee appAtt : filteredAtt) {
					try {
						if (!appAtt.getName().equals("Test")
								&& !appAtt.getContact().equals("Test"))
							attendeeBx.addItem(appAtt);
					} catch (DataException e) {
						if (!appAtt.getName().equals("Test"))
							attendeeBx.addItem(appAtt);
					}
				}
				clearPopup();

			}

			@Override
			public void keyTyped(KeyEvent arg0) {

			}
		});

		GUITools.addComponent(inputPanel, gbl, filterLbl, 0, 0, 2, 1, 0, 0, 15,
				10, 0, 10, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(inputPanel, gbl, filterTxtFld, 0, 1, 2, 1, 1.0,
				0, 5, 10, 0, 10, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(inputPanel, gbl, attFilterLbl, 0, 2, 2, 1, 0, 0,
				5, 10, 0, 10, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(inputPanel, gbl, attendeeBx, 0, 3, 1, 1, 1.0, 0,
				5, 10, 20, 10, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(inputPanel, gbl, deleteBttn, 1, 3, 1, 1, 0, 0, 5,
				0, 20, 10, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);

		panelBase.add(inputPanel, BorderLayout.CENTER);

		Dimension popupSize;

		popupSize = new Dimension(260, 200);

		/*
		 * The buttons to abort and confirm the input
		 */
		JButton buttonAbort = GUITools.newImageButton();
		buttonAbort.setIcon(Data.getInstance().getIcon(
				"buttonCancel_24x24_0.png"));
		buttonAbort.setRolloverIcon(Data.getInstance().getIcon(
				"buttonCancel_24x24.png"));
		buttonAbort.setToolTipText(_("Abort"));
		buttonAbort.addActionListener(new DirectoryPopupWindowAction(this,
				ButtonClicked.ABORT));

		JButton buttonConfirm = GUITools.newImageButton();
		buttonConfirm.setIcon(Data.getInstance()
				.getIcon("buttonOk_24x24_0.png"));
		buttonConfirm.setRolloverIcon(Data.getInstance().getIcon(
				"buttonOk_24x24.png"));
		buttonConfirm.setToolTipText(_("Confirm"));
		buttonConfirm.addActionListener(new DirectoryPopupWindowAction(this,
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
	 * Creates the attendee bx.
	 */
	private void createAttendeeBx() {

		try {
			for (AppAttendee appAtt : Data.getInstance().getAppData()
					.getAttendees()) {
				if (!appAtt.getName().equals("Test")
						&& !appAtt.getContact().equals("Test"))
					attendeeBx.addItem(appAtt);
				else if (!appAtt.getName().equals("Test"))
					attendeeBx.addItem(appAtt);
			}
		} catch (DataException e) {
			/*
			 * do nothing
			 */
		}
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

	/**
	 * Clear popup.
	 */
	private void clearPopup() {
		inputPanel.removeAll();
		inputPanel.validate();
		GUITools.addComponent(inputPanel, gbl, filterLbl, 0, 0, 2, 1, 0, 0, 15,
				10, 0, 10, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(inputPanel, gbl, filterTxtFld, 0, 1, 2, 1, 1.0,
				0, 5, 10, 0, 10, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(inputPanel, gbl, attFilterLbl, 0, 2, 2, 1, 0, 0,
				5, 10, 0, 10, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(inputPanel, gbl, attendeeBx, 0, 3, 1, 1, 1.0, 0,
				5, 10, 20, 10, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(inputPanel, gbl, deleteBttn, 1, 3, 1, 1, 0, 0, 5,
				0, 20, 10, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		inputPanel.validate();
		inputPanel.repaint();
		filterTxtFld.grabFocus();
	}

}
