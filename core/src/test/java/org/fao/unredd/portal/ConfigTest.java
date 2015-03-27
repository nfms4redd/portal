package org.fao.unredd.portal;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.junit.Test;

public class ConfigTest {

	@Test
	public void testConfigurationProvidersMerge() throws Exception {
		Config config = new DefaultConfig(mock(ConfigFolder.class), false);
		JSONObject conf1 = new JSONObject();
		conf1.element("a", "a");
		conf1.element("b", "b");
		JSONObject conf2 = new JSONObject();
		conf2.element("a", "z");
		conf2.element("c", "c");

		ModuleConfigurationProvider provider1 = mock(ModuleConfigurationProvider.class);
		when(
				provider1.getConfigurationMap(
						any(PortalRequestConfiguration.class),
						any(HttpServletRequest.class))).thenReturn(
				buildMap("myModule", conf1));
		ModuleConfigurationProvider provider2 = mock(ModuleConfigurationProvider.class);
		when(
				provider2.getConfigurationMap(
						any(PortalRequestConfiguration.class),
						any(HttpServletRequest.class))).thenReturn(
				buildMap("myModule", conf2));
		config.addModuleConfigurationProvider(provider1);
		config.addModuleConfigurationProvider(provider2);

		JSONObject pluginConf = config.getPluginConfiguration(
				Locale.getDefault(), mock(HttpServletRequest.class)).get(
				"myModule");

		assertTrue(pluginConf.has("a") && pluginConf.has("b")
				&& pluginConf.has("c"));
		assertEquals("c", pluginConf.get("c"));
		assertEquals("b", pluginConf.get("b"));
		// No defined priority when two providers have the same element
		assertTrue(pluginConf.get("a") == "a" || pluginConf.get("a") == "z");
	}

	private Map<String, JSONObject> buildMap(String pluginName, JSONObject conf) {
		Map<String, JSONObject> ret = new HashMap<String, JSONObject>();
		ret.put(pluginName, conf);
		return ret;
	}

	@Test
	public void testCache() throws Exception {
		String defaultLang = "es";
		Locale locale = new Locale(defaultLang);
		ResourceBundle resourceBundle = mock(ResourceBundle.class);
		Properties firstProperties = new Properties();
		firstProperties.put("languages", "{\"es\": \"Espa\u00f1ol\"}");
		firstProperties.put("languages.default", defaultLang);
		Config config = buildConfigReadOnceAndChangeFolderConfig(true,
				defaultLang, locale, resourceBundle, firstProperties);

		// Check we still have the same values
		assertTrue(config.getDefaultLang().equals(defaultLang));
		assertTrue(config.getLanguages()[0].get("code").equals("es"));
		assertTrue(config.getMessages(locale) == resourceBundle);
		assertTrue(config.getProperties() == firstProperties);
	}

	@Test
	public void testNoCache() throws Exception {
		String defaultLang = "es";
		Locale locale = new Locale(defaultLang);
		ResourceBundle resourceBundle = mock(ResourceBundle.class);
		Properties firstProperties = new Properties();
		firstProperties.put("languages", "{\"es\": \"Espa\u00f1ol\"}");
		firstProperties.put("languages.default", defaultLang);
		Config config = buildConfigReadOnceAndChangeFolderConfig(false,
				defaultLang, locale, resourceBundle, firstProperties);

		// Check we still have the same values
		assertFalse(config.getDefaultLang().equals(defaultLang));
		assertFalse(config.getLanguages()[0].get("code").equals("es"));
		assertFalse(config.getMessages(locale) == resourceBundle);
		assertFalse(config.getProperties() == firstProperties);
	}

	private Config buildConfigReadOnceAndChangeFolderConfig(boolean useCache,
			String defaultLang, Locale locale, ResourceBundle resourceBundle,
			Properties firstProperties) {
		ConfigFolder folder = mock(ConfigFolder.class);
		Config config = new DefaultConfig(folder, useCache);

		when(folder.getMessages(locale)).thenReturn(resourceBundle);
		when(folder.getProperties()).thenReturn(firstProperties);

		assertTrue(config.getDefaultLang().equals(defaultLang));
		assertTrue(config.getLanguages()[0].get("code").equals("es"));
		assertTrue(config.getMessages(locale) == resourceBundle);
		assertTrue(config.getProperties() == firstProperties);

		Properties secondProperties = new Properties();
		secondProperties.put("languages", "{\"fr\": \"Frances\"}");
		secondProperties.put("languages.default", "fr");
		ResourceBundle secondResourceBundle = mock(ResourceBundle.class);
		when(folder.getMessages(locale)).thenReturn(secondResourceBundle);
		when(folder.getProperties()).thenReturn(secondProperties);
		return config;
	}

	@Test
	public void testPluginConfigurationCached() throws Exception {
		readPluginConfigurationTwice(true, true, 1);
	}

	@Test
	public void testPluginConfigurationCacheIgnoredIfProviderCannotBeCached()
			throws Exception {
		readPluginConfigurationTwice(true, false, 2);
	}

	@Test
	public void testPluginConfigurationCacheIgnoredIfCacheDisabled()
			throws Exception {
		readPluginConfigurationTwice(false, true, 2);
	}

	private void readPluginConfigurationTwice(boolean useCache,
			boolean canBeCached, int numCalls) throws IOException {
		// Install configuration provider
		ModuleConfigurationProvider configurationProvider = mock(ModuleConfigurationProvider.class);
		when(configurationProvider.canBeCached()).thenReturn(canBeCached);
		when(
				configurationProvider.getConfigurationMap(
						any(PortalRequestConfiguration.class),
						any(HttpServletRequest.class))).thenReturn(
				new HashMap<String, JSONObject>());

		Config config = new DefaultConfig(mock(ConfigFolder.class), useCache);
		config.addModuleConfigurationProvider(configurationProvider);

		// Call twice
		config.getPluginConfiguration(Locale.getDefault(),
				mock(HttpServletRequest.class));
		config.getPluginConfiguration(Locale.getDefault(),
				mock(HttpServletRequest.class));

		// Check num calls
		verify(configurationProvider, times(numCalls)).getConfigurationMap(
				any(PortalRequestConfiguration.class),
				any(HttpServletRequest.class));
	}

}
