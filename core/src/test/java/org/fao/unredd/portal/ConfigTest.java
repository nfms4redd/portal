package org.fao.unredd.portal;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.junit.Test;

public class ConfigTest {

	@Test
	public void testConfigurationProvidersMerge() throws Exception {
		Config config = new DefaultConfig("", "", false);
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
}
