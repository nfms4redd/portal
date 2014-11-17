package org.fao.unredd.jwebclientAnalyzer;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

public class ExpandedClientContext implements Context {

	private String jeeContextFolder;

	public ExpandedClientContext(String jeeContextFolder) {
		this.jeeContextFolder = jeeContextFolder;
	}

	@Override
	public Set<String> getLibPaths() {
		return Collections.emptySet();
	}

	@Override
	public InputStream getLibAsStream(String jarFileName) {
		throw new UnsupportedOperationException("Internal error");
	}

	@Override
	public File getClientRoot() {
		return new File(jeeContextFolder);
	}

}