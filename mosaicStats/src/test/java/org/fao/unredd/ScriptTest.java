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
package org.fao.unredd;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.fao.unredd.statsCalculator.Script;
import org.fao.unredd.testcommons.RequirementsChecker;
import org.fao.unredd.testcommons.TestConditions;
import org.fao.unredd.testcommons.TestRequirements;
import org.junit.Test;

public class ScriptTest extends RequirementsChecker{

	@Test
	@TestRequirements(conditions = {TestConditions.UNIX})
	public void testFullWords() throws Exception {
		Script script = new Script(new ByteArrayInputStream(
				"echo '$subAAA' > $sub".getBytes()));
		File temp = new File("test");
		try {
			script.setParameter("sub", temp.getAbsolutePath());
			script.run();

			assertEquals("$subAAA\n",
					IOUtils.toString(new FileInputStream(temp)));
		} finally {
			temp.delete();
		}
	}

}
