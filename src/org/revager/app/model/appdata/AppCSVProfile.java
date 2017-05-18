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

import static org.revager.app.model.Data.translate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.revager.app.model.Data;
import org.revager.app.model.DataException;

/**
 * Instances of this class represent CSV profiles in the database.
 */
public class AppCSVProfile {

	/**
	 * The name of the CSV profile.
	 */
	private String name = null;

	/**
	 * This exception is thrown if the CSV profile could not be found.
	 */
	private DataException notFoundExc = new DataException(
			translate("CSV profile does not exist!") + " [NAME = " + this.name + "]");

	/**
	 * Internally used constructor to create a new instance of this class.
	 * 
	 * @param name
	 *            name of the CSV profile
	 */
	protected AppCSVProfile(String name) {
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
	 * If the name of two AppCSVProfile instances is equal, then they are
	 * declared as equal by this method.
	 * 
	 * @param obj
	 *            the CSV profile for comparation
	 * 
	 * @return true, if the name of the CSV profiles is equal
	 */
	@Override
	public boolean equals(Object obj) {
		AppCSVProfile otherProf = (AppCSVProfile) obj;
		boolean isEqual = false;

		if (this.name.equals(otherProf.getName())) {
			isEqual = true;
		}

		return isEqual;
	}

	/**
	 * Checks if the given CSV profile exists.
	 * 
	 * @return true, if the CSV profile exists
	 * 
	 * @throws DataException
	 *             If an error occurs while checking the existence of the CSV
	 *             profile
	 */
	public boolean exists() throws DataException {
		return Data.getInstance().getAppData().isCSVProfile(this.name);
	}

	/**
	 * Create a new instance of this class by the name of the CSV profile. If a
	 * profile with the given name does not exist in the database, a new one
	 * will be created.
	 * 
	 * @param name
	 *            of the CSV profile
	 * 
	 * @return CSV profile
	 * 
	 * @throws DataException
	 *             If an error occurs while creating a new instance of this
	 *             class.
	 */
	public static AppCSVProfile newInstance(String name) throws DataException {
		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn.prepareStatement("SELECT name FROM CSVProfiles " + "WHERE name = ?");

			ps.setString(1, name);

			ResultSet res = ps.executeQuery();

			/*
			 * If a profile with the given name does not exist
			 */
			if (!res.next()) {
				ps = conn.prepareStatement("INSERT INTO CSVProfiles "
						+ "(name, columnOrder, columnsInFirstLine, encapsulateContent) " + "VALUES (?, ?, ?, ?)");

				ps.setString(1, name);
				ps.setString(2, AppCSVColumnName.DESCRIPTION.toString() + "|" + AppCSVColumnName.REFERENCE.toString()
						+ "|" + AppCSVColumnName.SEVERITY.toString() + "|" + AppCSVColumnName.REPORTER.toString());
				ps.setBoolean(3, true);
				ps.setBoolean(4, true);

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
			throw new DataException(translate("Cannot add or get CSV profile.") + " [NAME = " + name + "] " + e.getMessage());
		}

		return new AppCSVProfile(name);
	}

	/**
	 * Returns the name of this CSV profile.
	 * 
	 * @return name of the CSV profile
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of this CSV profile.
	 * 
	 * @param name
	 *            of the CSV profile
	 * 
	 * @throws DataException
	 *             If an error occurs while setting CSV profile's name
	 */
	public void setName(String name) throws DataException {
		if (!exists()) {
			throw notFoundExc;
		}

		if (!this.name.equals(name)) {
			try {
				Connection conn = Data.getInstance().getAppData().openConnection();
				int result = 0;

				PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM CSVProfiles WHERE name=?");
				ps.setString(1, name);

				ResultSet res = ps.executeQuery();

				res.next();

				result = res.getInt(1);

				res.close();

				/*
				 * If there is no attendee with the given name
				 */
				if (result == 0) {
					ps = conn.prepareStatement("UPDATE CSVProfiles " + "SET name=? WHERE name=?");

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
				throw new DataException(
						translate("Cannot store name of the CSV profile.") + " [NAME = " + this.name + "] " + e.getMessage());
			}
		}
	}

	/**
	 * Returns the column order of this CSV profile.
	 * 
	 * @return list of column names
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the column order
	 */
	public List<AppCSVColumnName> getColumnOrder() throws DataException {
		List<AppCSVColumnName> colList = new ArrayList<AppCSVColumnName>();

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn.prepareStatement("SELECT columnOrder FROM CSVProfiles " + "WHERE name = ?");

			ps.setString(1, this.name);

			ResultSet res = ps.executeQuery();

			res.next();

			for (String colName : res.getString("columnOrder").split("\\|")) {
				colList.add(AppCSVColumnName.valueOf(colName));
			}

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(translate("Cannot get column order of the selected CSV profile.") + " [NAME = " + this.name
					+ "] " + e.getMessage());
		}

		return colList;
	}

	/**
	 * Sets the column order of this CSV profile.
	 * 
	 * @param colList
	 *            list of column names
	 * 
	 * @throws DataException
	 *             If an error occurs while setting the column order
	 */
	public void setColumnOrder(List<AppCSVColumnName> colList) throws DataException {
		if (!exists()) {
			throw notFoundExc;
		}

		/*
		 * Build a checklist to ensure the correctness of column order
		 */
		List<AppCSVColumnName> checkList = new ArrayList<AppCSVColumnName>();

		for (AppCSVColumnName col : AppCSVColumnName.values()) {
			checkList.add(col);
		}

		/*
		 * Create the column order string
		 */
		String colOrder = "";
		String separator = "";

		for (AppCSVColumnName col : colList) {
			checkList.remove(col);

			colOrder = colOrder + separator + col.toString();

			separator = "|";
		}

		try {
			if (checkList.isEmpty()) {
				Connection conn = Data.getInstance().getAppData().openConnection();

				PreparedStatement ps = conn.prepareStatement("UPDATE CSVProfiles " + "SET columnOrder=? WHERE name=?");

				ps.setString(1, colOrder);
				ps.setString(2, this.name);

				ps.executeUpdate();

				ps.close();
				conn.close();

				Data.getInstance().getAppData().fireDataChanged();
			} else {
				throw new DataException();
			}
		} catch (Exception e) {
			throw new DataException(translate("Cannot set column order of the selected CSV profile.") + " [NAME = " + this.name
					+ "] " + e.getMessage());
		}
	}

	/**
	 * Returns boolean value if the property showColumnsInFirstLine is set or
	 * not.
	 * 
	 * @return true if the property is set, otherwise false
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the value of the property
	 */
	public boolean isColsInFirstLine() throws DataException {
		boolean cfl = true;

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT columnsInFirstLine FROM CSVProfiles " + "WHERE name = ?");
			ps.setString(1, this.name);

			ResultSet res = ps.executeQuery();

			res.next();

			cfl = res.getBoolean("columnsInFirstLine");

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(translate("Cannot get the requested property of the selected CSV profile.") + " [NAME = "
					+ this.name + "] " + e.getMessage());
		}

		return cfl;
	}

	/**
	 * Sets the property showColumnsInFirstLine of this CSV profile.
	 * 
	 * @param cfl
	 *            value of the property
	 * 
	 * @throws DataException
	 *             If an error occurs while setting the property's value
	 */
	public void setColsInFirstLine(boolean cfl) throws DataException {
		if (!exists()) {
			throw notFoundExc;
		}

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("UPDATE CSVProfiles " + "SET columnsInFirstLine=? WHERE name=?");
			ps.setBoolean(1, cfl);
			ps.setString(2, this.name);

			ps.executeUpdate();

			ps.close();
			conn.close();

			Data.getInstance().getAppData().fireDataChanged();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(translate("Cannot store property of the selected CSV profile.") + " [NAME = " + this.name
					+ "] " + e.getMessage());
		}
	}

	/**
	 * Returns boolean value if the property encapsulateContent is set or not.
	 * 
	 * @return true if the property is set, otherwise false
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the value of the property
	 */
	public boolean isEncapsulateContent() throws DataException {
		boolean ec = true;

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT encapsulateContent FROM CSVProfiles " + "WHERE name = ?");
			ps.setString(1, this.name);

			ResultSet res = ps.executeQuery();

			res.next();

			ec = res.getBoolean("encapsulateContent");

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(translate("Cannot get the requested property of the selected CSV profile.") + " [NAME = "
					+ this.name + "] " + e.getMessage());
		}

		return ec;
	}

	/**
	 * Sets the property encapsulateContent of this CSV profile.
	 * 
	 * @param ec
	 *            value of the property
	 * 
	 * @throws DataException
	 *             If an error occurs while setting property's value
	 */
	public void setEncapsulateContent(boolean ec) throws DataException {
		if (!exists()) {
			throw notFoundExc;
		}

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("UPDATE CSVProfiles " + "SET encapsulateContent=? WHERE name=?");
			ps.setBoolean(1, ec);
			ps.setString(2, this.name);

			ps.executeUpdate();

			ps.close();
			conn.close();

			Data.getInstance().getAppData().fireDataChanged();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(translate("Cannot store property of the selected CSV profile.") + " [NAME = " + this.name
					+ "] " + e.getMessage());
		}
	}

	/**
	 * Returns the mapping for a given column. If there is no mapping, then null
	 * will be returned.
	 * 
	 * @param col
	 *            column of the requested mapping
	 * 
	 * @return the mapping for the given column; if no mapping exists, the
	 *         original column name will be returned
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the column mapping
	 */
	public String getColumnMapping(AppCSVColumnName col) throws DataException {
		String mapping = col.toString();

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn.prepareStatement(
					"SELECT columnMapping FROM CSVColumnMappings " + "WHERE profileName=? AND columnName=?");
			ps.setString(1, this.name);
			ps.setString(2, col.toString());

			ResultSet res = ps.executeQuery();

			if (res.next()) {
				mapping = res.getString("columnMapping");
			}

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(translate("Cannot get the requested property of the selected CSV profile.") + " [NAME = "
					+ this.name + "] " + e.getMessage());
		}

		return mapping;
	}

	/**
	 * Checks if a column mapping for the given column exists.
	 * 
	 * @param col
	 *            the col
	 * 
	 * @return true, if exist column mapping
	 * 
	 * @throws DataException
	 *             the data exception
	 */
	public boolean existColumnMapping(AppCSVColumnName col) throws DataException {
		boolean existMapping = false;

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn.prepareStatement(
					"SELECT columnMapping FROM CSVColumnMappings " + "WHERE profileName=? AND columnName=?");
			ps.setString(1, this.name);
			ps.setString(2, col.toString());

			ResultSet res = ps.executeQuery();

			if (res.next()) {
				existMapping = true;
			}

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(translate("Cannot get the requested property of the selected CSV profile.") + " [NAME = "
					+ this.name + "] " + e.getMessage());
		}

		return existMapping;
	}

	/**
	 * Sets a mapping for the given column.
	 * 
	 * @param col
	 *            column
	 * @param map
	 *            mapping
	 * 
	 * @throws DataException
	 *             If an error occurs while setting the column mapping
	 */
	public void setColumnMapping(AppCSVColumnName col, String map) throws DataException {
		if (!exists()) {
			throw notFoundExc;
		}

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps;

			if (existColumnMapping(col)) {
				ps = conn.prepareStatement(
						"UPDATE CSVColumnMappings " + "SET columnMapping=? " + "WHERE profileName=? AND columnName=?");
			} else {
				ps = conn.prepareStatement("INSERT INTO CSVColumnMappings "
						+ "(columnMapping, profileName, columnName) " + "VALUES (?, ?, ?)");
			}

			ps.setString(1, map);
			ps.setString(2, this.name);
			ps.setString(3, col.toString());

			ps.executeUpdate();

			ps.close();
			conn.close();

			Data.getInstance().getAppData().fireDataChanged();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(translate("Cannot store property of the selected CSV profile.") + " [NAME = " + this.name
					+ "] " + e.getMessage());
		}
	}

	/**
	 * Removes the mapping of the given column.
	 * 
	 * @param col
	 *            column
	 * 
	 * @throws DataException
	 *             If an error occurs while removing the column mapping
	 */
	public void removeColumnMapping(AppCSVColumnName col) throws DataException {
		if (!exists()) {
			throw notFoundExc;
		}

		try {
			if (getColumnMapping(col) != null) {
				Connection conn = Data.getInstance().getAppData().openConnection();

				PreparedStatement ps = conn
						.prepareStatement("DELETE FROM CSVColumnMappings " + "WHERE profileName=? AND columnName=?");

				ps.setString(1, this.name);
				ps.setString(2, col.toString());

				ps.executeUpdate();

				ps.close();
				conn.close();

				Data.getInstance().getAppData().fireDataChanged();
			}
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(translate("Cannot store property of the selected CSV profile.") + " [NAME = " + this.name
					+ "] " + e.getMessage());
		}
	}

	/**
	 * Returns the list of valid severity mapping of this CSV profile.
	 * 
	 * @return list of severity mappings
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the severity mappings
	 */
	public List<String> getValidSeverityMappings() throws DataException {
		List<String> mappings = new ArrayList<String>();

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT validMappings FROM CSVSeverityMappings " + "WHERE profileName = ?");
			ps.setString(1, this.name);

			ResultSet res = ps.executeQuery();

			if (res.next()) {
				for (String map : res.getString("validMappings").split("\\|")) {
					mappings.add(map);
				}
			}

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(translate("Cannot get the requested property of the selected CSV profile.") + " [NAME = "
					+ this.name + "] " + e.getMessage());
		}

		return mappings;
	}

	/**
	 * Sets the valid severity mappings for this CSV profile. One mapping can
	 * only occur once in the list.
	 * 
	 * @param mappings
	 *            list of severity mappings
	 * 
	 * @throws DataException
	 *             If an error occurs while setting the severity mappings
	 */
	public void setValidSeverityMappings(List<String> mappings) throws DataException {
		if (!exists()) {
			throw notFoundExc;
		}

		Set<String> mappingSet = new HashSet<String>();

		if (mappings == null) {
			mappings = new ArrayList<String>();
		}

		/*
		 * Prepare string of valid mappings
		 */
		String maps = "";
		String separator = "";

		for (String map : mappings) {
			/*
			 * To ensure that a mapping is added only once, the mappingSet is
			 * used for help
			 */
			if (mappingSet.add(map)) {
				maps = maps + separator + map.toString();
				separator = "|";
			}
		}

		try {
			Connection conn = Data.getInstance().getAppData().openConnection();

			PreparedStatement ps;

			if (mappings.isEmpty()) {
				ps = conn.prepareStatement("DELETE FROM CSVSeverityMappings " + "WHERE profileName=?");
				ps.setString(1, this.name);
			} else if (getValidSeverityMappings().isEmpty()) {
				ps = conn.prepareStatement(
						"INSERT INTO CSVSeverityMappings " + "(profileName, validMappings) VALUES (?, ?)");
				ps.setString(1, this.name);
				ps.setString(2, maps);
			} else {
				ps = conn.prepareStatement("UPDATE CSVSeverityMappings " + "SET validMappings=? WHERE profileName=?");
				ps.setString(1, maps);
				ps.setString(2, this.name);
			}

			ps.executeUpdate();

			ps.close();
			conn.close();

			Data.getInstance().getAppData().fireDataChanged();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(translate("Cannot store property of the selected CSV profile.") + " [NAME = " + this.name
					+ "] " + e.getMessage());
		}
	}

	/**
	 * Returns if the given mapping is a valid severity mapping.
	 * 
	 * @param mapping
	 *            to check
	 * 
	 * @return true if the the mapping is a valid severity mapping
	 * 
	 * @throws DataException
	 *             If an error occurs while checking the mapping
	 */
	public boolean isValidSeverityMapping(String mapping) throws DataException {
		return getValidSeverityMappings().contains(mapping);
	}

	/**
	 * Returns the index of the specified element in this list, or -1 if this
	 * list does not contain the element.
	 * 
	 * @param mapping
	 *            the mapping
	 * 
	 * @return index of the specified element
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the sorting position
	 */
	private int getSortPosOfSeverityMapping(String mapping) throws DataException {
		return getValidSeverityMappings().indexOf(mapping);
	}

	/**
	 * Returns constantly 0.
	 * 
	 * @return 0
	 */
	private int getFirstSortPosOfSeverityMappings() {
		return 0;
	}

	/**
	 * Returns the last index of the list of valid severity mappings.
	 * 
	 * @return last index of list of severity mappings
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the sorting position
	 */
	private int getLastSortPosOfSeverityMappings() throws DataException {
		return getValidSeverityMappings().size() - 1;
	}

	/**
	 * Pushes up the given severity mapping.
	 * 
	 * @param mapping
	 *            the mapping
	 * 
	 * @throws DataException
	 *             If an error occurs while pushing the severity mapping
	 */
	public void pushUpSeverityMapping(String mapping) throws DataException {
		if (!exists()) {
			throw notFoundExc;
		}

		List<String> mappings = getValidSeverityMappings();

		int sortPos = getSortPosOfSeverityMapping(mapping);

		if (sortPos > getFirstSortPosOfSeverityMappings() && isValidSeverityMapping(mapping)) {
			String otherMapping = mappings.get(sortPos - 1);
			mappings.set(sortPos - 1, mapping);
			mappings.set(sortPos, otherMapping);
		}

		setValidSeverityMappings(mappings);

		Data.getInstance().getAppData().fireDataChanged();
	}

	/**
	 * Pushes down the given severity mapping.
	 * 
	 * @param mapping
	 *            the mapping
	 * 
	 * @throws DataException
	 *             If an error occurs while pushing the severity mapping
	 */
	public void pushDownSeverityMapping(String mapping) throws DataException {
		if (!exists()) {
			throw notFoundExc;
		}

		List<String> mappings = getValidSeverityMappings();

		int sortPos = getSortPosOfSeverityMapping(mapping);

		if (sortPos < getLastSortPosOfSeverityMappings() && isValidSeverityMapping(mapping)) {
			String otherMapping = mappings.get(sortPos + 1);
			mappings.set(sortPos + 1, mapping);
			mappings.set(sortPos, otherMapping);
		}

		setValidSeverityMappings(mappings);

		Data.getInstance().getAppData().fireDataChanged();
	}

}
