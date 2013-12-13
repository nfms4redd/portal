package org.fao.unredd;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.fao.unredd.dbconf.DBConf;
import org.fao.unredd.layers.LayerFactory;
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

		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("layers");

		LayerFactory layerFactory = new DBConf(emf);
		servletContext.setAttribute("layer-factory", layerFactory);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
