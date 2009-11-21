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

import java.net.URL;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

/**
 * This class is the interface for the complete data model.
 */
public class Data {

	/**
	 * Path of the resources file.
	 */
	private final URL RESOURCES_FILE = this.getClass().getResource(
			"/neos/resi/resources/appResources.properties");

	/**
	 * The current locale of the data model.
	 */
	private Locale locale = null;

	/**
	 * Current mode (moderator, scribe etc.)
	 */
	private String modeName = null;

	/**
	 * Properties for the current mode parameters.
	 */
	private Properties modeProp = new Properties();

	/**
	 * Properties for the default mode parameters.
	 */
	private Properties modeDefaultProp = new Properties();

	/**
	 * Properties for the application resources.
	 */
	private Properties resourcesProp = new Properties();

	/**
	 * Bundle for the locale strings.
	 */
	private ResourceBundle localeBundle = null;

	/**
	 * The only instance of this class.
	 */
	private static Data theInstance = null;

	/**
	 * Instance of ResiData class.
	 */
	private ResiData resiDataInstance = null;

	/**
	 * Instance of ApplicationData class.
	 */
	private ApplicationData appDataInstance = null;

	/**
	 * Instance of HelpData class.
	 */
	private HelpData helpDataInstance = null;

	/**
	 * Constructor is protected because of Singleton pattern; You can get an
	 * instance of this class by the method getInstance().
	 */
	protected Data() {
		super();

		/*
		 * Set the standard file encoding
		 */
		System.setProperty("file.encoding", "UTF-8");

		/*
		 * Create instances of Data sub-classes
		 */
		resiDataInstance = new ResiData();
		appDataInstance = new ApplicationData();
		helpDataInstance = new HelpData();

		/*
		 * Load resources properties
		 */
		try {
			resourcesProp.load(RESOURCES_FILE.openStream());
		} catch (Exception e) {
			/*
			 * Not part of unit testing because this code will only be reached
			 * if an internal error occurs.
			 */
			System.err.println("Error while loading the resource file.");
		}

		/*
		 * Load default mode properties
		 */
		URL modeFile = this.getClass().getResource(
				getResource("path.modes") + "default.properties");

		try {
			modeDefaultProp.load(modeFile.openStream());
		} catch (Exception e) {
			/*
			 * Not part of unit testing because this code will only be reached
			 * if an internal error occurs.
			 */
			System.err.println("Error while loading the "
					+ "default mode properties.");
		}

		/*
		 * Set default locale
		 */
		setLocale(new Locale(getResource("appDefaultLang")));

		/*
		 * Set default mode
		 */
		setMode("default");
	}

	/**
	 * Get the only instance of Data class.
	 * 
	 * @return instance of Data class
	 */
	public static Data getInstance() {
		if (theInstance == null) {
			theInstance = new Data();
		}

		return theInstance;
	}

	/**
	 * Get the only instance of ResiData class.
	 * 
	 * @return instance of ResiData class
	 */
	public ResiData getResiData() {
		return resiDataInstance;
	}

	/**
	 * Get the only instance of ApplicationData class.
	 * 
	 * @return instance of ApplicationData class
	 */
	public ApplicationData getAppData() {
		return appDataInstance;
	}

	/**
	 * Get the only instance of HelpData class.
	 * 
	 * @return instance of HelpData class
	 */
	public HelpData getHelpData() {
		return helpDataInstance;
	}

	/**
	 * Get the current locale.
	 * 
	 * @return current locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Set the locale.
	 * 
	 * @param locale
	 *            the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;

		Locale.setDefault(locale);

		try {
			localeBundle = ResourceBundle.getBundle(getResource("path.locale"));
		} catch (Exception e) {
			/*
			 * Not part of unit testing because this code will only be reached
			 * if an internal error occurs.
			 */
			System.err.println("Error while loading locale data from the"
					+ " following path: " + getResource("path.locale"));
		}
	}

	/**
	 * Get the current mode.
	 * 
	 * @return current mode as String, e.g. "none", "moderator" or "scribe"
	 */
	public String getMode() {
		return modeName;
	}

	/**
	 * Set the mode as String, e.g. "none", "moderator" or "scribe"
	 * 
	 * @param mode
	 *            the mode to set
	 */
	public void setMode(String mode) {
		this.modeName = mode;

		URL modeFile = getClass().getResource(
				getResource("path.modes") + mode + ".properties");

		try {
			modeProp.clear();
			modeProp.load(modeFile.openStream());
		} catch (Exception e) {
			System.err.println("Error while loading the following "
					+ "mode properties: " + getResource("path.modes") + mode
					+ ".properties");
		}
	}

	/**
	 * Get an icon as ImageIcon object by file name.
	 * 
	 * @param iconFile
	 *            file name of the icon
	 * 
	 * @return icon as ImageIcon
	 */
	public ImageIcon getIcon(String iconFile) {
		return new ImageIcon(getClass().getResource(
				getResource("path.icons") + iconFile));
	}

	/**
	 * Get a resource by key.
	 * 
	 * @param key
	 *            the key
	 * 
	 * @return resource as String
	 */
	public String getResource(String key) {
		String resource = resourcesProp.getProperty(key);

		if (resource == null) {
			System.err.println("The following resource was not found: " + key);
		}

		return resource;
	}

	/**
	 * Get a localized string by key.
	 * 
	 * @param key
	 *            the key
	 * 
	 * @return localized string
	 */
	public String getLocaleStr(String key) {
		String localeStr = null;

		try {
			localeStr = localeBundle.getString(key);
		} catch (Exception exc) {
			localeStr = getLocaleStr("noLocaleString");
		}

		return localeStr;
	}

	/**
	 * Get a parameter of the current mode.
	 * 
	 * @param key
	 *            the key
	 * 
	 * @return parameter as boolean value
	 */
	public boolean getModeParam(String key) {
		boolean parameter;
		String paramValue = modeProp.getProperty(key);

		/*
		 * If parameter is not set in the current mode, try to get the default
		 * value
		 */
		if (paramValue == null) {
			paramValue = modeDefaultProp.getProperty(key);
		}

		if (paramValue != null && paramValue.equals("true")) {
			parameter = true;
		} else {
			parameter = false;
		}

		return parameter;
	}
}
