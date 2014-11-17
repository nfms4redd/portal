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
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mailer {

	private String host;
	private String port;
	private String userName;
	private String password;
	private String title;
	private String message;

	public Mailer(Properties properties) {
		this(properties.getProperty("feedback-mail-host"), properties
				.getProperty("feedback-mail-port"), properties
				.getProperty("feedback-mail-username"), properties
				.getProperty("feedback-mail-password"), properties
				.getProperty("feedback-mail-title"), properties
				.getProperty("feedback-mail-text"));
	}

	public Mailer(String host, String port, String userName, String password,
			String title, String message) {
		this.host = checkNull(host);
		this.port = checkNull(port);
		this.userName = checkNull(userName);
		this.password = checkNull(password);
		this.title = checkNull(title);
		this.message = checkNull(message);
	}

	private String checkNull(String value) {
		if (value != null) {
			return value;
		} else {
			throw new IllegalArgumentException(
					"All mail parameters must be specified");
		}
	}

	public void sendMail(String email, String verificationCode)
			throws MessagingException {
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
		String text = message;
		text = text.replaceAll(Pattern.quote("$code"), verificationCode);
		msg.setText(text);

		// sends the e-mail
		Transport.send(msg);
	}

}
