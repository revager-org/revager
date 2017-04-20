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

import static org.revager.app.model.Data._;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;

import org.revager.app.Application;
import org.revager.app.ProtocolManagement;
import org.revager.app.SeverityManagement;
import org.revager.app.model.ApplicationData;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.appdata.AppCSVProfile;
import org.revager.app.model.schema.Meeting;
import org.revager.app.model.schema.Protocol;
import org.revager.gui.AbstractDialog;
import org.revager.gui.helpers.ComboBoxEditor;
import org.revager.gui.helpers.ComboBoxRenderer;
import org.revager.gui.helpers.TreeProtocol;
import org.revager.gui.models.CSVProfileTableModel;
import org.revager.gui.workers.ExportCSVWorker;
import org.revager.tools.GUITools;

/**
 * The Class ExportCSVDialog.
 */
@SuppressWarnings("serial")
public class ExportCSVDialog extends AbstractDialog {

	private GridBagLayout gbl = new GridBagLayout();

	private JRadioButton compRevRB;

	private JComboBox localMeetCoBx;

	private ProtocolManagement protMgmt = Application.getInstance()
			.getProtocolMgmt();

	private JButton abortBttn;

	private JButton exportBttn;

	private JTextField reporterTxtFld;

	private JComboBox csvProfileCoBx = new JComboBox();

	private List<AppCSVProfile> csvProfileList;

	private ApplicationData appData = Data.getInstance().getAppData();

	private List<String> selValidSeverityMappingsList;

	private List<String> validSeverityMappingsList;

	private AppCSVProfile selProfile;

	private JTable sevTbl;

	private CSVProfileTableModel cptm = new CSVProfileTableModel(null, null);

	private SeverityManagement sevMgmt = Application.getInstance()
			.getSeverityMgmt();

	/*
	 * 
	 * Getters and setters
	 */
	/**
	 * Gets the sel col name list.
	 * 
	 * @return the sel col name list
	 */
	public List<String> getSelColNameList() {
		return selValidSeverityMappingsList;
	}

	/**
	 * Export rev.
	 * 
	 * @return true, if successful
	 */
	public boolean exportRev() {
		return compRevRB.isSelected();
	}

	/**
	 * Gets the selected meeting.
	 * 
	 * @return the selected meeting
	 */
	public Meeting getSelectedMeeting() {
		return ((TreeProtocol) localMeetCoBx.getSelectedItem()).getMeeting();
	}

	/**
	 * Gets the reporter.
	 * 
	 * @return the reporter
	 */
	public String getReporter() {
		return reporterTxtFld.getText();
	}

	/**
	 * Instantiates a new export csv dialog.
	 * 
	 * @param parent
	 *            the parent
	 */
	public ExportCSVDialog(Frame parent) {
		super(parent);

		setTitle(_("Export Findings into a CSV File"));
		setDescription(_("Here you can export the findings into a CSV file."));
		setIcon(Data.getInstance().getIcon("CSVExport_50x50.png"));

		setHelpChapter("findings_management", "5");

		setLayout(gbl);
		setMinimumSize(new Dimension(450, 600));
		setPreferredSize(new Dimension(450, 600));
		getContentPane().setPreferredSize(new Dimension(400, 600));

		pack();

		setLocationToCenter();

		/*
		 * Generating sevTable
		 */
		sevTbl = GUITools.newStandardTable(cptm, true);
		sevTbl.setCellSelectionEnabled(true);
		JScrollPane sevScrllPn = new JScrollPane(sevTbl);
		sevScrllPn.getViewport().setBackground(Color.WHITE);

		/*
		 * generating and initializing csvProfileComboBox
		 */

		try {
			csvProfileList = appData.getCSVProfiles();
		} catch (DataException exc) {
			JOptionPane.showMessageDialog(null,
					GUITools.getMessagePane(exc.getMessage()), _("Error"),
					JOptionPane.ERROR_MESSAGE);

			return;
		}

		selProfile = csvProfileList.get(0);

		genValidSevMapList();
		genSelValidSevMapList();

		csvProfileCoBx.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					int index = csvProfileCoBx.getSelectedIndex();
					selProfile = csvProfileList.get(index);
					genValidSevMapList();
					genSelValidSevMapList();
					cptm.setMapsAndProfile(selProfile,
							selValidSeverityMappingsList);
					setComBoBoxEditAndRend();
				}
			}
		});
		genProfileCoBx();

		/*
		 * generating tablemodel and setting the model
		 */
		cptm = new CSVProfileTableModel(selProfile,
				selValidSeverityMappingsList);
		sevTbl.setModel(cptm);

		setComBoBoxEditAndRend();

		ButtonGroup bttnGrp = new ButtonGroup();

		String compRecStr = _("All findigs of this review");
		compRevRB = new JRadioButton(compRecStr, true);
		compRevRB.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					localMeetCoBx.setEnabled(false);
				else
					localMeetCoBx.setEnabled(true);
			}
		});
		bttnGrp.add(compRevRB);

		String localMeetStr = _("Export findings of a certain meeting only:");
		JRadioButton localMeetRB = new JRadioButton(localMeetStr);
		bttnGrp.add(localMeetRB);

		localMeetCoBx = new JComboBox();
		localMeetCoBx.setEnabled(false);
		reporterTxtFld = new JTextField();

		JLabel reporterLbl = new JLabel(_("Bug Reporter:"));
		JLabel csvProfileLbl = new JLabel(_("CSV Profile:"));

		GUITools.addComponent(this, gbl, compRevRB, 0, 0, 1, 1, 1.0, 0, 10, 10,
				0, 10, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, localMeetRB, 0, 1, 1, 1, 1.0, 0, 5,
				10, 0, 10, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, localMeetCoBx, 0, 2, 1, 1, 1.0, 0, 5,
				10, 20, 10, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, reporterLbl, 0, 3, 1, 1, 1.0, 0, 15,
				10, 0, 10, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, reporterTxtFld, 0, 4, 1, 1, 1.0, 0, 5,
				10, 20, 10, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);

		GUITools.addComponent(this, gbl, csvProfileLbl, 0, 5, 1, 1, 1.0, 0, 15,
				10, 0, 10, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, csvProfileCoBx, 0, 6, 1, 1, 1.0, 0, 5,
				10, 10, 10, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, sevScrllPn, 0, 7, 1, 1, 1.0, 1.0, 5,
				10, 0, 10, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);

		abortBttn = new JButton(_("Abort"), Data.getInstance().getIcon(
				"buttonCancel_16x16.png"));
		abortBttn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		exportBttn = new JButton(_("Export"), Data.getInstance().getIcon(
				"buttonOk_16x16.png"));
		exportBttn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GUITools.executeSwingWorker(new ExportCSVWorker());
			}
		});

		addButton(abortBttn);
		addButton(exportBttn);
	}

	/**
	 * Gen profile co bx.
	 */
	private void genProfileCoBx() {
		for (AppCSVProfile profile : csvProfileList) {
			try {
				if (!profile.getValidSeverityMappings().isEmpty())
					csvProfileCoBx.addItem(profile.getName());
			} catch (DataException e) {
				JOptionPane.showMessageDialog(null,
						GUITools.getMessagePane(e.getMessage()), _("Error"),
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Sets the com bo box edit and rend.
	 */
	private void setComBoBoxEditAndRend() {
		String[] columnNameArray = validSeverityMappingsList
				.toArray(new String[0]);

		int vColIndex = 1;

		TableColumn col = sevTbl.getColumnModel().getColumn(vColIndex);

		col.setCellEditor(new ComboBoxEditor(columnNameArray));

		col.setCellRenderer(new ComboBoxRenderer(columnNameArray));
	}

	/**
	 * Gen valid sev map list.
	 */
	private void genValidSevMapList() {
		try {
			validSeverityMappingsList = selProfile.getValidSeverityMappings();
		} catch (DataException e) {
			JOptionPane.showMessageDialog(null,
					GUITools.getMessagePane(e.getMessage()), _("Error"),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Gen sel valid sev map list.
	 */
	private void genSelValidSevMapList() {
		selValidSeverityMappingsList = new ArrayList<String>();
		for (int index = 0; index < sevMgmt.getNumberOfSeverities(); index++) {
			if (index < validSeverityMappingsList.size()) {
				selValidSeverityMappingsList.add(validSeverityMappingsList
						.get(index));
			} else if (validSeverityMappingsList.size() != 0) {
				int lastElement = validSeverityMappingsList.size() - 1;
				selValidSeverityMappingsList.add(validSeverityMappingsList
						.get(lastElement));
			}
		}
	}

	/**
	 * Clear dialog.
	 */
	public void clearDialog() {
		csvProfileCoBx.removeAllItems();
		genProfileCoBx();
		csvProfileCoBx.validate();

		int index = csvProfileCoBx.getSelectedIndex();
		selProfile = csvProfileList.get(index);
		genValidSevMapList();
		genSelValidSevMapList();
		cptm.setMapsAndProfile(selProfile, selValidSeverityMappingsList);
		setComBoBoxEditAndRend();

		sevTbl.repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.revager.gui.AbstractDialog#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean vis) {
		if (vis) {
			localMeetCoBx.removeAllItems();

			for (Protocol prot : protMgmt.getProtocolsWithFindings()) {
				TreeProtocol treeProt = new TreeProtocol();
				treeProt.setMeeting(protMgmt.getMeeting(prot));
				localMeetCoBx.addItem(treeProt);
			}

			localMeetCoBx.validate();

			clearDialog();

			compRevRB.setSelected(false);
		}

		super.setVisible(vis);
	}

}
