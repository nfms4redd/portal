package org.fao.unredd.feedback.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fao.unredd.feedback.Feedback;
import org.fao.unredd.feedback.VerificationCodeNotFoundException;
import org.fao.unredd.portal.PersistenceException;
import org.fao.unredd.portal.StatusServletException;

public class VerifyCommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String verificationCode = req.getParameter("verificationCode");

		try {
			Feedback feedback = (Feedback) req.getServletContext()
					.getAttribute("feedback");
			feedback.verify(verificationCode);
			resp.setContentType("application/json");
			resp.getWriter().println(verificationCode);
		} catch (VerificationCodeNotFoundException e) {
			throw new StatusServletException(404,
					"Could not found any message with the specified validation code");
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
