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
		try {
			mailInfo.sendMail(email, verificationCode);
		} catch (AddressException e) {
			throw new CannotSendMailException("Invalid address", e);
		} catch (MessagingException e) {
			throw new CannotSendMailException("Error sending mail", e);
		}
		persistence.insert(geom, srid, comment, email, verificationCode);
		persistence.cleanOutOfDate();

		return verificationCode;
	}

	public void verify(String verificationCode)
			throws VerificationCodeNotFoundException {
		// TODO Auto-generated method stub

	}

	public String[] notifyValidated() {
		// TODO Auto-generated method stub
		return null;
	}

	public void createTable() throws PersistenceException {
		persistence.createTable();
	}

}
