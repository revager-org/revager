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
package org.revager.gui.aspects_manager;

import static org.revager.app.model.Data.translate;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import org.revager.app.Application;
import org.revager.app.AspectManagement;
import org.revager.app.AttendeeManagement;
import org.revager.app.model.Data;
import org.revager.app.model.schema.Aspect;
import org.revager.app.model.schema.Attendee;
import org.revager.gui.UI;
import org.revager.gui.models.ReviewerTableModel;
import org.revager.tools.GUITools;

/**
 * The Class ReviewerPanel.
 */
@SuppressWarnings("serial")
public class ReviewerPanel extends JPanel implements Observer {

	private AttendeeManagement attMgmt = Application.getInstance().getAttendeeMgmt();
	private AspectManagement aspMgmt = Application.getInstance().getAspectMgmt();

	private JTable table = null;
	private ReviewerTableModel tableModel = null;

	private JButton buttonRemove = null;
	private JButton buttonEdit = null;
	private JButton buttonPushUp = null;
	private JButton buttonPushDown = null;

	/**
	 * Instantiates a new reviewer panel.
	 * 
	 * @param reviewer
	 *            the reviewer
	 */
	public ReviewerPanel(final Attendee reviewer) {
		super();

		final ActionListener editAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selRow = table.getSelectedRow();

				if (selRow != -1) {
					Aspect aspect = attMgmt.getAspects(reviewer).get(selRow);

					EditAspectPopupWindow popup = new EditAspectPopupWindow(UI.getInstance().getAspectsManagerFrame(),
							aspect);

					popup.setVisible(true);

					aspMgmt.editAspect(aspect, aspect);

					table.setRowSelectionInterval(selRow, selRow);
					table.repaint();

					updateTable();
					updateButtons();
				}
			}
		};

		tableModel = new ReviewerTableModel(reviewer);
		table = GUITools.newStandardTable(tableModel, true);
		table.setShowGrid(true);
		table.getColumnModel().getColumn(1).setMaxWidth(200);
		table.getColumnModel().getColumn(1).setPreferredWidth(150);
		table.setAutoscrolls(true);
		table.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					editAction.actionPerformed(null);
				}
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
				updateButtons();
			}
		});

		/*
		 * Tooltips
		 */
		DefaultTableCellRenderer cellRend = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				String content = (String) value;

				setToolTipText(GUITools.getTextAsHtml(content));

				content = content.split("\n")[0];

				return super.getTableCellRendererComponent(table, content, isSelected, hasFocus, row, column);
			}
		};
		table.getColumnModel().getColumn(0).setCellRenderer(cellRend);
		table.getColumnModel().getColumn(1).setCellRenderer(cellRend);

		buttonRemove = GUITools.newImageButton(Data.getInstance().getIcon("remove_25x25_0.png"),
				Data.getInstance().getIcon("remove_25x25.png"));
		buttonRemove.setToolTipText(translate("Remove selected aspect"));
		buttonRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selRow = table.getSelectedRow();

				if (selRow != -1) {
					Aspect asp = attMgmt.getAspects(reviewer).get(selRow);
					attMgmt.removeAspect(asp, reviewer);

					if (selRow == attMgmt.getNumberOfAspects(reviewer) && selRow > 0) {
						table.setRowSelectionInterval(selRow - 1, selRow - 1);
					} else if (attMgmt.getNumberOfAspects(reviewer) > 0) {
						table.setRowSelectionInterval(selRow, selRow);
					}

					updateTable();
					updateButtons();
				}
			}
		});

		buttonEdit = GUITools.newImageButton(Data.getInstance().getIcon("edit_25x25_0.png"),
				Data.getInstance().getIcon("edit_25x25.png"));
		buttonEdit.setToolTipText(translate("Edit selected aspect"));
		buttonEdit.addActionListener(editAction);

		buttonPushUp = GUITools.newImageButton(Data.getInstance().getIcon("upArrow_25x25_0.png"),
				Data.getInstance().getIcon("upArrow_25x25.png"));
		buttonPushUp.setToolTipText(translate("Push up item"));
		buttonPushUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selRow = table.getSelectedRow();

				if (selRow != -1 && selRow > 0) {
					Aspect asp = attMgmt.getAspects(reviewer).get(selRow);
					attMgmt.pushUpAspect(reviewer, asp);

					table.setRowSelectionInterval(selRow - 1, selRow - 1);

					updateTable();
					updateButtons();
				}

			}
		});

		buttonPushDown = GUITools.newImageButton(Data.getInstance().getIcon("downArrow_25x25_0.png"),
				Data.getInstance().getIcon("downArrow_25x25.png"));
		buttonPushDown.setToolTipText(translate("Push down item"));
		buttonPushDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selRow = table.getSelectedRow();

				if (selRow != -1 && selRow < attMgmt.getNumberOfAspects(reviewer) - 1) {
					Aspect asp = attMgmt.getAspects(reviewer).get(selRow);
					attMgmt.pushDownAspect(reviewer, asp);

					table.setRowSelectionInterval(selRow + 1, selRow + 1);

					updateTable();
					updateButtons();
				}
			}
		});

		JLabel labelName = new JLabel(reviewer.getName(), Data.getInstance().getIcon("attendee_16x16.png"),
				SwingConstants.LEFT);

		/*
		 * Build the layout
		 */
		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		GUITools.addComponent(this, gbl, labelName, 0, 0, 2, 1, 0.0, 0.0, 0, 0, 7, 0, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, GUITools.setIntoScrollPane(table), 0, 1, 1, 4, 1.0, 1.0, 0, 0, 0, 7,
				GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, buttonEdit, 1, 1, 1, 1, 0.0, 0.0, 0, 0, 0, 0, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, buttonRemove, 1, 2, 1, 1, 0.0, 0.0, 0, 0, 0, 0, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, buttonPushUp, 1, 3, 1, 1, 0.0, 0.0, 0, 0, 0, 0, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);
		GUITools.addComponent(this, gbl, buttonPushDown, 1, 4, 1, 1, 0.0, 0.0, 0, 0, 0, 0, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST);

		/*
		 * Register observer
		 */
		Data.getInstance().getResiData().addObserver(this);

		/*
		 * Update the buttons
		 */
		updateButtons();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		tableModel.fireTableDataChanged();

		updateButtons();
	}

	/**
	 * Update buttons.
	 */
	private void updateButtons() {
		boolean enable = false;

		if (table.getSelectedRow() != -1) {
			enable = true;
		}

		buttonEdit.setEnabled(enable);
		buttonRemove.setEnabled(enable);
		buttonPushUp.setEnabled(enable);
		buttonPushDown.setEnabled(enable);

		if (table.getSelectedRow() == 0) {
			buttonPushUp.setEnabled(false);
		}

		if (table.getSelectedRow() == table.getRowCount() - 1) {
			buttonPushDown.setEnabled(false);
		}
	}

	/**
	 * Update table.
	 */
	private void updateTable() {
		int selRow = table.getSelectedRow();

		table.scrollRectToVisible(table.getCellRect(selRow, 0, false));
	}
}
