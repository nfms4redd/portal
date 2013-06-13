package org.fao.unredd.layers.folder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.apache.commons.io.FileUtils;
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
		assertTrue(file.mkdirs());
		LayerFolderImpl layer = new LayerFolderImpl(file);
		layer.setOutput("indicator-id1", "Deforestation", "the_id", "");
		layer.setOutput("indicator-id2", "Forestation", "id", "");
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

		FileUtils.deleteDirectory(file);
	}
}
