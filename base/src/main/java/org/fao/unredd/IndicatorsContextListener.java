package org.fao.unredd;

import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.fao.unredd.layers.bd.DBLayerFactory;
import org.fao.unredd.portal.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndicatorsContextListener implements ServletContextListener {

	private static final Logger logger = LoggerFactory
			.getLogger(IndicatorsContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		final Config config = (Config) servletContext.getAttribute("config");
		Properties properties = config.getProperties();
		try {
			DBLayerFactory layerFactory = new DBLayerFactory(
					properties.getProperty("db-schema"));
			servletContext.setAttribute("layer-factory", layerFactory);
		} catch (NullPointerException e) {
			logger.error(
					"The statistics metadata table name was not configured. Please set the \"indicators-metadata-db-table\" property",
					e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
