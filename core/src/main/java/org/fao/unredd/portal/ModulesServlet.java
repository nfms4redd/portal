package org.fao.unredd.portal;

import java.io.File;

public class ModulesServlet extends AbstractStaticContentServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected File[] getDirectories(Config config) {
		return new File[] { new File(config.getDir(), "modules") };
	}

	@Override
	protected String getClasspathPackage() {
		return "nfms/modules";
	}
}