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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import org.revager.app.model.Data;
import org.revager.gui.AbstractDialog;
import org.revager.gui.actions.ActionRegistry;
import org.revager.gui.actions.severities.AddSeverityAction;
import org.revager.gui.actions.severities.EditSeverityAction;
import org.revager.gui.actions.severities.PushSeverityBottomAction;
import org.revager.gui.actions.severities.PushSeverityDownAction;
import org.revager.gui.actions.severities.PushSeverityTopAction;
import org.revager.gui.actions.severities.PushSeverityUpAction;
import org.revager.gui.actions.severities.RemoveSeverityAction;
import org.revager.gui.models.SeverityTableModel;
import org.revager.tools.GUITools;

/**
 * The Class ManageSeveritiesDialog.
 */
@SuppressWarnings("serial")
public class ManageSeveritiesDialog extends AbstractDialog {

	private SeverityTableModel stm = new SeverityTableModel();

	private GridBagLayout gbl = new GridBagLayout();

	private Container contentPane = getContentPane();

	private JTable severityTbl = GUITools.newStandardTable(stm, false);

	private JLabel nameLbl = new JLabel(_("Severities for the findings in this review:"));

	private JPanel buttonPanel = new JPanel(new GridLayout(7, 1));

	private boolean dontShowAgain = false;

	private JScrollPane scrllPn;

	private JButton removeSeverity;

	private JButton editSeverity;

	private JButton severityTop;

	private JButton severityUp;

	private JButton severityDown;

	private JButton severityBottom;

	/*
	 * Getters and Setters
	 */
	/**
	 * Checks if is dont show again.
	 * 
	 * @return true, if is dont show again
	 */
	public boolean isDontShowAgain() {
		return dontShowAgain;
	}

	/**
	 * Sets the dont show again.
	 * 
	 * @param dontShowAgain
	 *            the new dont show again
	 */
	public void setDontShowAgain(boolean dontShowAgain) {
		this.dontShowAgain = dontShowAgain;
	}

	/**
	 * Gets the stm.
	 * 
	 * @return the stm
	 */
	public SeverityTableModel getStm() {
		return stm;
	}

	/**
	 * Gets the severity tbl.
	 * 
	 * @return the severity tbl
	 */
	public JTable getSeverityTbl() {
		return severityTbl;
	}

	/*
	 * generating elements of the dialog
	 */
	/**
	 * Generate elements.
	 */
	public void generateElements() {
		JButton addSeverity = GUITools.newImageButton();
		addSeverity.setIcon(Data.getInstance().getIcon("add_25x25_0.png"));
		addSeverity.setRolloverIcon(Data.getInstance().getIcon("add_25x25.png"));
		addSeverity.setToolTipText(_("Add Severity"));
		addSeverity.addActionListener(ActionRegistry.getInstance().get(AddSeverityAction.class.getName()));
		buttonPanel.add(addSeverity);

		removeSeverity = GUITools.newImageButton();
		removeSeverity.setIcon(Data.getInstance().getIcon("remove_25x25_0.png"));
		removeSeverity.setRolloverIcon(Data.getInstance().getIcon("remove_25x25.png"));
		removeSeverity.setToolTipText(_("Remove Severity"));
		removeSeverity.addActionListener(ActionRegistry.getInstance().get(RemoveSeverityAction.class.getName()));
		buttonPanel.add(removeSeverity);

		editSeverity = GUITools.newImageButton();
		editSeverity.setIcon(Data.getInstance().getIcon("edit_25x25_0.png"));
		editSeverity.setRolloverIcon(Data.getInstance().getIcon("edit_25x25.png"));
		editSeverity.setToolTipText(_("Edit Severity"));
		editSeverity.addActionListener(ActionRegistry.getInstance().get(EditSeverityAction.class.getName()));
		buttonPanel.add(editSeverity);

		severityTop = GUITools.newImageButton();
		severityTop.setIcon(Data.getInstance().getIcon("pushTop_25x25_0.png"));
		severityTop.setRolloverIcon(Data.getInstance().getIcon("pushTop_25x25.png"));
		severityTop.setToolTipText(_("Push to the top"));
		severityTop.addActionListener(ActionRegistry.getInstance().get(PushSeverityTopAction.class.getName()));
		buttonPanel.add(severityTop);

		severityUp = GUITools.newImageButton();
		severityUp.setIcon(Data.getInstance().getIcon("upArrow_25x25_0.png"));
		severityUp.setRolloverIcon(Data.getInstance().getIcon("upArrow_25x25.png"));
		severityUp.setToolTipText(_("Push up"));
		severityUp.addActionListener(ActionRegistry.getInstance().get(PushSeverityUpAction.class.getName()));
		buttonPanel.add(severityUp);

		severityDown = GUITools.newImageButton();
		severityDown.setIcon(Data.getInstance().getIcon("downArrow_25x25_0.png"));
		severityDown.setRolloverIcon(Data.getInstance().getIcon("downArrow_25x25.png"));
		severityDown.setToolTipText(_("Push down"));
		severityDown.addActionListener(ActionRegistry.getInstance().get(PushSeverityDownAction.class.getName()));
		buttonPanel.add(severityDown);

		severityBottom = GUITools.newImageButton();
		severityBottom.setIcon(Data.getInstance().getIcon("pushBottom_25x25_0.png"));
		severityBottom.setRolloverIcon(Data.getInstance().getIcon("pushBottom_25x25.png"));
		severityBottom.setToolTipText(_("Push to the bottom"));
		severityBottom.addActionListener(ActionRegistry.getInstance().get(PushSeverityBottomAction.class.getName()));
		buttonPanel.add(severityBottom);

		scrllPn = GUITools.setIntoScrollPane(severityTbl);

		severityTbl.setTableHeader(null);
		severityTbl.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				updateButtons();
			}
		});

		GUITools.addComponent(contentPane, gbl, nameLbl, 0, 0, 1, 1, 0, 0, 0, 5, 0, 5, GridBagConstraints.NONE,
				GridBagConstraints.WEST);
		GUITools.addComponent(contentPane, gbl, scrllPn, 0, 1, 2, 2, 1.0, 1.0, 10, 5, 0, 5, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(contentPane, gbl, buttonPanel, 2, 1, 2, 1, 0, 0, 10, 5, 0, 5, GridBagConstraints.VERTICAL,
				GridBagConstraints.NORTH);

	}

	/*
	 * 
	 * Constructor
	 */
	/**
	 * Instantiates a new manage severities dialog.
	 * 
	 * @param parent
	 *            the parent
	 */
	public ManageSeveritiesDialog(Frame parent) {
		super(parent);

		setTitle(_("Manage Severities"));
		setDescription(_(
				"The order of the severities for the findings affects the entire review and represents their relevance (decreasing from top to bottom in the list)."));
		setIcon(Data.getInstance().getIcon("severities_50x50.png"));

		setHelpChapter("severities_management");

		contentPane.setLayout(gbl);

		generateElements();

		severityTbl.setShowGrid(false);
		severityTbl.setShowHorizontalLines(true);

		JButton close = new JButton(_("Close"), Data.getInstance().getIcon("buttonClose_16x16.png"));
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (severityTbl.getCellEditor() != null) {
					severityTbl.getCellEditor().stopCellEditing();
				}

				setVisible(false);
			}
		});

		addButton(close);

		setMinimumSize(new Dimension(500, 550));
		setPreferredSize(new Dimension(500, 550));
		getContentPane().setPreferredSize(new Dimension(400, 550));

		pack();

		setLocationToCenter();

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (severityTbl.getCellEditor() != null) {
					severityTbl.getCellEditor().stopCellEditing();
				}

				super.windowClosing(e);
			}
		});
	}

	/**
	 * Update buttons.
	 */
	public void updateButtons() {
		boolean enable = true;

		if (severityTbl.getSelectedRow() == -1 || severityTbl.isEditing()) {
			enable = false;
		}

		removeSeverity.setEnabled(enable);
		editSeverity.setEnabled(enable);
		severityUp.setEnabled(enable);
		severityDown.setEnabled(enable);
		severityTop.setEnabled(enable);
		severityBottom.setEnabled(enable);

		if (severityTbl.getSelectedRow() == 0) {
			severityTop.setEnabled(false);
			severityUp.setEnabled(false);
		}

		if (severityTbl.getSelectedRow() == severityTbl.getRowCount() - 1) {
			severityBottom.setEnabled(false);
			severityDown.setEnabled(false);
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
		}

		updateButtons();

		super.setVisible(vis);
	}
}
