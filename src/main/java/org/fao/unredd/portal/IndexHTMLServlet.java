package org.fao.unredd.portal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

public class IndexHTMLServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HashMap<String, Object> data = new HashMap<String, Object>();

		ServletContext servletContext = getServletContext();
		ArrayList<String> styleSheets = getStyleSheets(servletContext,
				"modules");
		styleSheets.addAll(getStyleSheets(servletContext, "styles"));
		data.put("styleSheets", styleSheets);

		String lang = req.getParameter("lang");
		String url = "config.js";
		if (lang != null && lang.trim().length() > 0) {
			url += "?lang=" + lang;
		}
		data.put("configUrl", url);

		File index = new File(servletContext.getRealPath("index.html"));
		BufferedReader br = new BufferedReader(new FileReader(index));
		Template template = Mustache.compiler().compile(br);
		try {
			template.execute(data, resp.getWriter());
		} finally {
			br.close();
		}
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
