package org.fao.unredd.layers;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.fao.unredd.layers.folder.MosaicLayerFolder;
import org.junit.Before;
import org.junit.Test;

public class GeoserverDataLocatorTest {

	private GeoserverDataLocator locator;
	private File geoserverDataDir = new File(
			"src/test/resources/geoserver_data_dir");

	@Before
	public void setup() {
		locator = new GeoserverDataLocator(geoserverDataDir);
	}

	@Test
	public void testGetMosaic() throws Exception {
		File layerFolder = new File("src/test/resources/any_layer_conf_folder");
		Location location = locator.locate(new MosaicLayerFolder(
				"unredd_ws:population", layerFolder));
		FileUtils.deleteDirectory(layerFolder);
		assertEquals(location, new FileLocation(new File(geoserverDataDir,
				"nfms/unredd/evolucion_despoblacion/data")));
	}

	@Test
	public void testGetShapefile() throws Exception {
		File layerFolder = new File("src/test/resources/any_layer_conf_folder");
		Location location = locator.locate(new MosaicLayerFolder(
				"unredd_ws:roads", layerFolder));
		FileUtils.deleteDirectory(layerFolder);
		assertEquals(location, new FileLocation(new File(geoserverDataDir,
				"nfms/datos/ARG_roads.shp")));
	}

	@Test
	public void testGetShapefileFolder() throws Exception {
		File layerFolder = new File("src/test/resources/any_layer_conf_folder");
		Location location = locator.locate(new MosaicLayerFolder(
				"unredd_ws:rails", layerFolder));
		FileUtils.deleteDirectory(layerFolder);
		assertEquals(location, new FileLocation(new File(geoserverDataDir,
				"nfms/datos/ARG_rails.shp")));
	}

	@Test
	public void testPostGIS() throws Exception {
		File layerFolder = new File("src/test/resources/any_layer_conf_folder");
		Location location = locator.locate(new MosaicLayerFolder(
				"unredd_ws:admin", layerFolder));
		FileUtils.deleteDirectory(layerFolder);
		assertEquals(location, new DBLocation("localhost", "5432", "geoserver",
				"gis", "admin1", "postgres"));
	}
}
