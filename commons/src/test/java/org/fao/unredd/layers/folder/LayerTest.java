package org.fao.unredd.layers.folder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.fao.unredd.layers.OutputDescriptor;
import org.fao.unredd.layers.Outputs;
import org.junit.Test;

public class LayerTest {

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

	@Test
	public void testOutputMetadata() throws Exception {
		File file = new File("src/test/resources/layer");
		LayerFolderImpl layer = new LayerFolderImpl(file);
		Outputs outputs = layer.getOutputs();
		assertTrue(outputs.size() == 2);
		for (OutputDescriptor outputDescriptor : outputs) {
			if (outputDescriptor.getId().endsWith("1")) {
				assertEquals("indicator-id1", outputDescriptor.getId());
				assertEquals("the_id", outputDescriptor.getFieldId());
				assertEquals("Deforestation", outputDescriptor.getName());
			} else if (outputDescriptor.getId().endsWith("2")) {
				assertEquals("indicator-id2", outputDescriptor.getId());
				assertEquals("id", outputDescriptor.getFieldId());
				assertEquals("Forestation", outputDescriptor.getName());
			} else {
				fail();
			}
		}
	}
}
