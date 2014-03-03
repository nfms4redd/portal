package org.fao.unredd.portal;

import java.io.File;

public class ModulesServlet extends AbstractStaticContentServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected File[] getDirectories(Config config) {
		File portalConfig = new File(config.getDir(), "modules");
		File webapps = new File(getServletContext().getRealPath("modules"));

		return new File[] { portalConfig, webapps };
	}
}