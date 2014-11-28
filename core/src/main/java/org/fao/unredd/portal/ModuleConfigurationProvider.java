package org.fao.unredd.portal;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

public interface ModuleConfigurationProvider {

	Map<String, JSONObject> getConfigurationMap(HttpServletRequest request)
			throws IOException;

}
