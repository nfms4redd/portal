package org.fao.unredd.portal;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;

public class LayersModuleConfigurationProvider implements
		ModuleConfigurationProvider {

	@Override
	public Map<String, JSONObject> getConfigurationMap(
			PortalRequestConfiguration configurationContext,
			HttpServletRequest request) throws IOException {
		String id = request.getParameter("mapId");
		if (id == null) {
			id = "";
		}
		String layersTemplate = IOUtils.toString(
				new File(configurationContext.getConfigurationDirectory(),
						"layers" + id + ".json").toURI(), "UTF-8");
		JSONObject layersContent = (JSONObject) JSONSerializer
				.toJSON(configurationContext.localize(layersTemplate));

		HashMap<String, JSONObject> ret = new HashMap<String, JSONObject>();
		ret.put("layers", layersContent);
		return ret;
	}

	@Override
	public boolean canBeCached() {
		return true;
	}
}
