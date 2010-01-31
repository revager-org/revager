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
package org.revager.gui.helpers;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.revager.app.Application;
import org.revager.app.MeetingManagement;
import org.revager.app.model.Data;
import org.revager.gui.UI;


/**
 * The Class MeetingsTreeRenderer.
 */
@SuppressWarnings("serial")
public class MeetingsTreeRenderer extends DefaultTreeCellRenderer {

	public static int currentRow = -1;

	private MeetingManagement meetMgmt = Application.getInstance()
			.getMeetingMgmt();

	private final ImageIcon MEET_ICON = Data.getInstance().getIcon(
			"meeting_30x30.png");
	private final ImageIcon MEET_CANCEL_ICON = Data.getInstance().getIcon(
			"meetingCanceled_30x30.png");
	private final ImageIcon MEET_NEW_ICON = Data.getInstance().getIcon(
			"meetingNew_30x30.png");
	private final ImageIcon PROT_ICON = Data.getInstance().getIcon(
			"protocol_20x20.png");

	private final Color DEFAULT_BG = getBackgroundNonSelectionColor();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent
	 * (javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int,
	 * boolean)
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		DefaultMutableTreeNode currentTreeNode = (DefaultMutableTreeNode) value;
		Object userObject = (Object) currentTreeNode.getUserObject();

		if (userObject instanceof TreeProtocol) {
			setFont(UI.STANDARD_FONT);
			setLeafIcon(PROT_ICON);
			setOpenIcon(PROT_ICON);
			setClosedIcon(PROT_ICON);
		} else if (userObject instanceof TreeMeeting) {
			ImageIcon icon = MEET_ICON;

			if (meetMgmt.isMeetingCanceled(((TreeMeeting) userObject)
					.getMeeting())) {
				icon = MEET_CANCEL_ICON;
			} else if (((TreeMeeting) userObject).getMeeting().getProtocol() == null) {
				icon = MEET_NEW_ICON;
			}

			setFont(UI.LARGE_FONT);
			setLeafIcon(icon);
			setOpenIcon(icon);
			setClosedIcon(icon);
		}

		if (currentRow == row) {
			setBackgroundNonSelectionColor(UI.BLUE_BACKGROUND_COLOR);
		} else {
			setBackgroundNonSelectionColor(DEFAULT_BG);
		}

		return super.getTreeCellRendererComponent(tree, value, sel, expanded,
				leaf, row, hasFocus);
	}

}
