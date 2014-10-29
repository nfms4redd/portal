package org.fao.unredd.jwebclientAnalyzer;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
		JEEContextAnalyzer analyzer = new JEEContextAnalyzer(
				new ExpandedClientContext(webClientFolder));

		Map<String, String> paths = analyzer.getNonRequirePathMap();
		Map<String, String> shims = analyzer.getNonRequireShimMap();
		List<String> moduleNames = analyzer.getRequireJSModuleNames();

		RequireTemplate template = new RequireTemplate("/main.js", paths,
				shims, moduleNames);

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
