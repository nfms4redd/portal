package org.fao.unredd.jwebclientAnalyzer;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

/**
 * Generates a RequireJS main.js module and configuration file for the requirejs
 * minification process.
 * 
 * The plugin operates on a folder that contains all the client resources,
 * expects to find the RequireJS modules in a "modules" folder and CSS
 * stylesheets in the "styles" folder
 * 
 * @author fergonco
 */
@Mojo(name = "generate-buildconfig")
public class GenerateRequireJSBuildConfig extends AbstractMojo {

	/**
	 * Root of the client resources
	 */
	@Parameter
	protected String webClientFolder;

	/**
	 * Path where the buildconfig file will be generated
	 */
	@Parameter
	protected String buildconfigOutputPath;

	/**
	 * Path where the main.js file will be generated
	 */
	@Parameter
	protected String mainOutputPath;

	/**
	 * Path to search for the plugin configuration files
	 * (&lt;plugin&gt;-conf.json)
	 */
	@Parameter(defaultValue = "nfms")
	protected String pluginConfDir;

	/**
	 * Path to search for the web resources (modules, styles, ...)
	 */
	@Parameter(defaultValue = "nfms")
	protected String webResourcesDir;

	/**
	 * Path to the main.js template to use.
	 */
	@Parameter
	protected String mainTemplate;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		JEEContextAnalyzer analyzer = new JEEContextAnalyzer(
				new ExpandedClientContext(webClientFolder), pluginConfDir,
				webResourcesDir);

		InputStream mainStream;
		if (mainTemplate != null) {
			try {
				mainStream = new FileInputStream(mainTemplate);
			} catch (FileNotFoundException e) {
				throw new MojoExecutionException(
						"Cannot find main template file: " + mainTemplate);
			}
		} else {
			mainStream = getClass().getResourceAsStream("/main.js");
		}
		processTemplate(analyzer,
				getClass().getResourceAsStream("/buildconfig.js"),
				buildconfigOutputPath);
		processTemplate(analyzer, mainStream, mainOutputPath);
	}

	private void processTemplate(JEEContextAnalyzer analyzer,
			InputStream templateStream, String outputPath)
			throws MojoExecutionException {
		Map<String, String> paths = analyzer.getNonRequirePathMap();
		Map<String, String> shims = analyzer.getNonRequireShimMap();
		List<String> moduleNames = analyzer.getRequireJSModuleNames();
		RequireTemplate template = new RequireTemplate(templateStream,
				webResourcesDir, paths, shims, moduleNames);
		try {
			OutputStream outputStream = new BufferedOutputStream(
					new FileOutputStream(outputPath));
			IOUtils.write(template.generate(), outputStream);
			outputStream.close();
		} catch (IOException e) {
			throw new MojoExecutionException("Cannot generate the file in "
					+ outputPath, e);
		}
	}

}
