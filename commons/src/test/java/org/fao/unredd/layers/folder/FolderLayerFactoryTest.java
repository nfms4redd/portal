package org.fao.unredd.layers.folder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class FolderLayerFactoryTest {

	@Test
	public void testUnexistantFolder() throws Exception {
		File file = new File("does not exist");
		assertFalse(file.exists());
		try {
			new FolderLayerFactory(file);
			fail();
		} catch (IllegalArgumentException e) {
		}
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
