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
import static org.junit.Assert.fail;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class LayerTest {

	private static final String LAYER_NAME = "workspace:layer";

	@Test
	public void testConfigurationFolderCreatedOnConstructor() throws Exception {
		File file = new File("src/test/resources/confLayer");
		assertTrue(!file.exists() || file.delete());
		new LayerFolderImpl(LAYER_NAME, file);
		assertTrue(file.exists());
		FileUtils.deleteDirectory(file);
	}

	@Test
	public void testInvalidName() throws Exception {
		File file = new File("src/test/resources/layer");
		assertFalse(file.exists());
		try {
			new LayerFolderImpl("no semicolon", file);
			fail();
		} catch (IllegalArgumentException e) {
		}
		try {
			new LayerFolderImpl("too:many:semicolons", file);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

}
