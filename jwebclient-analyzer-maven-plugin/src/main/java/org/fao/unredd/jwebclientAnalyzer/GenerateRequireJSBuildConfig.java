package org.fao.unredd.jwebclientAnalyzer;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "generate-buildconfig")
public class GenerateRequireJSBuildConfig extends AbstractMojo {

	@Parameter
	protected String webClientFolder;

	@Parameter
	protected String outputPath;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		String output;
		try {
			InputStream stream = this.getClass().getResourceAsStream(
					"/buildconfig.js");
			output = IOUtils.toString(stream);
			stream.close();
		} catch (IOException e) {
			throw new MojoExecutionException("Internal error", e);
		}

		JEEContextAnalyzer analyzer = new JEEContextAnalyzer(
				new ExpandedClientContext(webClientFolder));

		Map<String, String> paths = analyzer.getNonRequirePathMap();
		Map<String, String> shims = analyzer.getNonRequireShimMap();
		output = output.replaceAll("\\Q$paths\\E", "paths:{"
				+ getCommaSeparatedMap(paths, "\"") + "}");
		output = output.replaceAll("\\Q$shim\\E", "shim:{"
				+ getCommaSeparatedMap(shims, "") + "}");

		List<String> moduleNames = analyzer.getRequireJSModuleNames();
		StringBuilder moduleList = new StringBuilder();
		for (String moduleName : moduleNames) {
			moduleList.append("\"").append(moduleName).append("\"").append(",");
		}
		output = output.replaceAll("\\Q$deps\\E", moduleList.toString());

		try {
			OutputStream outputStream = new BufferedOutputStream(
					new FileOutputStream(outputPath));
			IOUtils.write(output, outputStream);
			outputStream.close();
		} catch (IOException e) {
			throw new MojoExecutionException("Cannot generate the file in "
					+ outputPath, e);
		}
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
