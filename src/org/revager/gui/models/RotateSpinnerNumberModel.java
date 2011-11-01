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
package org.revager.gui.models;

import javax.swing.SpinnerNumberModel;

/**
 * The Class RotateSpinnerNumberModel.
 */
@SuppressWarnings("serial")
public class RotateSpinnerNumberModel extends SpinnerNumberModel {

	private int max = 0;

	private int min = 0;

	/**
	 * Instantiates a new rotate spinner number model.
	 * 
	 * @param value
	 *            the value
	 * @param minimum
	 *            the minimum
	 * @param maximum
	 *            the maximum
	 * @param stepSize
	 *            the step size
	 */
	public RotateSpinnerNumberModel(int value, int minimum, int maximum,
			int stepSize) {
		super(value, minimum, maximum, stepSize);
		this.max = maximum;
		this.min = minimum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SpinnerNumberModel#getNextValue()
	 */
	@Override
	public Object getNextValue() {
		Object value = super.getNextValue();
		if (value == null
				|| Integer.parseInt(value.toString()) == max
						+ Integer.parseInt(getStepSize().toString())) {
			super.setValue(min);
			return super.getValue();
		}
		return super.getNextValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SpinnerNumberModel#getPreviousValue()
	 */
	@Override
	public Object getPreviousValue() {
		Object value = super.getPreviousValue();
		if (value == null || Integer.parseInt(value.toString()) == max) {
			super.setValue(max);
			return super.getValue();
		}
		return super.getPreviousValue();
	}

}
