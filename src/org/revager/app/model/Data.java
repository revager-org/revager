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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
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
	private final URL RESOURCES_FILE = this.getClass().getResource("/org/revager/resources/appResources.properties");

	/**
	 * The current locale of the data model.
	 */
	private Locale locale = null;

	/**
	 * Properties for the application resources.
	 */
	private Properties resourcesProp = new Properties();

	/**
	 * Map for the available languages
	 */
	private HashMap<String, String> langMap = null;

	/**
	 * Bundle for the language strings.
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

		langBundle = null;

		if (!locale.equals(Locale.ENGLISH)) {
			try {
				langBundle = ResourceBundle.getBundle(getResource("path.lang"));
			} catch (Exception e) {
				/*
				 * Not part of unit testing because this code will only be
				 * reached if an internal error occurs.
				 */
				System.err.println(
						"Error while loading locale data from the" + " following path: " + getResource("path.lang"));
			}
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
		return new ImageIcon(getClass().getResource(getResource("path.icons") + iconFile));
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
	 * Returns the translation of the specified string from the bundle.
	 * 
	 * @param id
	 *            the string to translate
	 * @return translated string or the id
	 */
	public static String translate(String id) {
		/*
		 * Some special handling of the roles
		 */
		if (id.equals(Role.MODERATOR.toString())) {
			return translate("Moderator");
		} else if (id.equals(Role.AUTHOR.toString())) {
			return translate("Author");
		} else if (id.equals(Role.CUSTOMER.toString())) {
			return translate("Customer");
		} else if (id.equals(Role.REVIEWER.toString())) {
			return translate("Reviewer");
		} else if (id.equals(Role.SCRIBE.toString())) {
			return translate("Scribe");
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
	 * Returns the list of default severities in the default language
	 * 
	 * @return the list of default severities in the default language
	 */
	public static List<String> getDefLangSeverities() {
		List<String> list = new ArrayList<String>();

		list.add("Not rated");
		translate("Not rated");

		list.add("Critical error");
		translate("Critical error");

		list.add("Main error");
		translate("Main error");

		list.add("Minor error");
		translate("Minor error");

		list.add("(Possible) no error");
		translate("(Possible) no error");

		list.add("Good");
		translate("Good");

		return list;
	}

	/**
	 * Returns list of default severities.
	 * 
	 * @return list of default severities
	 */
	public static List<String> getDefaultSeverities() {
		List<String> list = new ArrayList<String>();

		for (String sev : getDefLangSeverities()) {
			list.add(translate(sev));
		}

		return list;
	}

	/**
	 * Returns the given severity in the default language; otherwise return the
	 * localized one.
	 * 
	 * @param severity
	 *            The localized severity
	 * @return the given severity in the default language; otherwise return the
	 *         localized one.
	 */
	public static String getDefLangSeverity(String severity) {
		for (String sev : getDefLangSeverities()) {
			if (severity.equals(translate(sev))) {
				return sev;
			}
		}

		return severity;
	}

	/**
	 * Returns the list of default recommendations in the default language
	 * 
	 * @return the list of default recommendations in the default language
	 */
	public static List<String> getDefLangRecommendations() {
		List<String> list = new ArrayList<>();

		list.add("Accepted");
		translate("Accepted");

		list.add("Accepted (changes required)");
		translate("Accepted (changes required)");

		list.add("Not accepted");
		translate("Not accepted");

		list.add("Not finished");
		translate("Not finished");

		list.add("Not finished (changes required)");
		translate("Not finished (changes required)");

		list.add("Not finished (complete revision required)");
		translate("Not finished (complete revision required)");

		return list;
	}

	/**
	 * Returns list of default recommendations.
	 * 
	 * @return list of default recommendations
	 */
	public static List<String> getDefaultRecommendations() {
		List<String> list = new ArrayList<>();

		for (String rec : getDefLangRecommendations()) {
			list.add(translate(rec));
		}

		return list;
	}

	/**
	 * Returns the given recommendation in the default language; otherwise
	 * return the localized one.
	 * 
	 * @param recommendation
	 *            The localized recommendation
	 * @return the given recommendation in the default language; otherwise
	 *         return the localized one.
	 */
	public static String getDefLangRecommendation(String recommendation) {
		for (String rec : getDefLangRecommendations()) {
			if (recommendation.equals(translate(rec))) {
				return rec;
			}
		}

		return recommendation;
	}

	/**
	 * Returns the default invitation text.
	 * 
	 * @return the default invitation text
	 */
	public static String getDefLangInvitationText() {
		translate("You are invited to the review meeting. Please consider the information which are part of this document.");

		return "You are invited to the review meeting. Please consider the information which are part of this document.";
	}

	/**
	 * Returns list of default catalogs.
	 * 
	 * @return list of default catalogs
	 */
	public static List<String> getDefaultCatalogs() {
		ArrayList<String> catalogs = new ArrayList<>();

		String pathCatalogs = getInstance().getResource("path.catalogs") + getInstance().getLocale().getLanguage()
				+ "/";
		String fileEnding = "." + getInstance().getResource("fileEndingCatalog");

		File jarFile = AppTools.getJarFile();

		try {
			/* Search files in the JAR file */
			JarFile jf = new JarFile(jarFile);
			Enumeration<JarEntry> ress = jf.entries();

			String path = pathCatalogs.substring(1);
			int pathLen = path.length();

			while (ress.hasMoreElements()) {
				JarEntry je = ress.nextElement();

				if (je.getName().matches(path + ".+" + fileEnding)) {
					String filename = je.getName();

					catalogs.add(filename.substring(pathLen, filename.length() - fileEnding.length()));
				}
			}
		} catch (IOException e) {
			/* Search files in the directory and add them */
			String absDir = new File(Data.class.getProtectionDomain().getCodeSource().getLocation().getPath())
					.getAbsolutePath().replace("\\", "/") + pathCatalogs;

			File[] files = (new File(absDir)).listFiles();

			if (files != null) {
				for (File file : files) {
					String filename = file.getName();

					catalogs.add(filename.substring(0, filename.length() - fileEnding.length()));
				}
			}
		}

		return catalogs;
	}

	/**
	 * Returns a list of all languages installed on the system. The format is 0
	 * => short, 1 => long language name.
	 * 
	 * @return list of all available languages
	 */
	public Map<String, String> getLanguages() {
		if (langMap == null) {
			langMap = new HashMap<String, String>();

			File jarFile = AppTools.getJarFile();

			int lastIdx = getResource("path.lang").lastIndexOf(".");
			String bundleBaseName = getResource("path.lang").substring(lastIdx + 1);
			int bundleBaseNameLen = bundleBaseName.length();

			/* Add English as standard language */
			langMap.put(Locale.ENGLISH.getLanguage(), Locale.ENGLISH.getDisplayLanguage());

			try {
				/* Search translations in the JAR file */
				JarFile jf = new JarFile(jarFile);
				Enumeration<JarEntry> ress = jf.entries();

				String path = getResource("path.searchLang").substring(1);
				int pathLen = path.length();

				while (ress.hasMoreElements()) {
					JarEntry je = ress.nextElement();

					if (je.getName().matches(path + bundleBaseName + "_[a-z]+.properties")) {
						int idx = je.getName().indexOf(".properties");
						String lang = je.getName().substring(pathLen + bundleBaseNameLen + 1, idx);

						langMap.put(lang, new Locale(lang).getDisplayLanguage());
					}
				}
			} catch (IOException e) {
				/* Search files in the lang directory and add them */
				String absLangDir = new File(Data.class.getProtectionDomain().getCodeSource().getLocation().getPath())
						.getAbsolutePath().replace("\\", "/") + getResource("path.searchLang");

				File[] files = (new File(absLangDir)).listFiles();

				if (files != null) {
					for (File file : files) {
						String filename = file.getName();
						int idx = filename.indexOf(".properties");
						if (idx > -1) {
							String lang = filename.substring(bundleBaseNameLen + 1, idx);

							langMap.put(lang, new Locale(lang).getDisplayLanguage());
						}
					}
				}
			}
		}

		return langMap;
	}

	public boolean isLanguageAvailable(String lang) {
		for (String langId : getLanguages().keySet()) {
			if (lang.equals(langId)) {
				return true;
			}
		}

		return false;
	}

}
