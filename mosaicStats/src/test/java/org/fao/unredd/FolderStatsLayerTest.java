package org.fao.unredd;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.File;

import org.fao.unredd.statsCalculator.LayerFolderImpl;
import org.junit.Test;

public class FolderStatsLayerTest {

	@Test
	public void testUnexistantFolder() throws Exception {
		File file = new File("does not exist");
		assertFalse(file.exists());
		try {
			new LayerFolderImpl(file);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

}
