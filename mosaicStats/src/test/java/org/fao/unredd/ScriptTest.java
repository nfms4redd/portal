package org.fao.unredd;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.fao.unredd.statsCalculator.Script;
import org.junit.Test;

public class ScriptTest {

	@Test
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
