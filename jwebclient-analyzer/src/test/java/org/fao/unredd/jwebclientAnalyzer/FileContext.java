package org.fao.unredd.jwebclientAnalyzer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class FileContext implements Context {

	private String root;
	private String[] clientDirs;

	public FileContext(String root, String... clientDirs) {
		this.root = root;
		this.clientDirs = clientDirs;
	}

	@Override
	public Set<String> getLibPaths() {
		HashSet<String> ret = new HashSet<String>();
		File[] jars = new File(root, "WEB-INF/lib").listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				String name = file.getName().toLowerCase();
				return name.endsWith(".zip") || name.endsWith(".jar");
			}
		});

		if (jars != null) {
			for (File file : jars) {
				ret.add("WEB-INF/lib/" + file.getName());
			}
		}
		return ret;
	}

	@Override
	public InputStream getLibAsStream(String jarFileName) {
		try {
			return new BufferedInputStream(new FileInputStream(new File(root,
					jarFileName)));
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	@Override
	public File getClientRoot() {
		return new File(this.root, "WEB-INF/classes");
	}

	@Override
	public String[] getClientDirectories() {
		return this.clientDirs;
	}
}
