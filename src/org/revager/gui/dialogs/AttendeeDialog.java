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
import java.awt.Frame;
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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
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
import org.revager.gui.StrengthPopupWindow;
import org.revager.gui.UI;
import org.revager.gui.StrengthPopupWindow.ButtonClicked;
import org.revager.gui.actions.ActionRegistry;
import org.revager.gui.actions.attendee.ConfirmAttendeeAction;
import org.revager.gui.actions.attendee.SelectAttOutOfDirAction;
import org.revager.gui.models.StrengthTableModel;
import org.revager.gui.workers.LoadStdCatalogsWorker;
import org.revager.tools.GUITools;


/**
 * The Class AttendeeDialog.
 */
@SuppressWarnings("serial")
public class AttendeeDialog extends AbstractDialog {

	private JTextField nameTxtFld = null;
	private JTextArea contactTxtArea = null;
	private JComboBox roleBox = null;

	private AppAttendee currentAppAttendee = null;
	private Attendee currentAttendee = null;

	private StrengthTableModel stm = null;
	private List<String> strengthList = null;
	private JTable strengthTbl = null;

	private JButton addStrength = null;
	private JButton removeStrength = null;
	private JButton confirmBttn;
	private JButton cancelBttn;

	private Container contentPane = null;
	private GridBagLayout gbl = new GridBagLayout();

	private JLabel name = null;
	private JButton directory = null;
	private JLabel contact = null;
	private JScrollPane contactScrllPn = null;
	private JLabel role = null;
	private JLabel strengthLbl = null;
	private JPanel buttonPanel = null;
	
	private boolean fromAssistant;

	public boolean isFromAssistant() {
		return fromAssistant;
	}

	public void setFromAssistant(boolean fromAssistant) {
		this.fromAssistant = fromAssistant;
	}

	private FocusListener focusListener = new FocusListener() {
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
	 * Gets the contact scrll pn.
	 * 
	 * @return the contact scrll pn
	 */
	public JScrollPane getContactScrllPn() {
		return contactScrllPn;
	}

	/**
	 * Gets the current app attendee.
	 * 
	 * @return the current app attendee
	 */
	public AppAttendee getCurrentAppAttendee() {
		return currentAppAttendee;
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
	 * Gets the stm.
	 * 
	 * @return the stm
	 */
	public StrengthTableModel getStm() {
		return stm;
	}

	/**
	 * Gets the name txt fld.
	 * 
	 * @return the name txt fld
	 */
	public JTextField getNameTxtFld() {
		return nameTxtFld;
	}

	/**
	 * Gets the contact txt area.
	 * 
	 * @return the contact txt area
	 */
	public JTextArea getContactTxtArea() {
		return contactTxtArea;
	}

	/**
	 * Gets the role box.
	 * 
	 * @return the role box
	 */
	public JComboBox getRoleBox() {
		return roleBox;
	}

	/**
	 * Checks if is edits the attendee.
	 * 
	 * @return true, if is edits the attendee
	 */
	public boolean isEditAttendee() {
		return currentAttendee != null;
	}

	/**
	 * Gets the current attendee.
	 * 
	 * @return the current attendee
	 */
	public Attendee getCurrentAttendee() {
		return this.currentAttendee;
	}

	/**
	 * Sets the current attendee.
	 * 
	 * @param att
	 *            the new current attendee
	 */
	public void setCurrentAttendee(Attendee att) {
		this.currentAttendee = att;
		currentAppAttendee = null;

		nameTxtFld.setBorder(UI.STANDARD_BORDER_INLINE);
		contactScrllPn.setBorder(UI.STANDARD_BORDER);

		if (currentAttendee == null) {

			setTitle(Data.getInstance().getLocaleStr("addAttendee.title"));
			setDescription(Data.getInstance().getLocaleStr(
					"addAttendee.description"));
			setIcon(Data.getInstance().getIcon("addAttendee_50x50.png"));
			setHelpChapter("attendees_management", "1");

			nameTxtFld.setText(null);
			contactTxtArea.setText(null);
			roleBox.setSelectedItem(Data.getInstance().getLocaleStr(
					"role." + Role.REVIEWER.toString().toLowerCase()));
			
			if(fromAssistant){
				setTitle(Data.getInstance().getLocaleStr("addYourself.title"));
				setDescription(Data.getInstance().getLocaleStr("addYourself.description"));
				cancelBttn.setText(Data.getInstance().getLocaleStr("back"));
				//cancelBttn.setEnabled(false);
				//closing operation has to be disabled, but how?
				
				
			}
		} else {
			setTitle(Data.getInstance().getLocaleStr("editAttendee.title"));
			setDescription(Data.getInstance().getLocaleStr(
					"editAttendee.description"));
			setIcon(Data.getInstance().getIcon("editAttendee_50x50.png"));
			setHelpChapter("attendees_management", "2");

			nameTxtFld.setText(currentAttendee.getName());
			contactTxtArea.setText(currentAttendee.getContact());
			roleBox.setSelectedItem(Data.getInstance().getLocaleStr(
					"role." + currentAttendee.getRole().value()));

			try {
				currentAppAttendee = Data.getInstance().getAppData()
						.getAttendee(currentAttendee.getName(),
								currentAttendee.getContact());
			} catch (DataException e) {
				JOptionPane.showMessageDialog(this, GUITools.getMessagePane(e
						.getMessage()), Data.getInstance()
						.getLocaleStr("error"), JOptionPane.ERROR_MESSAGE);
			}

		}

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
	 * Instantiates a new attendee dialog.
	 * 
	 * @param parent
	 *            the parent
	 */
	public AttendeeDialog(Frame parent) {
		super(parent);

		contentPane = getContentPane();
		contentPane.setLayout(gbl);

		name = new JLabel(Data.getInstance().getLocaleStr("attendee.name"));
		nameTxtFld = new JTextField();
		nameTxtFld.addFocusListener(focusListener);

		directory = GUITools.newImageButton();
		directory.setIcon(Data.getInstance().getIcon("directory_25x25_0.png"));
		directory.setRolloverIcon(Data.getInstance().getIcon(
				"directory_25x25.png"));
		directory.setToolTipText(Data.getInstance().getLocaleStr(
				"attendee.directory"));
		directory.addActionListener(ActionRegistry.getInstance().get(
				SelectAttOutOfDirAction.class.getName()));
		contact = new JLabel(Data.getInstance()
				.getLocaleStr("attendee.contact"));

		contactTxtArea = new JTextArea();
		contactTxtArea.addFocusListener(focusListener);
		contactScrllPn = GUITools.setIntoScrllPn(contactTxtArea);

		role = new JLabel(Data.getInstance().getLocaleStr("attendee.role"));
		roleBox = new JComboBox();
		roleBox.addFocusListener(focusListener);

		for (Role x : Role.values()) {
			String roleString = "role.".concat(x.value());
			roleBox.addItem(Data.getInstance().getLocaleStr(roleString));
		}

		roleBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ev) {
				updateStrengthTable();
			}
		});

		strengthLbl = new JLabel(Data.getInstance().getLocaleStr(
				"attendee.priorities"));

		buttonPanel = new JPanel(new GridLayout(3, 1));
		strengthTbl = GUITools.newStandardTable(null, false);
		strengthTbl.addFocusListener(focusListener);
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

		addStrength = GUITools.newImageButton();
		addStrength.setIcon(Data.getInstance().getIcon("add_25x25_0.png"));
		addStrength
				.setRolloverIcon(Data.getInstance().getIcon("add_25x25.png"));
		addStrength.setToolTipText(Data.getInstance().getLocaleStr(
				"attendeeDialog.addStrength"));

		addStrength.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				final String title = Data.getInstance().getLocaleStr(
						"popup.addStrength.title");

				SwingWorker<Void, Void> showPopupWorker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						StrengthPopupWindow popup = new StrengthPopupWindow(UI
								.getInstance().getAttendeeDialog(), title);

						/*
						 * Import the standard catalogs, if no catalogs exist in
						 * the database
						 */
						try {
							if (Data.getInstance().getAppData()
									.getNumberOfCatalogs() == 0) {
								switchToProgressMode(Data
										.getInstance()
										.getLocaleStr("status.importingCatalog"));

								LoadStdCatalogsWorker catalogWorker = new LoadStdCatalogsWorker();

								catalogWorker.execute();

								while (!catalogWorker.isDone()
										&& !catalogWorker.isCancelled()) {
									Thread.sleep(500);
								}

								switchToEditMode();
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
		});

		buttonPanel.add(addStrength);

		removeStrength = GUITools.newImageButton();
		removeStrength
				.setIcon(Data.getInstance().getIcon("remove_25x25_0.png"));
		removeStrength.setRolloverIcon(Data.getInstance().getIcon(
				"remove_25x25.png"));
		removeStrength.setToolTipText(Data.getInstance().getLocaleStr(
				"attendeeDialog.remStrength"));
		removeStrength.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				int selRow = strengthTbl.getSelectedRow();

				String str = (String) stm.getValueAt(selRow, 1);

				strengthList.remove(str);

				stm.fireTableDataChanged();

				updateStrengthButtons();
			}
		});

		buttonPanel.add(removeStrength);

		JScrollPane strScrllPn = GUITools.setIntoScrollPane(strengthTbl);

		GUITools.addComponent(contentPane, gbl, name, 0, 0, 1, 1, 0, 0, 0, 20,
				0, 20, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(contentPane, gbl, nameTxtFld, 1, 0, 3, 1, 1.0, 0,
				0, 20, 0, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools
				.addComponent(contentPane, gbl, directory, 4, 0, 1, 1, 0, 0, 0,
						5, 0, 20, GridBagConstraints.NONE,
						GridBagConstraints.NORTHWEST);
		GUITools.addComponent(contentPane, gbl, contact, 0, 1, 1, 1, 0, 0, 5,
				20, 0, 20, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(contentPane, gbl, contactScrllPn, 1, 1, 3, 3,
				1.0, 0.5, 5, 20, 0, 0, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(contentPane, gbl, role, 0, 4, 1, 1, 0, 0, 10, 20,
				0, 20, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(contentPane, gbl, roleBox, 1, 4, 3, 1, 1.0, 0,
				10, 20, 0, 0, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(contentPane, gbl, strengthLbl, 0, 5, 1, 1, 0, 0,
				17, 20, 0, 20, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(contentPane, gbl, strScrllPn, 1, 5, 3, 2, 1.0,
				0.5, 15, 20, 0, 0, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(contentPane, gbl, buttonPanel, 4, 5, 1, 2, 0, 0,
				17, 5, 0, 0, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);

		cancelBttn = new JButton(Data.getInstance().getLocaleStr("abort"),
				Data.getInstance().getIcon("buttonCancel_16x16.png"));
		cancelBttn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				setVisible(false);
				if(fromAssistant==true)
					UI.getInstance().getAssistantDialog().setVisible(true);
					
			}
		});

		addButton(cancelBttn);

		confirmBttn = new JButton(Data.getInstance()
				.getLocaleStr("confirm"), Data.getInstance().getIcon(
				"buttonOk_16x16.png"));
		confirmBttn.addActionListener(ActionRegistry.getInstance().get(
				ConfirmAttendeeAction.class.getName()));

		addButton(confirmBttn);

		setMinimumSize(new Dimension(500, 550));
		setPreferredSize(new Dimension(500, 550));
		getContentPane().setPreferredSize(new Dimension(400, 550));

		setLocationToCenter();

		pack();

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	}

	/**
	 * Update strength table.
	 */
	private void updateStrengthTable() {

		try {
			strengthList = currentAppAttendee.getStrengths();
		} catch (Exception e) {
			strengthList = null;
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

		if (((String) roleBox.getSelectedItem()).equals(Data.getInstance()
				.getLocaleStr("role.reviewer"))) {
			enable = true;
		}

		addStrength.setEnabled(enable);
		removeStrength.setEnabled(false);
		strengthTbl.setEnabled(enable);
		strengthLbl.setEnabled(enable);

		if (enable) {
			strengthTbl.setForeground(Color.BLACK);
		} else {
			strengthTbl.setForeground(Color.GRAY);
		}
	}

	/**
	 * Update strength buttons.
	 */
	private void updateStrengthButtons() {
		if (strengthTbl.getSelectedRow() != -1 && strengthTbl.isEnabled()) {
			removeStrength.setEnabled(true);
		} else {
			removeStrength.setEnabled(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.revager.gui.AbstractDialog#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean vis) {
		if (vis) {
			setLocationToCenter();
			updateStrengthButtons();
		}

		super.setVisible(vis);
	}

}
