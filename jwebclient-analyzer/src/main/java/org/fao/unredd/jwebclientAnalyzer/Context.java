package org.fao.unredd.jwebclientAnalyzer;

import java.io.File;
import java.io.InputStream;
import java.util.Set;

public interface Context {
	String[] DEFAULT_CLIENT_DIRECTORIES = new String[] { "nfms", "conf",
			"webapp" };

	Set<String> getLibPaths();

	InputStream getLibAsStream(String jarFileName);

	File getClientRoot();

	String[] getClientDirectories();
}
