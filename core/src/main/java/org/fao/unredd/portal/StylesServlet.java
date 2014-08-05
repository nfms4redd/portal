package org.fao.unredd.portal;

import java.io.File;

public class StylesServlet extends AbstractStaticContentServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected File[] getDirectories(Config config) {
		// Do not search requirejs in folders
		return new File[0];
	}

	@Override
	protected String getClasspathPackage() {
		// Search requireJS in the root
		return "nfms/styles";
	}
}