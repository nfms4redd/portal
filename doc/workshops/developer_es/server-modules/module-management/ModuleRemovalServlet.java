package org.fao.unredd.portal;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ModuleRemovalServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Config config = (Config) getServletContext().getAttribute("config");

		String moduleName = req.getParameter("moduleName");

		Properties properties = config.getProperties();
		String modules = properties.getProperty("client.modules");
		modules = modules.replaceAll(",?\\s*" + moduleName, "");
		properties.put("client.modules", modules);
		properties.store(
				new FileOutputStream(config.getPortalPropertiesFile()), null);
	}

}
