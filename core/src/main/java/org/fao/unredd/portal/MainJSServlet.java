package org.fao.unredd.portal;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fao.unredd.jwebclientAnalyzer.RequireTemplate;

public class MainJSServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String output;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		if (output == null) {

			@SuppressWarnings("unchecked")
			Map<String, String> paths = (Map<String, String>) getServletContext()
					.getAttribute("requirejs-paths");
			@SuppressWarnings("unchecked")
			Map<String, String> shims = (Map<String, String>) getServletContext()
					.getAttribute("requirejs-shims");

			RequireTemplate template = new RequireTemplate("/main.js", paths,
					shims, Collections.<String> emptyList());

			output = template.generate();
		}

		resp.getWriter().print(output);
	}

}