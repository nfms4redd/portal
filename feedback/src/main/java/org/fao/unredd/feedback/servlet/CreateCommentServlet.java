package org.fao.unredd.feedback.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fao.unredd.feedback.CannotSendMailException;
import org.fao.unredd.feedback.Feedback;
import org.fao.unredd.feedback.PersistenceException;

public class CreateCommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String geom = req.getParameter("geometry");
		String comment = req.getParameter("comment");
		String email = req.getParameter("email");
		String srid = req.getParameter("srid");

		Feedback feedback = (Feedback) req.getServletContext().getAttribute(
				"feedback");
		try {
			String verificationCode = feedback.insertNew(geom, srid, comment,
					email);
			resp.setContentType("application/json");
			resp.getWriter().println(verificationCode);
		} catch (CannotSendMailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
