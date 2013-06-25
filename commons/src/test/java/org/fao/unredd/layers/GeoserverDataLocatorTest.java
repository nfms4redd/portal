package org.fao.unredd.layers;

import java.io.File;

import org.fao.unredd.layers.folder.LayerFolderImpl;
import org.junit.Before;
import org.junit.Test;

public class GeoserverDataLocatorTest {

	private GeoserverDataLocator locator;

	@Before
	public void setup() {
		locator = new GeoserverDataLocator(new File(
				"src/test/resources/geoserver_data_dir"));
	}

	@Test
	public void testGetMosaic() throws Exception {
		System.out.println(locator.locate(new LayerFolderImpl(
				"workspace:population", new File(
						"src/test/resources/vectorLayer"))));
		System.out
				.println(locator.locate(new LayerFolderImpl("workspace:roads",
						new File("src/test/resources/vectorLayer"))));
	}
}
