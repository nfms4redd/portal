package org.fao.unredd;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.fao.unredd.statsCalculator.InvalidFolderStructureException;
import org.fao.unredd.statsCalculator.MosaicLayerFolder;
import org.junit.Test;

public class FolderMosaicLayer {

	@Test
	public void testEmptyMosaic() throws Exception {
		try {
			new MosaicLayerFolder(new File("src/test/resources/emptyMosaic"));
		} catch (InvalidFolderStructureException e) {
			assertTrue(e
					.getOffendingFile()
					.equals(new File(
							new File("src/test/resources/emptyMosaic"), "data")));
		}
	}

	@Test
	public void testBadSnapshotNaming() throws Exception {
		File mosaic = new File("src/test/resources/badSnapshotNaming");
		try {
			new MosaicLayerFolder(mosaic);
		} catch (InvalidFolderStructureException e) {
			assertTrue(e.getOffendingFile().equals(
					new File(mosaic, "data/snapshot_202.tiff")));
		}
	}

	@Test
	public void testBadSnapshotTimeFormat() throws Exception {
		File mosaic = new File("src/test/resources/badSnapshotTimeFormat");
		try {
			new MosaicLayerFolder(mosaic);
		} catch (InvalidFolderStructureException e) {
			assertTrue(e.getOffendingFile().equals(
					new File(mosaic, "data/snapshot_20021313.tiff")));
		}
	}

	@Test
	public void testBadTimeregexProperties() throws Exception {
		File mosaic = new File("src/test/resources/badTimeregexProperties");
		try {
			new MosaicLayerFolder(mosaic);
		} catch (InvalidFolderStructureException e) {
			assertTrue(e.getOffendingFile().equals(
					new File(mosaic, "data/timeregex.properties")));
		}
	}

	@Test
	public void testNonExistingTimeregexProperties() throws Exception {
		File mosaic = new File(
				"src/test/resources/nonExistingTimeregexProperties");
		try {
			new MosaicLayerFolder(mosaic);
		} catch (InvalidFolderStructureException e) {
			assertTrue(e.getOffendingFile().equals(
					new File(mosaic, "data/timeregex.properties")));
		}
	}

}
