package org.fao.unredd.portal;

import java.io.File;

public class StaticContentServlet extends AbstractStaticContentServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected File[] getDirectories(Config config) {
		return new File[] { new File(config.getDir(), "static") };
	}

	@Override
	protected String getClasspathPackage() {
		return "nfms_static";
	}
}
