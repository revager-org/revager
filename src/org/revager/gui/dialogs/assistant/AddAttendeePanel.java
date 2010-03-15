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
package org.revager.gui.dialogs.assistant;

import java.awt.Choice;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.appdata.AppAttendee;
import org.revager.app.model.schema.Attendee;
import org.revager.app.model.schema.Role;
import org.revager.gui.AbstractDialog;
import org.revager.gui.AbstractDialogPanel;
import org.revager.gui.StrengthPopupWindow;
import org.revager.gui.UI;
import org.revager.gui.StrengthPopupWindow.ButtonClicked;
import org.revager.gui.actions.ActionRegistry;
import org.revager.gui.actions.attendee.SelectAttOutOfDirAction;
import org.revager.gui.models.StrengthTableModel;
import org.revager.gui.workers.LoadStdCatalogsWorker;
import org.revager.tools.GUITools;

/**
 * The class AddAttendeePanel. Should be used in the assistant only.
 * 
 * @author D.Casciato
 *
 */
@SuppressWarnings("serial")
public class AddAttendeePanel extends AbstractDialogPanel {

	private GridBagLayout gbl3 = new GridBagLayout();

	private boolean nameMissing;

	/*
	 * Strings
	 */
	private String reviewerStrng = Data.getInstance().getLocaleStr(
			"role.reviewer");
	private String nameStrng = Data.getInstance().getLocaleStr("attendee.name");
	private String contactStrng = Data.getInstance().getLocaleStr(
			"attendee.contact");
	private String roleStrng = Data.getInstance().getLocaleStr("attendee.role");
	private String strengthStrng = Data.getInstance().getLocaleStr(
			"attendee.priorities");
	private String directoryTooltipStrng = Data.getInstance().getLocaleStr(
			"attendee.directory");
	private String addStrengthTooltipStrng = Data.getInstance().getLocaleStr(
			"attendeeDialog.addStrength");
	private String removeStrengthTooltipStrng = Data.getInstance()
			.getLocaleStr("attendeeDialog.remStrength");

	/*
	 * ImageIcons
	 */
	private ImageIcon directoryIcon = Data.getInstance().getIcon(
			"directory_25x25_0.png");
	private ImageIcon directoryRolloverIcon = Data.getInstance().getIcon(
			"directory_25x25.png");
	private ImageIcon addStrengthIcon = Data.getInstance().getIcon(
			"add_25x25_0.png");
	private ImageIcon addStrengthRolloverIcon = Data.getInstance().getIcon(
			"add_25x25.png");
	private ImageIcon removeStrengthIcon = Data.getInstance().getIcon(
			"remove_25x25_0.png");
	private ImageIcon removeStrengthRolloverIcon = Data.getInstance().getIcon(
			"remove_25x25.png");

	/*
	 * Labels
	 */
	private JLabel nameLbl = new JLabel(nameStrng);
	private JLabel contactLbl = new JLabel(contactStrng);
	private JLabel roleLbl = new JLabel(roleStrng);
	private JLabel strengthLbl = new JLabel(strengthStrng);

	/*
	 * Buttons
	 */
	private JButton directoryBttn;
	private JButton addStrengthBttn;
	private JButton removeStrengthBttn;

	/*
	 * Other
	 */
	public JTextField nameTxtFld;
	private JTextArea contactTxtArea;
	private Choice roleCmbBx;
	private JTable strengthTbl;
	private JScrollPane contactScrllPn;
	private JPanel buttonPnl = null;

	private StrengthTableModel stm = null;

	private AppAttendee currentAppAttendee;
	private List<String> strengthList;

	/*
	 * Actions
	 */
	/**
	 * Action to select an attendee out of the address book.
	 */
	private Action browseAction = ActionRegistry.getInstance().get(
			SelectAttOutOfDirAction.class.getName());
	/**
	 * Action to add an strength to th current AppAttendee.
	 */
	private ActionListener addStrengthAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent ev) {
			final String title = Data.getInstance().getLocaleStr(
					"popup.addStrength.title");

			SwingWorker<Void, Void> showPopupWorker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					StrengthPopupWindow popup = new StrengthPopupWindow(UI
							.getInstance().getAssistantDialog(), title);

					/*
					 * Import the standard catalogs, if no catalogs exist in the
					 * database
					 */
					try {
						if (Data.getInstance().getAppData()
								.getNumberOfCatalogs() == 0) {
							getParent().switchToProgressMode(
									Data.getInstance().getLocaleStr(
											"status.importingCatalog"));

							LoadStdCatalogsWorker catalogWorker = new LoadStdCatalogsWorker();

							catalogWorker.execute();

							while (!catalogWorker.isDone()
									&& !catalogWorker.isCancelled()) {
								Thread.sleep(500);
							}

							getParent().switchToEditMode();
						}
					} catch (Exception exc) {
						/*
						 * do nothing
						 */
					}

					/*
					 * Show the popup
					 */
					popup.setVisible(true);

					if (popup.getButtonClicked() == ButtonClicked.OK) {
						for (String cat : popup.getSelCateList()) {
							if (!strengthList.contains(cat)) {
								strengthList.add(cat);
							}
						}

						stm.fireTableDataChanged();

						updateStrengthButtons();
					}

					return null;
				}
			};

			showPopupWorker.execute();
		}
	};

	/**
	 * Action to remove the selected strength from the current AppAttendee.
	 */
	private ActionListener removeStrengthAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent ev) {
			int selRow = strengthTbl.getSelectedRow();

			String str = (String) stm.getValueAt(selRow, 1);

			strengthList.remove(str);

			stm.fireTableDataChanged();

			updateStrengthButtons();
		}
	};

	/**
	 * Listener which updates the strength table and buttons.
	 */
	private FocusListener roleCmbBxListener = new FocusListener() {
		@Override
		public void focusGained(FocusEvent e) {
			if (e.getSource() != strengthTbl) {
				if (strengthTbl.getRowCount() > 0) {
					strengthTbl.removeRowSelectionInterval(0, strengthTbl
							.getRowCount() - 1);

					updateStrengthButtons();
				}
			}
		}

		@Override
		public void focusLost(FocusEvent e) {
		}
	};

	/**
	 * constructor
	 * 
	 * @param parent
	 */
	public AddAttendeePanel(AbstractDialog parent) {
		super(parent);
		createAddAttendeePnl();
	}

	/**
	 * Method which creates and locate the components.
	 */
	private void createAddAttendeePnl() {
		this.setLayout(gbl3);

		getStrengthList();

		nameLbl = new JLabel(nameStrng);
		contactLbl = new JLabel(contactStrng);
		roleLbl = new JLabel(roleStrng);
		strengthLbl = new JLabel(strengthStrng);

		nameTxtFld = new JTextField();
		contactTxtArea = new JTextArea();
		contactTxtArea.addFocusListener(roleCmbBxListener);
		contactScrllPn = GUITools.setIntoScrllPn(contactTxtArea);
		roleCmbBx = new Choice();
		roleCmbBx.addFocusListener(roleCmbBxListener);

		for (Role x : Role.values()) {
			String roleString = "role.".concat(x.value());
			roleCmbBx.addItem(Data.getInstance().getLocaleStr(roleString));
		}

		roleCmbBx.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ev) {
				updateStrengthTable();
			}
		});

		strengthTbl = GUITools.newStandardTable(null, false);

		directoryBttn = GUITools.newImageButton(directoryIcon,
				directoryRolloverIcon);
		directoryBttn.setToolTipText(directoryTooltipStrng);
		directoryBttn.addActionListener(browseAction);
		buttonPnl = new JPanel(new GridLayout(3, 1));

		strengthTbl.addFocusListener(roleCmbBxListener);
		strengthTbl.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				updateStrengthButtons();
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

		addStrengthBttn = GUITools.newImageButton();
		addStrengthBttn.setIcon(addStrengthIcon);
		addStrengthBttn.setRolloverIcon(addStrengthRolloverIcon);
		addStrengthBttn.setToolTipText(addStrengthTooltipStrng);

		addStrengthBttn.addActionListener(addStrengthAction);

		buttonPnl.add(addStrengthBttn);

		removeStrengthBttn = GUITools.newImageButton();
		removeStrengthBttn.setIcon(removeStrengthIcon);
		removeStrengthBttn.setRolloverIcon(removeStrengthRolloverIcon);
		removeStrengthBttn.setToolTipText(removeStrengthTooltipStrng);
		removeStrengthBttn.addActionListener(removeStrengthAction);

		buttonPnl.add(removeStrengthBttn);

		JScrollPane strScrllPn = GUITools.setIntoScrollPane(strengthTbl);

		GUITools.addComponent(this, gbl3, nameLbl, 0, 0, 1, 1, 0, 0, 0, 20, 0,
				20, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl3, nameTxtFld, 1, 0, 3, 1, 1.0, 0, 0,
				20, 0, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools
				.addComponent(this, gbl3, directoryBttn, 4, 0, 1, 1, 0, 0, 0,
						5, 0, 20, GridBagConstraints.NONE,
						GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl3, contactLbl, 0, 1, 1, 1, 0, 0, 5, 20,
				0, 20, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl3, contactScrllPn, 1, 1, 3, 3, 1.0, 0.5,
				5, 20, 0, 0, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl3, roleLbl, 0, 4, 1, 1, 0, 0, 10, 20, 0,
				20, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl3, roleCmbBx, 1, 4, 3, 1, 1.0, 0, 10,
				20, 0, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl3, strengthLbl, 0, 5, 1, 1, 0, 0, 17,
				20, 0, 20, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools
				.addComponent(this, gbl3, strScrllPn, 1, 5, 3, 2, 1.0, 0.5, 15,
						20, 0, 0, GridBagConstraints.BOTH,
						GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl3, buttonPnl, 4, 5, 1, 2, 0, 0, 17, 5,
				0, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);

		setCurrentAttendee(null);

	}

	/**
	 * Update strength buttons.
	 */
	private void updateStrengthButtons() {
		if (strengthTbl.getSelectedRow() != -1 && strengthTbl.isEnabled()) {
			removeStrengthBttn.setEnabled(true);
		} else {
			removeStrengthBttn.setEnabled(false);
		}
	}

	/**
	 * Update strength table.
	 */
	private void updateStrengthTable() {

		try {
			strengthList = currentAppAttendee.getStrengths();
		} catch (Exception e) {
			strengthList = new ArrayList<String>();
		}

		if (stm == null) {
			stm = new StrengthTableModel();
			strengthTbl.setModel(stm);
		}

		stm.fireTableDataChanged();

		/*
		 * View of strengths
		 */
		boolean enable = false;

		if (((String) roleCmbBx.getSelectedItem()).equals(reviewerStrng)) {
			enable = true;
		}

		addStrengthBttn.setEnabled(enable);
		removeStrengthBttn.setEnabled(false);
		strengthTbl.setEnabled(enable);
		strengthLbl.setEnabled(enable);

		if (enable) {
			strengthTbl.setForeground(Color.BLACK);
		} else {
			strengthTbl.setForeground(Color.GRAY);
		}
	}

	/**
	 * Sets the current attendee.
	 * 
	 * @param att
	 *            the new current attendee
	 */
	public void setCurrentAttendee(Attendee att) {
		// currentAttendee = att;
		currentAppAttendee = null;

		nameTxtFld.setText(null);
		contactTxtArea.setText(null);
		roleCmbBx.select(Data.getInstance().getLocaleStr(
				"role." + Role.REVIEWER.toString().toLowerCase()));

		updateStrengthTable();
	}

	/**
	 * Gets the strength list.
	 * 
	 * @return the strengthList
	 */
	public List<String> getStrengthList() {
		if (strengthList == null) {
			strengthList = new ArrayList<String>();
		}

		return strengthList;
	}

	/**
	 * Sets the current app attendee.
	 * 
	 * @param appAtt
	 *            the new current app attendee
	 */
	public void setCurrentAppAttendee(AppAttendee appAtt) {
		this.currentAppAttendee = appAtt;

		nameTxtFld.setBorder(UI.STANDARD_BORDER_INLINE);
		contactScrllPn.setBorder(UI.STANDARD_BORDER);

		nameTxtFld.setText(currentAppAttendee.getName());

		try {
			contactTxtArea.setText(currentAppAttendee.getContact());
		} catch (DataException e) {
			JOptionPane.showMessageDialog(this, GUITools.getMessagePane(e
					.getMessage()), Data.getInstance().getLocaleStr("error"),
					JOptionPane.ERROR_MESSAGE);
		}

		updateStrengthTable();
	}

	/**
	 * Adds instant reviewer
	 * 
	 */
	public void updateInstantAtt() {

		Role[] roles = Role.values();
		String attContact;

		nameTxtFld.setBorder(UI.STANDARD_BORDER_INLINE);
		contactScrllPn.setBorder(UI.STANDARD_BORDER);

		String attName = nameTxtFld.getText();
		if (contactTxtArea.getText() != null)
			attContact = contactTxtArea.getText();
		else
			attContact = "";

		Role attRole = roles[roleCmbBx.getSelectedIndex()];

		nameMissing = false;

		String message = "";

		if (attName.trim().equals("")) {
			nameMissing = true;
		}

		if (nameMissing) {
			message = Data.getInstance().getLocaleStr(
					"attendeeDialog.message.noName");

			getParent().setMessage(message);
			nameTxtFld.setBorder(UI.MARKED_BORDER_INLINE);
		} else {

			/*
			 * Update the app attendee in the database
			 */
			try {
				if (currentAppAttendee == null) {
					currentAppAttendee = Data.getInstance().getAppData()
							.getAttendee(attName, attContact);

					if (currentAppAttendee == null) {
						currentAppAttendee = Data.getInstance().getAppData()
								.newAttendee(attName, attContact);
					}
				} else {
					currentAppAttendee.setNameAndContact(attName, attContact);
				}

				for (String str : currentAppAttendee.getStrengths()) {
					currentAppAttendee.removeStrength(str);
				}

				for (String str : strengthList) {
					currentAppAttendee.addStrength(str);
				}
			} catch (DataException e1) {
				JOptionPane.showMessageDialog(UI.getInstance()
						.getAssistantDialog(), GUITools.getMessagePane(e1
						.getMessage()), Data.getInstance()
						.getLocaleStr("error"), JOptionPane.ERROR_MESSAGE);
			}

			/*
			 * update the review attendee
			 */
			Attendee newAtt = new Attendee();

			newAtt.setName(attName);
			newAtt.setContact(attContact);
			newAtt.setRole(attRole);

			org.revager.app.Application.getInstance().getAttendeeMgmt()
					.addAttendee(attName, attContact, attRole, null);

			setVisible(false);

			UI.getInstance().getAspectsManagerFrame().updateViews();
		}

	}

}
