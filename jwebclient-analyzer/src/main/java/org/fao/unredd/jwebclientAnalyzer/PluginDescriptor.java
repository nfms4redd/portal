package org.fao.unredd.jwebclientAnalyzer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class PluginDescriptor {

	private JSONObject requireJS;
	private JSONObject configuration;

	public PluginDescriptor(String content) {
		JSONObject jsonRoot = (JSONObject) JSONSerializer.toJSON(content);

		if (jsonRoot.has("requirejs")) {
			requireJS = jsonRoot.getJSONObject("requirejs");
		}
		if (jsonRoot.has("default-conf")) {
			configuration = jsonRoot.getJSONObject("default-conf");
		}

	}

	public Map<String, String> getRequireJSPathsMap() {
		Map<String, String> ret = new HashMap<String, String>();

		if (requireJS != null) {
			fill(ret, (JSONObject) requireJS.get("paths"));
		}
		return ret;
	}

	private void fill(Map<String, String> map, JSONObject jsonMap) {
		if (jsonMap == null) {
			return;
		}

		for (Object key : jsonMap.keySet()) {
			Object value = jsonMap.get(key.toString());
			map.put(key.toString(), value.toString());
		}
	}

	public Map<String, String> getRequireJSShims() {
		Map<String, String> ret = new HashMap<String, String>();

		if (requireJS != null) {
			fill(ret, (JSONObject) requireJS.get("shim"));
		}
		return ret;
	}

	public Map<String, JSONObject> getConfigurationMap() {
		Map<String, JSONObject> configurationMap = new HashMap<String, JSONObject>();
		if (configuration != null) {
			@SuppressWarnings("rawtypes")
			Iterator iterator = configuration.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				configurationMap.put(key, configuration.getJSONObject(key));
			}
		}
		return configurationMap;
	}

}
