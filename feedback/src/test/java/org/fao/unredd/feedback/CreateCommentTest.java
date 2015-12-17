package org.fao.unredd.feedback;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Locale;
import java.util.PropertyResourceBundle;

import javax.mail.MessagingException;

import org.fao.unredd.portal.Config;
import org.fao.unredd.portal.PersistenceException;
import org.junit.Test;

public class CreateCommentTest {

	private static final String title = "Comentario en portal REDD";
	private static final String language = "es";
	private static final String verificationMessage = "clic aquí para confirmar mensaje";
	private static final String validationMessage = "Mensaje validado";
	private static final String validGeometry = "POINT(0 0)";
	private static final String validComment = "boh";
	private static final String validEmail = "nombre@dominio.com";
	private static final String validLayer = "classification";
	private static final String validDate = "1/1/2008";
	private Feedback feedback;

	@Test
	public void testMissingParameters() throws CannotSendMailException,
			PersistenceException {
		feedback = new Feedback(mock(FeedbackPersistence.class),
				mock(Mailer.class));
		testMandatoryParameter(null, validComment, validEmail, validLayer);
		testMandatoryParameter(validGeometry, null, validEmail, validLayer);
		testMandatoryParameter(validGeometry, validComment, null, validLayer);
		testMandatoryParameter(validGeometry, validComment, validEmail, null);
	}

	private void testMandatoryParameter(String geom, String comment,
			String email, String layerName) throws CannotSendMailException,
			PersistenceException {
		try {
			feedback.insertNew(geom, comment, email, layerName, validDate,
					language, title, verificationMessage);
			fail();
		} catch (MissingArgumentException e) {
		}
	}

	@Test
	public void testDateCanBeNull() throws Exception {
		feedback = new Feedback(mock(FeedbackPersistence.class),
				mock(Mailer.class));
		feedback.insertNew(validGeometry, validComment, validEmail, validDate,
				null, language, title, verificationMessage);
	}

	@Test
	public void testInvalidInsertNoMail() throws Exception {
		FeedbackPersistence persistence = mock(FeedbackPersistence.class);
		doThrow(new PersistenceException("", null)).when(persistence).insert(
				anyString(), anyString(), anyString(), anyString(),
				anyString(), anyString(), anyString(), anyString());
		Mailer mailer = mock(Mailer.class);
		feedback = new Feedback(persistence, mailer);

		try {
			feedback.insertNew(validGeometry, validComment, validEmail,
					validLayer, validDate, "ca", title, verificationMessage);
			fail();
		} catch (Exception e) {
		}
		verify(mailer, never()).sendVerificationMail(anyString(), anyString(),
				anyString(), anyString(), anyString());
	}

	@Test
	public void testInsert() throws Exception {
		FeedbackPersistence persistence = mock(FeedbackPersistence.class);
		feedback = new Feedback(persistence, mock(Mailer.class));
		feedback.insertNew(validGeometry, validComment, validEmail, validLayer,
				validDate, language, title, verificationMessage);
		verify(persistence, times(1)).insert(eq(validGeometry), eq("900913"),
				eq(validComment), eq(validEmail), eq(validLayer),
				eq(validDate), anyString(), anyString());
	}

	@Test
	public void testDifferentVerificationCodes() throws Exception {
		feedback = new Feedback(mock(FeedbackPersistence.class),
				mock(Mailer.class));
		assertTrue(feedback.insertNew(validGeometry, validComment, validEmail,
				validLayer, validDate, language, title, verificationMessage) != feedback
				.insertNew("POINT(1 1)", validComment, validEmail, validLayer,
						validDate, language, title, verificationMessage));
	}

	@Test
	public void testInsertCleansOutOfDate() throws Exception {
		FeedbackPersistence persistence = mock(FeedbackPersistence.class);
		feedback = new Feedback(persistence, mock(Mailer.class));
		feedback.insertNew(validGeometry, validComment, validEmail, validLayer,
				validDate, language, title, verificationMessage);
		verify(persistence, times(1)).cleanOutOfDate();
	}

	@Test
	public void testVerifyComment() throws Exception {
		FeedbackPersistence persistence = mock(FeedbackPersistence.class);
		when(persistence.existsUnverified("100")).thenReturn(true);
		Mailer mailer = mock(Mailer.class);
		feedback = new Feedback(persistence, mailer);
		feedback.verify("100");
		verify(persistence).verify("100");
		verify(mailer).sendVerifiedMail("100");
	}

	@Test
	public void testVerifyVerifiedComment() throws Exception {
		FeedbackPersistence persistence = mock(FeedbackPersistence.class);
		when(persistence.existsUnverified("100")).thenReturn(false);
		feedback = new Feedback(persistence, mock(Mailer.class));
		try {
			feedback.verify("100");
			fail();
		} catch (VerificationCodeNotFoundException e) {
		}
	}

	@Test
	public void testNotifyAuthors() throws Exception {
		Config config = mockConfigMessages();
		FeedbackPersistence persistence = mock(FeedbackPersistence.class);
		when(persistence.getValidatedToNotifyInfo()).thenReturn(
				new CommentInfo[] { new CommentInfo(1, "a@b.com", "100",
						language) });
		Mailer mailer = mock(Mailer.class);
		feedback = new Feedback(persistence, mailer);
		feedback.notifyValidated(config);

		verify(mailer).sendValidatedMail("a@b.com", "100", validationMessage,
				title);
		verify(persistence).setNotified(1);
	}

	private Config mockConfigMessages() {
		PropertyResourceBundle bundle = mock(PropertyResourceBundle.class);
		when(bundle.handleGetObject("Feedback.validated-mail-text"))
				.thenReturn(validationMessage);
		when(bundle.handleGetObject("Feedback.mail-title")).thenReturn(title);
		Config config = mock(Config.class);
		when(config.getMessages(any(Locale.class))).thenReturn(bundle);
		return config;
	}

	@Test
	public void testNotifyAuthorsMailError() throws Exception {
		Config config = mockConfigMessages();
		FeedbackPersistence persistence = mock(FeedbackPersistence.class);
		when(persistence.getValidatedToNotifyInfo()).thenReturn(
				new CommentInfo[] {
						new CommentInfo(1, "a@b.com", "100", language),
						new CommentInfo(2, "c@d.com", "101", language) });
		Mailer mailer = mock(Mailer.class);
		doThrow(new MessagingException()).when(mailer).sendValidatedMail(
				"c@d.com", "101", validationMessage, title);
		feedback = new Feedback(persistence, mailer);
		feedback.notifyValidated(config);

		verify(mailer).sendValidatedMail("a@b.com", "100", validationMessage,
				title);
		verify(persistence, times(1)).setNotified(1);
	}
}
