package org.fao.unredd;

import java.io.File;
import java.io.InputStream;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.fao.unredd.jwebclientAnalyzer.Context;
import org.fao.unredd.jwebclientAnalyzer.JEEContextAnalyzer;
import org.fao.unredd.portal.ConfigFolder;
import org.fao.unredd.portal.DefaultConfig;
import org.fao.unredd.portal.LayersModuleConfigurationProvider;
import org.fao.unredd.portal.PluginJSONConfigurationProvider;

public class AppContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		String rootPath = servletContext.getRealPath("/");
		String configInitParameter = servletContext
				.getInitParameter("PORTAL_CONFIG_DIR");
		boolean configCache = Boolean.parseBoolean(System
				.getenv("NFMS_CONFIG_CACHE"));
		DefaultConfig config = new DefaultConfig(new ConfigFolder(rootPath,
				configInitParameter), configCache);
		config.addModuleConfigurationProvider(new PluginJSONConfigurationProvider());
		config.addModuleConfigurationProvider(new LayersModuleConfigurationProvider());
		servletContext.setAttribute("config", config);

		JEEContextAnalyzer context = new JEEContextAnalyzer(new JEEContext(
				servletContext));
		servletContext.setAttribute("js-paths",
				context.getRequireJSModuleNames());
		servletContext.setAttribute("css-paths", context.getCSSRelativePaths());
		servletContext.setAttribute("requirejs-paths",
				context.getNonRequirePathMap());
		servletContext.setAttribute("requirejs-shims",
				context.getNonRequireShimMap());
		servletContext.setAttribute("plugin-configuration",
				context.getConfigurationElements());
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

	private class JEEContext implements Context {

		private ServletContext servletContext;

		public JEEContext(ServletContext servletContext) {
			this.servletContext = servletContext;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Set<String> getLibPaths() {
			return servletContext.getResourcePaths("/WEB-INF/lib");
		}

		@Override
		public InputStream getLibAsStream(String jarFileName) {
			return servletContext.getResourceAsStream(jarFileName);
		}

		@Override
		public File getClientRoot() {
			return new File(servletContext.getRealPath("/WEB-INF/classes/"));
		}

	}

}
