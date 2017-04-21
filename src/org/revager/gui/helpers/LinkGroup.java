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
import java.util.ArrayList;
import java.util.List;

import org.revager.gui.UI;

/**
 * The class LinkGroup.
 * 
 * @author D.Casciato
 *
 */
public class LinkGroup {

	private List<HLink> hLinksList = new ArrayList<HLink>();

	/**
	 * Method which adds an Link to the LinkGroup.
	 * 
	 * @param link
	 */
	public void addLink(HLink link) {
		hLinksList.add(link);
		link.setLocalRolloverIcon(link.getLocalIcon());
	}

	/**
	 * Method which resets the look of the links.
	 */
	public void resetAllLinks() {
		for (HLink link : hLinksList) {
			// link.setBold(false);
			link.setColor(Color.BLACK);
			link.setLocalRolloverIcon(link.getLocalDisIcon());
			link.setLocalIcon(link.getLocalDisIcon());

		}
	}

	/**
	 * Method which selects the given link in the LinkGroup.
	 * 
	 * @param selLink
	 */
	public void selectLink(HLink selLink) {
		for (HLink link : hLinksList) {
			if (link == selLink) {
				// link.setBold(true);
				link.setColor(UI.LINK_COLOR);
				link.setLocalRolloverIcon(link.getLocalSelIcon());
				link.setLocalIcon(link.getLocalSelIcon());
				link.setSelected(true);
			} else {
				// link.setBold(false);
				link.setColor(Color.BLACK);
				link.setLocalRolloverIcon(link.getLocalDisIcon());
				link.setLocalIcon(link.getLocalDisIcon());
				link.setSelected(false);
			}
		}
	}

	/**
	 * Returns the text of the selected Link.
	 * 
	 * @return
	 */
	public String getSelectedLinkText() {
		for (HLink link : hLinksList) {
			if (link.getSelected())
				return link.getLocalLbl().getText();
		}
		return null;
	}

	/**
	 * Returns the index of the selected Link.
	 * 
	 * @return
	 */
	public int getSelectedLinkIndex() {
		for (int index = 0; index < hLinksList.size(); index++) {
			if (hLinksList.get(index).getSelected())
				return index;
		}
		return -1;
	}

	/**
	 * Method which deselects all Links.
	 */
	public void deselectAllLinks() {
		for (HLink link : hLinksList) {
			link.setSelected(false);
		}

	}

}
