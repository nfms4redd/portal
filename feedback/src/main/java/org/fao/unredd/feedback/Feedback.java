package org.fao.unredd.feedback;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

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
			String layerName, String layerDate) throws CannotSendMailException,
			PersistenceException {
		if (geom == null || comment == null || email == null
				|| layerName == null) {
			throw new IllegalArgumentException("all parameters are mandatory: "
					+ geom + comment + email + layerName);
		}

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
				verificationCode);
		try {
			mailInfo.sendVerificationMail(email, verificationCode);
		} catch (MessagingException e) {
			throw new CannotSendMailException("Error sending mail", e);
		}
		persistence.cleanOutOfDate();

		return verificationCode;
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

	public void notifyValidated() throws PersistenceException {
		CommentInfo[] entries = persistence.getValidatedToNotifyInfo();
		for (CommentInfo entry : entries) {
			try {
				mailInfo.sendValidatedMail(entry.getMail(),
						entry.getVerificationCode());
				persistence.setNotified(entry.getId());
			} catch (MessagingException e) {
				logger.error("Could not send mail to " + entry.getMail(), e);
				// ignore
			}
		}
	}

	public void createTable() throws PersistenceException {
		persistence.createTable();
	}

}
