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
import net.sf.json.JSONSerializer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;

public class JEEContextAnalyzer {
	private static Logger logger = Logger.getLogger(JEEContextAnalyzer.class);

	private ArrayList<String> js = null;
	private ArrayList<String> css = null;
	private Map<String, String> requirejsPaths = null;
	private Map<String, String> requirejsShims = null;

	public JEEContextAnalyzer(Context context) {
		js = new ArrayList<String>();
		css = new ArrayList<String>();
		requirejsPaths = new HashMap<String, String>();
		requirejsShims = new HashMap<String, String>();

		ContextEntryListener cssAndJsCollector = new ContextEntryListener() {

			@Override
			public void accept(String path, ContextEntryReader entryReader)
					throws IOException {
				boolean modules = path.startsWith("nfms/modules");
				boolean styles = path.startsWith("nfms/styles");
				File pathFile = new File(path);
				if ((styles || modules) && path.endsWith(".css")) {
					css.add(pathFile.getParentFile().getName() + "/"
							+ pathFile.getName());
				} else if (modules && path.endsWith(".js")) {
					String name = pathFile.getName();
					name = name.substring(0, name.length() - 3);
					js.add(name);
				}
			}

		};
		scanClasses(context, cssAndJsCollector);
		scanJars(context, cssAndJsCollector);

		ContextEntryListener pluginDescriptorAnalyzer = new ContextEntryListener() {

			@Override
			public void accept(String path, ContextEntryReader entryReader)
					throws IOException {
				if (path.matches("\\Qnfms/\\E\\w+\\Q-conf.json\\E")) {
					JSONObject jsonRoot = (JSONObject) JSONSerializer
							.toJSON(entryReader.getContent());
					if (jsonRoot.has("requirejs")) {
						JSONObject requireJS = jsonRoot
								.getJSONObject("requirejs");
						fill(requirejsPaths,
								(JSONObject) requireJS.get("paths"));
						fill(requirejsShims, (JSONObject) requireJS.get("shim"));
					}
				}
			}

			private void fill(Map<String, String> map, JSONObject jsonMap) {
				if (jsonMap == null) {
					return;
				}

				for (Object key : jsonMap.keySet()) {
					Object value = jsonMap.get(key.toString());
					map.put(key.toString(), value.toString());
				}
			}
		};
		scanClasses(context, pluginDescriptorAnalyzer);
		scanJars(context, pluginDescriptorAnalyzer);
	}

	private void scanClasses(Context context,
			ContextEntryListener contextEntryListener) {
		File rootFolder = context.getClientRoot();
		if (rootFolder.exists()) {
			Iterator<File> allFiles = FileUtils.iterateFiles(rootFolder,
					relevantExtensions, TrueFileFilter.INSTANCE);

			final File referenceFolder = rootFolder.getParentFile();
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
					contextEntryListener.accept(relativePath, contentReader);
				} catch (IOException e) {
					logger.info("Cannot analyze file:" + relativePath);
				}
			}
		}
	}

	private void scanJars(Context context,
			ContextEntryListener contextEntryListener) {
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
						contextEntryListener.accept(entryPath, contentReader);
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

}
