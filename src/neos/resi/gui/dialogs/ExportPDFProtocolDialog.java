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
package neos.resi.gui.dialogs;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;

import neos.resi.app.Application;
import neos.resi.app.ProtocolManagement;
import neos.resi.app.model.Data;
import neos.resi.app.model.schema.Meeting;
import neos.resi.app.model.schema.Protocol;
import neos.resi.gui.AbstractDialog;
import neos.resi.gui.helpers.TreeProtocol;
import neos.resi.gui.workers.ExportPDFProtocolWorker;
import neos.resi.tools.GUITools;

/**
 * The Class ExportPDFProtocolDialog.
 */
@SuppressWarnings("serial")
public class ExportPDFProtocolDialog extends AbstractDialog {
	private GridBagLayout gbl = new GridBagLayout();
	private JRadioButton compRevRB;
	private JComboBox localMeetCoBx;
	private ProtocolManagement protMgmt = Application.getInstance()
			.getProtocolMgmt();
	private JButton abortBttn;
	private JButton exportBttn;
	private JCheckBox showFieldsChBx;
	private JCheckBox addExProRefChBx;
	private JCheckBox addExFindRefChBx;

	/**
	 * Adds the ex pro ref.
	 * 
	 * @return true, if successful
	 */
	public boolean addExProRef() {
		return addExProRefChBx.isSelected();
	}

	/**
	 * Adds the ex find ref.
	 * 
	 * @return true, if successful
	 */
	public boolean addExFindRef() {
		return addExFindRefChBx.isSelected();
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
	 * Show fields.
	 * 
	 * @return true, if successful
	 */
	public boolean showFields() {
		return showFieldsChBx.isSelected();
	}

	/**
	 * Instantiates a new export pdf protocol dialog.
	 * 
	 * @param parent
	 *            the parent
	 */
	public ExportPDFProtocolDialog(Frame parent) {
		super(parent);

		setTitle(Data.getInstance().getLocaleStr("pdfExport.title"));
		setDescription(Data.getInstance().getLocaleStr("pdfExport.description"));
		setIcon(Data.getInstance().getIcon("PDFExport_50x50.png"));

		setHelpChapter("protocol", "9");

		setLayout(gbl);
		setMinimumSize(new Dimension(450, 500));
		setPreferredSize(new Dimension(450, 500));
		getContentPane().setPreferredSize(new Dimension(400, 500));

		pack();

		setLocationToCenter();

		ButtonGroup radioBttnGrp = new ButtonGroup();
		compRevRB = new JRadioButton(Data.getInstance().getLocaleStr(
				"pdfExport.compRev"), true);
		radioBttnGrp.add(compRevRB);

		String fieldsStr = Data.getInstance().getLocaleStr(
				"pdfExport.showFields");
		String addFindRStr = Data.getInstance().getLocaleStr(
				"pdfExport.addFindRef");
		String addProExRefStr = Data.getInstance().getLocaleStr(
				"pdfExport.addExProRef");

		showFieldsChBx = new JCheckBox(fieldsStr);
		addExFindRefChBx = new JCheckBox(addFindRStr);
		addExProRefChBx = new JCheckBox(addProExRefStr);

		String locMeetStr = Data.getInstance().getLocaleStr("pdfExport.meet");
		JRadioButton localMeetRB = new JRadioButton(locMeetStr);
		localMeetRB.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					localMeetCoBx.setEnabled(true);
				else
					localMeetCoBx.setEnabled(false);
			}
		});

		radioBttnGrp.add(localMeetRB);

		localMeetCoBx = new JComboBox();
		localMeetCoBx.setEnabled(false);

		GUITools.addComponent(this, gbl, compRevRB, 0, 0, 1, 1, 1.0, 0, 10, 10,
				0, 10, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, localMeetRB, 0, 1, 1, 1, 1.0, 0, 5,
				10, 0, 10, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, localMeetCoBx, 0, 2, 1, 1, 1.0, 0, 5,
				30, 20, 10, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, showFieldsChBx, 0, 3, 1, 1, 1.0, 0,
				20, 10, 0, 10, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, addExProRefChBx, 0, 4, 1, 1, 1.0, 0,
				20, 10, 0, 10, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, addExFindRefChBx, 0, 5, 1, 1, 1.0, 0,
				20, 10, 0, 10, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST);

		abortBttn = new JButton(Data.getInstance().getLocaleStr("abort"), Data
				.getInstance().getIcon("buttonCancel_16x16.png"));
		abortBttn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		exportBttn = new JButton(Data.getInstance().getLocaleStr(
				"pdfExport.exp"), Data.getInstance().getIcon(
				"buttonOk_16x16.png"));
		exportBttn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ExportPDFProtocolWorker().execute();
			}
		});

		addButton(abortBttn);
		addButton(exportBttn);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see neos.resi.gui.AbstractDialog#setVisible(boolean)
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

			showFieldsChBx.setSelected(false);

			compRevRB.setSelected(false);
		}

		super.setVisible(vis);
	}

}
