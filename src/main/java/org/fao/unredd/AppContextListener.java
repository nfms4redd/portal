package org.fao.unredd;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

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
		boolean configCache = Boolean.parseBoolean(System
				.getenv("NFMS_CONFIG_CACHE"));
		Config config = new Config(rootPath, configInitParameter, configCache);
		servletContext.setAttribute("config", config);

		String indicatorsFolder = config.getIndicatorsFolder();
		LayerFactory layerFactory = new FolderLayerFactory(new File(
				indicatorsFolder));
		servletContext.setAttribute("layer-factory", layerFactory);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
