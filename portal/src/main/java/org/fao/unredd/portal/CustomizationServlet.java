package org.fao.unredd.portal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

public class CustomizationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// Find languages
		Config config = (Config) getServletContext().getAttribute("config");
		Locale locale = Locale.getDefault();

		HashMap<String, String> messages = new HashMap<String, String>();
		ResourceBundle bundle = config.getMessages(locale);
		for (String key : bundle.keySet()) {
			messages.put(key, bundle.getString(key));
		}
		JSONObject configurationObject = new JSONObject()//
				.element("title", bundle.getString("title"))//
				.element("languages", config.getLanguages())//
				.element("languageCode", locale.getLanguage())//
				.element("messages", messages);

		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf8");
		PrintWriter writer = resp.getWriter();
		configurationObject.write(writer);
	}
}
