package org.fao.unredd.feedback;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.mail.MessagingException;

import org.fao.unredd.portal.PersistenceException;
import org.junit.Test;

public class CreateCommentTest {

	private static final String validGeometry = "POINT(0 0)";
	private static final String validComment = "boh";
	private static final String validEmail = "nombre@dominio.com";
	private Feedback feedback;

	@Test
	public void testMissingParameters() throws CannotSendMailException,
			PersistenceException {
		feedback = new Feedback(mock(FeedbackPersistence.class),
				mock(Mailer.class));
		testMandatoryParameter(null, validComment, validEmail);
		testMandatoryParameter(validGeometry, null, validEmail);
		testMandatoryParameter(validGeometry, validComment, null);
	}

	private void testMandatoryParameter(String geom, String comment,
			String email) throws CannotSendMailException, PersistenceException {
		try {
			feedback.insertNew(geom, comment, email);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testInvalidInsertNoMail() throws Exception {
		FeedbackPersistence persistence = mock(FeedbackPersistence.class);
		doThrow(new PersistenceException("", null)).when(persistence)
				.insert(anyString(), anyString(), anyString(), anyString(),
						anyString());
		Mailer mailer = mock(Mailer.class);
		feedback = new Feedback(persistence, mailer);

		try {
			feedback.insertNew(validGeometry, validComment, validEmail);
			fail();
		} catch (Exception e) {
		}
		verify(mailer, never()).sendVerificationMail(anyString(), anyString());
	}

	@Test
	public void testInsert() throws Exception {
		FeedbackPersistence persistence = mock(FeedbackPersistence.class);
		feedback = new Feedback(persistence, mock(Mailer.class));
		feedback.insertNew(validGeometry, validComment, validEmail);
		verify(persistence, times(1)).insert(eq(validGeometry), eq("900913"),
				eq(validComment), eq(validEmail), anyString());
	}

	@Test
	public void testDifferentVerificationCodes() throws Exception {
		feedback = new Feedback(mock(FeedbackPersistence.class),
				mock(Mailer.class));
		assertTrue(feedback.insertNew(validGeometry, validComment, validEmail) != feedback
				.insertNew("POINT(1 1)", validComment, validEmail));
	}

	@Test
	public void testInsertCleansOutOfDate() throws Exception {
		FeedbackPersistence persistence = mock(FeedbackPersistence.class);
		feedback = new Feedback(persistence, mock(Mailer.class));
		feedback.insertNew(validGeometry, validComment, validEmail);
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
		FeedbackPersistence persistence = mock(FeedbackPersistence.class);
		when(persistence.getValidatedToNotifyInfo()).thenReturn(
				new CommentInfo[] { new CommentInfo(1, "a@b.com", "100") });
		Mailer mailer = mock(Mailer.class);
		feedback = new Feedback(persistence, mailer);
		feedback.notifyValidated();

		verify(mailer).sendValidatedMail("a@b.com", "100");
		verify(persistence).setNotified(1);
	}

	@Test
	public void testNotifyAuthorsMailError() throws Exception {
		FeedbackPersistence persistence = mock(FeedbackPersistence.class);
		when(persistence.getValidatedToNotifyInfo()).thenReturn(
				new CommentInfo[] { new CommentInfo(1, "a@b.com", "100"),
						new CommentInfo(2, "c@d.com", "101") });
		Mailer mailer = mock(Mailer.class);
		doThrow(new MessagingException()).when(mailer).sendValidatedMail(
				"c@d.com", "101");
		feedback = new Feedback(persistence, mailer);
		feedback.notifyValidated();

		verify(mailer).sendValidatedMail("a@b.com", "100");
		verify(persistence, times(1)).setNotified(1);
	}
}
