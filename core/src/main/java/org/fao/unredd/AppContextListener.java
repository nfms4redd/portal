package org.fao.unredd;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

	//	String indicatorsFolder = config.getIndicatorsFolder();
		LayerFactory layerFactory;
		try {
			layerFactory = new DBLayerFactory("workspace:newlayer");
			servletContext.setAttribute("layer-factory", layerFactory);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ArrayList<String> js = new ArrayList<String>();
		ArrayList<String> css = new ArrayList<String>();
		Set<String> libJars = servletContext.getResourcePaths("/WEB-INF/lib");
		for (Object jar : libJars) {
			InputStream jarStream = servletContext.getResourceAsStream(jar
					.toString());
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(
					jarStream));
			ZipEntry entry;
			try {
				while ((entry = zis.getNextEntry()) != null) {
					String entryPath = entry.getName();
					if (entryPath.startsWith("nfms/modules")) {
						String entryFileName = new File(entryPath).getName();
						if (entryPath.endsWith(".js")) {
							js.add(entryFileName.substring(0,
									entryFileName.length() - 3));
						} else if (entryPath.endsWith(".css")) {
							css.add("modules/" + entryFileName);
						}
					} else if (entryPath.startsWith("nfms/styles")
							&& entryPath.endsWith(".css")) {
						String relativePath = new File("nfms/styles").toURI().relativize(new File(entryPath).toURI()).getPath();
						css.add("styles/" + relativePath);
					}
				}
			} catch (IOException e) {
				throw new RuntimeException("Cannot start the application", e);
			} finally {
				try {
					zis.close();
				} catch (IOException e) {
				}
			}
			servletContext.setAttribute("js-paths", js);
			servletContext.setAttribute("css-paths", css);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
