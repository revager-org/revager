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
package org.revager.app.model.appdata;

import static org.revager.app.model.Data._;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.revager.app.model.ApplicationData.PushMode;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.schema.Aspect;

/**
 * Instances of this class represent aspects in the database.
 */
public class AppAspect {

	/**
	 * The id of the aspect.
	 */
	private int id = 0;

	/**
	 * The catalog to which the aspect belongs to.
	 */
	private AppCatalog catalog = null;

	/**
	 * Constructor to create a new instance of this class by the aspect id and
	 * its catalog.
	 * 
	 * @param id
	 *            id of the aspect
	 * @param catalog
	 *            the catalog
	 */
	public AppAspect(AppCatalog catalog, int id) {
		super();
		this.id = id;
		this.catalog = catalog;
	}

	/**
	 * Constructor to create a new aspect by the given parameters.
	 * 
	 * @param directive
	 *            directive of the aspect
	 * @param description
	 *            description of the aspect
	 * @param categoryName
	 *            category name of the aspect
	 * @param catalog
	 *            the catalog
	 * 
	 * @throws DataException
	 *             If an error occurs while creating the aspect
	 */
	public AppAspect(AppCatalog catalog, String directive, String description,
			String categoryName) throws DataException {
		super();

		this.catalog = catalog;

		int sortPos = catalog.getLastSortPosOfAspects(categoryName) + 1;

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			/*
			 * Add new aspect
			 */
			PreparedStatement ps = conn
					.prepareStatement("INSERT INTO Aspects "
							+ "(directive, description, categoryName, catalogName, sortPos) "
							+ "VALUES (?, ?, ?, ?, ?)");

			ps.setString(1, directive);
			ps.setString(2, description);
			ps.setString(3, categoryName);
			ps.setString(4, this.catalog.getName());
			ps.setInt(5, sortPos);

			ps.executeUpdate();

			/*
			 * Add category if it does not exist
			 */
			if (!catalog.isCategory(categoryName)) {
				ps = conn.prepareStatement("INSERT INTO Categories "
						+ "(name, catalogName, sortPos) " + "VALUES (?, ?, ?)");

				ps.setString(1, categoryName);
				ps.setString(2, this.catalog.getName());
				ps.setInt(3, this.catalog.getLastSortPosOfCategories() + 1);

				ps.executeUpdate();
			}

			/*
			 * Get ID of newly added AppAspect
			 */
			ps = conn.prepareStatement("SELECT id FROM Aspects "
					+ "WHERE sortPos = ? AND categoryName = ? "
					+ "AND catalogName = ?");

			ps.setInt(1, sortPos);
			ps.setString(2, categoryName);
			ps.setString(3, this.catalog.getName());

			ResultSet res = ps.executeQuery();

			res.next();

			this.id = res.getInt("id");

			res.close();
			ps.close();
			conn.close();

			Data.getInstance().getAppData().fireDataChanged();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(_("Cannot add new aspect.")
					+ " [DIRECTIVE = " + directive + "] " + e.getMessage());
		}
	}

	/**
	 * Returns a string representation of this object.
	 * 
	 * @return the string
	 */
	@Override
	public String toString() {
		try {
			return getDirective();
		} catch (DataException e) {
			return "";
		}
	}

	/**
	 * If the id of two AppAspect instances is equal, then they are declared as
	 * equal by this method.
	 * 
	 * @param obj
	 *            the aspect for comparation
	 * 
	 * @return true, the aspects are equal
	 */
	@Override
	public boolean equals(Object obj) {
		AppAspect otherAsp = (AppAspect) obj;
		boolean isEqual = false;

		if (this.getId() == otherAsp.getId()) {
			isEqual = true;
		}

		return isEqual;
	}

	/**
	 * Returns the id of this aspect.
	 * 
	 * @return id of the aspect
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Returns the directive of this aspect.
	 * 
	 * @return aspect directive
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the directive of this aspect
	 */
	public String getDirective() throws DataException {
		String directive = null;

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT directive FROM Aspects "
							+ "WHERE id = ?");
			ps.setInt(1, this.id);

			ResultSet res = ps.executeQuery();

			res.next();

			directive = res.getString("directive");

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			throw new DataException(_("Cannot read directive of the aspect.")
					+ " [ASPECT_ID = " + this.id + "] " + e.getMessage());
		}

		return directive;
	}

	/**
	 * Sets the directive of this aspect.
	 * 
	 * @param dir
	 *            aspect directive
	 * 
	 * @throws DataException
	 *             If an error occurs while setting the directive
	 */
	public void setDirective(String dir) throws DataException {
		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn.prepareStatement("UPDATE Aspects "
					+ "SET directive=? WHERE id=?");
			ps.setString(1, dir);
			ps.setInt(2, this.id);

			ps.executeUpdate();

			ps.close();
			conn.close();

			Data.getInstance().getAppData().fireDataChanged();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(_("Cannot store directive of the aspect.")
					+ " [ASPECT_ID = " + this.id + "] " + e.getMessage());
		}
	}

	/**
	 * Returns the description of this aspect.
	 * 
	 * @return aspect description
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the description
	 */
	public String getDescription() throws DataException {
		String description = null;

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT description FROM Aspects "
							+ "WHERE id = ?");
			ps.setInt(1, this.id);

			ResultSet res = ps.executeQuery();

			res.next();

			description = res.getString("description");

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(_("Cannot read description of the aspect.")
					+ " [ASPECT_ID = " + this.id + "] " + e.getMessage());
		}

		return description;
	}

	/**
	 * Sets the description of this aspect.
	 * 
	 * @param desc
	 *            aspect description
	 * 
	 * @throws DataException
	 *             If an error occurs while setting the aspect description
	 */
	public void setDescription(String desc) throws DataException {
		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn.prepareStatement("UPDATE Aspects "
					+ "SET description=? WHERE id=?");
			ps.setString(1, desc);
			ps.setInt(2, this.id);

			ps.executeUpdate();

			ps.close();
			conn.close();

			Data.getInstance().getAppData().fireDataChanged();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(
					_("Cannot store description of the aspect.")
							+ " [ASPECT_ID = " + this.id + "] "
							+ e.getMessage());
		}
	}

	/**
	 * Gets the catalog.
	 * 
	 * @return the catalog
	 * 
	 * @throws DataException
	 *             the data exception
	 */
	public AppCatalog getCatalog() throws DataException {
		AppCatalog catalog = null;

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT catalogName FROM Aspects "
							+ "WHERE id = ?");
			ps.setInt(1, this.id);

			ResultSet res = ps.executeQuery();

			res.next();

			catalog = Data.getInstance().getAppData()
					.getCatalog(res.getString("catalogName"));

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException();
		}

		return catalog;
	}

	/**
	 * Returns the category of this aspect.
	 * 
	 * @return aspect category
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the category of this aspect
	 */
	public String getCategory() throws DataException {
		String category = null;

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT categoryName FROM Aspects "
							+ "WHERE id = ?");
			ps.setInt(1, this.id);

			ResultSet res = ps.executeQuery();

			res.next();

			category = res.getString("categoryName");

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(_("Cannot read category of the aspect.")
					+ " [ASPECT_ID = " + this.id + "] " + e.getMessage());
		}

		return category;
	}

	/**
	 * Sets the category of this aspect.
	 * 
	 * @param cat
	 *            aspect category
	 * 
	 * @throws DataException
	 *             If an error occurs while setting the category of this aspect
	 */
	public void setCategory(String cat) throws DataException {
		String currentCat = getCategory();

		if (!currentCat.equals(cat)) {
			try {
				Connection conn = Data.getInstance().getAppData()
						.openConnection();

				PreparedStatement ps;

				/*
				 * Add new category
				 */
				if (this.catalog.isCategory(cat) == false) {
					ps = conn.prepareStatement("INSERT INTO Categories "
							+ "(name, catalogName, sortPos) VALUES (?, ?, ?)");
					ps.setString(1, cat);
					ps.setString(2, this.catalog.getName());
					ps.setInt(3, this.catalog.getLastSortPosOfCategories() + 1);
					ps.executeUpdate();
				}

				/*
				 * Update aspect
				 */
				ps = conn.prepareStatement("UPDATE Aspects "
						+ "SET categoryName=?, sortPos=? WHERE id=?");
				ps.setString(1, cat);
				ps.setInt(2, this.catalog.getLastSortPosOfAspects(cat) + 1);
				ps.setInt(3, this.id);
				ps.executeUpdate();

				/*
				 * Remove old category
				 */
				if (this.catalog.getNumberOfAspects(currentCat) == 0) {
					ps = conn.prepareStatement("DELETE FROM Categories "
							+ "WHERE name = ? AND catalogName = ?");
					ps.setString(1, currentCat);
					ps.setString(2, catalog.getName());
					ps.executeUpdate();
				}

				ps.close();
				conn.close();

				Data.getInstance().getAppData().fireDataChanged();
			} catch (Exception e) {
				/*
				 * Not part of the unit testing, because this exception is only
				 * thrown if there occurs an internal error.
				 */
				throw new DataException(
						_("Cannot store category of the aspect.")
								+ " [ASPECT_ID = " + this.id + "] "
								+ e.getMessage());
			}
		}
	}

	/**
	 * Pushes this aspect depending on the given push mode.
	 * 
	 * @param mode
	 *            push mode
	 * 
	 * @throws DataException
	 *             If an error occurs while pushing the aspect
	 */
	private void pushAspect(PushMode mode) throws DataException {
		int curSortPos = getSortPos();
		int pushSortPos = 0;
		boolean doPush = false;

		if (mode == PushMode.UP) {
			pushSortPos = getSortPosOfPredecessor();
			doPush = curSortPos > catalog
					.getFirstSortPosOfAspects(getCategory());
		} else if (mode == PushMode.DOWN) {
			pushSortPos = getSortPosOfSuccessor();
			doPush = curSortPos < catalog
					.getLastSortPosOfAspects(getCategory());
		}

		if (doPush) {
			try {
				Connection conn = Data.getInstance().getAppData()
						.openConnection();
				conn.setAutoCommit(false);

				/*
				 * Update sorting position of the other element
				 */
				PreparedStatement ps = conn
						.prepareStatement("UPDATE Aspects "
								+ "SET sortPos=? WHERE sortPos=? AND catalogName=? AND categoryName=?");
				ps.setInt(1, curSortPos);
				ps.setInt(2, pushSortPos);
				ps.setString(3, catalog.getName());
				ps.setString(4, getCategory());
				ps.executeUpdate();

				/*
				 * Update sorting position of this element
				 */
				ps = conn.prepareStatement("UPDATE Aspects "
						+ "SET sortPos=? WHERE id=?");
				ps.setInt(1, pushSortPos);
				ps.setInt(2, id);
				ps.executeUpdate();

				conn.commit();

				ps.close();
				conn.close();

				Data.getInstance().getAppData().fireDataChanged();
			} catch (Exception e) {
				/*
				 * Not part of the unit testing, because this exception is only
				 * thrown if there occurs an internal error.
				 */
				throw new DataException(_("Cannot move selected aspect.")
						+ " [ASPECT_ID = " + this.id + "] " + e.getMessage());
			}
		}
	}

	/**
	 * Pushes the aspect one step up.
	 * 
	 * @throws DataException
	 *             If an error occurs while pushing the aspect
	 */
	public void pushUp() throws DataException {
		pushAspect(PushMode.UP);
	}

	/**
	 * Pushes the aspect one step down.
	 * 
	 * @throws DataException
	 *             If an error occurs while pushing the aspect
	 */
	public void pushDown() throws DataException {
		pushAspect(PushMode.DOWN);
	}

	/**
	 * Pushes the aspect to the top.
	 * 
	 * @throws DataException
	 *             If an error occurs while pushing the aspect
	 */
	public void pushTop() throws DataException {
		while (getSortPos() > catalog.getFirstSortPosOfAspects(getCategory())) {
			pushUp();
		}
	}

	/**
	 * Pushes the aspect to the bottom.
	 * 
	 * @throws DataException
	 *             If an error occurs while pushing the aspect
	 */
	public void pushBottom() throws DataException {
		while (getSortPos() < catalog.getLastSortPosOfAspects(getCategory())) {
			pushDown();
		}
	}

	/**
	 * Returns the sorting position of this aspect.
	 * 
	 * @return sorting position
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the sorting position
	 */
	public int getSortPos() throws DataException {
		int sortPos = 0;

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT sortPos FROM Aspects "
							+ "WHERE id = ?");
			ps.setInt(1, this.id);

			ResultSet res = ps.executeQuery();

			if (res.next()) {
				sortPos = res.getInt("sortPos");
			}

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(
					_("Cannot get sorting position of the aspect.")
							+ " [ASPECT_ID = " + this.id + "] "
							+ e.getMessage());
		}

		return sortPos;
	}

	/**
	 * Returns the sorting position of the predecessor.
	 * 
	 * @return sorting position
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the sorting position
	 */
	private int getSortPosOfPredecessor() throws DataException {
		int sortPos = 0;

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT sortPos FROM Aspects "
							+ "WHERE sortPos < ? AND categoryName = ? AND catalogName = ? "
							+ "ORDER BY sortPos DESC");
			ps.setInt(1, getSortPos());
			ps.setString(2, getCategory());
			ps.setString(3, catalog.getName());
			ps.setMaxRows(1);

			ResultSet res = ps.executeQuery();

			if (res.next()) {
				sortPos = res.getInt("sortPos");
			}

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(
					_("Cannot get sorting position of the aspect.")
							+ " [ASPECT_ID = " + this.id + "] "
							+ e.getMessage());
		}

		return sortPos;
	}

	/**
	 * Returns the sorting position of the successor.
	 * 
	 * @return sorting position
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the sorting position
	 */
	private int getSortPosOfSuccessor() throws DataException {
		int sortPos = 0;

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT sortPos FROM Aspects "
							+ "WHERE sortPos > ? AND categoryName = ? AND catalogName = ? "
							+ "ORDER BY sortPos ASC");
			ps.setInt(1, getSortPos());
			ps.setString(2, getCategory());
			ps.setString(3, catalog.getName());
			ps.setMaxRows(1);

			ResultSet res = ps.executeQuery();

			if (res.next()) {
				sortPos = res.getInt("sortPos");
			}

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(
					_("Cannot get sorting position of the aspect.")
							+ " [ASPECT_ID = " + this.id + "] "
							+ e.getMessage());
		}

		return sortPos;
	}

	/**
	 * Returns this AppAspect instance as an instance of the Aspect class (Resi
	 * XML schema). The id is null and has to be set!
	 * 
	 * @return instance of Aspect class
	 * 
	 * @throws DataException
	 *             If an error occurs while converting from AppAspect to Aspect
	 */
	public Aspect getAsResiAspect() throws DataException {
		Aspect asp = new Aspect();

		asp.setCategory(this.getCategory());
		asp.setDescription(this.getDescription());
		asp.setDirective(this.getDirective());
		asp.setId(null);

		return asp;
	}

}
