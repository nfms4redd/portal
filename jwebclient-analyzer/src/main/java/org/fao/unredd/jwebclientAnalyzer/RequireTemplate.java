package org.fao.unredd.jwebclientAnalyzer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

public class RequireTemplate {

	private String resourceName;
	private Map<String, String> paths;
	private Map<String, String> shims;
	private List<String> moduleNames;

	public RequireTemplate(String resourceName, Map<String, String> paths,
			Map<String, String> shims, List<String> moduleNames) {
		this.resourceName = resourceName;
		this.paths = paths;
		this.shims = shims;
		this.moduleNames = moduleNames;
	}

	public String generate() throws IOException {
		InputStream stream = this.getClass().getResourceAsStream(resourceName);
		String output = IOUtils.toString(stream);
		stream.close();

		output = output.replaceAll("\\Q$paths\\E", "paths:{"
				+ getCommaSeparatedMap(paths, "\"") + "}");
		output = output.replaceAll("\\Q$shim\\E", "shim:{"
				+ getCommaSeparatedMap(shims, "") + "}");
		StringBuilder moduleList = new StringBuilder();
		for (String moduleName : moduleNames) {
			moduleList.append("\"").append(moduleName).append("\"").append(",");
		}
		output = output.replaceAll("\\Q$deps\\E", moduleList.toString());

		return output;
	}

	private String getCommaSeparatedMap(Map<String, String> paths,
			String valueQuotes) {
		StringBuilder ret = new StringBuilder();
		String separator = "";
		for (String key : paths.keySet()) {
			ret.append(separator).append("\"").append(key).append("\":")
					.append(valueQuotes).append(paths.get(key))
					.append(valueQuotes);
			separator = ",";
		}

		return ret.toString();
	}

}
