package org.fao.unredd;

import java.io.File;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import org.fao.unredd.layers.LayerFactory;
import org.fao.unredd.layers.folder.FolderLayerFactory;
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

		Properties configurationProperties = config.getProperties();
		String indicatorsFolder = configurationProperties
				.getProperty("layers.rootFolder");
		LayerFactory layerFactory = new FolderLayerFactory(new File(
				indicatorsFolder));
		servletContext.setAttribute("layer-factory", layerFactory);

		try {
			InitialContext cxt = new InitialContext();
			DataSource ds = (DataSource) cxt.lookup("java:/comp/env/jdbc/app");
			servletContext.setAttribute("datasource.app", ds);
		} catch (NamingException e) {
			throw new UnsupportedOperationException(
					"Cannot initialize database connection", e);
		}

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
