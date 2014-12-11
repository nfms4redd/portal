package org.fao.unredd.feedback.servlet;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fao.unredd.feedback.CannotSendMailException;
import org.fao.unredd.feedback.Feedback;
import org.fao.unredd.feedback.MissingArgumentException;
import org.fao.unredd.portal.Config;
import org.fao.unredd.portal.PersistenceException;
import org.fao.unredd.portal.StatusServletException;

public class CreateCommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Config config = (Config) req.getServletContext().getAttribute("config");
		Locale locale = (Locale) req.getAttribute("locale");
		ResourceBundle messages = config.getMessages(locale);

		String geom = req.getParameter("geometry");
		String comment = req.getParameter("comment");
		String email = req.getParameter("email");
		String layerName = req.getParameter("layerName");
		String date = req.getParameter("date");

		Feedback feedback = (Feedback) req.getServletContext().getAttribute(
				"feedback");
		try {
			feedback.insertNew(geom, comment, email, layerName, date,
					locale.getLanguage(),
					messages.getString("Feedback.mail-title"),
					messages.getString("Feedback.verify-mail-text"));
			resp.setContentType("text/plain");
			resp.setStatus(200);
		} catch (MissingArgumentException e) {
			throw new StatusServletException(400,
					messages.getString("Feedback.all_parameters_mandatory")
							+ geom + comment + email + layerName, e);
		} catch (CannotSendMailException e) {
			throw new StatusServletException(500,
					messages.getString("Feedback.error_sending_mail") + email,
					e);
		} catch (PersistenceException e) {
			throw new StatusServletException(500, e);
		}
	}
}
