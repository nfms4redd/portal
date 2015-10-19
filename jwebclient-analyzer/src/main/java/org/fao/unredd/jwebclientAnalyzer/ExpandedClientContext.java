package org.fao.unredd.jwebclientAnalyzer;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

public class ExpandedClientContext implements Context {

	private File jeeContextFolder;
	private String[] clientDirs;

	public ExpandedClientContext(String jeeContextFolderPath,
			String... clientDirs) {
		this.jeeContextFolder = new File(jeeContextFolderPath);
		this.clientDirs = clientDirs;
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
		return jeeContextFolder;
	}

	public String[] getClientDirectories() {
		return clientDirs;
	}
}