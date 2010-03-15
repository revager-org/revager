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

import java.awt.Panel;

/**
 * This class is the superclass for the assistant panels.
 */
@SuppressWarnings("serial")
public abstract class AbstractDialogPanel extends Panel {

	private AbstractDialog parent;

	/**
	 * Returns the parent of this component.
	 */
	public AbstractDialog getParent() {
		return parent;
	}
	
	/**
	 * Sets the hint message in the parent.
	 * @param hintText
	 */
	public void setHint(String hintText) {

		parent.setMessage(hintText);

	}
	
	/**
	 * Constructor with it's parent as parameter
	 * @param parent
	 */
	public AbstractDialogPanel(AbstractDialog parent) {

		super();

		this.parent = parent;

	}

	

}
