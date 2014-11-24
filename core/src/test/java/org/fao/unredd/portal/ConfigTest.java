package org.fao.unredd.portal;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.junit.Test;

public class ConfigTest {

	@Test
	public void testConfigurationProvidersDontOverride() throws Exception {
		Config config = new Config("", "", false);
		JSONObject conf1 = new JSONObject();
		conf1.element("a", "a");
		conf1.element("b", "b");
		JSONObject conf2 = new JSONObject();
		conf2.element("a", "z");
		conf2.element("c", "c");

		ModuleConfigurationProvider provider1 = mock(ModuleConfigurationProvider.class);
		when(provider1.getConfigurationMap()).thenReturn(
				buildMap("myModule", conf1));
		ModuleConfigurationProvider provider2 = mock(ModuleConfigurationProvider.class);
		when(provider2.getConfigurationMap()).thenReturn(
				buildMap("myModule", conf2));
		config.addModuleConfigurationProvider(provider1);
		config.addModuleConfigurationProvider(provider2);

		JSONObject pluginConf = config.getPluginConfiguration().get("myModule");

		assertTrue(pluginConf.has("a") && pluginConf.has("b")
				&& pluginConf.has("c"));
		assertEquals("c", pluginConf.get("c"));
		assertEquals("b", pluginConf.get("b"));
		// No defined priority when two providers have the same element
		assertTrue(pluginConf.get("a") == "a" || pluginConf.get("b") == "b");
	}

	private Map<String, JSONObject> buildMap(String pluginName, JSONObject conf) {
		Map<String, JSONObject> ret = new HashMap<String, JSONObject>();
		ret.put(pluginName, conf);
		return ret;
	}
}
