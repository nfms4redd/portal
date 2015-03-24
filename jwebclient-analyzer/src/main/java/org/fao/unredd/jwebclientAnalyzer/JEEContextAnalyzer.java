package org.fao.unredd.jwebclientAnalyzer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;

public class JEEContextAnalyzer {
	private static Logger logger = Logger.getLogger(JEEContextAnalyzer.class);

	private ArrayList<String> js = new ArrayList<String>();
	private ArrayList<String> css = new ArrayList<String>();
	private Map<String, String> requirejsPaths = new HashMap<String, String>();
	private Map<String, String> requirejsShims = new HashMap<String, String>();
	private Map<String, JSONObject> configurationMap = new HashMap<String, JSONObject>();

	public JEEContextAnalyzer(Context context) {
		this(context, "nfms", "nfms");
	}

	public JEEContextAnalyzer(Context context, String pluginConfDir,
			String webResourcesDir) {
		PluginConfigEntryListener pluginConfListener = new PluginConfigEntryListener(
				pluginConfDir);
		WebResourcesEntryListener webResourcesListener = new WebResourcesEntryListener(
				webResourcesDir);
		scanClasses(context, pluginConfListener, webResourcesListener);
		scanJars(context, pluginConfListener, webResourcesListener);
	}

	private void scanClasses(Context context,
			PluginConfigEntryListener pluginConfListener,
			WebResourcesEntryListener webResourcesListener) {
		File rootFolder = context.getClientRoot();
		if (rootFolder.exists()) {
			scanDir(context, new File(rootFolder, pluginConfListener.dir),
					pluginConfListener);
			scanDir(context, new File(rootFolder, webResourcesListener.dir),
					webResourcesListener);
		}
	}

	private void scanDir(Context context, final File dir,
			ContextEntryListener listener) {
		if (dir.isDirectory()) {
			Iterator<File> allFiles = FileUtils.iterateFiles(dir,
					relevantExtensions, TrueFileFilter.INSTANCE);

			final File referenceFolder = dir.getParentFile();
			int rootPathLength = referenceFolder.getAbsolutePath().length() + 1;
			while (allFiles.hasNext()) {
				File file = allFiles.next();
				String name = file.getAbsolutePath();
				final String relativePath = name.substring(rootPathLength);
				try {
					ContextEntryReader contentReader = new ContextEntryReader() {

						@Override
						public String getContent() throws IOException {
							InputStream input = new BufferedInputStream(
									new FileInputStream(new File(
											referenceFolder, relativePath)));
							String content = IOUtils.toString(input);
							input.close();

							return content;
						}
					};
					listener.accept(relativePath, contentReader);
				} catch (IOException e) {
					logger.info("Cannot analyze file:" + relativePath);
				}
			}
		}
	}

	private void scanJars(Context context,
			ContextEntryListener... contextEntryListeners) {
		Set<String> libJars = context.getLibPaths();
		for (Object jar : libJars) {
			InputStream jarStream = context.getLibAsStream(jar.toString());
			final ZipInputStream zis = new ZipInputStream(
					new BufferedInputStream(jarStream));
			ZipEntry entry;
			try {
				while ((entry = zis.getNextEntry()) != null) {
					String entryPath = entry.getName();
					if (relevantExtensions.accept(new File(entryPath))) {
						ContextEntryReader contentReader = new ContextEntryReader() {

							@Override
							public String getContent() throws IOException {
								return IOUtils.toString(zis);
							}
						};

						for (ContextEntryListener listener : contextEntryListeners) {
							listener.accept(entryPath, contentReader);
						}
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

		}
	}

	public List<String> getRequireJSModuleNames() {
		return js;
	}

	public List<String> getCSSRelativePaths() {
		return css;
	}

	public Map<String, String> getNonRequirePathMap() {
		return requirejsPaths;
	}

	public Map<String, String> getNonRequireShimMap() {
		return requirejsShims;
	}

	public Map<String, JSONObject> getConfigurationElements() {
		return configurationMap;
	}

	private interface ContextEntryListener {

		void accept(String path, ContextEntryReader contentReader)
				throws IOException;

	}

	private interface ContextEntryReader {
		String getContent() throws IOException;
	}

	private static IOFileFilter relevantExtensions = new IOFileFilter() {

		@Override
		public boolean accept(File file, String name) {
			return true;
		}

		@Override
		public boolean accept(File file) {
			String lowerCase = file.getName().toLowerCase();
			return lowerCase.endsWith(".js") || lowerCase.endsWith(".css")
					|| lowerCase.endsWith(".json");
		}
	};

	private class PluginConfigEntryListener implements ContextEntryListener {
		private String dir;

		public PluginConfigEntryListener(String dir) {
			this.dir = dir;
		}

		@Override
		public void accept(String path, ContextEntryReader contentReader)
				throws IOException {
			if (path.matches("\\Q" + dir + "/\\E\\w+\\Q-conf.json\\E")) {
				PluginDescriptor pluginDescriptor = new PluginDescriptor(
						contentReader.getContent());
				requirejsPaths.putAll(pluginDescriptor.getRequireJSPathsMap());
				requirejsShims.putAll(pluginDescriptor.getRequireJSShims());
				configurationMap.putAll(pluginDescriptor.getConfigurationMap());
			}
		}
	}

	private class WebResourcesEntryListener implements ContextEntryListener {
		private String dir;

		public WebResourcesEntryListener(String dir) {
			this.dir = dir;
		}

		@Override
		public void accept(String path, ContextEntryReader contentReader)
				throws IOException {
			String stylesPrefix = dir + "/styles";
			String modulesPrefix = dir + "/modules";
			File pathFile = new File(path);
			if (path.startsWith(modulesPrefix)) {
				if (path.endsWith(".css")) {
					String output = path.substring(dir.length() + 1);
					css.add(output);
				}
				if (path.endsWith(".js")) {
					String name = pathFile.getName();
					name = name.substring(0, name.length() - 3);
					js.add(name);
				}
			} else {
				if (path.startsWith(stylesPrefix) && path.endsWith(".css")) {
					String output = path.substring(dir.length() + 1);
					css.add(output);
				}
			}
		}
	}
}
