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
package org.revager.app.model;

import java.util.Observable;

import org.revager.app.model.schema.Aspects;
import org.revager.app.model.schema.Catalog;
import org.revager.app.model.schema.Review;

/**
 * This class is a sub-class of Data an handles all data concerning the Resi XML
 * schema.
 */
public class ResiData extends Observable {

	/**
	 * Instance of the Review class (XML schema).
	 */
	private Review review = null;

	/**
	 * Instance of the Catalog class (XML schema).
	 */
	private Catalog catalog = null;

	/**
	 * Instance of the Aspects class (XML schema).
	 */
	private Aspects aspects = null;

	/**
	 * The path to the currently loaded review.
	 */
	private String reviewPath = null;

	/**
	 * The path to the currently loaded catalog.
	 */
	private String catalogPath = null;

	/**
	 * The path to the currently loaded aspects.
	 */
	private String aspectsPath = null;

	/**
	 * Get Aspects object (Resi XML schema).
	 * 
	 * @return the aspects
	 */
	public Aspects getAspects() {
		return aspects;
	}

	/**
	 * Set Aspects object (Resi XML schema).
	 * 
	 * @param aspects
	 *            the aspects to set
	 */
	public void setAspects(Aspects aspects) {
		this.aspects = aspects;
	}

	/**
	 * Clear Aspects object and its path to null.
	 */
	public void clearAspects() {
		aspects = null;
		aspectsPath = null;
	}

	/**
	 * Get Catalog object (Resi XML schema).
	 * 
	 * @return the catalog
	 */
	public Catalog getCatalog() {
		return catalog;
	}

	/**
	 * Set Catalog object (Resi XML schema).
	 * 
	 * @param catalog
	 *            the catalog to set
	 */
	public void setCatalog(Catalog catalog) {
		this.catalog = catalog;
	}

	/**
	 * Clear Catalog object and its path to null.
	 */
	public void clearCatalog() {
		catalog = null;
		catalogPath = null;
	}

	/**
	 * Get Review object (Resi XML schema).
	 * 
	 * @return the review
	 */
	public Review getReview() {
		return review;
	}

	/**
	 * Set Review object (Resi XML schema).
	 * 
	 * @param review
	 *            the review to set
	 */
	public void setReview(Review review) {
		this.review = review;
	}

	/**
	 * Clear Review object and its path to null.
	 */
	public void clearReview() {
		review = null;
		reviewPath = null;
	}

	/**
	 * If something changed in the ResiData model, this method should be
	 * invoked.
	 */
	public void fireDataChanged() {
		setChanged();
		notifyObservers();
	}

	/**
	 * Get the file path of currently loaded Aspects object.
	 * 
	 * @return the aspectsPath
	 */
	public String getAspectsPath() {
		return aspectsPath;
	}

	/**
	 * Set the file path of currently loaded Aspects object.
	 * 
	 * @param aspectsPath
	 *            the aspectsPath to set
	 */
	public void setAspectsPath(String aspectsPath) {
		if (aspectsPath.trim().equals("")) {
			aspectsPath = null;
		}

		this.aspectsPath = aspectsPath;
	}

	/**
	 * Get the file path of currently loaded Catalog object.
	 * 
	 * @return the catalogPath
	 */
	public String getCatalogPath() {
		return catalogPath;
	}

	/**
	 * Set the file path of currently loaded Catalog object.
	 * 
	 * @param catalogPath
	 *            the catalogPath to set
	 */
	public void setCatalogPath(String catalogPath) {
		if (catalogPath.trim().equals("")) {
			catalogPath = null;
		}

		this.catalogPath = catalogPath;
	}

	/**
	 * Get the file path of currently loaded Review object.
	 * 
	 * @return the reviewPath
	 */
	public String getReviewPath() {
		return reviewPath;
	}

	/**
	 * Set the file path of currently loaded Review object.
	 * 
	 * @param reviewPath
	 *            the reviewPath to set
	 */
	public void setReviewPath(String reviewPath) {
		if (reviewPath.trim().equals("")) {
			reviewPath = null;
		}

		this.reviewPath = reviewPath;
	}

}
