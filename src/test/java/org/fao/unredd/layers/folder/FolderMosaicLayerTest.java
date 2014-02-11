/**
 * nfms4redd Portal Interface - http://nfms4redd.org/
 *
 * (C) 2012, FAO Forestry Department (http://www.fao.org/forestry/)
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */
package org.fao.unredd.layers.folder;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.fao.unredd.layers.Location;
import org.junit.Test;

public class FolderMosaicLayerTest {

	private static final String LAYER_NAME = "workspace:layer";

	@Test
	public void testConfigurationFolderCreatedOnConstructor() throws Exception {
		File file = new File("src/test/resources/confTemporalMosaic");
		assertTrue(!file.exists() || file.delete());
		new MosaicLayerFolder(LAYER_NAME, file);
		assertTrue(file.exists());
		FileUtils.deleteDirectory(file);
	}

	@Test
	public void testEmptyMosaic() throws Exception {
		File emptyMosaic = new File("src/test/resources/emptyMosaic");
		try {
			new MosaicLayerFolder(LAYER_NAME, emptyMosaic)
					.getTimestamps(mockLocation(emptyMosaic));
		} catch (InvalidFolderStructureException e) {
			assertTrue(e.getOffendingFile().equals(emptyMosaic));
		}
	}

	private Location mockLocation(File dataFolder) {
		Location ret = mock(Location.class);
		when(ret.getFile()).thenReturn(dataFolder);
		return ret;
	}

	@Test
	public void testBadSnapshotNaming() throws Exception {
		File mosaic = new File("src/test/resources/badSnapshotNaming");
		try {
			new MosaicLayerFolder(LAYER_NAME, mosaic)
					.getTimestamps(mockLocation(mosaic));
		} catch (InvalidFolderStructureException e) {
			assertTrue(e.getOffendingFile().equals(
					new File(mosaic, "snapshot_202.tiff")));
		}
	}

	@Test
	public void testBadSnapshotTimeFormat() throws Exception {
		File mosaic = new File("src/test/resources/badSnapshotTimeFormat");
		try {
			new MosaicLayerFolder(LAYER_NAME, mosaic)
					.getTimestamps(mockLocation(mosaic));
		} catch (InvalidFolderStructureException e) {
			assertTrue(e.getOffendingFile().equals(
					new File(mosaic, "snapshot_20021313.tiff")));
		}
	}

	@Test
	public void testBadTimeregexProperties() throws Exception {
		File mosaic = new File("src/test/resources/badTimeregexProperties");
		try {
			new MosaicLayerFolder(LAYER_NAME, mosaic)
					.getTimestamps(mockLocation(mosaic));
		} catch (InvalidFolderStructureException e) {
			assertTrue(e.getOffendingFile().equals(
					new File(mosaic, "timeregex.properties")));
		}
	}

	@Test
	public void testNonExistingTimeregexProperties() throws Exception {
		File mosaic = new File(
				"src/test/resources/nonExistingTimeregexProperties");
		try {
			new MosaicLayerFolder(LAYER_NAME, mosaic)
					.getTimestamps(mockLocation(mosaic));
		} catch (InvalidFolderStructureException e) {
			assertTrue(e.getOffendingFile().equals(
					new File(mosaic, "timeregex.properties")));
		}
	}
}
