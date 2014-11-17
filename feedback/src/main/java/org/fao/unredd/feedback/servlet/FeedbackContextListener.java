package org.fao.unredd.feedback.servlet;

import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.fao.unredd.feedback.DBFeedbackPersistence;
import org.fao.unredd.feedback.Feedback;
import org.fao.unredd.feedback.Mailer;
import org.fao.unredd.feedback.PersistenceException;
import org.fao.unredd.portal.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeedbackContextListener implements ServletContextListener {

	private static final Logger logger = LoggerFactory
			.getLogger(FeedbackContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		Config config = (Config) servletContext.getAttribute("config");
		Properties properties = config.getProperties();

		DBFeedbackPersistence feedbackPersistence = new DBFeedbackPersistence(
				properties.getProperty("feedback-db-table"));
		Feedback feedback = new Feedback(feedbackPersistence, new Mailer(
				properties));
		try {
			feedback.createTable();
			servletContext.setAttribute("feedback", feedback);
		} catch (PersistenceException e) {
			logger.error(
					"Could not create feedback table. Feedback function will not work properly.",
					e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
