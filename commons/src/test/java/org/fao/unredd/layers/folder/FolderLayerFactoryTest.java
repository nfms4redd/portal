package org.fao.unredd.layers.folder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.fao.unredd.layers.NoSuchLayerException;
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
	public void testUnexistantLayer() throws Exception {
		FolderLayerFactory factory = new FolderLayerFactory(
				new File("src/test"));
		try {
			factory.newLayer("resources:unexistant");
			fail();
		} catch (NoSuchLayerException e) {
		}
	}

	@Test
	public void testMosaicLayer() throws Exception {
		FolderLayerFactory factory = new FolderLayerFactory(
				new File("src/test"));
		assertTrue(factory.newMosaicLayer("resources:temporalMosaic") != null);
	}

	@Test
	public void testUnexistantMosaicLayer() throws Exception {
		FolderLayerFactory factory = new FolderLayerFactory(
				new File("src/test"));
		try {
			factory.newMosaicLayer("resources:unexistant");
			fail();
		} catch (NoSuchLayerException e) {
		}
	}
}
