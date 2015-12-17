package org.fao.unredd.geoexplorerReader;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.fao.unredd.portal.Config;

public class GeoExplorerReaderContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		Config config = (Config) servletContext.getAttribute("config");

		config.addModuleConfigurationProvider(new GeoExplorerDBConfigurationProvider());
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
