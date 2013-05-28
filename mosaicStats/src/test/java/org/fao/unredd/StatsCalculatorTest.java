package org.fao.unredd;

import static org.junit.Assert.assertFalse;
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
import org.fao.unredd.statsCalculator.CalculationListener;
import org.fao.unredd.statsCalculator.GeoserverLayerFolderTranslator;
import org.fao.unredd.statsCalculator.InvalidFolderStructureException;
import org.fao.unredd.statsCalculator.MixedRasterGeometryException;
import org.fao.unredd.statsCalculator.MosaicLayerFolder;
import org.fao.unredd.statsCalculator.SnapshotNamingException;
import org.fao.unredd.statsCalculator.StatsLayerFolder;
import org.junit.Ignore;
import org.junit.Test;

public class StatsCalculatorTest {

	@Ignore
	@Test
	public void testUnexistantFolder() throws Exception {
		File file = new File("does not exist");
		assertFalse(file.exists());
		try {
			new StatsLayerFolder(file);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	@Ignore
	@Test
	public void testUnexistantDataFolder() throws Exception {
		File file = new File("src/test/resources/noDataDir");
		try {
			new StatsLayerFolder(file);
			fail();
		} catch (InvalidFolderStructureException e) {
			assertTrue(e.getOffendingFile().equals(new File(file, "data")));
		}
	}

	@Ignore
	@Test
	public void testUnexistantConfigurationFolder() throws Exception {
		File file = new File("src/test/resources/noConfigurationDir");
		try {
			new StatsLayerFolder(file);
			fail();
		} catch (InvalidFolderStructureException e) {
			assertTrue(e.getOffendingFile().equals(
					new File(file, "configuration")));
		}
	}

	@Ignore
	@Test
	public void testEmptyMosaic() throws Exception {
		File file = new File("src/test/resources/emptyMosaic");
		try {
			new StatsLayerFolder(file);
			fail();
		} catch (InvalidFolderStructureException e) {
			assertTrue(e.getOffendingFile().equals(new File(file, "data")));
		}
	}

	@Ignore
	@Test
	public void testBadSnapshotNaming() throws Exception {
		File file = new File("src/test/resources/badSnapshotNaming");
		try {
			new StatsLayerFolder(file);
			fail();
		} catch (SnapshotNamingException e) {
		}
	}

	@Ignore
	@Test
	public void testBadSnapshotTimeFormat() throws Exception {
		File file = new File("src/test/resources/badSnapshotTimeFormat");
		try {
			new StatsLayerFolder(file);
			fail();
		} catch (SnapshotNamingException e) {
		}
	}

	@Ignore
	@Test
	public void testBadTimeregexProperties() throws Exception {
		File file = new File("src/test/resources/badTimeregexProperties");
		try {
			new StatsLayerFolder(file);
			fail();
		} catch (InvalidFolderStructureException e) {
			assertTrue(e.getOffendingFile().equals(
					new File(file, "data/timeregex.properties")));
		}
	}

	@Ignore
	@Test
	public void testNonExistingTimeregexProperties() throws Exception {
		File file = new File(
				"src/test/resources/nonExistingTimeregexProperties");
		try {
			new StatsLayerFolder(file);
			fail();
		} catch (InvalidFolderStructureException e) {
			assertTrue(e.getOffendingFile().equals(
					new File(file, "data/timeregex.properties")));
		}
	}

	@Ignore
	@Test
	public void testOkThreeSnapshots() throws Exception {
		File folderBase = new File("src/test/resources/okThreeSnapshots");
		StatsLayerFolder statsCalculator = new StatsLayerFolder(folderBase);
		CalculationListener calculationListener = mock(CalculationListener.class);
		statsCalculator.run(calculationListener, null);

		// File areaRaster = statsCalculator.getSampleAreasFile();
		// // clean up before checks
		// assertTrue(!areaRaster.exists() || areaRaster.delete());
		//
		// verifyOk(folderBase, calculationListener, areaRaster);
		fail();
	}

	@Ignore
	@Test
	public void testOkExistingBadSampleAreas() throws Exception {
		File folderBase = new File(
				"src/test/resources/okExistingBadSampleAreas");
		StatsLayerFolder statsCalculator = new StatsLayerFolder(folderBase);
		File areaRaster = null;// statsCalculator.getSampleAreasFile();
		fail();
		File backupAreaRaster = new File(areaRaster.getParentFile(),
				"backup-sample-areas.tiff");
		IOUtils.copy(new FileInputStream(backupAreaRaster),
				new FileOutputStream(areaRaster));
		CalculationListener calculationListener = mock(CalculationListener.class);
		statsCalculator.run(calculationListener, null);

		// clean up before checks
		assertTrue(!areaRaster.exists() || areaRaster.delete());

		verifyOk(folderBase, calculationListener, areaRaster);
	}

	@Ignore
	@Test
	public void testOkExistingBadSampleAreasCannotBeDeleted() throws Exception {
		File folderBase = new File(
				"src/test/resources/okExistingBadSampleAreas");
		StatsLayerFolder statsCalculator = new StatsLayerFolder(folderBase);
		File areaRaster = null;// statsCalculator.getSampleAreasFile();
		fail();
		File backupAreaRaster = new File(areaRaster.getParentFile(),
				"backup-sample-areas.tiff");
		IOUtils.copy(new FileInputStream(backupAreaRaster),
				new FileOutputStream(areaRaster));
		assertTrue(areaRaster.getParentFile().setReadOnly()
				&& !areaRaster.delete());
		CalculationListener calculationListener = mock(CalculationListener.class);
		try {
			statsCalculator.run(calculationListener, null);
			fail();
		} catch (IOException e) {
		}

		// clean up before checks
		areaRaster.getParentFile().setWritable(true);
		assertTrue(!areaRaster.exists() || areaRaster.delete());
	}

	private void verifyOk(File folderBase,
			CalculationListener calculationListener, File areaRaster) {
		// verify(calculationListener).calculate(areaRaster,
		// new File(folderBase, "data/snapshot_2000.tiff"),
		// "unredd:provinces", "name");
		// verify(calculationListener).calculate(areaRaster,
		// new File(folderBase, "data/snapshot_2000.tiff"),
		// "unredd:projects", "id");
		// verify(calculationListener).calculate(areaRaster,
		// new File(folderBase, "data/snapshot_2001.tiff"),
		// "unredd:provinces", "name");
		// verify(calculationListener).calculate(areaRaster,
		// new File(folderBase, "data/snapshot_2001.tiff"),
		// "unredd:projects", "id");
		fail();
	}

	@Ignore
	@Test
	public void testSnapshotDifferentGeometry() throws Exception {
		File folderBase = new File(
				"src/test/resources/snapshotDifferentGeometry");
		StatsLayerFolder statsCalculator = new StatsLayerFolder(folderBase);
		CalculationListener calculationListener = mock(CalculationListener.class);
		try {
			statsCalculator.run(calculationListener, null);
			fail();
		} catch (MixedRasterGeometryException e) {
		} finally {
			// File areaRaster = statsCalculator.getSampleAreasFile();
			// // clean up before checks
			// assertTrue(!areaRaster.exists() || areaRaster.delete());
			fail();
		}
	}

	@Ignore
	@Test
	public void testErrorCreatingAreaRaster() throws Exception {
		File folderBase = new File("src/test/resources/errorCreatingAreaRaster");
		StatsLayerFolder statsCalculator = new StatsLayerFolder(folderBase);
		statsCalculator.getConfigurationFolder().setReadOnly();
		CalculationListener calculationListener = mock(CalculationListener.class);
		try {
			statsCalculator.run(calculationListener, null);
			fail();
		} catch (IOException e) {
		} finally {
			// File areaRaster = statsCalculator.getSampleAreasFile();
			// // clean up before checks
			// assertTrue(!areaRaster.exists() || areaRaster.delete());
			fail();
		}
	}

	@Ignore
	@Test
	public void testCorruptedTiff() throws Exception {
		File folderBase = new File("src/test/resources/corruptedTiff");
		StatsLayerFolder statsCalculator = new StatsLayerFolder(folderBase);
		CalculationListener calculationListener = mock(CalculationListener.class);
		try {
			statsCalculator.run(calculationListener, null);
			fail();
		} catch (IOException e) {
		}
	}

	@Test
	public void testOkZonesSHP() throws Exception {
		File temporalMosaic = new File("src/test/resources/temporalMosaic");
		File layer = new File("src/test/resources/okZonesSHP");
		GeoserverLayerFolderTranslator geoserverLayerFactory = mock(GeoserverLayerFolderTranslator.class);
		when(geoserverLayerFactory.getLayerFolder(anyString())).thenReturn(
				temporalMosaic);
		CalculationListener calculationListener = mock(CalculationListener.class);

		StatsLayerFolder statsLayerFolder = new StatsLayerFolder(layer);
		statsLayerFolder.run(calculationListener, geoserverLayerFactory);

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
