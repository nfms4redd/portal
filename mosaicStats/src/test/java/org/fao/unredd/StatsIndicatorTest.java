package org.fao.unredd;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.fao.unredd.layers.LayerFactory;
import org.fao.unredd.layers.NoSuchGeoserverLayerException;
import org.fao.unredd.layers.folder.InvalidFolderStructureException;
import org.fao.unredd.layers.folder.LayerFolderImpl;
import org.fao.unredd.layers.folder.MosaicLayerFolder;
import org.fao.unredd.statsCalculator.AreaRasterManager;
import org.fao.unredd.statsCalculator.ConfigurationException;
import org.fao.unredd.statsCalculator.MixedRasterGeometryException;
import org.fao.unredd.statsCalculator.MosaicProcessor;
import org.fao.unredd.statsCalculator.OutputGenerator;
import org.fao.unredd.statsCalculator.RasterInfo;
import org.fao.unredd.statsCalculator.StatsIndicator;
import org.fao.unredd.statsCalculator.StatsIndicatorConstants;
import org.fao.unredd.statsCalculator.generated.PresentationDataType;
import org.fao.unredd.statsCalculator.generated.ZonalStatistics;
import org.geotools.gce.imagemosaic.properties.time.TimeParser;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.referencing.CRS;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

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
	public void testOkExistingBadSampleAreas() throws Exception {
		long now = System.currentTimeMillis();
		File areaRaster = new File(
				"src/test/resources/backup-sample-areas.tiff");
		RasterInfo referenceRasterInfo = mockNonMatchingRasterInfo();
		AreaRasterManager areaRasterManager = new AreaRasterManager(areaRaster,
				referenceRasterInfo);
		areaRasterManager.createCompatibleAreaRaster();

		assertTrue(new RasterInfo(areaRaster)
				.matchesGeometry(referenceRasterInfo));
		assertTrue(areaRaster.lastModified() >= now);
	}

	private RasterInfo mockNonMatchingRasterInfo()
			throws NoSuchAuthorityCodeException, FactoryException {
		RasterInfo referenceRasterInfo = mock(RasterInfo.class);
		when(referenceRasterInfo.getWidth()).thenReturn(100);
		when(referenceRasterInfo.getHeight()).thenReturn(50);
		when(referenceRasterInfo.getCRS()).thenReturn(CRS.decode("EPSG:4326"));
		when(referenceRasterInfo.getEnvelope()).thenReturn(
				new GeneralEnvelope(new Rectangle2D.Double(2, 2, 10, 10)));
		when(referenceRasterInfo.matchesGeometry(any(RasterInfo.class)))
				.thenReturn(false);
		return referenceRasterInfo;
	}

	@Test
	public void testOkExistingBadSampleAreasCannotBeDeleted() throws Exception {
		File areaRaster = new File(
				"src/test/resources/readonly/backup-sample-areas.tiff");
		RasterInfo referenceRasterInfo = mockNonMatchingRasterInfo();
		AreaRasterManager areaRasterManager = new AreaRasterManager(areaRaster,
				referenceRasterInfo);
		try {
			areaRasterManager.createCompatibleAreaRaster();
			fail();
		} catch (IOException e) {
		}
	}

	@Test
	public void testErrorCreatingAreaRaster() throws Exception {
		File areaRaster = new File(
				"src/test/resources/readonly/new-sample-areas.tiff");
		RasterInfo referenceRasterInfo = mockNonMatchingRasterInfo();
		AreaRasterManager areaRasterManager = new AreaRasterManager(areaRaster,
				referenceRasterInfo);
		try {
			areaRasterManager.createCompatibleAreaRaster();
			fail();
		} catch (IOException e) {
		}
	}

	@Test
	public void testCorruptedTiff() throws Exception {
		File temporalMosaic = new File("src/test/resources/corruptedTiff");
		MosaicProcessor mosaicProcessor = new MosaicProcessor(
				mock(OutputGenerator.class), new MosaicLayerFolder(
						temporalMosaic));
		ZonalStatistics conf = new ZonalStatistics();
		conf.setPresentationData(new PresentationDataType());
		try {
			mosaicProcessor.process(new File(
					"src/test/resources/okZonesSHP/data/zones.shp"), conf);
			fail();
		} catch (IOException e) {
		}
	}

	@Test
	public void testSnapshotDifferentGeometry() throws Exception {
		File temporalMosaic = new File(
				"src/test/resources/snapshotDifferentGeometry");
		MosaicProcessor mosaicProcessor = new MosaicProcessor(
				mock(OutputGenerator.class), new MosaicLayerFolder(
						temporalMosaic));
		ZonalStatistics conf = new ZonalStatistics();
		conf.setPresentationData(new PresentationDataType());
		try {
			mosaicProcessor.process(new File(
					"src/test/resources/okZonesSHP/data/zones.shp"), conf);
			fail();
		} catch (MixedRasterGeometryException e) {
		}
	}

	@Test
	public void testOkZonesSHP() throws Exception {
		File temporalMosaic = new File("src/test/resources/temporalMosaic");
		MosaicLayerFolder mosaicLayer = new MosaicLayerFolder(temporalMosaic);
		OutputGenerator outputGenerator = mock(OutputGenerator.class);
		MosaicProcessor mosaicProcessor = new MosaicProcessor(outputGenerator,
				mosaicLayer);
		ZonalStatistics conf = mock(ZonalStatistics.class);
		File zones = new File("src/test/resources/okZonesSHP/data/zones.shp");
		mosaicProcessor.process(zones, conf);

		try {
			File areaRaster = new MosaicLayerFolder(temporalMosaic)
					.getWorkFile(StatsIndicatorConstants.SAMPLE_AREAS_FILE_NAME);
			verify(outputGenerator).generateOutput(areaRaster,
					new TimeParser().parse("2000").get(0).toString(),
					new File(temporalMosaic, "data/snapshot_2000.tiff"), zones,
					conf, 5, 5);
			verify(outputGenerator).generateOutput(areaRaster,
					new TimeParser().parse("2001").get(0).toString(),
					new File(temporalMosaic, "data/snapshot_2001.tiff"), zones,
					conf, 5, 5);
		} finally {
			File workFolder = mosaicLayer.getWorkFolder();
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
		try {
			statsIndicator.run();
			assertTrue(layer.getOutputs().size() == 1);
			assertTrue(layer.getOutput(StatsIndicatorConstants.OUTPUT_ID) != null);
		} finally {
			// Clean up
			FileUtils.deleteDirectory(layer.getOutputFolder());
		}
	}

}
