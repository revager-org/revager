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
package neos.resi.gui.protocol;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import neos.resi.app.Application;
import neos.resi.app.FindingManagement;
import neos.resi.app.model.schema.Finding;
import neos.resi.app.model.schema.Protocol;
import neos.resi.gui.UI;
import neos.resi.tools.GUITools;

/**
 * The Class ColoredTableCellRenderer.
 */
@SuppressWarnings("serial")
public class ColoredTableCellRenderer extends DefaultTableCellRenderer {
	FindingManagement findMgmt = Application.getInstance().getFindingMgmt();
	private Protocol prot;

	/**
	 * Instantiates a new colored table cell renderer.
	 * 
	 * @param currentProt
	 *            the current prot
	 */
	public ColoredTableCellRenderer(Protocol currentProt) {
		prot = currentProt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax
	 * .swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		JPanel localPnl = new JPanel();

		JLabel label = new JLabel(String.valueOf(value));
		label.setFont(UI.PROTOCOL_FONT);
		label.setForeground(UI.DARK_BLUE_COLOR);

		localPnl.add(label);

		if (findMgmt.getFindings(prot).get(row).getDescription().trim().equals(
				"")) {
			localPnl.setBorder(BorderFactory.createLineBorder(UI.MARKED_COLOR,
					2));
		} else {
			localPnl.setBorder(UI.STANDARD_BORDER);
		}

		// if (isSelected)
		// localPnl.setBackground(UI.TABLE_SELECTION_COLOR);
		// else
		// localPnl.setBackground(null);

		int fVF = UI.getInstance().getProtocolFrame().getFirstVisibleFinding();
		int lVF = fVF
				+ UI.getInstance().getProtocolFrame().getVisibleFindingsCount()
				- 1;
		if (row >= fVF && row <= lVF) {
			localPnl.setBackground(UI.BLUE_BACKGROUND_COLOR);
		} else {
			localPnl.setBackground(null);
		}

		Finding find = Application.getInstance().getFindingMgmt().getFinding(
				(Integer) value, prot);

		if (find != null) {
			String tip = GUITools.getTextAsHtml("<font size=\"5\"><b>("
					+ find.getId() + ") " + find.getSeverity() + "</b>"
					+ "\n\n" + find.getDescription() + "</font>");

			setToolTipText(tip);
		}

		return localPnl;
	}

}
