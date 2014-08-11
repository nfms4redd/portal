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

public class ClientContentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger
			.getLogger(ClientContentServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Config config = (Config) getServletContext().getAttribute("config");

		String pathInfo = req.getServletPath() + req.getPathInfo();
		File file = new File(config.getDir(), pathInfo);

		InputStream stream = null;
		if (file.isFile()) {
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
			stream = new BufferedInputStream(new FileInputStream(file));
		} else {
			String path = "/nfms" + pathInfo;
			InputStream classPathResource = this.getClass()
					.getResourceAsStream(path);
			if (classPathResource != null) {
				resp.setStatus(HttpServletResponse.SC_OK);
				stream = new BufferedInputStream(classPathResource);
			}
		}

		if (stream == null) {
			throw new StatusServletException(404,
					"The file could not be found: " + pathInfo);
		} else {
			// Send contents
			try {
				IOUtils.copy(stream, resp.getOutputStream());
			} catch (IOException e) {
				logger.error("Error reading file", e);
				throw new StatusServletException(500,
						"Could transfer the resource");
			}
		}

	}
}
