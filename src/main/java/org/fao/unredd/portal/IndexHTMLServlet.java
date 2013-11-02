package org.fao.unredd.portal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
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
		engine.init();
		VelocityContext context = new VelocityContext();

		ServletContext servletContext = getServletContext();
		ArrayList<String> styleSheets = getStyleSheets(servletContext,
				"modules/styles");
		styleSheets.addAll(getStyleSheets(servletContext, "styles"));
		context.put("styleSheets", styleSheets);

		String lang = req.getParameter("lang");
		String url = "config.js";
		if (lang != null && lang.trim().length() > 0) {
			url += "?lang=" + lang;
		}
		context.put("configUrl", url);

		StringResourceRepository repo = StringResourceLoader.getRepository();
		String templateName = "/index.html";
		File index = new File(servletContext.getRealPath("index.html"));
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				index));
		String indexContent = IOUtils.toString(bis);
		bis.close();
		repo.putStringResource(templateName, indexContent);

		Template t = engine.getTemplate("/index.html");

		t.merge(context, resp.getWriter());
	}

	private ArrayList<String> getStyleSheets(ServletContext servletContext,
			String path) {
		File styleFolder = new File(servletContext.getRealPath(path));
		File[] styleSheetFiles = styleFolder.listFiles();
		ArrayList<String> styleSheets = new ArrayList<String>();
		for (File file : styleSheetFiles) {
			String fileName = file.getName();
			if (fileName.toLowerCase().endsWith(".css")) {
				styleSheets.add(path + "/" + fileName);
			}
		}
		return styleSheets;
	}
}
