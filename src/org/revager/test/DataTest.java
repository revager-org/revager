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
package org.revager.test;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Test;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;

/**
 * This class tests the Data class.
 * 
 * @author Johannes Wettinger
 * @version 1.0
 */
public class DataTest {

	private Data data;

	@Test(expected = DataException.class)
	public void dataExceptionWithoutMessage() throws DataException {
		throw new DataException();
	}

	@Test(expected = DataException.class)
	public void dataExceptionWithMessage() throws DataException {
		throw new DataException("Test-Meldung");
	}

	@Test
	public void getInstancesOfDataClasses() {
		data = Data.getInstance();

		assertNotNull(data.getAppData());
		assertNotNull(data.getResiData());
		assertNotNull(data.getHelpData());
	}

	@Test
	public void getAndSetLocale() {
		data = Data.getInstance();

		data.setLocale(Locale.GERMANY);

		assertEquals(Locale.GERMANY, data.getLocale());
	}

	@Test
	public void getAndSetMode() {
		data = Data.getInstance();

		data.setMode("moderator");

		assertEquals("moderator", data.getMode());
	}

	@Test
	public void setInvalidMode() {
		data = Data.getInstance();

		System.err.println("\nSHOW ERROR MESSAGE BECAUSE OF INVALID MODE:");
		data.setMode("diesenModusGibtsNicht");
	}

	@Test
	public void getIcon() {
		data = Data.getInstance();

		data.getIcon("createInvitations_50x50.png");
	}

	@Test
	public void getResource() {
		data = Data.getInstance();

		assertEquals("/org/revager/resources/appLogo.png",
				data.getResource("path.appLogo"));
	}

	@Test
	public void getInvalidResource() {
		data = Data.getInstance();

		System.err.println("\nSHOW ERROR MESSAGE BECAUSE OF INVALID RESOURCE:");
		data.getResource("dieseResourceGibtsNicht");
	}

	@Test
	public void getRegularModeParameter() {
		data = Data.getInstance();

		data.setMode("moderator");

		assertFalse(data.getModeParam("ableToCreateNewProtocol"));
	}

	@Test
	public void getDefaultFallbackModeParameter() {
		data = Data.getInstance();

		data.setMode("moderator");

		assertTrue(data.getModeParam("ableToManageSeverities"));
	}

	@Test
	public void getInvalidModeParameter() {
		data = Data.getInstance();

		data.setMode("moderator");

		assertFalse(data.getModeParam("diesenParameterGibtsNicht"));
	}

}
