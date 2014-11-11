package org.fao.unredd.feedback.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fao.unredd.feedback.Feedback;

public class NotifyValidatedServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Feedback feedback = (Feedback) req.getServletContext().getAttribute(
				"feedback");
		String[] notified = feedback.notifyValidated();
		resp.setContentType("application/json");
		resp.getWriter().println(notified);
	}
}
