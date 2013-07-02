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
package org.fao.unredd;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.fao.unredd.layers.DataLocator;
import org.fao.unredd.layers.FileLocation;
import org.fao.unredd.layers.LayerFactory;
import org.fao.unredd.layers.OutputDescriptor;
import org.fao.unredd.layers.Outputs;
import org.fao.unredd.layers.PasswordGetter;
import org.fao.unredd.layers.folder.FolderLayerFactory;
import org.fao.unredd.layers.folder.LayerFolderImpl;
import org.fao.unredd.layers.folder.MosaicLayerFolder;
import org.fao.unredd.statsCalculator.ConfigurationException;
import org.fao.unredd.statsCalculator.StatsIndicator;
import org.junit.Ignore;
import org.junit.Test;

public class StatsIndicatorTest {

	private static final String LAYER_NAME = "workspace:layer";

	@Ignore
	@Test
	public void testNonExistentFieldId() throws Exception {
		// File mosaic = new File("src/test/resources/data/temporalMosaic");
		// File file = new File("src/test/resources/nonExistentField");
		// try {
		// // do the call
		// fail();
		// } catch (ConfigurationException e) {
		// }
		fail();
	}

	private StatsIndicator newStatsIndicator(File file)
			throws IllegalArgumentException, IOException {
		return new StatsIndicator(mock(DataLocator.class),
				mock(LayerFactory.class), new LayerFolderImpl(LAYER_NAME, file));
	}

	@Test
	public void testUnexistantConfigurationFolder() throws Exception {
		File file = new File("src/test/resources/noConfigurationDir");
		try {
			StatsIndicator indicator = newStatsIndicator(file);
			indicator.run(mock(PasswordGetter.class));
			fail();
		} catch (ConfigurationException e) {
		}
	}

	@Test
	public void testBadDateFormat() throws Exception {
		try {
			StatsIndicator indicator = newStatsIndicator(new File(
					"src/test/resources/badTimeFormat"));
			indicator.run(mock(PasswordGetter.class));
			fail();
		} catch (ConfigurationException e) {
		}
	}

	@Test
	public void testMosaicLayerCreatedIfNotExisting() throws Exception {
		File layerFolderRoot = new File("src/test/resources/portal_data_dir");
		LayerFactory layerFactory = new FolderLayerFactory(layerFolderRoot);
		DataLocator dataLocator = mock(DataLocator.class);
		when(dataLocator.locate(isA(LayerFolderImpl.class))).thenReturn(
				new FileLocation(new File(
						"src/test/resources/data/zones/zones.shp")));
		when(dataLocator.locate(isA(MosaicLayerFolder.class))).thenReturn(
				new FileLocation(new File(
						"src/test/resources/data/temporalMosaic")));
		StatsIndicator statsIndicator = new StatsIndicator(dataLocator,
				layerFactory, layerFactory.newLayer("unredd:vector"));
		statsIndicator.run(mock(PasswordGetter.class));
		File temporalMosaic1 = new File(layerFolderRoot,
				"unredd/temporalMosaic1");
		File temporalMosaic2 = new File(layerFolderRoot,
				"unredd/temporalMosaic2");
		assertTrue(temporalMosaic1.exists());
		assertTrue(temporalMosaic2.exists());
		FileUtils.deleteDirectory(temporalMosaic1);
		FileUtils.deleteDirectory(temporalMosaic2);
		FileUtils.deleteDirectory(new File(layerFolderRoot,
				"unredd/vector/output"));
	}

	@Test
	public void testOutputs() throws Exception {
		LayerFactory layerFactory = mock(LayerFactory.class);
		File temporalMosaicFile = new File(
				"src/test/resources/data/temporalMosaic");
		MosaicLayerFolder mosaicLayer = new MosaicLayerFolder(LAYER_NAME,
				temporalMosaicFile);
		when(layerFactory.newMosaicLayer(anyString())).thenReturn(mosaicLayer);
		File vectorLayerFile = new File("src/test/resources/okZonesSHP");
		LayerFolderImpl layer = new LayerFolderImpl(LAYER_NAME, vectorLayerFile);
		DataLocator dataLocator = mock(DataLocator.class);
		when(dataLocator.locate(mosaicLayer)).thenReturn(
				new FileLocation(temporalMosaicFile));
		when(dataLocator.locate(layer)).thenReturn(
				new FileLocation(new File(
						"src/test/resources/data/zones/zones.shp")));
		StatsIndicator statsIndicator = new StatsIndicator(dataLocator,
				layerFactory, layer);
		try {
			statsIndicator.run(mock(PasswordGetter.class));
			Outputs outputs = layer.getOutputs();
			assertTrue(outputs.size() == 2);
			for (OutputDescriptor outputDescriptor : outputs) {
				String output = layer.getOutput(outputDescriptor.getId());
				assertTrue(StringUtils.countMatches(output, "<value>") == 6);
			}
		} finally {
			// Clean up
			FileUtils.deleteDirectory(layer.getOutputFolder());
			FileUtils.deleteDirectory(mosaicLayer.getWorkFolder());
		}
	}

}
