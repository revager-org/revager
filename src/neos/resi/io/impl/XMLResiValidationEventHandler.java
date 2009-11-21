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
package neos.resi.io.impl;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

/**
 * This class implements the ValidationEventHandler interface to provide an
 * event handler for validating Resi XML files.
 */
public class XMLResiValidationEventHandler implements ValidationEventHandler {

	/**
	 * The message.
	 */
	private String message = "no detailed information available";

	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.xml.bind.ValidationEventHandler#handleEvent(javax.xml.bind.
	 * ValidationEvent)
	 */
	public boolean handleEvent(ValidationEvent event) {
		if (event.getSeverity() == ValidationEvent.FATAL_ERROR
				|| event.getSeverity() == ValidationEvent.ERROR) {
			ValidationEventLocator locator = event.getLocator();

			message = event.getMessage() + " [COLUMN = "
					+ locator.getColumnNumber() + ", LINE = "
					+ locator.getLineNumber() + "]";
		}

		return false;
	}

	/**
	 * Get the message from the last handled event.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

}
