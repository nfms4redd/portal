package org.fao.unredd;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.fao.unredd.statsCalculator.InvalidFolderStructureException;
import org.fao.unredd.statsCalculator.StatsLayerFolder;
import org.junit.Test;

public class FolderStatsLayerTest {

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

}
