package org.fao.unredd;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.fao.unredd.portal.Config;

public class AppContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		String rootPath = servletContext.getRealPath("/");
		String configInitParameter = servletContext
				.getInitParameter("PORTAL_CONFIG_DIR");
		Config config = new Config(rootPath, configInitParameter);
		servletContext.setAttribute("config", config);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
