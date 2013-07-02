/**
 * nfms4redd Portal Interface - http://nfms4redd.org/
 *
 * (C) 2012, FAO Forestry Department (http://www.fao.org/forestry/)
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */
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
