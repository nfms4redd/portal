package org.fao.unredd.feedback;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

public class Feedback {

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

}
