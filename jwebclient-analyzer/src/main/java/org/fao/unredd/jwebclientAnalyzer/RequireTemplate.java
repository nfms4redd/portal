package org.fao.unredd.jwebclientAnalyzer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

public class RequireTemplate {

	private InputStream template;
	private String webResourcesDir;
	private Map<String, String> paths;
	private Map<String, String> shims;
	private List<String> moduleNames;

	public RequireTemplate(String template, Map<String, String> paths,
			Map<String, String> shims, List<String> moduleNames) {
		this(RequireTemplate.class.getResourceAsStream(template), "", paths,
				shims, moduleNames);
	}

	public RequireTemplate(InputStream template, String webResourcesDir,
			Map<String, String> paths, Map<String, String> shims,
			List<String> moduleNames) {
		this.template = template;
		this.webResourcesDir = webResourcesDir;
		this.paths = paths;
		this.shims = shims;
		this.moduleNames = moduleNames;
	}

	public String generate() throws IOException {
		String output = IOUtils.toString(template);

		output = output.replaceAll("\\Q$webResourcesDir\\E", webResourcesDir);
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
