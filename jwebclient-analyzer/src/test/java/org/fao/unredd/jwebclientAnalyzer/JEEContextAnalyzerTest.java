package org.fao.unredd.jwebclientAnalyzer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.input.BoundedInputStream;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class JEEContextAnalyzerTest {
	private static File libFolder = new File(
			"src/test/resources/test1/WEB-INF/lib");

	@BeforeClass
	public static void packTest2AsJar() throws IOException {
		assertTrue(libFolder.exists() || libFolder.mkdirs());

		File jarFile = new File(libFolder, "test2.jar");
		assertTrue(!jarFile.exists() || jarFile.delete());

		FileOutputStream stream = new FileOutputStream(jarFile);
		File jarContentRoot = new File("src/test/resources/test2");
		Collection<File> files = FileUtils.listFiles(jarContentRoot,
				TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		JarOutputStream out = new JarOutputStream(stream);
		for (File file : files) {
			String entryName = file.getPath();
			entryName = entryName
					.substring(jarContentRoot.getPath().length() + 1);
			out.putNextEntry(new ZipEntry(entryName));
			InputStream entryInputStream = new BoundedInputStream(
					new FileInputStream(file));
			IOUtils.copy(entryInputStream, out);
			entryInputStream.close();
		}
		out.close();
	}

	@AfterClass
	public static void removeTest2Jar() throws IOException {
		FileUtils.deleteDirectory(libFolder);
	}

	@Test
	public void checkTest1() {
		JEEContextAnalyzer context = new JEEContextAnalyzer(new FileContext(
				"src/test/resources/test1"));

		checkList(context.getRequireJSModuleNames(), "module1", "module2",
				"module3");
		checkList(context.getCSSRelativePaths(), "styles/general.css",
				"modules/module2.css", "modules/module3.css",
				"styles/general2.css");
		checkMapKeys(context.getNonRequirePathMap(), "jquery-ui", "fancy-box",
				"openlayers", "mustache");
		checkMapKeys(context.getNonRequireShimMap(), "fancy-box", "mustache");
		Map<String, JSONObject> confElements = context
				.getConfigurationElements();
		assertEquals("29px", confElements.get("layout")
				.getString("banner-size"));
		assertEquals(true, confElements.get("legend").getBoolean("show-title"));
		assertEquals(14, confElements.get("layer-list").getInt("top"));
	}

	@Test
	public void checkExpandedClient() {
		JEEContextAnalyzer context = new JEEContextAnalyzer(
				new ExpandedClientContext("src/test/resources/test2/nfms"));

		checkList(context.getRequireJSModuleNames(), "module3");
		checkList(context.getCSSRelativePaths(), "modules/module3.css",
				"styles/general2.css");
		checkMapKeys(context.getNonRequirePathMap(), "mustache");
		checkMapKeys(context.getNonRequireShimMap(), "mustache");
	}

	private void checkList(List<String> result, String... testEntries) {
		for (String entry : testEntries) {
			assertTrue(entry, result.remove(entry));
		}

		assertTrue(result.size() == 0);
	}

	private void checkMapKeys(Map<String, ?> result, String... testKeys) {
		for (String entry : testKeys) {
			assertTrue(result.remove(entry) != null);
		}

		assertTrue(result.size() == 0);
	}
}
