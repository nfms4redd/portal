package org.fao.unredd.portal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

/**
 * Utility class to access the custom resources placed in PORTAL_CONFIG_DIR.
 * 
 * @author Oscar Fonts
 * @author Fernando Gonzalez
 */
public class DefaultConfig implements Config {

	private static final String PROPERTY_DEFAULT_LANG = "languages.default";

	private static Logger logger = Logger.getLogger(DefaultConfig.class);

	private File dir = null;
	private Properties properties;

	private String rootPath;
	private String configInitParameter;
	private boolean useCache;
	private HashMap<Locale, ResourceBundle> localeBundles = new HashMap<Locale, ResourceBundle>();
	private ArrayList<ModuleConfigurationProvider> moduleConfigurationProviders = new ArrayList<ModuleConfigurationProvider>();

	public DefaultConfig(String rootPath, String configInitParameter,
			boolean useCache) {
		this.rootPath = rootPath;
		this.configInitParameter = configInitParameter;
		this.useCache = useCache;
	}

	@Override
	public File getPortalPropertiesFile() {
		return new File(getDir() + "/portal.properties");
	}

	@Override
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

	@Override
	public boolean isMinifiedJs() {
		return Boolean.parseBoolean(System.getProperty("MINIFIED_JS", "false"));
	}

	@Override
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

	/**
	 * Returns an array of <code>Map&lt;String, String&gt;</code>. For each
	 * element of the array, a {@link Map} is returned containing two
	 * keys/values: <code>code</code> (for language code) and <code>name</code>
	 * (for language name).
	 * 
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String>[] getLanguages() {
		List<Map<String, String>> ret = new ArrayList<Map<String, String>>();

		JSONObject json = JSONObject.fromObject(getProperty("languages"));
		for (Object langCode : json.keySet()) {
			Map<String, String> langObject = new HashMap<String, String>();
			langObject.put("code", langCode.toString());
			langObject.put("name", json.getString(langCode.toString()));

			ret.add(langObject);
		}

		return ret.toArray(new Map[ret.size()]);
	}

	private File getTranslationFolder() {
		return new File(getDir(), "messages");
	}

	public File getLayersFile() {
		return new File(getDir() + "/layers.json");
	}

	@Override
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

	private String localize(String template, Locale locale)
			throws ConfigurationException {
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
	}

	@Override
	public String[] getPropertyAsArray(String property) {
		return getProperty(property).split(",");
	}

	@Override
	public String getDefaultLang() {
		try {
			return getProperty(PROPERTY_DEFAULT_LANG);
		} catch (ConfigurationException e) {
			Map<String, String>[] langs = getLanguages();
			if (langs != null && langs.length > 0) {
				return langs[0].get("code");
			}
		}

		throw new ConfigurationException("No \"" + PROPERTY_DEFAULT_LANG
				+ "\" property in configuration");
	}

	private String getProperty(String propertyName)
			throws ConfigurationException {
		Properties props = getProperties();
		String value = props.getProperty(propertyName);
		if (value != null) {
			return value;
		} else {
			throw new ConfigurationException("No \"" + propertyName
					+ "\" property in configuration. File: "
					+ getPortalPropertiesFile().getAbsolutePath()
					+ ". Contents: " + props.keySet().size());
		}
	}

	@Override
	public String getIndicatorsFolder() {
		return getDir() + "/indicators";
	}

	@Override
	public Map<String, JSONObject> getPluginConfiguration(Locale locale,
			HttpServletRequest request) throws IOException {
		Map<String, JSONObject> ret = new HashMap<String, JSONObject>();
		for (ModuleConfigurationProvider provider : moduleConfigurationProviders) {
			Map<String, JSONObject> moduleConfigurations = provider
					.getConfigurationMap(new PortalConfigurationContextImpl(
							locale), request);
			Set<String> moduleNames = moduleConfigurations.keySet();
			for (String moduleName : moduleNames) {
				JSONObject moduleConfiguration = ret.get(moduleName);
				if (moduleConfiguration == null) {
					moduleConfiguration = new JSONObject();
					ret.put(moduleName, moduleConfiguration);
				}

				JSONObject moduleConfigurationToMerge = moduleConfigurations
						.get(moduleName);
				moduleConfiguration.putAll(moduleConfigurationToMerge);
			}

		}
		return ret;
	}

	@Override
	public void addModuleConfigurationProvider(
			ModuleConfigurationProvider provider) {
		moduleConfigurationProviders.add(provider);
	}

	private class PortalConfigurationContextImpl implements
			PortalRequestConfiguration {

		private Locale locale;

		public PortalConfigurationContextImpl(Locale locale) {
			this.locale = locale;
		}

		@Override
		public String localize(String template) {
			return DefaultConfig.this.localize(template, locale);
		}

		@Override
		public File getConfigurationDirectory() {
			return getDir();
		}

	}

}
