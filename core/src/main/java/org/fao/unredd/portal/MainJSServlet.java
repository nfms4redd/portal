package org.fao.unredd.portal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

public class MainJSServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String output;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		if (output == null) {
			InputStream stream = this.getClass()
					.getResourceAsStream("/main.js");
			output = IOUtils.toString(stream);
			stream.close();
			@SuppressWarnings("unchecked")
			Map<String, String> paths = (Map<String, String>) getServletContext()
					.getAttribute("requirejs-paths");
			@SuppressWarnings("unchecked")
			Map<String, String> shims = (Map<String, String>) getServletContext()
					.getAttribute("requirejs-shims");

			output = output.replaceAll("\\Q$paths\\E", "paths:{"
					+ getCommaSeparatedMap(paths, "\"") + "}");
			output = output.replaceAll("\\Q$shim\\E", "shim:{"
					+ getCommaSeparatedMap(shims, "") + "}");
		}

		resp.getWriter().print(output);
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