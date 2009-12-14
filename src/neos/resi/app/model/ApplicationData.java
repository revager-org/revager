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
package neos.resi.app.model;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.SortedSet;
import java.util.TreeSet;

import neos.resi.app.model.appdata.AppAspect;
import neos.resi.app.model.appdata.AppAttendee;
import neos.resi.app.model.appdata.AppCSVColumnName;
import neos.resi.app.model.appdata.AppCSVProfile;
import neos.resi.app.model.appdata.AppCatalog;
import neos.resi.app.model.appdata.AppSettingKey;
import neos.resi.app.model.appdata.AppSettingValue;
import neos.resi.tools.FileTools;

/**
 * This class is a sub-class of Data an handles the application data, internally
 * stored in a JavaDB.
 */
public class ApplicationData extends Observable {

	/**
	 * Push mode values for the methods for pushing catalogs, categories and
	 * aspects.
	 */
	public static enum PushMode {
		UP, DOWN;
	}

	/**
	 * Indicates the current version of the database (structure); it is
	 * important for later database (structure) updates.
	 */
	private final int CURRENT_DATABASE_VERSION = 1;

	/**
	 * This attribute indicates if the application data are initialized or not.
	 */
	private boolean appDataInitialized = false;

	/**
	 * Attribute to save the path of the application data.
	 */
	private String appDataPath = null;

	/**
	 * If a custum path for the application data is given, it is saved here.
	 */
	private String customAppDataDirectory = null;

	/**
	 * Initialization of the application data.
	 * 
	 * @throws DataException
	 *             If an error occurs while the initialization of application
	 *             data
	 */
	public void initialize() throws DataException {
		if (appDataInitialized == false) {
			/*
			 * Find a location to store the database
			 */
			boolean dbLocationFound = false;

			/*
			 * Possible paths to search for, to save the application data
			 * automatically.
			 */
			String currentDirectory = new File(ApplicationData.class
					.getProtectionDomain().getCodeSource().getLocation()
					.getPath()).getAbsolutePath();

			int endIndex = currentDirectory.lastIndexOf(File.separator);

			String[] possibleDirectories = {
					System.getProperty("user.home") + File.separator,
					currentDirectory = currentDirectory.substring(0,
							endIndex + 1),
					System.getProperty("user.dir") + File.separator };

			/*
			 * Check if a custom directory is given and valid
			 */
			if (customAppDataDirectory != null
					&& new File(customAppDataDirectory).canWrite()) {
				dbLocationFound = true;

				appDataPath = customAppDataDirectory
						+ Data.getInstance().getResource("dataDirectoryName");
			}

			/*
			 * Check if database is existing;
			 * 
			 * not part of the unit testing because in the unit testing, we use
			 * a custom directory for the database.
			 */
			for (String dir : possibleDirectories) {
				if (new File(dir
						+ Data.getInstance().getResource("dataDirectoryName"))
						.exists()
						&& dbLocationFound == false) {
					dbLocationFound = true;

					appDataPath = dir
							+ Data.getInstance().getResource(
									"dataDirectoryName");
				}
			}

			/*
			 * Check where to create new database;
			 * 
			 * not part of the unit testing because in the unit testing, we use
			 * a custom directory for the database.
			 */
			for (String dir : possibleDirectories) {
				if (new File(dir).canWrite() && dbLocationFound == false) {
					dbLocationFound = true;

					appDataPath = dir
							+ Data.getInstance().getResource(
									"dataDirectoryName");
				}
			}

			System.setProperty("derby.system.home", appDataPath + "db"
					+ File.separator);

			appDataInitialized = true;

			/*
			 * Create tables in the database if the do not exist
			 */
			try {
				createTables();
			} catch (SQLException e) {
				/*
				 * Not part of the unit testing, because this exception is only
				 * thrown if there occurs an internal error while creating the
				 * tables.
				 */
				throw new DataException(Data.getInstance().getLocaleStr(
						"message.sqlTableCreationFailed")
						+ " [ERROR = "
						+ e.getErrorCode()
						+ " "
						+ e.getMessage() + "]");
			}

			fireDataChanged();
		}
	}

	/**
	 * Opens the database connection and initialize the database if necessary.
	 * 
	 * @return Connection to the database
	 * 
	 * @throws DataException
	 *             if there is an error while opening and initializing the
	 *             database
	 */
	public Connection openConnection() throws DataException {
		Connection c = null;

		/*
		 * Initialize application data
		 */
		initialize();

		/*
		 * Open the connection to the database
		 */
		try {
			c = DriverManager.getConnection("jdbc:derby:Resi;create=true");
		} catch (SQLException e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(Data.getInstance().getLocaleStr(
					"message.dbOpenFailed"));
		}

		return c;
	}

	/**
	 * Resets the database, that means delete all application data.
	 */
	public void resetDatabase() {
		if (appDataInitialized == true) {
			FileTools.deleteDirectory(new File(appDataPath));

			appDataInitialized = false;
			appDataPath = null;
		}
	}

	/**
	 * Returns the path of the currently used application data.
	 * 
	 * @return path to application data
	 */
	public String getAppDataPath() {
		if (appDataPath == null) {
			return "";
		} else {
			return appDataPath;
		}
	}

	/**
	 * Returns the custom path to the application data.
	 * 
	 * @return custom path to application data
	 */
	public String getCustomAppDataDirectory() {
		return customAppDataDirectory;
	}

	/**
	 * Set the custom path to the application data.
	 * 
	 * @param customDatabasePath
	 *            the custom database path
	 */
	public void setCustomAppDataDirectory(String customDatabasePath) {
		customDatabasePath = customDatabasePath.replace("\\", "/");

		if (!customDatabasePath.endsWith("/")) {
			customDatabasePath = customDatabasePath + "/";
		}

		this.customAppDataDirectory = customDatabasePath;
	}

	/**
	 * Creates tables in the database if they does not exist.
	 * 
	 * @throws DataException
	 *             If an error while the creation of tables occurs
	 * @throws SQLException
	 *             If an error while running the SQL statements occurs
	 */
	private void createTables() throws DataException, SQLException {
		Connection conn = openConnection();

		String[] names = { "TABLE" };
		SortedSet<String> tables = new TreeSet<String>();

		String tableName = null;

		/*
		 * Get existing tables;
		 * 
		 * normally the createTables() method is only invoked if you have a
		 * clean and empty database, so not tables exists; so the following
		 * statements are not part of the unit testing.
		 */
		DatabaseMetaData meta = conn.getMetaData();
		ResultSet tableNames = meta.getTables(null, null, null, names);

		while (tableNames.next()) {
			tables.add(tableNames.getString("TABLE_NAME").toUpperCase());
		}

		tableNames.close();

		/*
		 * Table LastReviews
		 */
		tableName = "LastReviews";

		if (!tables.contains(tableName.toUpperCase())) {
			PreparedStatement ps = conn
					.prepareStatement("CREATE TABLE "
							+ tableName
							+ " ( filePath VARCHAR(150) NOT NULL PRIMARY KEY, time BIGINT NOT NULL )");

			ps.executeUpdate();
			ps.close();
		}

		/*
		 * Table ReviewAttendees
		 */
		tableName = "Attendees";

		if (!tables.contains(tableName.toUpperCase())) {
			PreparedStatement ps = conn
					.prepareStatement("CREATE TABLE "
							+ tableName
							+ " ( name VARCHAR(100) NOT NULL, contact VARCHAR(1000) NOT NULL,"
							+ " PRIMARY KEY(name,contact) )");

			ps.executeUpdate();
			ps.close();
		}

		/*
		 * Table AttendeesStrenghts
		 */
		tableName = "AttendeesStrengths";

		if (!tables.contains(tableName.toUpperCase())) {
			PreparedStatement ps = conn
					.prepareStatement("CREATE TABLE "
							+ tableName
							+ " ( attendeeName VARCHAR(100) NOT NULL, attendeeContact VARCHAR(1000) NOT NULL, categoryName VARCHAR(100) NOT NULL,"
							+ " PRIMARY KEY(attendeeName,attendeeContact,categoryName) )");

			ps.executeUpdate();
			ps.close();
		}

		/*
		 * Table Catalogs
		 */
		tableName = "Catalogs";

		if (!tables.contains(tableName.toUpperCase())) {
			PreparedStatement ps = conn
					.prepareStatement("CREATE TABLE "
							+ tableName
							+ " ( name VARCHAR(100) NOT NULL PRIMARY KEY, description VARCHAR(10000) NOT NULL, sortPos INTEGER NOT NULL )");

			ps.executeUpdate();
			ps.close();
		}

		/*
		 * Table Categories
		 */
		tableName = "Categories";

		if (!tables.contains(tableName.toUpperCase())) {
			PreparedStatement ps = conn
					.prepareStatement("CREATE TABLE "
							+ tableName
							+ " ( name VARCHAR(100) NOT NULL, catalogName VARCHAR(100) NOT NULL, sortPos INTEGER NOT NULL,"
							+ " PRIMARY KEY(name,catalogName) )");

			ps.executeUpdate();
			ps.close();
		}

		/*
		 * Table Aspects
		 */
		tableName = "Aspects";

		if (!tables.contains(tableName.toUpperCase())) {
			PreparedStatement ps = conn
					.prepareStatement("CREATE TABLE "
							+ tableName
							+ " ( id INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
							+ " directive VARCHAR(500) NOT NULL, description VARCHAR(3000), categoryName VARCHAR(100) NOT NULL, catalogName VARCHAR(100) NOT NULL, sortPos INTEGER NOT NULL )");

			ps.executeUpdate();
			ps.close();
		}

		/*
		 * Table ApplicationSettings
		 */
		tableName = "ApplicationSettings";

		if (!tables.contains(tableName.toUpperCase())) {
			PreparedStatement ps = conn
					.prepareStatement("CREATE TABLE "
							+ tableName
							+ " ( settingKey VARCHAR(100) NOT NULL PRIMARY KEY, settingValue VARCHAR(3000) NOT NULL )");

			ps.executeUpdate();
			ps.close();

			insertDefaultsApplicationSettings();
		}

		/*
		 * Table CSVProfiles
		 */
		tableName = "CSVProfiles";

		if (!tables.contains(tableName.toUpperCase())) {
			PreparedStatement ps = conn
					.prepareStatement("CREATE TABLE "
							+ tableName
							+ " ( name VARCHAR(100) NOT NULL PRIMARY KEY,"
							+ " columnOrder VARCHAR(200) NOT NULL, columnsInFirstLine VARCHAR(10) NOT NULL, encapsulateContent VARCHAR(10) NOT NULL )");

			ps.executeUpdate();
			ps.close();

			insertDefaultsCSVProfiles();
		}

		/*
		 * Table CSVColumnMappings
		 */
		tableName = "CSVColumnMappings";

		if (!tables.contains(tableName.toUpperCase())) {
			PreparedStatement ps = conn
					.prepareStatement("CREATE TABLE "
							+ tableName
							+ " ( profileName VARCHAR(100) NOT NULL, columnName VARCHAR(100) NOT NULL, columnMapping VARCHAR(100) NOT NULL, PRIMARY KEY(profileName,columnName) )");

			ps.executeUpdate();
			ps.close();

			insertDefaultsCSVColumnMappings();
		}

		/*
		 * Table CSVSeverityMappings
		 */
		tableName = "CSVSeverityMappings";

		if (!tables.contains(tableName.toUpperCase())) {
			PreparedStatement ps = conn.prepareStatement("CREATE TABLE "
					+ tableName
					+ " ( profileName VARCHAR(100) NOT NULL PRIMARY KEY,"
					+ " validMappings VARCHAR(1000) NOT NULL )");

			ps.executeUpdate();
			ps.close();

			insertDefaultsCSVSeverityMappings();
		}

		/*
		 * Database connection will be closed
		 */
		conn.close();
	}

	/**
	 * This method sets the default values for the application settings for a
	 * newly created database.
	 * 
	 * @throws DataException
	 *             If an error occurs while setting the default values
	 * @throws SQLException
	 *             If an error while running the SQL statements occurs
	 */
	private void insertDefaultsApplicationSettings() throws DataException,
			SQLException {
		Connection conn = openConnection();
		PreparedStatement ps;

		/*
		 * Table ApplicationSettings
		 */
		ps = conn.prepareStatement("INSERT INTO ApplicationSettings "
				+ "(settingKey, settingValue) VALUES (?, ?)");

		ps.setString(1, AppSettingKey.DATABASE_VERSION.toString());
		ps.setInt(2, CURRENT_DATABASE_VERSION);
		ps.executeUpdate();

		ps.setString(1, AppSettingKey.APP_LANGUAGE.toString());
		ps.setString(2, Data.getInstance().getResource("appDefaultLang"));
		ps.executeUpdate();

		ps.setString(1, AppSettingKey.APP_DO_AUTO_SAVE.toString());
		ps.setString(2, AppSettingValue.TRUE.toString());
		ps.executeUpdate();

		ps.setString(1, AppSettingKey.APP_AUTO_SAVE_INTERVAL.toString());
		ps.setInt(2, 10);
		ps.executeUpdate();

		ps.setString(1, AppSettingKey.APP_CHECK_VERSION.toString());
		ps.setString(2, AppSettingValue.TRUE.toString());
		ps.executeUpdate();

		ps.setString(1, AppSettingKey.APP_SHOW_HINTS.toString());
		ps.setString(2, AppSettingValue.TRUE.toString());
		ps.executeUpdate();

		ps.setString(1, AppSettingKey.APP_HIGHLIGHT_FIELDS.toString());
		ps.setString(2, AppSettingValue.TRUE.toString());
		ps.executeUpdate();

		ps.setString(1, AppSettingKey.APP_SHOW_PROTOCOL_WARNING.toString());
		ps.setString(2, AppSettingValue.TRUE.toString());
		ps.executeUpdate();

		ps.setString(1, AppSettingKey.APP_PROTOCOL_WARNING_TIME.toString());
		ps.setInt(2, 120);
		ps.executeUpdate();

		ps.setString(1, AppSettingKey.APP_FONT_SIZE.toString());
		ps.setString(2, AppSettingValue.NORMAL.toString());
		ps.executeUpdate();

		ps.setString(1, AppSettingKey.PDF_PROTOCOL_SHOW_SIGN_FIELDS.toString());
		ps.setString(2, AppSettingValue.FALSE.toString());
		ps.executeUpdate();

		ps.setString(1, AppSettingKey.PDF_INVITATION_TEXT.toString());
		ps.setString(2, Data.getInstance().getLocaleStr(
				"export.standardInvitationText"));
		ps.executeUpdate();

		/*
		 * Close the statement and the connection
		 */
		ps.close();
		conn.close();
	}

	/**
	 * This method sets the default CSV profile for the bug tracking system of
	 * Trac.
	 * 
	 * @throws DataException
	 *             If an error occurs while setting the default values
	 * @throws SQLException
	 *             If an error while running the SQL statements occurs
	 */
	private void insertDefaultsCSVProfiles() throws DataException, SQLException {
		Connection conn = openConnection();
		PreparedStatement ps;

		/*
		 * Table CSVProfiles
		 */
		ps = conn
				.prepareStatement("INSERT INTO CSVProfiles "
						+ "(name, columnOrder, columnsInFirstLine, encapsulateContent) "
						+ "VALUES (?, ?, ?, ?)");

		ps.setString(1, "Trac");
		ps.setString(2, AppCSVColumnName.DESCRIPTION.toString() + "|"
				+ AppCSVColumnName.REFERENCE.toString() + "|"
				+ AppCSVColumnName.SEVERITY.toString() + "|"
				+ AppCSVColumnName.REPORTER.toString());
		ps.setBoolean(3, true);
		ps.setBoolean(4, false);
		ps.executeUpdate();

		/*
		 * Close the statement and the connection
		 */
		ps.close();
		conn.close();
	}

	/**
	 * This method sets the column mappings for the default CSV profile Trac.
	 * 
	 * @throws DataException
	 *             If an error occurs while setting the default values
	 * @throws SQLException
	 *             If an error while running the SQL statements occurs
	 */
	private void insertDefaultsCSVColumnMappings() throws DataException,
			SQLException {
		Connection conn = openConnection();
		PreparedStatement ps;

		ps = conn.prepareStatement("INSERT INTO CSVColumnMappings "
				+ " (profileName, columnName, columnMapping) VALUES (?, ?, ?)");

		ps.setString(1, "Trac");
		ps.setString(2, AppCSVColumnName.DESCRIPTION.toString());
		ps.setString(3, "summary");
		ps.executeUpdate();

		ps.setString(1, "Trac");
		ps.setString(2, AppCSVColumnName.REFERENCE.toString());
		ps.setString(3, "description");
		ps.executeUpdate();

		ps.setString(1, "Trac");
		ps.setString(2, AppCSVColumnName.SEVERITY.toString());
		ps.setString(3, "priority");
		ps.executeUpdate();

		ps.setString(1, "Trac");
		ps.setString(2, AppCSVColumnName.REPORTER.toString());
		ps.setString(3, "reporter");
		ps.executeUpdate();

		/*
		 * Close the statement and the connection
		 */
		ps.close();
		conn.close();
	}

	/**
	 * This method sets the severity mappings for the CSV profile Trac.
	 * 
	 * @throws DataException
	 *             If an error occurs while setting the default values
	 * @throws SQLException
	 *             If an error while running the SQL statements occurs
	 */
	private void insertDefaultsCSVSeverityMappings() throws DataException,
			SQLException {
		Connection conn = openConnection();
		PreparedStatement ps;

		ps = conn.prepareStatement("INSERT INTO CSVSeverityMappings "
				+ " (profileName, validMappings) VALUES (?, ?)");

		ps.setString(1, "Trac");
		ps.setString(2, "critical|major|minor");
		ps.executeUpdate();

		/*
		 * Close the statement and the connection
		 */
		ps.close();
		conn.close();
	}

	/**
	 * If something changed in the application data model, this method should be
	 * invoked.
	 */
	public void fireDataChanged() {
		setChanged();
		notifyObservers();
	}

	/**
	 * Returns the application setting of the given key as String.
	 * 
	 * @param key
	 *            key of the application setting
	 * 
	 * @return value of the application setting
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the value from the database
	 */
	public String getSetting(AppSettingKey key) throws DataException {
		String setting = null;

		try {
			Connection conn = openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT settingValue FROM ApplicationSettings "
							+ "WHERE settingKey = ?");

			ps.setString(1, key.toString());

			ResultSet res = ps.executeQuery();

			if (res.next()) {
				setting = res.getString("settingValue");
			}

			res.close();
			ps.close();

			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(Data.getInstance().getLocaleStr(
					"message.getAppSettingFailed")
					+ " [SETTING = " + key + "] " + e.getMessage());
		}

		return setting;
	}

	/**
	 * Returns the application setting of the given key as AppSettingValue.
	 * 
	 * @param key
	 *            key of the application setting
	 * 
	 * @return value of the application setting
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the value from the database
	 */
	public AppSettingValue getSettingValue(AppSettingKey key)
			throws DataException {
		AppSettingValue setting = null;
		String settingStr = getSetting(key);

		try {
			if (settingStr != null) {
				setting = AppSettingValue.valueOf(settingStr);
			}
		} catch (IllegalArgumentException e) {
			setting = AppSettingValue.INVALID_VALUE;
		}

		return setting;
	}

	/**
	 * Sets the application setting by the given key and value (String).
	 * 
	 * @param key
	 *            key of the application setting
	 * @param value
	 *            value of the application setting
	 * 
	 * @throws DataException
	 *             If an error occurs while setting the value from the database
	 */
	public void setSetting(AppSettingKey key, String value)
			throws DataException {
		try {
			Connection conn = openConnection();
			PreparedStatement ps = null;

			if (getSetting(key) == null) {
				ps = conn.prepareStatement("INSERT INTO ApplicationSettings "
						+ "(settingValue, settingKey) VALUES (?, ?)");
			} else {
				ps = conn.prepareStatement("UPDATE ApplicationSettings "
						+ "SET settingValue=? WHERE settingKey=?");
			}

			ps.setString(1, value);
			ps.setString(2, key.toString());

			ps.executeUpdate();

			ps.close();
			conn.close();

			fireDataChanged();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(Data.getInstance().getLocaleStr(
					"message.setAppSettingFailed")
					+ " [SETTING = " + key + "] " + e.getMessage());
		}
	}

	/**
	 * Sets the application setting by the given key and value
	 * (AppSettingValue).
	 * 
	 * @param key
	 *            key of the application setting
	 * @param value
	 *            value of the application setting
	 * 
	 * @throws DataException
	 *             If an error occurs while setting the value from the database
	 */
	public void setSettingValue(AppSettingKey key, AppSettingValue value)
			throws DataException {
		setSetting(key, value.toString());
	}

	/**
	 * Get the paths of the last opened reviews as list.
	 * 
	 * @return list of review paths
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the paths
	 */
	public List<String> getLastReviews() throws DataException {
		List<String> lastReviews = new ArrayList<String>();

		try {
			Connection conn = openConnection();

			/*
			 * Get reviews
			 */
			PreparedStatement ps = conn
					.prepareStatement("SELECT filePath FROM LastReviews "
							+ "ORDER BY time DESC");
			ps.setMaxRows(Integer.parseInt(Data.getInstance().getResource(
					"numberOfLastReviews")));

			ResultSet res = ps.executeQuery();

			while (res.next()) {
				lastReviews.add(res.getString("filePath"));
			}

			res.close();
			ps.close();

			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(Data.getInstance().getLocaleStr(
					"message.getLastReviewsFailed")
					+ " " + e.getMessage());
		}

		return lastReviews;
	}

	/**
	 * Adds the path of a new review.
	 * 
	 * @param filePath
	 *            file path to add to the list of reviews
	 * 
	 * @throws DataException
	 *             If an error occurs while adding a path to the list of last
	 *             opened reviews
	 */
	public void addLastReview(String filePath) throws DataException {
		try {
			Connection conn = openConnection();

			/*
			 * 70 days ago from now
			 */
			long timeAgo = Calendar.getInstance().getTimeInMillis()
					- (70 * 24 * 60 * 60 * 1000);

			/*
			 * Remove old reviews
			 */
			PreparedStatement ps = conn
					.prepareStatement("DELETE FROM LastReviews WHERE time < ?");

			ps.setLong(1, timeAgo);

			ps.executeUpdate();

			ps.close();

			/*
			 * Insert new review or just update time
			 */
			if (getLastReviews().contains(filePath)) {
				ps = conn.prepareStatement("UPDATE LastReviews "
						+ "SET time=? WHERE filePath=?");
			} else {
				ps = conn.prepareStatement("INSERT INTO LastReviews "
						+ "(time, filePath) VALUES (?, ?)");
			}

			ps.setLong(1, Calendar.getInstance().getTimeInMillis());
			ps.setString(2, filePath);

			ps.executeUpdate();

			ps.close();
			conn.close();

			fireDataChanged();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(Data.getInstance().getLocaleStr(
					"message.addLastReviewFailed")
					+ " (" + filePath + ") " + e.getMessage());
		}
	}

	/**
	 * Removes the given path from the list of opened reviews.
	 * 
	 * @param filePath
	 *            file path to remove from the list
	 * 
	 * @throws DataException
	 *             If an error occurs while removing
	 */
	public void removeLastReview(String filePath) throws DataException {
		try {
			Connection conn = openConnection();

			PreparedStatement ps = conn
					.prepareStatement("DELETE FROM LastReviews "
							+ "WHERE filePath = ?");

			ps.setString(1, filePath);

			ps.executeUpdate();

			ps.close();
			conn.close();

			fireDataChanged();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(Data.getInstance().getLocaleStr(
					"message.removeLastReviewFailed")
					+ " (" + filePath + ") " + e.getMessage());
		}
	}

	/**
	 * Returns the list of catalogs saved in the application data.
	 * 
	 * @return list of catalogs
	 * 
	 * @throws DataException
	 *             If an error occurs while reading the catalogs from the
	 *             application data
	 */
	public List<AppCatalog> getCatalogs() throws DataException {
		List<AppCatalog> catalogs = new ArrayList<AppCatalog>();

		try {
			Connection conn = openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT name FROM Catalogs "
							+ "ORDER BY sortPos ASC");

			ResultSet res = ps.executeQuery();

			while (res.next()) {
				catalogs.add(AppCatalog.newInstance(res.getString("name")));
			}

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(Data.getInstance().getLocaleStr(
					"message.getAppCatalogsFailed")
					+ " " + e.getMessage());
		}

		return catalogs;
	}

	/**
	 * Returns a list of all catalogs which fit to the given filter.
	 * 
	 * @param filter
	 *            filter to search for
	 * 
	 * @return list of catalogs
	 * 
	 * @throws DataException
	 *             If an error occurs while reading the catalogs from the
	 *             application data
	 */
	public List<AppCatalog> getCatalogs(String filter) throws DataException {
		List<AppCatalog> catalogs = getCatalogs();
		List<AppCatalog> catalogsFiltered = new ArrayList<AppCatalog>();

		for (AppCatalog cat : catalogs) {
			if (cat.getName().toLowerCase().contains(filter.toLowerCase())) {
				catalogsFiltered.add(cat);
			}
		}

		return catalogsFiltered;
	}

	/**
	 * Returns the catalog with the given name. If a catalog with the given name
	 * does not exist, then null will be returned.
	 * 
	 * @param name
	 *            name of the catalog
	 * 
	 * @return the catalog with the given name
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the catalog
	 */
	public AppCatalog getCatalog(String name) throws DataException {
		AppCatalog catalog = null;

		try {
			Connection conn = openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT name FROM Catalogs "
							+ " WHERE name = ?");

			ps.setString(1, name);

			ResultSet res = ps.executeQuery();

			if (res.next()) {
				catalog = AppCatalog.newInstance(res.getString("name"));
			}

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(Data.getInstance().getLocaleStr(
					"message.getAppCatalogFailed")
					+ " [NAME = " + name + "] " + e.getMessage());
		}

		return catalog;
	}

	/**
	 * Checks if the given catalog exists.
	 * 
	 * @param name
	 *            the name
	 * 
	 * @return true, if checks if is catalog
	 * 
	 * @throws DataException
	 *             If an error occurs while checking if the catalog exists
	 */
	public boolean isCatalog(String name) throws DataException {
		return getCatalog(name) != null;
	}

	/**
	 * Create a new AppCatalog object. If a catalog with the given name does not
	 * exist, a new one will be created in the database.
	 * 
	 * @param name
	 *            of the catalog
	 * 
	 * @return catalog with the given name
	 * 
	 * @throws DataException
	 *             If an error occurs while creating a new AppCatalog object
	 */
	public AppCatalog newCatalog(String name) throws DataException {
		return AppCatalog.newInstance(name);
	}

	/**
	 * Copies the given catalog.
	 * 
	 * @param origCatalog
	 *            the original catalog
	 * @param copCatName
	 *            the name for the copyied catalog
	 * 
	 * @return the copied catalog
	 * 
	 * @throws DataException
	 *             If an error occurs while copying an AppCatalog object
	 */
	public AppCatalog copyCatalog(AppCatalog origCatalog, String copCatName)
			throws DataException {
		AppCatalog newCatalog = newCatalog(copCatName);

		for (String category : origCatalog.getCategories()) {
			for (AppAspect aspect : origCatalog.getAspects(category)) {
				newCatalog.newAspect(aspect.getDirective(), aspect
						.getDescription(), aspect.getCategory());
			}
		}

		fireDataChanged();

		return newCatalog;
	}

	/**
	 * Sorts the catalogs by the bubble sort algorithm.
	 * 
	 * @throws DataException
	 *             If an error occurs while sorting the catalogs
	 */
	public void sortCatalogsAlphabetical() throws DataException {
		int length = getCatalogs().size();

		boolean changed = true;

		while (changed) {
			int count = 0;

			for (int j = 0; j <= length - 1; j++, length--)
				for (int i = 0; i < length - 1; i++) {
					if (getCatalogs().get(i).getName().compareToIgnoreCase(
							getCatalogs().get(i + 1).getName()) > 0) {
						getCatalogs().get(i).pushDown();

						count++;
					}
				}

			if (count == 0) {
				changed = false;
			}
		}

		fireDataChanged();
	}

	/**
	 * Removes the catalog with the given name from the database.
	 * 
	 * @param name
	 *            name of the catalog to remove
	 * 
	 * @throws DataException
	 *             If an error occurs while removing the catalog
	 */
	public void removeCatalog(String name) throws DataException {
		try {
			Connection conn = openConnection();
			PreparedStatement ps;

			/*
			 * Remove catalog and linked data
			 */
			ps = conn.prepareStatement("DELETE FROM Catalogs "
					+ "WHERE name = ?");
			ps.setString(1, name);
			ps.executeUpdate();

			ps = conn.prepareStatement("DELETE FROM Categories "
					+ "WHERE catalogName = ?");
			ps.setString(1, name);
			ps.executeUpdate();

			ps = conn.prepareStatement("DELETE FROM Aspects "
					+ "WHERE catalogName = ?");
			ps.setString(1, name);
			ps.executeUpdate();

			ps.close();
			conn.close();

			fireDataChanged();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(Data.getInstance().getLocaleStr(
					"message.removeAppCatalogFailed")
					+ " [NAME = " + name + "] " + e.getMessage());
		}
	}

	/**
	 * Removes the given catalog from the database.
	 * 
	 * @param catalog
	 *            catalog to remove
	 * 
	 * @throws DataException
	 *             If an error occurs while removing the catalog
	 */
	public void removeCatalog(AppCatalog catalog) throws DataException {
		removeCatalog(catalog.getName());
	}

	/**
	 * Returns the number of catalogs saved in the database.
	 * 
	 * @return the number of catalogs
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the number of catalogs
	 */
	public int getNumberOfCatalogs() throws DataException {
		int numberOfCatalogs = 0;

		try {
			Connection conn = openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT COUNT(*) FROM Catalogs");

			ResultSet res = ps.executeQuery();

			res.next();

			numberOfCatalogs = res.getInt(1);

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(Data.getInstance().getLocaleStr(
					"message.getNumberOfAppCatalogsFailed")
					+ " " + e.getMessage());
		}

		return numberOfCatalogs;
	}

	/**
	 * Returns the first sorting position of all catalogs saved in the database.
	 * 
	 * @return first sorting position
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the sorting position
	 */
	public int getFirstSortPosOfCatalogs() throws DataException {
		int sortPos = 0;

		try {
			Connection conn = openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT sortPos FROM Catalogs "
							+ "ORDER BY sortPos ASC");
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
			throw new DataException(Data.getInstance().getLocaleStr(
					"message.getFirstSortPosOfAppCatalogsFailed")
					+ " " + e.getMessage());
		}

		return sortPos;
	}

	/**
	 * Returns the last sorting position of all catalogs saved in the database.
	 * 
	 * @return last sorting position
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the sorting position
	 */
	public int getLastSortPosOfCatalogs() throws DataException {
		int sortPos = 0;

		try {
			Connection conn = openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT sortPos FROM Catalogs "
							+ "ORDER BY sortPos DESC");
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
			throw new DataException(Data.getInstance().getLocaleStr(
					"message.getLastSortPosOfAppCatalogsFailed")
					+ " " + e.getMessage());
		}

		return sortPos;
	}

	/**
	 * Returns the list of attendees saved in the database.
	 * 
	 * @return list of attendees
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the attendees
	 */
	public List<AppAttendee> getAttendees() throws DataException {
		List<AppAttendee> attendees = new ArrayList<AppAttendee>();

		try {
			Connection conn = openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT name,contact FROM Attendees "
							+ "ORDER BY name ASC");

			ResultSet res = ps.executeQuery();

			while (res.next()) {
				attendees.add(AppAttendee.newInstance(res.getString("name"),
						res.getString("contact")));
			}

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(Data.getInstance().getLocaleStr(
					"message.getAppAttendeesFailed")
					+ " " + e.getMessage());
		}

		return attendees;
	}

	/**
	 * Returns a list of attendees which fit to the given filter.
	 * 
	 * @param filter
	 *            filter to search for
	 * 
	 * @return list of attendees
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the attendees
	 */
	public List<AppAttendee> getAttendees(String filter) throws DataException {
		List<AppAttendee> attendees = getAttendees();
		List<AppAttendee> attendeesFiltered = new ArrayList<AppAttendee>();

		for (AppAttendee att : attendees) {
			String contact = att.getContact();
			boolean isInContact = false;

			if (contact != null) {
				if (contact.toLowerCase().contains(filter.toLowerCase())) {
					isInContact = true;
				}
			}

			if (att.getName().toLowerCase().contains(filter.toLowerCase())
					|| isInContact) {
				attendeesFiltered.add(att);
			}
		}

		return attendeesFiltered;
	}

	/**
	 * Returns the attendee with the given name. If an attendee with the given
	 * name does not exist, then null will be returned.
	 * 
	 * @param name
	 *            name of the attendee in the database
	 * @param contact
	 *            contact information of the attendee in the database
	 * 
	 * @return the attendee
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the attendee
	 */
	public AppAttendee getAttendee(String name, String contact)
			throws DataException {
		AppAttendee attendee = null;

		try {
			Connection conn = openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT name,contact FROM Attendees "
							+ " WHERE name = ? AND contact = ?");

			ps.setString(1, name);
			ps.setString(2, contact);

			ResultSet res = ps.executeQuery();

			if (res.next()) {
				attendee = AppAttendee.newInstance(res.getString("name"), res
						.getString("contact"));
			}

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(Data.getInstance().getLocaleStr(
					"message.getAppAttendeeFailed")
					+ " [NAME = " + name + "] " + e.getMessage());
		}

		return attendee;
	}

	/**
	 * Checks if the given attendee exists.
	 * 
	 * @param name
	 *            the name
	 * @param contact
	 *            the contact
	 * 
	 * @return true, if checks if is attendee
	 * 
	 * @throws DataException
	 *             If an error occurs while checking the existence of the
	 *             attendee
	 */
	public boolean isAttendee(String name, String contact) throws DataException {
		return getAttendee(name, contact) != null;
	}

	/**
	 * Create a new AppAttendee object. If an attendee with the given name does
	 * not exist, a new one will be created in the database.
	 * 
	 * @param name
	 *            name of the attendee
	 * @param contact
	 *            contact information of the attendee
	 * 
	 * @return the newly created attendee
	 * 
	 * @throws DataException
	 *             If an error occurs while creating the new attendee
	 */
	public AppAttendee newAttendee(String name, String contact)
			throws DataException {
		return AppAttendee.newInstance(name, contact);
	}

	/**
	 * Removes the attendee with the given name from the database.
	 * 
	 * @param name
	 *            name of the attendee
	 * @param contact
	 *            contact information of the attendee
	 * 
	 * @throws DataException
	 *             If an error occurs while removing the attendee
	 */
	public void removeAttendee(String name, String contact)
			throws DataException {
		try {
			Connection conn = openConnection();
			PreparedStatement ps;

			/*
			 * Remove attendee and linked data
			 */
			ps = conn.prepareStatement("DELETE FROM Attendees "
					+ "WHERE name = ? AND contact = ?");
			ps.setString(1, name);
			ps.setString(2, contact);
			ps.executeUpdate();

			ps = conn.prepareStatement("DELETE FROM AttendeesStrengths "
					+ "WHERE attendeeName = ? AND attendeeContact = ?");
			ps.setString(1, name);
			ps.setString(2, contact);
			ps.executeUpdate();

			ps.close();
			conn.close();

			fireDataChanged();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(Data.getInstance().getLocaleStr(
					"message.removeAppAttendeeFailed")
					+ " [NAME = " + name + "] " + e.getMessage());
		}
	}

	/**
	 * Removes the given attendee from the database.
	 * 
	 * @param attendee
	 *            the attendee
	 * 
	 * @throws DataException
	 *             If an error occurs while removing the attendee
	 */
	public void removeAttendee(AppAttendee attendee) throws DataException {
		removeAttendee(attendee.getName(), attendee.getContact());
	}

	/**
	 * Returns the number of attendees stored in the database.
	 * 
	 * @return number of attendees
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the number of attendees
	 */
	public int getNumberOfAttendees() throws DataException {
		int numberOfAttendees = 0;

		try {
			Connection conn = openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT COUNT(*) FROM Attendees");

			ResultSet res = ps.executeQuery();

			res.next();

			numberOfAttendees = res.getInt(1);

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(Data.getInstance().getLocaleStr(
					"message.getNumberOfAppAttendeesFailed")
					+ " " + e.getMessage());
		}

		return numberOfAttendees;
	}

	/**
	 * Returns the list of CSV profiles stored in the database.
	 * 
	 * @return list of CSV profiles
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the list of CSV profiles
	 */
	public List<AppCSVProfile> getCSVProfiles() throws DataException {
		List<AppCSVProfile> profiles = new ArrayList<AppCSVProfile>();

		try {
			Connection conn = openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT name FROM CSVProfiles "
							+ "ORDER BY name ASC");

			ResultSet res = ps.executeQuery();

			while (res.next()) {
				profiles.add(AppCSVProfile.newInstance(res.getString("name")));
			}

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(Data.getInstance().getLocaleStr(
					"message.getAppCSVProfilesFailed")
					+ " " + e.getMessage());
		}

		return profiles;
	}

	/**
	 * Returns a list of CSV profiles which fit to the given filter.
	 * 
	 * @param filter
	 *            filter to search for
	 * 
	 * @return list of CSV profiles
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the CSV profiles
	 */
	public List<AppCSVProfile> getCSVProfiles(String filter)
			throws DataException {
		List<AppCSVProfile> profiles = getCSVProfiles();
		List<AppCSVProfile> profilesFiltered = new ArrayList<AppCSVProfile>();

		for (AppCSVProfile prof : profiles) {
			if (prof.getName().toLowerCase().contains(filter.toLowerCase())) {
				profilesFiltered.add(prof);
			}
		}

		return profilesFiltered;
	}

	/**
	 * Returns the CSV profile with the given name. If no profile with the given
	 * name exists, then null will be returned.
	 * 
	 * @param name
	 *            name of the CSV profile
	 * 
	 * @return a CSV profile
	 * 
	 * @throws DataException
	 *             If an error occurs while
	 */
	public AppCSVProfile getCSVProfile(String name) throws DataException {
		AppCSVProfile profile = null;

		try {
			Connection conn = openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT name FROM CSVProfiles "
							+ " WHERE name = ?");

			ps.setString(1, name);

			ResultSet res = ps.executeQuery();

			if (res.next()) {
				profile = AppCSVProfile.newInstance(res.getString("name"));
			}

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(Data.getInstance().getLocaleStr(
					"message.getAppCSVProfileFailed")
					+ " [NAME = " + name + "] " + e.getMessage());
		}

		return profile;
	}

	/**
	 * Checks if the given profile exists.
	 * 
	 * @param name
	 *            the name
	 * 
	 * @return true, if checks if is csv profile
	 * 
	 * @throws DataException
	 *             If an error occurs while checking the existence of the CSV
	 *             profile
	 */
	public boolean isCSVProfile(String name) throws DataException {
		return getCSVProfile(name) != null;
	}

	/**
	 * Create a new AppCSVProfile object. If a profile with the given name does
	 * not exist, a new one will be created in the database.
	 * 
	 * @param name
	 *            name of the profile
	 * 
	 * @return a CSV profile
	 * 
	 * @throws DataException
	 *             If an error occurs while creating a new profile
	 */
	public AppCSVProfile newCSVProfile(String name) throws DataException {
		return AppCSVProfile.newInstance(name);
	}

	/**
	 * Remove the CSV profile with the given name from the database.
	 * 
	 * @param name
	 *            name of the CSV profile
	 * 
	 * @throws DataException
	 *             If an error occurs while removing the profile
	 */
	public void removeCSVProfile(String name) throws DataException {
		try {
			Connection conn = openConnection();
			PreparedStatement ps;

			/*
			 * Remove catalog and linked data
			 */
			ps = conn.prepareStatement("DELETE FROM CSVProfiles "
					+ "WHERE name = ?");
			ps.setString(1, name);
			ps.executeUpdate();

			ps = conn.prepareStatement("DELETE FROM CSVColumnMappings "
					+ "WHERE profileName = ?");
			ps.setString(1, name);
			ps.executeUpdate();

			ps = conn.prepareStatement("DELETE FROM CSVSeverityMappings "
					+ "WHERE profileName = ?");
			ps.setString(1, name);
			ps.executeUpdate();

			ps.close();
			conn.close();

			fireDataChanged();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(Data.getInstance().getLocaleStr(
					"message.removeAppCSVProfileFailed")
					+ " [NAME = " + name + "] " + e.getMessage());
		}
	}

	/**
	 * Remove the given CSV profile from the database.
	 * 
	 * @param profile
	 *            the profile
	 * 
	 * @throws DataException
	 *             If an error occurs while removing the CSV profile
	 */
	public void removeCSVProfile(AppCSVProfile profile) throws DataException {
		removeCSVProfile(profile.getName());
	}

	/**
	 * Returns the number of CSV profiles stored in the database.
	 * 
	 * @return number of CSV profiles
	 * 
	 * @throws DataException
	 *             If an error occurs while getting the number of CSV profiles
	 */
	public int getNumberOfCSVProfiles() throws DataException {
		int numberOfCSVProfiles = 0;

		try {
			Connection conn = openConnection();

			PreparedStatement ps = conn
					.prepareStatement("SELECT COUNT(*) FROM CSVProfiles");

			ResultSet res = ps.executeQuery();

			res.next();

			numberOfCSVProfiles = res.getInt(1);

			res.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			/*
			 * Not part of the unit testing, because this exception is only
			 * thrown if there occurs an internal error.
			 */
			throw new DataException(Data.getInstance().getLocaleStr(
					"message.getNumberOfAppCatalogsFailed")
					+ " " + e.getMessage());
		}

		return numberOfCSVProfiles;
	}

}
