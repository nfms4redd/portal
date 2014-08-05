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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class FolderLayerFactoryTest {

	@Test
	public void testUnexistantFolder() throws Exception {
		File file = new File("does not exist");
		assertFalse(file.exists());
		FolderLayerFactory factory = new FolderLayerFactory(file);
		assertFalse(factory.exists("non:existing"));
	}

	@Test
	public void testLayer() throws Exception {
		FolderLayerFactory factory = new FolderLayerFactory(
				new File("src/test"));
		assertTrue(factory.newLayer("resources:vectorLayer") != null);
	}

	@Test
	public void testUnexistantLayerIsCreated() throws Exception {
		File root = new File("src/test");
		FolderLayerFactory factory = new FolderLayerFactory(root);
		String layerName = "resources:unexistant";
		assertFalse(factory.exists(layerName));
		factory.newLayer(layerName);
		assertTrue(factory.exists(layerName));

		File layerFolder = new File(root, "resources/unexistant");
		assertTrue(layerFolder.exists());
		FileUtils.deleteDirectory(layerFolder);
	}

	@Test
	public void testMosaicLayer() throws Exception {
		FolderLayerFactory factory = new FolderLayerFactory(
				new File("src/test"));
		assertTrue(factory.newMosaicLayer("resources:temporalMosaic") != null);
	}

	@Test
	public void testUnexistantMosaicLayerIsCreated() throws Exception {
		File root = new File("src/test");
		FolderLayerFactory factory = new FolderLayerFactory(root);
		String layerName = "resources:unexistant";
		assertFalse(factory.exists(layerName));
		factory.newMosaicLayer(layerName);
		assertTrue(factory.exists(layerName));

		File layerFolder = new File(root, "resources/unexistant");
		assertTrue(layerFolder.exists());
		FileUtils.deleteDirectory(layerFolder);
	}
}
