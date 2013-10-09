package org.fao.unredd.portal;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class ErrorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(ErrorServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Throwable throwable = (Throwable) req
				.getAttribute("javax.servlet.error.exception");
		Integer statusCode = (Integer) req
				.getAttribute("javax.servlet.error.status_code");
		String servletName = (String) req
				.getAttribute("javax.servlet.error.servlet_name");
		String errorMsg = (String) req
				.getAttribute("javax.servlet.error.message");
		if (servletName == null) {
			servletName = "Unknown";
		}
		String requestUri = (String) req
				.getAttribute("javax.servlet.error.request_uri");
		if (requestUri == null) {
			requestUri = "Unknown";
		}

		logger.error("Error handling request: " + requestUri, throwable);
		if (throwable == null && statusCode == null) {
			PrintWriter out = resp.getWriter();
			out.println("<h2>Error information is missing</h2>");
		} else {
			resp.setStatus(500);
			resp.getWriter().write(errorMsg);
		}
	}
}
