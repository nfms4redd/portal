package org.fao.unredd.portal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.fao.unredd.jwebclientAnalyzer.PluginDescriptor;

public class PluginJSONConfigurationProvider implements
		ModuleConfigurationProvider {

	@Override
	public Map<String, JSONObject> getConfigurationMap(
			PortalRequestConfiguration configurationContext,
			HttpServletRequest request) throws IOException {
		File configProperties = new File(
				configurationContext.getConfigurationDirectory(),
				"plugin-conf.json");
		BufferedInputStream stream;
		try {
			stream = new BufferedInputStream(new FileInputStream(
					configProperties));
		} catch (FileNotFoundException e) {
			return new HashMap<String, JSONObject>();
		}
		String content = IOUtils.toString(stream);
		stream.close();
		PluginDescriptor pluginDescriptor = new PluginDescriptor(content);
		return pluginDescriptor.getConfigurationMap();
	}

	@Override
	public boolean canBeCached() {
		return true;
	}

}