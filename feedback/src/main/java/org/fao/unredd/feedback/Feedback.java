package org.fao.unredd.feedback;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.fao.unredd.portal.Config;
import org.fao.unredd.portal.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Feedback {

	private static final Logger logger = LoggerFactory
			.getLogger(Feedback.class);

	private FeedbackPersistence persistence;
	private Mailer mailInfo;
	private String srid = "900913";

	public Feedback(FeedbackPersistence feedbackPersistence, Mailer mailInfo) {
		this.persistence = feedbackPersistence;
		this.mailInfo = mailInfo;
	}

	public String insertNew(String geom, String comment, String email,
			String layerName, String layerDate, String linkLanguage,
			String mailTitle, String verificationMessage)
			throws CannotSendMailException, PersistenceException,
			MissingArgumentException {
		checkNull("geom", geom);
		checkNull("comment", comment);
		checkNull("email", email);
		checkNull("layerName", layerName);

		logger.info("Feedback requested with the following parameters:");
		logger.info("email: " + email);
		logger.info("geom: " + geom);
		logger.info("comment: " + comment);
		logger.info("layerName: " + layerName);
		logger.info("date: " + layerDate);

		/*
		 * non unique verification code, but these are valid only a period of
		 * time so collisions are very rare
		 */
		String verificationCode = Integer.toString((geom + comment + email)
				.hashCode());
		persistence.insert(geom, srid, comment, email, layerName, layerDate,
				verificationCode, linkLanguage);
		try {
			mailInfo.sendVerificationMail(linkLanguage, mailTitle,
					verificationMessage, email, verificationCode);
		} catch (MessagingException e) {
			throw new CannotSendMailException(e);
		}
		persistence.cleanOutOfDate();

		return verificationCode;
	}

	private void checkNull(String paramName, String paramValue)
			throws MissingArgumentException {
		if (paramValue == null) {
			throw new MissingArgumentException(paramName);
		}
	}

	public void verify(String verificationCode)
			throws VerificationCodeNotFoundException, PersistenceException,
			AddressException, MessagingException {
		if (persistence.existsUnverified(verificationCode)) {
			persistence.verify(verificationCode);
			mailInfo.sendVerifiedMail(verificationCode);
		} else {
			throw new VerificationCodeNotFoundException();
		}
	}

	public void notifyValidated(Config config) throws PersistenceException {
		CommentInfo[] entries = persistence.getValidatedToNotifyInfo();
		for (CommentInfo entry : entries) {
			try {
				ResourceBundle messages = config.getMessages(new Locale(entry
						.getLanguage()));
				mailInfo.sendValidatedMail(entry.getMail(),
						entry.getVerificationCode(),
						messages.getString("Feedback.validated-mail-text"),
						messages.getString("Feedback.mail-title"));
				persistence.setNotified(entry.getId());
			} catch (MessagingException e) {
				logger.error("Error sending mail:" + entry.getMail(), e);
				// ignore
			}
		}
	}

}
