package org.fao.unredd.feedback.servlet;

import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.fao.unredd.feedback.DBFeedbackPersistence;
import org.fao.unredd.feedback.Feedback;
import org.fao.unredd.feedback.Mailer;
import org.fao.unredd.portal.Config;

public class FeedbackContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		Config config = (Config) servletContext.getAttribute("config");
		Properties properties = config.getProperties();

		Feedback feedback = new Feedback(new DBFeedbackPersistence(),
				new Mailer(properties));
		servletContext.setAttribute("feedback", feedback);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
