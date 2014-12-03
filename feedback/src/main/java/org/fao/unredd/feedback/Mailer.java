package org.fao.unredd.feedback;

import java.util.Date;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mailer {

	private String host;
	private String port;
	private String userName;
	private String password;
	private String title;
	private String verifyMessage;
	private String verifiedMessage;

	public Mailer(Properties properties) {
		this(properties.getProperty("feedback-mail-host"), properties
				.getProperty("feedback-mail-port"), properties
				.getProperty("feedback-mail-username"), properties
				.getProperty("feedback-mail-password"), properties
				.getProperty("feedback-mail-title"), properties
				.getProperty("feedback-verify-mail-text"), properties
				.getProperty("feedback-admin-mail-text"));
	}

	public Mailer(String host, String port, String userName, String password,
			String title, String verifyMessage, String verifiedMessage) {
		this.host = checkNull(host);
		this.port = checkNull(port);
		this.userName = checkNull(userName);
		this.password = checkNull(password);
		this.title = checkNull(title);
		this.verifyMessage = checkNull(verifyMessage);
		this.verifiedMessage = checkNull(verifiedMessage);
	}

	private String checkNull(String value) {
		if (value != null) {
			return value;
		} else {
			throw new IllegalArgumentException(
					"All mail parameters must be specified");
		}
	}

	public void sendVerificationMail(String email, String verificationCode)
			throws MessagingException {
		sendVerificationMail(email, verificationCode, verifyMessage);
	}

	private void sendVerificationMail(String email, String verificationCode,
			String text) throws MessagingException, AddressException {
		text = text.replaceAll(Pattern.quote("$code"), verificationCode);
		sendMail(email, title, text);
	}

	private void sendMail(String email, String title, String text)
			throws MessagingException, AddressException {
		// sets SMTP server properties
		Properties properties = new Properties();
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", port);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");

		// creates a new session with an authenticator
		Authenticator auth = new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		};

		Session session = Session.getInstance(properties, auth);
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(userName));
		InternetAddress[] toAddresses = { new InternetAddress(email) };
		msg.setRecipients(Message.RecipientType.TO, toAddresses);
		msg.setSubject(title);
		msg.setSentDate(new Date());

		// set plain text message
		msg.setText(text);

		// sends the e-mail
		Transport.send(msg);
	}

	public void sendVerifiedMail(String verificationCode)
			throws AddressException, MessagingException {
		sendVerificationMail(userName, verificationCode, verifiedMessage);
	}

}
