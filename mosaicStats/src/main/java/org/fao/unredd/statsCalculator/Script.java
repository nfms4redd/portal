package org.fao.unredd.statsCalculator;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.fao.unredd.process.ProcessExecutionException;
import org.fao.unredd.process.ProcessRunner;

public class Script {

	private String content;

	public Script(String resourceName) throws IOException {
		InputStream scriptStream = this.getClass().getResourceAsStream(
				"stats.sh");
		content = IOUtils.toString(scriptStream);
		scriptStream.close();
	}

	public void setParameter(String paramName, Object value) {
		content = content.replaceAll("\\Q$" + paramName + "\\E",
				value.toString());
	}

	public void run() throws ProcessExecutionException {
		new ProcessRunner("bash", "-c", content).run();
	}
}
