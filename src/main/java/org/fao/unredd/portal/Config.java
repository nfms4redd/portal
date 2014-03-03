/*
 * nfms4redd Portal Interface - http://nfms4redd.org/
 *
 * (C) 2012, FAO Forestry Department (http://www.fao.org/forestry/)
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */
package org.fao.unredd.portal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * Utility class to access the custom resources placed in PORTAL_CONFIG_DIR.
 * 
 * @author Oscar Fonts
 */
public class Config {

	private static final String PROPERTY_CLIENT_MODULES = "client.modules";
	private static final String PROPERTY_SERVER_QUERY_URL = "info.queryUrl";
	private static final String PROPERTY_SERVER_LAYER_URL = "info.layerUrl";

	private static Logger logger = Logger.getLogger(Config.class);

	private File dir = null;
	private Properties properties;

	private String rootPath;
	private String configInitParameter;
	private boolean useCache;
	private HashMap<Locale, ResourceBundle> localeBundles = new HashMap<Locale, ResourceBundle>();
	private String layersContent;

	public Config(String rootPath, String configInitParameter, boolean useCache) {
		this.rootPath = rootPath;
		this.configInitParameter = configInitParameter;
		this.useCache = useCache;
	}

	public File getPortalPropertiesFile() {
		return new File(getDir() + "/portal.properties");
	}

	public File getDir() {
		if (dir == null) {
			String defaultDir = rootPath + File.separator + "WEB-INF"
					+ File.separator + "default_config";

			// Get the portal config dir property from Java system properties
			String portalConfigDir = System.getProperty("PORTAL_CONFIG_DIR");

			// If not set in the system properties, get it from the Servlet
			// context parameters (web.xml)
			if (portalConfigDir == null)
				portalConfigDir = configInitParameter;

			// Otherwise:
			if (portalConfigDir == null) {
				// if not set already, use the default portal config dir
				logger.warn("PORTAL_CONFIG_DIR property not found. Using default config.");
				dir = new File(defaultDir);
			} else {
				// if set but not existing, use the default portal config dir
				dir = new File(portalConfigDir);
				if (!dir.exists()) {
					logger.warn("PORTAL_CONFIG_DIR is set to "
							+ dir.getAbsolutePath()
							+ ", but it doesn't exist. Using default config.");
					dir = new File(defaultDir);
				}
			}

			logger.info("============================================================================");
			logger.info("PORTAL_CONFIG_DIR: " + dir.getAbsolutePath());
			logger.info("============================================================================");
		}

		return dir;
	}

	public boolean isMinifiedJs() {
		return Boolean.parseBoolean(System.getProperty("MINIFIED_JS", "false"));
	}

	public Properties getProperties() {
		if (properties == null || !useCache) {
			File file = getPortalPropertiesFile();
			logger.debug("Reading portal properties file " + file);
			properties = new Properties();
			try {
				properties.load(new FileInputStream(file));
			} catch (IOException e) {
				logger.error("Error reading portal properties file", e);
			}
		}
		return properties;
	}

	public ArrayList<String> getLanguages() {
		File translationFolder = getTranslationFolder();
		final Pattern pattern = Pattern.compile("messages_(..)\\.properties");
		File[] translationFiles = translationFolder.listFiles();
		ArrayList<String> locales = new ArrayList<String>();
		if (translationFiles != null) {
			for (File translationFile : translationFiles) {
				Matcher matcher = pattern.matcher(translationFile.getName());
				if (matcher.matches()) {
					String localeString = matcher.group(1);
					locales.add(localeString);
				}
			}
		}

		return locales;
	}

	private File getTranslationFolder() {
		return new File(getDir(), "messages");
	}

	public String getLayers(Locale locale) throws IOException,
			ConfigurationException {
		if (layersContent == null || !useCache) {
			layersContent = getLocalizedFileContents(getLayersFile(), locale);
		}
		return layersContent;
	}

	public File getLayersFile() {
		return new File(getDir() + "/layers.json");
	}

	public ResourceBundle getMessages(Locale locale)
			throws ConfigurationException {
		ResourceBundle bundle = localeBundles.get(locale);
		if (bundle == null || !useCache) {
			URLClassLoader urlClassLoader;
			try {
				urlClassLoader = new URLClassLoader(
						new URL[] { getTranslationFolder().toURI().toURL() });
			} catch (MalformedURLException e) {
				logger.error(
						"Something is wrong with the configuration directory",
						e);
				throw new ConfigurationException(e);
			}
			bundle = ResourceBundle.getBundle("messages", locale,
					urlClassLoader);
			localeBundles.put(locale, bundle);
		}

		return bundle;
	}

	public String getLocalizedFileContents(File file, Locale locale)
			throws IOException, ConfigurationException {
		try {
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(file));
			String template = IOUtils.toString(bis, "UTF-8");
			bis.close();
			Pattern patt = Pattern.compile("\\$\\{([\\w.]*)\\}");
			Matcher m = patt.matcher(template);
			StringBuffer sb = new StringBuffer(template.length());
			ResourceBundle messages = getMessages(locale);
			while (m.find()) {
				String text;
				try {
					text = messages.getString(m.group(1));
					m.appendReplacement(sb, text);
				} catch (MissingResourceException e) {
					// do not replace
				}
			}
			m.appendTail(sb);
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			logger.error("Unsupported encoding", e);
			return "";
		}
	}

	public String[] getModules() throws ConfigurationException {
		return getProperty(PROPERTY_CLIENT_MODULES).split(",");
	}

	public String getQueryURL() throws ConfigurationException {
		return getProperty(PROPERTY_SERVER_QUERY_URL);
	}

	public String getLayerURL() {
		return getProperty(PROPERTY_SERVER_LAYER_URL);
	}

	private String getProperty(String propertyName)
			throws ConfigurationException {
		String value = getProperties().getProperty(propertyName);
		if (value != null) {
			return value;
		} else {
			throw new ConfigurationException("No \"" + propertyName
					+ "\" property in configuration");
		}
	}

}
