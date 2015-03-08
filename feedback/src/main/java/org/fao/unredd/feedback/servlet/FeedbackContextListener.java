package org.fao.unredd.feedback.servlet;

import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.fao.unredd.feedback.DBFeedbackPersistence;
import org.fao.unredd.feedback.Feedback;
import org.fao.unredd.feedback.Mailer;
import org.fao.unredd.feedback.MissingArgumentException;
import org.fao.unredd.portal.Config;
import org.fao.unredd.portal.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeedbackContextListener implements ServletContextListener {

	private static final Logger logger = LoggerFactory
			.getLogger(FeedbackContextListener.class);
	private Timer timer;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		final ServletContext servletContext = sce.getServletContext();
		final Config config = (Config) servletContext.getAttribute("config");
		Properties properties = config.getProperties();

		try {
			DBFeedbackPersistence feedbackPersistence = new DBFeedbackPersistence(
					properties.getProperty("db-schema"));
			Mailer mailer = new Mailer(properties);
			Feedback feedback = new Feedback(feedbackPersistence, mailer);
			servletContext.setAttribute("feedback", feedback);
		} catch (MissingArgumentException e) {
			logger.error("All mail parameters must be configured. "
					+ e.getArgumentName() + " missing");
		}

		timer = new Timer();
		int rate;
		try {
			rate = Integer.parseInt(properties
					.getProperty("feedback-validation-check-delay"));
		} catch (NumberFormatException e) {
			logger.warn("feedback-validation-check property not present. Will check each 10 minutes");
			int tenMinutes = 1000 * 60 * 10;
			rate = tenMinutes;
		}
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				try {
					Feedback feedback = (Feedback) servletContext
							.getAttribute("feedback");
					if (feedback != null) {
						feedback.notifyValidated(config);
					} else {
						logger.error("No feedback instance skipping author notification");
					}
				} catch (PersistenceException e) {
					logger.error(
							"Database error notifying the comment authors", e);
				}
			}
		}, 0, rate);

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		timer.cancel();
	}

}
