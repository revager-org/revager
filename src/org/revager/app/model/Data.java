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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.ImageIcon;

import org.revager.app.model.schema.Role;
import org.revager.tools.AppTools;

/**
 * This class is the interface for the complete data model.
 */
public class Data {

	/**
	 * Path of the resources file.
	 */
	private final URL RESOURCES_FILE = this.getClass().getResource(
			"/org/revager/resources/appResources.properties");

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
	private static ResourceBundle langBundle = null;

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

		/* Load the language file */
		InputStream inputStream = null;

		if (!locale.equals(Locale.ENGLISH)) {
			inputStream = getClass().getResourceAsStream(
					getResource("path.lang") + locale.getLanguage()
							+ ".properties");

			/* Finally load the resource */
			if (inputStream != null) {
				try {
					langBundle = new PropertyResourceBundle(inputStream);
					inputStream.close();
				} catch (IOException e) {
					System.err
							.println("Error while loading language data from the"
									+ " following path: "
									+ getResource("path.lang")
									+ locale.getLanguage() + ".properties");
				}
			}
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

	/**
	 * Returns the translation of the specified string from the bundle.
	 * 
	 * @param id
	 *            the string to translate
	 * @return translated string or the id
	 */
	public static String _(String id) {
		/*
		 * Some special handling of the roles
		 */
		if (id.equals(Role.MODERATOR.toString())) {
			return _("Moderator");
		} else if (id.equals(Role.AUTHOR.toString())) {
			return _("Author");
		} else if (id.equals(Role.CUSTOMER.toString())) {
			return _("Customer");
		} else if (id.equals(Role.REVIEWER.toString())) {
			return _("Reviewer");
		} else if (id.equals(Role.SCRIBE.toString())) {
			return _("Scribe");
		}

		try {
			return langBundle.getString(id);
		} catch (Exception e) {
			/*
			 * If the string is not translated in the bundle, or the bundle
			 * cannot be found, just return the original string.
			 */
			return id;
		}
	}

	/**
	 * Returns list of standard severities.
	 * 
	 * @return list of standard severities
	 */
	public static List<String> getStandardSeverities() {
		List<String> list = new ArrayList<String>();

		for (String sev : _("Critical error; Main error; Minor error; Good")
				.split(";")) {
			list.add(sev.trim());
		}

		return list;
	}

	/**
	 * Returns list of standard recommendations.
	 * 
	 * @return list of standard recommendations
	 */
	public static List<String> getStandardRecommendations() {
		List<String> list = new ArrayList<String>();

		for (String sev : _(
				"Accepted; Accepted (changes required); Not accepted; Not finished; Not finished (changes required); Not finished (complete revision required)")
				.split(";")) {
			list.add(sev.trim());
		}

		return list;
	}

	/**
	 * Returns list of standard catalogs.
	 * 
	 * @return list of standard catalogs
	 */
	public static List<String> getStandardCatalogs() {
		List<String> list = new ArrayList<String>();

		for (String sev : _(
				"Entwurfsunterlage; Entwurfsunterlage (elementare Komponente); Entwurfsunterlage (gegliederte Komponente); Lastenheft; Pflichtenheft; Quellcode; Testkonzept; Testunterlage")
				.split(";")) {
			list.add(sev.trim());
		}

		return list;
	}

	/**
	 * Returns a list of all languages installed on the system. The format is 0
	 * => short, 1 => long language name.
	 * 
	 * @return list of all available languages
	 */
	public Map<String, String> getLanguages() {
		HashMap<String, String> map = new HashMap<String, String>();

		String jarLocation = AppTools.getJarLocation();

		/* Add English as standard language */
		map.put(Locale.ENGLISH.getLanguage(),
				Locale.ENGLISH.getDisplayLanguage());

		/* Search all translations in the JAR file */
		try {
			JarFile jf = new JarFile(jarLocation);
			Enumeration<JarEntry> ress = jf.entries();

			while (ress.hasMoreElements()) {
				JarEntry je = (JarEntry) ress.nextElement();

				if (je.getName().matches(getResource("path.searchLang"))) {
					int idx = je.getName().indexOf(".properties");
					String lang = je.getName().substring(idx - 2, idx);

					map.put(lang, new Locale(lang).getDisplayLanguage());
				}
			}
		} catch (IOException e) {
		}

		return map;
	}
}
