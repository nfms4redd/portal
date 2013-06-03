package org.fao.unredd;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.fao.unredd.layers.LayerFactory;
import org.fao.unredd.statsCalculator.CalculationListener;
import org.fao.unredd.statsCalculator.MixedRasterGeometryException;
import org.fao.unredd.statsCalculator.MosaicLayerFolder;
import org.fao.unredd.statsCalculator.StatsLayerFolder;
import org.junit.Ignore;
import org.junit.Test;

public class StatsCalculatorTest {

	private CalculationListener executionWithMosaic(File layer, File mosaic)
			throws Exception {
		LayerFactory layerFactory = mock(LayerFactory.class);
		when(layerFactory.newMosaicLayer(anyString())).thenReturn(
				new MosaicLayerFolder(mosaic));
		CalculationListener calculationListener = mock(CalculationListener.class);

		StatsLayerFolder statsLayerFolder = new StatsLayerFolder(layer);
		statsLayerFolder.run(calculationListener, layerFactory);

		return calculationListener;
	}

	@Ignore
	@Test
	public void testOkExistingBadSampleAreas() throws Exception {
		// File mosaic = new File(
		// "src/test/resources/temporalMosaicExistingBadSampleAreas");
		// File layer = new File("src/test/resources/okZonesSHP");
		// File areaRaster = new StatsLayerFolder(layer)
		// .getSampleAreasRasterFile(new MosaicLayerFolder(mosaic));
		// File backupAreaRaster = new File(areaRaster.getParentFile(),
		// "backup-sample-areas.tiff");
		// IOUtils.copy(new FileInputStream(backupAreaRaster),
		// new FileOutputStream(areaRaster));
		//
		// CalculationListener calculationListener = executionWithMosaic(layer,
		// mosaic);
		//
		// // clean up before checks
		// assertTrue(!areaRaster.exists() || areaRaster.delete());
		//
		// verify(calculationListener).calculate(
		// new File(mosaic, StatsLayerFolder.SAMPLE_AREAS_RELATIVE_PATH),
		// new File(mosaic, "data/snapshot_2000.tiff"),
		// new File(layer, "data/zones.shp"), "id");
		// verify(calculationListener).calculate(
		// new File(mosaic, StatsLayerFolder.SAMPLE_AREAS_RELATIVE_PATH),
		// new File(mosaic, "data/snapshot_2001.tiff"),
		// new File(layer, "data/zones.shp"), "id");
	}

	@Ignore
	@Test
	public void testOkExistingBadSampleAreasCannotBeDeleted() throws Exception {
//		File mosaic = new File(
//				"src/test/resources/temporalMosaicExistingBadSampleAreas");
//		File layer = new File("src/test/resources/okZonesSHP");
//		File areaRaster = new StatsLayerFolder(layer)
//				.getSampleAreasRasterFile(new MosaicLayerFolder(mosaic));
//		File backupAreaRaster = new File(areaRaster.getParentFile(),
//				"backup-sample-areas.tiff");
//		IOUtils.copy(new FileInputStream(backupAreaRaster),
//				new FileOutputStream(areaRaster));
//		assertTrue(areaRaster.getParentFile().setReadOnly()
//				&& !areaRaster.delete());
//		try {
//			executionWithMosaic(layer, mosaic);
//			fail();
//		} catch (IOException e) {
//		} finally {
//			// clean up
//			areaRaster.getParentFile().setWritable(true);
//			assertTrue(!areaRaster.exists() || areaRaster.delete());
//		}
	}

	@Ignore
	@Test
	public void testSnapshotDifferentGeometry() throws Exception {
//		File temporalMosaic = new File(
//				"src/test/resources/snapshotDifferentGeometry");
//		File layer = new File("src/test/resources/okZonesSHP");
//		try {
//			executionWithMosaic(layer, temporalMosaic);
//			fail();
//		} catch (MixedRasterGeometryException e) {
//		} finally {
//			File areaRaster = new StatsLayerFolder(layer)
//					.getSampleAreasRasterFile(new MosaicLayerFolder(
//							temporalMosaic));
//			// clean up before checks
//			assertTrue(!areaRaster.exists() || areaRaster.delete());
//		}
	}

	@Ignore
	@Test
	public void testErrorCreatingAreaRaster() throws Exception {
//		File temporalMosaic = new File(
//				"src/test/resources/errorCreatingAreaRaster");
//		File layer = new File("src/test/resources/okZonesSHP");
//		File mosaicWorkFolder = new MosaicLayerFolder(temporalMosaic)
//				.getWorkFolder();
//		assertTrue(mosaicWorkFolder.exists() || mosaicWorkFolder.mkdir());
//		mosaicWorkFolder.setReadOnly();
//		try {
//			executionWithMosaic(layer, temporalMosaic);
//			fail();
//		} catch (IOException e) {
//		} finally {
//			File areaRaster = new StatsLayerFolder(layer)
//					.getSampleAreasRasterFile(new MosaicLayerFolder(
//							temporalMosaic));
//			// clean up before checks
//			assertTrue(!areaRaster.exists() || areaRaster.delete());
//		}
	}

	@Test
	public void testCorruptedTiff() throws Exception {
		File temporalMosaic = new File("src/test/resources/corruptedTiff");
		File layer = new File("src/test/resources/okZonesSHP");
		new StatsLayerFolder(layer).getConfigurationFolder().setReadOnly();
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
		CalculationListener calculationListener = executionWithMosaic(layer,
				temporalMosaic);

		try {
			verify(calculationListener).calculate(
					new File(temporalMosaic,
							StatsLayerFolder.SAMPLE_AREAS_RELATIVE_PATH),
					new File(temporalMosaic, "data/snapshot_2000.tiff"),
					new File(layer, "data/zones.shp"), "id");
			verify(calculationListener).calculate(
					new File(temporalMosaic,
							StatsLayerFolder.SAMPLE_AREAS_RELATIVE_PATH),
					new File(temporalMosaic, "data/snapshot_2001.tiff"),
					new File(layer, "data/zones.shp"), "id");
		} finally {
			File workFolder = new MosaicLayerFolder(temporalMosaic)
					.getWorkFolder();
			if (workFolder.exists()) {
				FileUtils.deleteDirectory(workFolder);
			}
		}
	}
}
