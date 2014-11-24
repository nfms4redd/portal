package org.fao.unredd.portal;

import java.io.IOException;
import java.util.Map;

import net.sf.json.JSONObject;

public interface ModuleConfigurationProvider {

	Map<String, JSONObject> getConfigurationMap() throws IOException;

}
