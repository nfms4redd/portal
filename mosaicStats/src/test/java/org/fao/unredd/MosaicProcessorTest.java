package org.fao.unredd;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.commons.io.FileUtils;
import org.fao.unredd.layers.folder.MosaicLayerFolder;
import org.fao.unredd.statsCalculator.MixedRasterGeometryException;
import org.fao.unredd.statsCalculator.MosaicProcessor;
import org.fao.unredd.statsCalculator.OutputGenerator;
import org.fao.unredd.statsCalculator.StatsIndicatorConstants;
import org.fao.unredd.statsCalculator.generated.PresentationDataType;
import org.fao.unredd.statsCalculator.generated.ZonalStatistics;
import org.junit.Test;

public class MosaicProcessorTest {

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
					"src/test/resources/okZonesSHP/data/zones.shp"),
					new SimpleDateFormat(), conf);
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
				mock(OutputGenerator.class), mosaicLayer);
		ZonalStatistics conf = new ZonalStatistics();
		conf.setPresentationData(new PresentationDataType());
		try {
			mosaicProcessor.process(new File(
					"src/test/resources/okZonesSHP/data/zones.shp"),
					new SimpleDateFormat(), conf);
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
		OutputGenerator outputGenerator = mock(OutputGenerator.class);
		MosaicProcessor mosaicProcessor = new MosaicProcessor(outputGenerator,
				mosaicLayer);
		ZonalStatistics conf = mock(ZonalStatistics.class);
		File zones = new File("src/test/resources/okZonesSHP/data/zones.shp");

		mosaicProcessor.process(zones, new SimpleDateFormat("yyyy"), conf);

		try {
			File areaRaster = new MosaicLayerFolder(temporalMosaic)
					.getWorkFile(StatsIndicatorConstants.SAMPLE_AREAS_FILE_NAME);
			verify(outputGenerator).generateOutput(areaRaster, "2000",
					new File(temporalMosaic, "data/snapshot_2000.tiff"), zones,
					conf, 5, 5);
			verify(outputGenerator).generateOutput(areaRaster, "2001",
					new File(temporalMosaic, "data/snapshot_2001.tiff"), zones,
					conf, 5, 5);
		} finally {
			File workFolder = mosaicLayer.getWorkFolder();
			if (workFolder.exists()) {
				FileUtils.deleteDirectory(workFolder);
			}
		}
	}
}
