package org.fao.unredd.portal;

import java.io.File;
import java.io.IOException;
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

/**
 * Utility class to access the custom resources placed in PORTAL_CONFIG_DIR.
 * 
 * @author Oscar Fonts
 * @author Fernando Gonzalez
 */
public class DefaultConfig implements Config {

	private static final String PROPERTY_DEFAULT_LANG = "languages.default";

	private Properties properties;

	private ConfigFolder folder;
	private boolean useCache;
	private HashMap<Locale, ResourceBundle> localeBundles = new HashMap<Locale, ResourceBundle>();
	private Map<ModuleConfigurationProvider, Map<String, JSONObject>> pluginConfigurations = new HashMap<ModuleConfigurationProvider, Map<String, JSONObject>>();
	private ArrayList<ModuleConfigurationProvider> moduleConfigurationProviders = new ArrayList<ModuleConfigurationProvider>();

	public DefaultConfig(ConfigFolder folder, boolean useCache) {
		this.folder = folder;
		this.useCache = useCache;
	}

	@Override
	public File getDir() {
		return folder.getFilePath();
	}

	@Override
	public synchronized Properties getProperties() {
		if (properties == null || !useCache) {
			properties = folder.getProperties();
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

	@Override
	public ResourceBundle getMessages(Locale locale)
			throws ConfigurationException {
		ResourceBundle bundle = localeBundles.get(locale);
		if (bundle == null || !useCache) {
			bundle = folder.getMessages(locale);
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
					+ "\" property in configuration. Conf folder: "
					+ folder.getFilePath().getAbsolutePath() + ". Contents: "
					+ props.keySet().size());
		}
	}

	@Override
	public Map<String, JSONObject> getPluginConfiguration(Locale locale,
			HttpServletRequest request) throws IOException {
		Map<String, JSONObject> ret = new HashMap<String, JSONObject>();
		for (ModuleConfigurationProvider provider : moduleConfigurationProviders) {

			// Get the configuration
			Map<String, JSONObject> moduleConfigurations = pluginConfigurations
					.get(provider);
			if (moduleConfigurations == null || !useCache
					|| !provider.canBeCached()) {
				moduleConfigurations = provider.getConfigurationMap(
						new PortalConfigurationContextImpl(locale), request);
				pluginConfigurations.put(provider, moduleConfigurations);
			}

			// Merge the configuration in the result
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

		@Override
		public boolean usingCache() {
			return useCache;
		}

	}

}
