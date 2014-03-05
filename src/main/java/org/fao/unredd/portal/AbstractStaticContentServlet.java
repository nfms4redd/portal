package org.fao.unredd.portal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public abstract class AbstractStaticContentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger
			.getLogger(AbstractStaticContentServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Config config = (Config) getServletContext().getAttribute("config");

		String pathInfo = req.getPathInfo();
		File file = null;
		for (File dir : getDirectories(config)) {
			File f = new File(dir, pathInfo);
			if (f.isFile()) {
				file = f;
				break;
			}
		}

		if (file == null) {
			throw new StatusServletException(404,
					"The file could not be found");
		}

		// Manage cache headers: Last-Modified and If-Modified-Since
		long ifModifiedSince = req.getDateHeader("If-Modified-Since");
		long lastModified = file.lastModified();
		if (ifModifiedSince >= (lastModified / 1000 * 1000)) {
			resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return;
		}
		resp.setDateHeader("Last-Modified", lastModified);

		// Set content type
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String type = fileNameMap.getContentTypeFor(pathInfo);
		resp.setContentType(type);

		// Send contents
		try {
			InputStream is = new BufferedInputStream(new FileInputStream(file));
			IOUtils.copy(is, resp.getOutputStream());
		} catch (IOException e) {
			logger.error("Error reading file", e);
			throw new StatusServletException(500,
					"Could transfer the resource");
		}
	}

	/**
	 * Get the directories where the content must be searched. The array must be
	 * ordered by preference; this is, the first directory containing the
	 * specified resource will be used to handle the request.
	 * 
	 * @param config
	 *            The {@link Config} attribute from the servlet context.
	 * @return The array of directories, ordered by preference.
	 */
	protected abstract File[] getDirectories(Config config);
}
