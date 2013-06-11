package org.fao.unredd;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.fao.unredd.layers.LayerFactory;
import org.fao.unredd.layers.NoSuchGeoserverLayerException;
import org.fao.unredd.layers.folder.InvalidFolderStructureException;
import org.fao.unredd.layers.folder.LayerFolderImpl;
import org.fao.unredd.layers.folder.MosaicLayerFolder;
import org.fao.unredd.statsCalculator.ConfigurationException;
import org.fao.unredd.statsCalculator.StatsIndicator;
import org.fao.unredd.statsCalculator.StatsIndicatorConstants;
import org.junit.Ignore;
import org.junit.Test;

public class StatsIndicatorTest {

	@Ignore
	@Test
	public void testNonExistentFieldId() throws Exception {
		// File mosaic = new File("src/test/resources/temporalMosaic");
		// File file = new File("src/test/resources/nonExistentField");
		// try {
		// // do the call
		// fail();
		// } catch (ConfigurationException e) {
		// }
		fail();
	}

	@Test
	public void testUnexistantDataFolder() throws Exception {
		File file = new File("src/test/resources/noDataDir");
		try {
			newStatsIndicator(file);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	private StatsIndicator newStatsIndicator(File file)
			throws NoSuchGeoserverLayerException,
			InvalidFolderStructureException {
		return new StatsIndicator(mock(LayerFactory.class),
				new LayerFolderImpl(file));
	}

	@Test
	public void testUnexistantConfigurationFolder() throws Exception {
		File file = new File("src/test/resources/noConfigurationDir");
		try {
			StatsIndicator indicator = newStatsIndicator(file);
			indicator.run();
			fail();
		} catch (ConfigurationException e) {
		}
	}

	@Test
	public void testBadDateFormat() throws Exception {
		try {
			StatsIndicator indicator = newStatsIndicator(new File(
					"src/test/resources/badTimeFormat"));
			indicator.run();
			fail();
		} catch (ConfigurationException e) {
		}
	}

	@Test
	public void testOutputs() throws Exception {
		LayerFactory layerFactory = mock(LayerFactory.class);
		MosaicLayerFolder mosaicLayer = new MosaicLayerFolder(new File(
				"src/test/resources/temporalMosaic"));
		when(layerFactory.newMosaicLayer(anyString())).thenReturn(mosaicLayer);
		LayerFolderImpl layer = new LayerFolderImpl(new File(
				"src/test/resources/okZonesSHP"));
		StatsIndicator statsIndicator = new StatsIndicator(layerFactory, layer);
		try {
			statsIndicator.run();
			assertTrue(layer.getOutputs().size() == 1);
			assertTrue(layer.getOutput(StatsIndicatorConstants.OUTPUT_ID) != null);
		} finally {
			// Clean up
			FileUtils.deleteDirectory(layer.getOutputFolder());
			FileUtils.deleteDirectory(mosaicLayer.getWorkFolder());
		}
	}

}
