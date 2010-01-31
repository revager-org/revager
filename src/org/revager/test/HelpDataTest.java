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


import org.junit.Test;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.HelpData;

/**
 * This class tests the HelpData class.
 * 
 * @author Johannes Wettinger
 * @version 1.0
 */
public class HelpDataTest {

	private HelpData helpData;

	@Test
	public void getChapters() throws DataException {
		helpData = Data.getInstance().getHelpData();

		assertNotNull(helpData.getChapters());
	}

	@Test
	public void getChapterContent() throws DataException {
		helpData = new HelpData();

		assertNotNull(helpData.getChapterContent("start"));
	}

	@Test
	public void getChapterNumber() throws DataException {
		helpData = new HelpData();

		assertEquals(0, helpData.getChapterNumber("start"));
	}

	@Test
	public void getChapterTitle() throws DataException {
		helpData = new HelpData();

		assertNotNull(helpData.getChapterTitle("start"));
	}
	
	@Test(expected = DataException.class)
	public void getNotExistingChapterTitle() throws DataException {
		helpData = new HelpData();

		helpData.getChapterTitle("gibts_nicht");
	}
	
	@Test(expected = DataException.class)
	public void getNotExistingChapterContent() throws DataException {
		helpData = new HelpData();

		helpData.getChapterContent("gibts_nicht");
	}

}
