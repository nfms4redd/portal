package org.fao.unredd.portal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

public class IndexHTMLServlet extends HttpServlet {
	private static final String OPTIMIZED_FOLDER = "optimized";
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty("resource.loader", "string");
		engine.setProperty("string.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.StringResourceLoader");
		engine.setProperty("string.resource.loader.repository.class",
				"org.apache.velocity.runtime.resource.util.StringResourceRepositoryImpl");
		engine.setProperty("runtime.log.logsystem.class",
				"org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
		engine.setProperty("runtime.log.logsystem.log4j.category", "velocity");
		engine.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
		engine.init();
		VelocityContext context = new VelocityContext();

		boolean minifiedjs = Boolean.parseBoolean(System.getenv("MINIFIED_JS"));
		ServletContext servletContext = getServletContext();
		Config config = (Config) getServletContext().getAttribute("config");
		ArrayList<String> styleSheets = new ArrayList<String>();
		if (minifiedjs) {
			styleSheets.add(OPTIMIZED_FOLDER + "/portal-style.css");
		} else {
			@SuppressWarnings("unchecked")
			ArrayList<String> classPathStylesheets = (ArrayList<String>) servletContext
					.getAttribute("css-paths");
			styleSheets.addAll(classPathStylesheets);
			styleSheets.addAll(getStyleSheets(config, "modules"));
		}
		context.put("styleSheets", styleSheets);

		String queryString = req.getQueryString();
		String url = "config.js";
		if (queryString != null) {
			url += "?" + queryString;
		}
		context.put("configUrl", url);

		if (minifiedjs) {
			context.put("mainModulePath", OPTIMIZED_FOLDER + "/portal");
		} else {
			context.put("mainModulePath", "modules/main");
		}

		StringResourceRepository repo = StringResourceLoader.getRepository();
		String templateName = "/index.html";
		BufferedInputStream bis = new BufferedInputStream(this.getClass()
				.getResourceAsStream("/index.html"));
		String indexContent = IOUtils.toString(bis);
		bis.close();
		repo.putStringResource(templateName, indexContent);

		Template t = engine.getTemplate("/index.html");

		t.merge(context, resp.getWriter());
	}

	private ArrayList<String> getStyleSheets(Config config, String path) {
		ArrayList<String> styleSheets = new ArrayList<String>();
		File styleFolder = new File(config.getDir(), path);
		return getStyleSheets(styleFolder, path);
	}

	private ArrayList<String> getStyleSheets(File styleFolder, String path) {
		ArrayList<String> styleSheets = new ArrayList<String>();
		File[] styleSheetFiles = styleFolder.listFiles();
		if (styleSheetFiles != null) {
			for (File file : styleSheetFiles) {
				String fileName = file.getName();
				if (fileName.toLowerCase().endsWith(".css")) {
					styleSheets.add(path + "/" + fileName);
				}
			}
		}
		return styleSheets;
	}
}
