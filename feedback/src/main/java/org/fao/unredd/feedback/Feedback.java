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

	public Feedback(FeedbackPersistence feedbackPersistence, Mailer mailInfo) {
		this.persistence = feedbackPersistence;
		this.mailInfo = mailInfo;
	}

	public String insertNew(String geom, String srid, String comment,
			String email) throws CannotSendMailException, PersistenceException {
		if (geom == null || srid == null || comment == null || email == null) {
			throw new IllegalArgumentException("all parameters are mandatory: "
					+ geom + srid + comment + email);
		}

		logger.info("Feedback requested with the following parameters:");
		logger.info("email: " + email);
		logger.info("geom: " + geom);
		logger.info("srid: " + srid);
		logger.info("comment: " + comment);

		/*
		 * non unique verification code, but these are valid only a period of
		 * time so collisions are very rare
		 */
		String verificationCode = Integer
				.toString((geom + comment + email + srid).hashCode());
		persistence.insert(geom, srid, comment, email, verificationCode);
		try {
			mailInfo.sendVerificationMail(email, verificationCode);
		} catch (AddressException e) {
			throw new CannotSendMailException("Invalid address", e);
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
		System.out.println(entries.length);
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
