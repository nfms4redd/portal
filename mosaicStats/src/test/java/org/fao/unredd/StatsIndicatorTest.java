package org.fao.unredd;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.fao.unredd.layers.LayerFactory;
import org.fao.unredd.layers.folder.LayerFolderImpl;
import org.fao.unredd.layers.folder.MosaicLayerFolder;
import org.fao.unredd.statsCalculator.ConfigurationException;
import org.fao.unredd.statsCalculator.Execution;
import org.fao.unredd.statsCalculator.MixedRasterGeometryException;
import org.fao.unredd.statsCalculator.StatsIndicator;
import org.junit.Ignore;
import org.junit.Test;

public class StatsIndicatorTest {

	private Execution[] executionWithMosaic(File layer, File mosaic)
			throws Exception {
		LayerFactory layerFactory = mock(LayerFactory.class);
		when(layerFactory.newMosaicLayer(anyString())).thenReturn(
				new MosaicLayerFolder(mosaic));

		StatsIndicator statsIndicator = new StatsIndicator(layerFactory,
				new LayerFolderImpl(layer));
		statsIndicator.analyze();

		return statsIndicator.getExecutions();
	}

	@Ignore
	@Test
	public void testNonExistentFieldId() throws Exception {
		File mosaic = new File("src/test/resources/temporalMosaic");
		File file = new File("src/test/resources/nonExistentField");
		try {
			executionWithMosaic(file, mosaic);
			fail();
		} catch (ConfigurationException e) {
		}
	}

	@Test
	public void testUnexistantDataFolder() throws Exception {
		File mosaic = new File("src/test/resources/temporalMosaic");
		File file = new File("src/test/resources/noDataDir");
		try {
			executionWithMosaic(file, mosaic);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testUnexistantConfigurationFolder() throws Exception {
		File mosaic = new File("src/test/resources/temporalMosaic");
		File file = new File("src/test/resources/noConfigurationDir");
		try {
			executionWithMosaic(file, mosaic);
			fail();
		} catch (ConfigurationException e) {
		}
	}

	@Test
	public void testOkExistingBadSampleAreas() throws Exception {
		File mosaic = new File(
				"src/test/resources/temporalMosaicExistingBadSampleAreas");
		File layer = new File("src/test/resources/okZonesSHP");
		File areaRaster = new MosaicLayerFolder(mosaic)
				.getWorkFile(StatsIndicator.SAMPLE_AREAS_FILE_NAME);
		File backupAreaRaster = new File(areaRaster.getParentFile(),
				"backup-sample-areas.tiff");
		IOUtils.copy(new FileInputStream(backupAreaRaster),
				new FileOutputStream(areaRaster));

		Execution[] executions = executionWithMosaic(layer, mosaic);

		// clean up before checks
		assertTrue(!areaRaster.exists() || areaRaster.delete());

		assertTrue(executions[0].equals(new Execution(areaRaster, "2000",
				new File(mosaic, "data/snapshot_2000.tiff"), new File(layer,
						"data/zones.shp"), "id", 5, 5)));
		assertTrue(executions[1].equals(new Execution(areaRaster, "2001",
				new File(mosaic, "data/snapshot_2001.tiff"), new File(layer,
						"data/zones.shp"), "id", 5, 5)));
	}

	@Test
	public void testOkExistingBadSampleAreasCannotBeDeleted() throws Exception {
		File mosaic = new File(
				"src/test/resources/temporalMosaicExistingBadSampleAreas");
		File layer = new File("src/test/resources/okZonesSHP");
		File areaRaster = new MosaicLayerFolder(mosaic)
				.getWorkFile(StatsIndicator.SAMPLE_AREAS_FILE_NAME);
		File backupAreaRaster = new File(areaRaster.getParentFile(),
				"backup-sample-areas.tiff");
		IOUtils.copy(new FileInputStream(backupAreaRaster),
				new FileOutputStream(areaRaster));
		assertTrue(areaRaster.getParentFile().setReadOnly()
				&& !areaRaster.delete());
		try {
			executionWithMosaic(layer, mosaic);
			fail();
		} catch (IOException e) {
		} finally {
			// clean up
			areaRaster.getParentFile().setWritable(true);
			assertTrue(!areaRaster.exists() || areaRaster.delete());
		}
	}

	@Test
	public void testSnapshotDifferentGeometry() throws Exception {
		File temporalMosaic = new File(
				"src/test/resources/snapshotDifferentGeometry");
		File layer = new File("src/test/resources/okZonesSHP");
		try {
			executionWithMosaic(layer, temporalMosaic);
			fail();
		} catch (MixedRasterGeometryException e) {
		} finally {
			File areaRaster = new MosaicLayerFolder(temporalMosaic)
					.getWorkFile(StatsIndicator.SAMPLE_AREAS_FILE_NAME);
			// clean up before checks
			assertTrue(!areaRaster.exists() || areaRaster.delete());
		}
	}

	@Test
	public void testErrorCreatingAreaRaster() throws Exception {
		File temporalMosaic = new File(
				"src/test/resources/errorCreatingAreaRaster");
		File layer = new File("src/test/resources/okZonesSHP");
		File mosaicWorkFolder = new MosaicLayerFolder(temporalMosaic)
				.getWorkFolder();
		assertTrue(mosaicWorkFolder.exists() || mosaicWorkFolder.mkdir());
		mosaicWorkFolder.setReadOnly();
		try {
			executionWithMosaic(layer, temporalMosaic);
			fail();
		} catch (IOException e) {
		} finally {
			File areaRaster = new MosaicLayerFolder(temporalMosaic)
					.getWorkFile(StatsIndicator.SAMPLE_AREAS_FILE_NAME);
			// clean up before checks
			assertTrue(!areaRaster.exists() || areaRaster.delete());
		}
	}

	@Test
	public void testCorruptedTiff() throws Exception {
		File temporalMosaic = new File("src/test/resources/corruptedTiff");
		File layer = new File("src/test/resources/okZonesSHP");
		new LayerFolderImpl(layer).getConfigurationFolder().setReadOnly();
		try {
			executionWithMosaic(layer, temporalMosaic);
			fail();
		} catch (IOException e) {
		}
	}

	@Test
	public void testOkZonesSHP() throws Exception {
		File temporalMosaic = new File("src/test/resources/temporalMosaic");
		File layer = new File("src/test/resources/okZonesSHP");
		Execution[] executions = executionWithMosaic(layer, temporalMosaic);
		File areaRaster = new MosaicLayerFolder(temporalMosaic)
				.getWorkFile(StatsIndicator.SAMPLE_AREAS_FILE_NAME);
		try {
			assertTrue(executions[0].equals(new Execution(areaRaster, "2000",
					new File(temporalMosaic, "data/snapshot_2000.tiff"),
					new File(layer, "data/zones.shp"), "id", 5, 5)));
			assertTrue(executions[1].equals(new Execution(areaRaster, "2001",
					new File(temporalMosaic, "data/snapshot_2001.tiff"),
					new File(layer, "data/zones.shp"), "id", 5, 5)));
		} finally {
			File workFolder = new MosaicLayerFolder(temporalMosaic)
					.getWorkFolder();
			if (workFolder.exists()) {
				FileUtils.deleteDirectory(workFolder);
			}
		}
	}

	@Test
	public void testOutputs() throws Exception {
		LayerFactory layerFactory = mock(LayerFactory.class);
		when(layerFactory.newMosaicLayer(anyString())).thenReturn(
				new MosaicLayerFolder(new File(
						"src/test/resources/temporalMosaic")));
		LayerFolderImpl layer = new LayerFolderImpl(new File(
				"src/test/resources/okZonesSHP"));
		StatsIndicator statsIndicator = new StatsIndicator(layerFactory, layer);
		statsIndicator.analyze();
		try {
			statsIndicator.run();
			assertTrue(layer.getOutputs().size() == 1);
			assertTrue(layer.getOutput(StatsIndicator.OUTPUT_ID) != null);
		} finally {
			// Clean up
			FileUtils.deleteDirectory(layer.getOutputFolder());
		}
	}

}
