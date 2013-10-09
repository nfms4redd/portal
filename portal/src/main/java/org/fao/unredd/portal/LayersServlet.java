package org.fao.unredd.portal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LayersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Config config = (Config) getServletContext().getAttribute("config");
		Locale locale = (Locale) req.getAttribute("locale");

		try {
			String layers = config.getLayers(locale);
			resp.setContentType("application/json");
			resp.setCharacterEncoding("utf8");
			PrintWriter writer = resp.getWriter();
			writer.write(layers);
		} catch (IOException e) {
			throw new ServletException("Could not obtain the layer tree", e);
		}
	}
}
