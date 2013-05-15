package org.fao.unredd;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.fao.unredd.statsCalculator.CalculationListener;
import org.fao.unredd.statsCalculator.InvalidFolderStructureException;
import org.fao.unredd.statsCalculator.MixedRasterGeometryException;
import org.fao.unredd.statsCalculator.SnapshotNamingException;
import org.fao.unredd.statsCalculator.StatsCalculator;
import org.junit.Test;

public class StatsCalculatorTest {

	@Test
	public void testUnexistantFolder() throws Exception {
		File file = new File("does not exist");
		assertFalse(file.exists());
		try {
			new StatsCalculator(file);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testUnexistantMosaicFolder() throws Exception {
		File file = new File("src/test/resources/noMosaicDir");
		try {
			new StatsCalculator(file);
			fail();
		} catch (InvalidFolderStructureException e) {
		}
	}

	@Test
	public void testUnexistantConfigurationFolder() throws Exception {
		File file = new File("src/test/resources/noConfigurationDir");
		try {
			new StatsCalculator(file);
			fail();
		} catch (InvalidFolderStructureException e) {
		}
	}

	@Test
	public void testEmptyMosaic() throws Exception {
		File file = new File("src/test/resources/emptyMosaic");
		try {
			new StatsCalculator(file);
			fail();
		} catch (InvalidFolderStructureException e) {
		}
	}

	@Test
	public void testBadSnapshotNaming() throws Exception {
		File file = new File("src/test/resources/badSnapshotNaming");
		try {
			new StatsCalculator(file);
			fail();
		} catch (SnapshotNamingException e) {
		}
	}

	@Test
	public void testBadSnapshotTimeFormat() throws Exception {
		File file = new File("src/test/resources/badSnapshotTimeFormat");
		try {
			new StatsCalculator(file);
			fail();
		} catch (SnapshotNamingException e) {
		}
	}

	@Test
	public void testBadTimeregexPropertiesKey() throws Exception {
		File file = new File("src/test/resources/badTimeregexProperties");
		try {
			new StatsCalculator(file);
			fail();
		} catch (InvalidFolderStructureException e) {
		}
	}

	@Test
	public void testNonExistingTimeregexProperties() throws Exception {
		File file = new File(
				"src/test/resources/nonExistingTimeregexProperties");
		try {
			new StatsCalculator(file);
			fail();
		} catch (InvalidFolderStructureException e) {
		}
	}

	@Test
	public void testOkThreeSnapshots() throws Exception {
		File folderBase = new File("src/test/resources/okThreeSnapshots");
		StatsCalculator statsCalculator = new StatsCalculator(folderBase);
		CalculationListener calculationListener = mock(CalculationListener.class);
		statsCalculator.run(calculationListener);

		File areaRaster = statsCalculator.getSampleAreasFile();
		// clean up before checks
		assertTrue(!areaRaster.exists() || areaRaster.delete());

		verifyOk(folderBase, calculationListener, areaRaster);
	}

	@Test
	public void testOkExistingBadSampleAreas() throws Exception {
		File folderBase = new File(
				"src/test/resources/okExistingBadSampleAreas");
		StatsCalculator statsCalculator = new StatsCalculator(folderBase);
		File areaRaster = statsCalculator.getSampleAreasFile();
		File backupAreaRaster = new File(areaRaster.getParentFile(),
				"backup-sample-areas.tiff");
		IOUtils.copy(new FileInputStream(backupAreaRaster),
				new FileOutputStream(areaRaster));
		CalculationListener calculationListener = mock(CalculationListener.class);
		statsCalculator.run(calculationListener);

		// clean up before checks
		assertTrue(!areaRaster.exists() || areaRaster.delete());

		verifyOk(folderBase, calculationListener, areaRaster);
	}

	@Test
	public void testOkExistingBadSampleAreasCannotBeDeleted() throws Exception {
		File folderBase = new File(
				"src/test/resources/okExistingBadSampleAreas");
		StatsCalculator statsCalculator = new StatsCalculator(folderBase);
		File areaRaster = statsCalculator.getSampleAreasFile();
		File backupAreaRaster = new File(areaRaster.getParentFile(),
				"backup-sample-areas.tiff");
		IOUtils.copy(new FileInputStream(backupAreaRaster),
				new FileOutputStream(areaRaster));
		assertTrue(areaRaster.getParentFile().setReadOnly()
				&& !areaRaster.delete());
		CalculationListener calculationListener = mock(CalculationListener.class);
		try {
			statsCalculator.run(calculationListener);
			fail();
		} catch (IOException e) {
		}

		// clean up before checks
		areaRaster.getParentFile().setWritable(true);
		assertTrue(!areaRaster.exists() || areaRaster.delete());
	}

	private void verifyOk(File folderBase,
			CalculationListener calculationListener, File areaRaster) {
		verify(calculationListener).calculate(areaRaster,
				new File(folderBase, "mosaic/snapshot_2000.tiff"),
				"unredd:provinces", "name");
		verify(calculationListener).calculate(areaRaster,
				new File(folderBase, "mosaic/snapshot_2000.tiff"),
				"unredd:projects", "id");
		verify(calculationListener).calculate(areaRaster,
				new File(folderBase, "mosaic/snapshot_2001.tiff"),
				"unredd:provinces", "name");
		verify(calculationListener).calculate(areaRaster,
				new File(folderBase, "mosaic/snapshot_2001.tiff"),
				"unredd:projects", "id");
	}

	@Test
	public void testSnapshotDifferentGeometry() throws Exception {
		File folderBase = new File(
				"src/test/resources/snapshotDifferentGeometry");
		StatsCalculator statsCalculator = new StatsCalculator(folderBase);
		CalculationListener calculationListener = mock(CalculationListener.class);
		try {
			statsCalculator.run(calculationListener);
			fail();
		} catch (MixedRasterGeometryException e) {
		} finally {
			File areaRaster = statsCalculator.getSampleAreasFile();
			// clean up before checks
			assertTrue(!areaRaster.exists() || areaRaster.delete());
		}
	}

	@Test
	public void testErrorCreatingAreaRaster() throws Exception {
		File folderBase = new File("src/test/resources/errorCreatingAreaRaster");
		StatsCalculator statsCalculator = new StatsCalculator(folderBase);
		statsCalculator.getConfigurationFolder().setReadOnly();
		CalculationListener calculationListener = mock(CalculationListener.class);
		try {
			statsCalculator.run(calculationListener);
			fail();
		} catch (IOException e) {
		} finally {
			File areaRaster = statsCalculator.getSampleAreasFile();
			// clean up before checks
			assertTrue(!areaRaster.exists() || areaRaster.delete());
		}
	}

	@Test
	public void testCorruptedTiff() throws Exception {
		File folderBase = new File("src/test/resources/corruptedTiff");
		StatsCalculator statsCalculator = new StatsCalculator(folderBase);
		CalculationListener calculationListener = mock(CalculationListener.class);
		try {
			statsCalculator.run(calculationListener);
			fail();
		} catch (IOException e) {
		}
	}
}
