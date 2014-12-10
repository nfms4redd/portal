package org.fao.unredd.feedback.servlet;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fao.unredd.feedback.Feedback;
import org.fao.unredd.feedback.VerificationCodeNotFoundException;
import org.fao.unredd.portal.PersistenceException;
import org.fao.unredd.portal.StatusServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerifyCommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory
			.getLogger(VerifyCommentServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String verificationCode = req.getParameter("verificationCode");

		try {
			Feedback feedback = (Feedback) req.getServletContext()
					.getAttribute("feedback");
			feedback.verify(verificationCode);
			resp.setContentType("text/plain");
			resp.getWriter().println("El mensaje ha sido validado.");
		} catch (VerificationCodeNotFoundException e) {
			logger.debug("Cannot find verification code", e);
			throw new StatusServletException(404,
					"No se encontró ningún comentario con el código");
		} catch (PersistenceException e) {
			throw new StatusServletException(500, e);
		} catch (MessagingException e) {
			throw new StatusServletException(500, e);
		}
	}
}
