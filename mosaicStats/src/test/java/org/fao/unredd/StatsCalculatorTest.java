package org.fao.unredd;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;

import org.fao.unredd.statsCalculator.CalculationListener;
import org.fao.unredd.statsCalculator.SnapshotNamingException;
import org.fao.unredd.statsCalculator.StatsCalculator;
import org.junit.Test;

public class StatsCalculatorTest {

	@Test
	public void testOkThreeSnapshots() throws Exception {
		File file = new File("src/test/resources/okThreeSnapshots");
		StatsCalculator statsCalculator = new StatsCalculator(file);
		CalculationListener calculationListener = mock(CalculationListener.class);
		statsCalculator.run(calculationListener);

		String folderBase = "src/test/resources/okThreeSnapshots/";
		File areaRaster = new File(folderBase
				+ "configuration/sample-areas.tiff");
		// clean up before checks
		assertTrue(!areaRaster.exists() || areaRaster.delete());

		verify(calculationListener).calculate(areaRaster,
				new File(folderBase + "mosaic/snapshot_2000.tiff"),
				"unredd:provinces", "name");
		verify(calculationListener).calculate(areaRaster,
				new File(folderBase + "mosaic/snapshot_2000.tiff"),
				"unredd:projects", "id");
		verify(calculationListener).calculate(areaRaster,
				new File(folderBase + "mosaic/snapshot_2001.tiff"),
				"unredd:provinces", "name");
		verify(calculationListener).calculate(areaRaster,
				new File(folderBase + "mosaic/snapshot_2001.tiff"),
				"unredd:projects", "id");
	}

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

}
