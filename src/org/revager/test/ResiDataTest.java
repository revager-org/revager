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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.revager.app.model.Data;
import org.revager.app.model.ResiData;
import org.revager.app.model.schema.Aspects;
import org.revager.app.model.schema.Catalog;
import org.revager.app.model.schema.Review;

/**
 * This class tests the ResiData class.
 * 
 * @author Johannes Wettinger
 * @version 1.0
 */
public class ResiDataTest {
	
	private ResiData resiData;

	@Test
	public void handleReview() {
		resiData = Data.getInstance().getResiData();
		Review exampleReview = new Review();
		
		assertEquals(null, resiData.getReview());
		assertEquals(null, resiData.getReviewPath());
		
		resiData.setReview(exampleReview);
		assertEquals(exampleReview, resiData.getReview());
		
		resiData.setReviewPath("/ein/pfad/zur review/datei.xml");
		assertEquals("/ein/pfad/zur review/datei.xml", resiData.getReviewPath());
		
		resiData.clearReview();
		
		assertEquals(null, resiData.getReview());
		assertEquals(null, resiData.getReviewPath());
	}
	
	@Test
	public void handleCatalog() {
		resiData = Data.getInstance().getResiData();
		Catalog exampleCatalog = new Catalog();
		
		assertEquals(null, resiData.getCatalog());
		assertEquals(null, resiData.getCatalogPath());
		
		resiData.setCatalog(exampleCatalog);
		assertEquals(exampleCatalog, resiData.getCatalog());
		
		resiData.setCatalogPath("/ein/pfad/zur review/datei.xml");
		assertEquals("/ein/pfad/zur review/datei.xml", resiData.getCatalogPath());
		
		resiData.clearCatalog();
		
		assertEquals(null, resiData.getCatalog());
		assertEquals(null, resiData.getCatalogPath());
	}
	
	@Test
	public void handleAspects() {
		resiData = Data.getInstance().getResiData();
		Aspects exampleAspects = new Aspects();
		
		assertEquals(null, resiData.getAspects());
		assertEquals(null, resiData.getAspectsPath());
		
		resiData.setAspects(exampleAspects);
		assertEquals(exampleAspects, resiData.getAspects());
		
		resiData.setAspectsPath("/ein/pfad/zur review/datei.xml");
		assertEquals("/ein/pfad/zur review/datei.xml", resiData.getAspectsPath());
		
		resiData.clearAspects();
		
		assertEquals(null, resiData.getAspects());
		assertEquals(null, resiData.getAspectsPath());
	}
	
	@Test
	public void tryToNotifyObservers() {
		resiData = Data.getInstance().getResiData();
		
		resiData.fireDataChanged();
	}
	
}
