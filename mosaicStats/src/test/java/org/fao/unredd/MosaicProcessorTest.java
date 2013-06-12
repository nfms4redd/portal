package org.fao.unredd;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.fao.unredd.layers.folder.MosaicLayerFolder;
import org.fao.unredd.statsCalculator.MixedRasterGeometryException;
import org.fao.unredd.statsCalculator.MosaicProcessor;
import org.fao.unredd.statsCalculator.OutputBuilder;
import org.fao.unredd.statsCalculator.StatsIndicatorConstants;
import org.geotools.gce.imagemosaic.properties.time.TimeParser;
import org.junit.Test;

public class MosaicProcessorTest {

	@Test
	public void testCorruptedTiff() throws Exception {
		File temporalMosaic = new File("src/test/resources/corruptedTiff");
		MosaicProcessor mosaicProcessor = new MosaicProcessor(
				mock(OutputBuilder.class),
				new MosaicLayerFolder(temporalMosaic));
		try {
			mosaicProcessor.process(new File(
					"src/test/resources/okZonesSHP/data/zones.shp"));
			fail();
		} catch (IOException e) {
		}
	}

	@Test
	public void testSnapshotDifferentGeometry() throws Exception {
		File temporalMosaic = new File(
				"src/test/resources/snapshotDifferentGeometry");
		MosaicLayerFolder mosaicLayer = new MosaicLayerFolder(temporalMosaic);
		MosaicProcessor mosaicProcessor = new MosaicProcessor(
				mock(OutputBuilder.class), mosaicLayer);
		try {
			mosaicProcessor.process(new File(
					"src/test/resources/okZonesSHP/data/zones.shp"));
			fail();
		} catch (MixedRasterGeometryException e) {
		} finally {
			FileUtils.deleteDirectory(mosaicLayer.getWorkFolder());
		}
	}

	@Test
	public void testOkZonesSHP() throws Exception {
		File temporalMosaic = new File("src/test/resources/temporalMosaic");
		MosaicLayerFolder mosaicLayer = new MosaicLayerFolder(temporalMosaic);
		OutputBuilder outputBuilder = mock(OutputBuilder.class);
		MosaicProcessor mosaicProcessor = new MosaicProcessor(outputBuilder,
				mosaicLayer);
		File zones = new File("src/test/resources/okZonesSHP/data/zones.shp");

		mosaicProcessor.process(zones);

		try {
			File areaRaster = new MosaicLayerFolder(temporalMosaic)
					.getWorkFile(StatsIndicatorConstants.SAMPLE_AREAS_FILE_NAME);
			TimeParser timeParser = new TimeParser();
			verify(outputBuilder).addToOutput(areaRaster,
					timeParser.parse("2000").get(0),
					new File(temporalMosaic, "data/snapshot_2000.tiff"), zones,
					5, 5);
			verify(outputBuilder).addToOutput(areaRaster,
					timeParser.parse("2001").get(0),
					new File(temporalMosaic, "data/snapshot_2001.tiff"), zones,
					5, 5);
		} finally {
			File workFolder = mosaicLayer.getWorkFolder();
			if (workFolder.exists()) {
				FileUtils.deleteDirectory(workFolder);
			}
		}
	}
}
