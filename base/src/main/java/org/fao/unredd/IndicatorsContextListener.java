package org.fao.unredd;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.fao.unredd.layers.LayerFactory;
import org.fao.unredd.layers.bd.DBLayerFactory;

public class IndicatorsContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		LayerFactory layerFactory;
		layerFactory = new DBLayerFactory("workspace:newlayer");
		servletContext.setAttribute("layer-factory", layerFactory);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
