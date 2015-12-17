package org.fao.unredd.jwebclientAnalyzer;

import static junit.framework.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class RequireTemplateTest {

	@Test
	public void checkGenerate() throws IOException {

		Map<String, String> paths = new LinkedHashMap<String, String>();
		paths.put("foo", "http://boh");
		paths.put("bar", "http://bohboh");
		paths.put("boh", "http://bohbohboh");
		Map<String, String> shims = new LinkedHashMap<String, String>();
		shims.put("bar", "[\"foo\", \"boh\"]");
		shims.put("boh", "[\"foo\"]");
		List<String> moduleNames = new ArrayList<String>();
		moduleNames.add("module1");
		moduleNames.add("module2");

		RequireTemplate template = new RequireTemplate("/test.js", paths,
				shims, moduleNames);

		String output = template.generate();
		assertTrue(output
				.indexOf("\"foo\":\"http://boh\","
						+ "\"bar\":\"http://bohboh\","
						+ "\"boh\":\"http://bohbohboh\"") != -1);
		assertTrue(output.indexOf("\"bar\":[\"foo\", \"boh\"],"
				+ "\"boh\":[\"foo\"]") != -1);
		assertTrue(output.indexOf("\"module1\",\"module2\"") != -1);
	}

	@Test
	public void checkWebResourcesDir() throws IOException {
		RequireTemplate template = new RequireTemplate(getClass()
				.getResourceAsStream("/test.js"), "webapp",
				new HashMap<String, String>(), new HashMap<String, String>(),
				new ArrayList<String>());

		String output = template.generate();
		assertTrue(output.indexOf("requirejs/webapp/modules") != -1);
	}
}
