package org.fao.unredd.portal;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

public interface ModuleConfigurationProvider {

	/**
	 * Returns a map where the keys are the names of the modules and the
	 * JSONObjects are the configuration of each module
	 * 
	 * @param request
	 *            Request that loads the application
	 * @return
	 * @throws IOException
	 */
	Map<String, JSONObject> getConfigurationMap(HttpServletRequest request)
			throws IOException;

}
