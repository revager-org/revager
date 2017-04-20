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
import java.util.ArrayList;
import java.util.List;

import org.revager.app.model.ApplicationData.PushMode;
import org.revager.app.model.Data;
import org.revager.app.model.DataException;

/**
 * Instances of this class represent catalogs in the database.
 */
public class AppCatalog {

	/**
	 * The name of the catalog.
	 */
	private String name = null;

	/**
	 * This exception is thrown if the catalog could not be found.
	 */
	private DataException notFoundExc = new DataException(
			_("Catalog doesn't exists!") + " [NAME = " + this.name + "]");

	/**
	 * Internally used contructor.
	 * 
	 * @param name
	 *            the name
	 */
	protected AppCatalog(String name) {
		super();
		this.name = name;
	}

	/**
	 * Returns a string representation of this object.
	 * 
	 * @return the string
	 */
	@Override
	public String toString() {
		return this.name;
	}

	/**
	 * If the name of two catalogs is equal, then they are declared as equal by
	 * this method.
	 * 
	 * @param obj
	 *            the catalog for comparation
	 * 
	 * @return true, if the catalogs are equal
	 */
	@Override
	public boolean equals(Object obj) {
		AppCatalog otherCat = (AppCatalog) obj;
		boolean isEqual = false;

		if (this.name.equals(otherCat.getName())) {
			isEqual = true;
		}

		return isEqual;
	}

	/**
	 * Checks if this catalog exists in the database.
	 * 
	 * @return true, if the catalog exists
	 * 
	 * @throws DataException
	 *             If an error occurs while checking the existence of the
	 *             catalog
	 */
	public boolean exists() throws DataException {
		return Data.getInstance().getAppData().isCatalog(this.name);
	}

	/**
	 * Creates a new instance of this class. If a catalog with the given name
	 * does not exist, then a new one will be created in the database.
	 * 
	 * @param name
	 *            of the catalog
	 * 
	 * @return instance of this class
	 * 
	 * @throws DataException
	 *             If an error occurs while creating a new instance
	 */
	public static AppCatalog newInstance(String name) throws DataException {
		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT name FROM Catalogs "
							+ "WHERE name = ?");

			ps.setString(1, name);

			ResultSet res = ps.executeQuery();

			/*
			 * If a catalog with the given name does not exist
			 */
			if (!res.next()) {
				ps = conn.prepareStatement("INSERT INTO Catalogs "
						+ "(name, description, sortPos) VALUES (?, ?, ?)");

				ps.setString(1, name);
				ps.setString(2, "");
				ps.setInt(3, Data.getInstance().getAppData()
						.getLastSortPosOfCatalogs() + 1);

				ps.executeUpdate();

				Data.getInstance().getAppData().fireDataChanged();
			}

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(_("Cannot add or get catalog.")
					+ " [NAME = " + name + "] " + e.getMessage());
		}

		return new AppCatalog(name);
	}

	/**
	 * Returns the name of a catalog.
	 * 
	 * @return name of the catalog
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the catalog.
	 * 
	 * @param name
	 *            of the catalog
	 * 
	 * @throws DataException
	 *             If an error occurs while setting the name
	 */
	public void setName(String name) throws DataException {
		if (!exists()) {
			throw notFoundExc;
		}

		if (!this.name.equals(name)) {
			try {
				Connection conn = Data.getInstance().getAppData()
						.openConnection();
				int result = 0;

				PreparedStatement ps = conn
						.prepareStatement("SELECT COUNT(*) FROM Catalogs WHERE name=?");
				ps.setString(1, name);

				ResultSet res = ps.executeQuery();

				res.next();

				result = res.getInt(1);

				res.close();

				/*
				 * If there exists no catalog with the given name
				 */
				if (result == 0) {
					ps = conn.prepareStatement("UPDATE Catalogs "
							+ "SET name=? WHERE name=?");

					ps.setString(1, name);
					ps.setString(2, this.name);

					ps.executeUpdate();

					ps.close();

					ps = conn.prepareStatement("UPDATE Categories "
							+ "SET catalogName=? WHERE catalogName=?");

					ps.setString(1, name);
					ps.setString(2, this.name);

					ps.executeUpdate();

					ps.close();

					ps = conn.prepareStatement("UPDATE Aspects "
							+ "SET catalogName=? WHERE catalogName=?");

					ps.setString(1, name);
					ps.setString(2, this.name);

					ps.executeUpdate();

					ps.close();
					conn.close();

					this.name = name;

					Data.getInstance().getAppData().fireDataChanged();
				} else {
					ps.close();
					conn.close();

					throw new DataException();
				}
			} catch (Exception e) {
				throw new DataException(_("Cannot set catalog name.")
						+ " [NAME = " + this.name + "]");
			}
		}
	}

	/**
	 * Returns the description of this catalog.
	 * 
	 * @return description of catalog
	 * 
	 * @throws DataException
	 *             If an error occurs while reading the description from the
	 *             database.
	 */
	public String getDescription() throws DataException {
		String description = null;

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT description FROM Catalogs WHERE name=?");
			ps.setString(1, this.name);

			ResultSet res = ps.executeQuery();

			if (res.next()) {
				description = res.getString("description");
			}

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			throw new DataException();
		}

		return description;
	}

	/**
	 * Sets the description of this catalog.
	 * 
	 * @param desc
	 *            description of this catalog
	 * 
	 * @throws DataException
	 *             If an error occurs while writing the description into the
	 *             database.
	 */
	public void setDescription(String desc) throws DataException {
		if (!exists()) {
			throw notFoundExc;
		}

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn.prepareStatement("UPDATE Catalogs "
					+ "SET description=? WHERE name=?");

			ps.setString(1, desc);
			ps.setString(2, this.name);

			ps.executeUpdate();

			ps.close();
			conn.close();

			Data.getInstance().getAppData().fireDataChanged();
		} catch (Exception e) {
			throw new DataException();
		}
	}

	/**
	 * Pushes the catalog dependent on the given push mode.
	 * 
	 * @param mode
	 *            to push
	 * 
	 * @throws DataException
	 *             If an error occurs while pushing the catalog
	 */
	private void pushCatalog(PushMode mode) throws DataException {
		if (!exists()) {
			throw notFoundExc;
		}

		int curSortPos = this.getSortPos();
		int pushSortPos = 0;
		boolean doPush = false;

		if (mode == PushMode.UP) {
			pushSortPos = getSortPosOfPredecessor();
			doPush = getSortPos() > Data.getInstance().getAppData()
					.getFirstSortPosOfCatalogs();
		} else if (mode == PushMode.DOWN) {
			pushSortPos = getSortPosOfSuccessor();
			doPush = getSortPos() < Data.getInstance().getAppData()
					.getLastSortPosOfCatalogs();
		}

		if (doPush) {
			try {
				Connection conn = Data.getInstance().getAppData()
						.openConnection();
				conn.setAutoCommit(false);

				/*
				 * Update sorting position of the other catalog
				 */
				PreparedStatement ps = conn.prepareStatement("UPDATE Catalogs "
						+ "SET sortPos=? WHERE sortPos=?");
				ps.setInt(1, curSortPos);
				ps.setInt(2, pushSortPos);
				ps.executeUpdate();

				/*
				 * Update sorting position of this catalog
				 */
				ps = conn.prepareStatement("UPDATE Catalogs "
						+ "SET sortPos=? WHERE name=?");
				ps.setInt(1, pushSortPos);
				ps.setString(2, this.name);
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
				throw new DataException(_("Cannot move the selected catalog.")
						+ " [NAME = " + this.name + "] " + e.getMessage());
			}
		}
	}

	/**
	 * Pushes the catalog one step up.
	 * 
	 * @throws DataException
	 *             If an error occurs while pushing the catalog
	 */
	public void pushUp() throws DataException {
		pushCatalog(PushMode.UP);
	}

	/**
	 * Pushes the catalog one step down.
	 * 
	 * @throws DataException
	 *             If an error occurs while pushing the catalog
	 */
	public void pushDown() throws DataException {
		pushCatalog(PushMode.DOWN);
	}

	/**
	 * Pushes the catalog to the top of the list.
	 * 
	 * @throws DataException
	 *             If an error occurs while pushing the catalog
	 */
	public void pushTop() throws DataException {
		while (this.getSortPos() > Data.getInstance().getAppData()
				.getFirstSortPosOfCatalogs()) {
			pushUp();
		}
	}

	/**
	 * Pushes the catalog to the bottom of the list.
	 * 
	 * @throws DataException
	 *             If an error occurs while pushing the catalog
	 */
	public void pushBottom() throws DataException {
		while (this.getSortPos() < Data.getInstance().getAppData()
				.getLastSortPosOfCatalogs()) {
			pushDown();
		}
	}

	/**
	 * Returns the sorting position of the catalog.
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
					.prepareStatement("SELECT sortPos FROM Catalogs "
							+ "WHERE name = ?");
			ps.setString(1, this.name);

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
					_("Cannot get the sorting position of the catalogs.")
							+ " [NAME = " + this.name + "] " + e.getMessage());
		}

		return sortPos;
	}

	/**
	 * Get the sorting position of the predecessor catalog.
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
					.prepareStatement("SELECT sortPos FROM Catalogs "
							+ "WHERE sortPos < ? ORDER BY sortPos DESC");
			ps.setInt(1, getSortPos());
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
					_("Cannot get the sorting position of the catalogs.")
							+ " [NAME = " + this.name + "] " + e.getMessage());
		}

		return sortPos;
	}

	/**
	 * Get the sorting position of the successor catalog.
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
					.prepareStatement("SELECT sortPos FROM Catalogs "
							+ "WHERE sortPos > ? ORDER BY sortPos ASC");
			ps.setInt(1, getSortPos());
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
					_("Cannot get the sorting position of the catalogs.")
							+ " [NAME = " + this.name + "] " + e.getMessage());
		}

		return sortPos;
	}

	/**
	 * Get the sorting position of the given category.
	 * 
	 * @param category
	 *            the category
	 * 
	 * @return sorting position
	 * 
	 * @throws DataException
	 *             If an error occurs while the sorting position
	 */
	public int getSortPosCategory(String category) throws DataException {
		int sortPos = 0;

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT sortPos FROM Categories "
							+ "WHERE name = ? AND catalogName = ?");
			ps.setString(1, category);
			ps.setString(2, this.name);

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
					_("Cannot get sorting position of the category.")
							+ " [NAME = " + category + "] " + e.getMessage());
		}

		return sortPos;
	}

	/**
	 * Get the sorting position of the predecessor of the given category.
	 * 
	 * @param category
	 *            the category
	 * 
	 * @return sorting position
	 * 
	 * @throws DataException
	 *             If an error occurs while the sorting position
	 */
	private int getSortPosCategoryOfPre(String category) throws DataException {
		int sortPos = 0;

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT sortPos FROM Categories "
							+ "WHERE sortPos < ? AND catalogName = ? "
							+ "ORDER BY sortPos DESC");
			ps.setInt(1, getSortPosCategory(category));
			ps.setString(2, this.name);
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
					_("Cannot get sorting position of the category.")
							+ " [NAME = " + category + "] " + e.getMessage());
		}

		return sortPos;
	}

	/**
	 * Get the sorting position of the successor of the given category.
	 * 
	 * @param category
	 *            the category
	 * 
	 * @return sorting position
	 * 
	 * @throws DataException
	 *             If an error occurs while the sorting position
	 */
	private int getSortPosCategoryOfSuc(String category) throws DataException {
		int sortPos = 0;

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT sortPos FROM Categories "
							+ "WHERE sortPos > ? AND catalogName = ? "
							+ "ORDER BY sortPos ASC");
			ps.setInt(1, getSortPosCategory(category));
			ps.setString(2, this.name);
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
					_("Cannot get sorting position of the category.")
							+ " [NAME = " + category + "] " + e.getMessage());
		}

		return sortPos;
	}

	/**
	 * Returns the first sorting position of categories of this catalog.
	 * 
	 * @return sorting position
	 * 
	 * @throws DataException
	 *             If an error occurs while the sorting position
	 */
	public int getFirstSortPosOfCategories() throws DataException {
		int sortPos = 0;

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT sortPos FROM Categories "
							+ "WHERE catalogName = ? " + "ORDER BY sortPos ASC");
			ps.setString(1, this.name);
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
					_("Cannot get first sorting position of the categories.")
							+ " [NAME = " + this.name + "] " + e.getMessage());
		}

		return sortPos;
	}

	/**
	 * Returns the last sorting position of categories of this catalog.
	 * 
	 * @return sorting position
	 * 
	 * @throws DataException
	 *             If an error occurs while the sorting position
	 */
	public int getLastSortPosOfCategories() throws DataException {
		int sortPos = 0;

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT sortPos FROM Categories "
							+ "WHERE catalogName = ? "
							+ "ORDER BY sortPos DESC");
			ps.setString(1, this.name);
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
					_("Cannot get last sorting position of the categories.")
							+ " [NAME = " + this.name + "] " + e.getMessage());
		}

		return sortPos;
	}

	/**
	 * Pushes the given category with the given push mode.
	 * 
	 * @param category
	 *            to push
	 * @param mode
	 *            the mode
	 * 
	 * @throws DataException
	 *             If an error occurs while pushing the category
	 */
	private void pushCategory(String category, PushMode mode)
			throws DataException {
		if (!exists()) {
			throw notFoundExc;
		}

		int curSortPos = getSortPosCategory(category);
		int pushSortPos = 0;
		boolean doPush = false;

		if (mode == PushMode.UP) {
			pushSortPos = getSortPosCategoryOfPre(category);
			doPush = curSortPos > getFirstSortPosOfCategories();
		} else if (mode == PushMode.DOWN) {
			pushSortPos = getSortPosCategoryOfSuc(category);
			doPush = curSortPos < getLastSortPosOfCategories();
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
						.prepareStatement("UPDATE Categories "
								+ "SET sortPos=? WHERE sortPos=? AND catalogName=?");
				ps.setInt(1, curSortPos);
				ps.setInt(2, pushSortPos);
				ps.setString(3, this.name);
				ps.executeUpdate();

				/*
				 * Update sorting position of this element
				 */
				ps = conn.prepareStatement("UPDATE Categories "
						+ "SET sortPos=? WHERE name=? AND catalogName=?");
				ps.setInt(1, pushSortPos);
				ps.setString(2, category);
				ps.setString(3, this.name);
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
				throw new DataException(_("Cannot move the selected category.")
						+ " [NAME = " + category + "] " + e.getMessage());
			}
		}
	}

	/**
	 * Pushes the given category one step up.
	 * 
	 * @param category
	 *            the category
	 * 
	 * @throws DataException
	 *             If an error occurs while pushing the category
	 */
	public void pushUpCategory(String category) throws DataException {
		pushCategory(category, PushMode.UP);
	}

	/**
	 * Pushes the given category one step down.
	 * 
	 * @param category
	 *            the category
	 * 
	 * @throws DataException
	 *             If an error occurs while pushing the category
	 */
	public void pushDownCategory(String category) throws DataException {
		pushCategory(category, PushMode.DOWN);
	}

	/**
	 * Pushes the given category to the top.
	 * 
	 * @param category
	 *            the category
	 * 
	 * @throws DataException
	 *             If an error occurs while pushing the category
	 */
	public void pushTopCategory(String category) throws DataException {
		while (getSortPosCategory(category) > getFirstSortPosOfCategories()) {
			pushUpCategory(category);
		}
	}

	/**
	 * Pushes the given category to the bottom.
	 * 
	 * @param category
	 *            the category
	 * 
	 * @throws DataException
	 *             If an error occurs while pushing the category
	 */
	public void pushBottomCategory(String category) throws DataException {
		while (getSortPosCategory(category) < getLastSortPosOfCategories()) {
			pushDownCategory(category);
		}
	}

	/**
	 * Returns the number of categories of this catalog.
	 * 
	 * @return number of categories
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the number of categories
	 */
	public int getNumberOfCategories() throws DataException {
		int numberOfCategories = 0;

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT COUNT(*) FROM Categories WHERE catalogName=?");
			ps.setString(1, this.name);

			ResultSet res = ps.executeQuery();

			res.next();

			numberOfCategories = res.getInt(1);

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(_("Cannot get the number of categories.")
					+ " [NAME = " + this.name + "] " + e.getMessage());
		}

		return numberOfCategories;
	}

	/**
	 * Returns the list of categories of this catalog.
	 * 
	 * @return list of categories
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the categories
	 */
	public List<String> getCategories() throws DataException {
		List<String> categories = new ArrayList<String>();

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT name FROM Categories "
							+ "WHERE catalogName = ? ORDER BY sortPos ASC");
			ps.setString(1, this.name);

			ResultSet res = ps.executeQuery();

			while (res.next()) {
				categories.add(res.getString("name"));
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
					_("Cannot get category of the selected catalog.")
							+ " [NAME = " + this.name + "] " + e.getMessage());
		}

		return categories;
	}

	/**
	 * Returns a list of the categories which fit to the given filter.
	 * 
	 * @param filter
	 *            to search for
	 * 
	 * @return list of categories
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the categories
	 */
	public List<String> getCategories(String filter) throws DataException {
		List<String> categories = getCategories();
		List<String> categoriesFiltered = new ArrayList<String>();

		for (String cat : categories) {
			if (cat.toLowerCase().contains(filter.toLowerCase())) {
				categoriesFiltered.add(cat);
			}
		}

		return categoriesFiltered;
	}

	/**
	 * Returns true if the given category exists.
	 * 
	 * @param name
	 *            of the category
	 * 
	 * @return true if category exists
	 * 
	 * @throws DataException
	 *             If an error occurs while
	 */
	public boolean isCategory(String name) throws DataException {
		boolean isCat = false;

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT name FROM Categories "
							+ "WHERE name=? AND catalogName=?");
			ps.setString(1, name);
			ps.setString(2, this.name);

			ResultSet res = ps.executeQuery();

			if (res.next()) {
				isCat = true;
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
					_("Cannot detect wether the given category exists or not.")
							+ " [NAME = " + name + "] " + e.getMessage());
		}

		return isCat;
	}

	/**
	 * Updates tha name of the given category.
	 * 
	 * @param oldName
	 *            of the category
	 * @param newName
	 *            of the category
	 * 
	 * @throws DataException
	 *             If an error occurs while updating the name of the category
	 */
	public void editCategory(String oldName, String newName)
			throws DataException {
		/*
		 * If the name didn't change.
		 */
		if (oldName.trim().equals(newName.trim())) {
			return;
		}
		
		if (!exists()) {
			throw notFoundExc;
		}

		try {
			/*
			 * If the given new name for the category does not exist
			 */
			if (!isCategory(newName)) {
				Connection conn = Data.getInstance().getAppData()
						.openConnection();
				conn.setAutoCommit(false);

				PreparedStatement ps = conn
						.prepareStatement("UPDATE Categories "
								+ "SET name=? WHERE name=? AND catalogName=?");
				ps.setString(1, newName);
				ps.setString(2, oldName);
				ps.setString(3, this.name);
				ps.executeUpdate();

				ps = conn.prepareStatement("UPDATE Aspects "
						+ "SET categoryName=? WHERE categoryName=? "
						+ "AND catalogName=?");
				ps.setString(1, newName);
				ps.setString(2, oldName);
				ps.setString(3, this.name);
				ps.executeUpdate();

				conn.commit();

				ps.close();
				conn.close();

				Data.getInstance().getAppData().fireDataChanged();
			} else {
				throw new DataException();
			}
		} catch (Exception e) {
			throw new DataException(_("Cannot change category.") + " [NAME = "
					+ oldName + "] " + e.getMessage());
		}
	}

	/**
	 * Returns the first sorting position of the category of this catalog.
	 * 
	 * @param category
	 *            the category
	 * 
	 * @return sorting position
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the sorting position
	 */
	public int getFirstSortPosOfAspects(String category) throws DataException {
		int sortPos = 0;

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT sortPos FROM Aspects "
							+ "WHERE categoryName = ? AND catalogName = ? "
							+ "ORDER BY sortPos ASC");
			ps.setString(1, category);
			ps.setString(2, this.name);
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
					_("Cannot get first sorting position of the aspects.")
							+ " [NAME = " + category + "] " + e.getMessage());
		}

		return sortPos;
	}

	/**
	 * Returns the last sorting position of the category of this catalog.
	 * 
	 * @param category
	 *            the category
	 * 
	 * @return sorting position
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the sorting position
	 */
	public int getLastSortPosOfAspects(String category) throws DataException {
		int sortPos = 0;

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT sortPos FROM Aspects "
							+ "WHERE categoryName = ? AND catalogName = ? "
							+ "ORDER BY sortPos DESC");
			ps.setString(1, category);
			ps.setString(2, this.name);
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
					_("Cannot get last sorting position of the aspects.")
							+ " [NAME = " + category + "] " + e.getMessage());
		}

		return sortPos;
	}

	/**
	 * Returns the number of aspects of the given category.
	 * 
	 * @param category
	 *            the category
	 * 
	 * @return number of aspects
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the number of aspects
	 */
	public int getNumberOfAspects(String category) throws DataException {
		int numberOfAspects = 0;

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps;

			if (category == null) {
				ps = conn.prepareStatement("SELECT COUNT(*) FROM Aspects "
						+ "WHERE catalogName=?");
				ps.setString(1, this.name);
			} else {
				ps = conn.prepareStatement("SELECT COUNT(*) FROM Aspects "
						+ "WHERE catalogName=? AND categoryName=?");
				ps.setString(1, this.name);
				ps.setString(2, category);
			}

			ResultSet res = ps.executeQuery();

			res.next();

			numberOfAspects = res.getInt(1);

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(_("Cannot get the number of aspects.")
					+ " [NAME = " + this.name + "] " + e.getMessage());
		}

		return numberOfAspects;
	}

	/**
	 * Returns the number of all aspects in this catalog.
	 * 
	 * @return number of aspects
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the number of aspects
	 */
	public int getNumberOfAspects() throws DataException {
		return getNumberOfAspects(null);
	}

	/**
	 * Returns a list of aspects which are part of the given category.
	 * 
	 * @param category
	 *            the category
	 * 
	 * @return list of aspects
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the aspects
	 */
	public List<AppAspect> getAspects(String category) throws DataException {
		List<AppAspect> aspects = new ArrayList<AppAspect>();

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT id FROM Aspects "
							+ "WHERE catalogName = ? AND categoryName = ? "
							+ "ORDER BY sortPos ASC");
			ps.setString(1, this.name);
			ps.setString(2, category);

			ResultSet res = ps.executeQuery();

			while (res.next()) {
				aspects.add(new AppAspect(this, res.getInt("id")));
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
					_("Cannot get aspects of the selected catalog / category.")
							+ " [NAME = " + this.name + "] " + e.getMessage());
		}

		return aspects;
	}

	/**
	 * Returns a list of aspects which are part of the given category and fit to
	 * the given filter.
	 * 
	 * @param category
	 *            the category
	 * @param filter
	 *            to search for
	 * 
	 * @return list of aspects
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the aspects
	 */
	public List<AppAspect> getAspects(String category, String filter)
			throws DataException {
		List<AppAspect> aspects = getAspects(category);
		List<AppAspect> aspectsFiltered = new ArrayList<AppAspect>();

		for (AppAspect asp : aspects) {
			if (asp.getDirective().toLowerCase().contains(filter.toLowerCase())
					|| asp.getDescription().toLowerCase()
							.contains(filter.toLowerCase())) {
				aspectsFiltered.add(asp);
			}
		}

		return aspectsFiltered;
	}

	/**
	 * Returns the aspect with the given id.
	 * 
	 * @param id
	 *            the id
	 * 
	 * @return aspect
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the aspect
	 */
	public AppAspect getAspect(int id) throws DataException {
		return new AppAspect(this, id);
	}

	/**
	 * Create and returns a new aspect by the given parameters.
	 * 
	 * @param directive
	 *            of the aspect
	 * @param description
	 *            of the aspect
	 * @param category
	 *            of the aspect
	 * 
	 * @return aspect
	 * 
	 * @throws DataException
	 *             If an error occurs while creating the aspect
	 */
	public AppAspect newAspect(String directive, String description,
			String category) throws DataException {
		if (!exists()) {
			throw notFoundExc;
		}

		return new AppAspect(this, directive, description, category);
	}

	/**
	 * Remove the given aspect from the database.
	 * 
	 * @param aspect
	 *            the aspect
	 * 
	 * @throws DataException
	 *             If an error occurs while removing the aspect
	 */
	public void removeAspect(AppAspect aspect) throws DataException {
		if (!exists()) {
			throw notFoundExc;
		}

		String category = aspect.getCategory();

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn.prepareStatement("DELETE FROM Aspects "
					+ "WHERE id = ?");
			ps.setInt(1, aspect.getId());
			ps.executeUpdate();

			if (getNumberOfAspects(category) == 0) {
				ps = conn.prepareStatement("DELETE FROM Categories "
						+ "WHERE name = ? AND catalogName = ?");
				ps.setString(1, category);
				ps.setString(2, this.name);
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
			throw new DataException(_("Cannot remove aspect.")
					+ " [ASPECT_ID = " + aspect.getId() + "] " + e.getMessage());
		}
	}

	/**
	 * Removes the given category and all its aspects.
	 * 
	 * @param category
	 *            the category
	 * 
	 * @throws DataException
	 *             If an error occurs while removing the category
	 */
	public void removeCategory(String category) throws DataException {
		if (!exists()) {
			throw notFoundExc;
		}

		for (AppAspect asp : getAspects(category)) {
			removeAspect(asp);
		}
	}

}
