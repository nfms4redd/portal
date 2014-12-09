package org.fao.unredd.feedback.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fao.unredd.feedback.CannotSendMailException;
import org.fao.unredd.feedback.Feedback;
import org.fao.unredd.portal.PersistenceException;
import org.fao.unredd.portal.StatusServletException;

public class CreateCommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String geom = req.getParameter("geometry");
		String comment = req.getParameter("comment");
		String email = req.getParameter("email");

		Feedback feedback = (Feedback) req.getServletContext().getAttribute(
				"feedback");
		try {
			feedback.insertNew(geom, comment, email);
			resp.setContentType("text/plain");
			resp.setStatus(200);
		} catch (IllegalArgumentException e) {
			throw new StatusServletException(400, e);
		} catch (CannotSendMailException e) {
			throw new StatusServletException(500, e);
		} catch (PersistenceException e) {
			throw new StatusServletException(500, e);
		}
	}
}
