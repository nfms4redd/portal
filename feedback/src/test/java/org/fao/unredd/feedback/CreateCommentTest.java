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

import javax.mail.MessagingException;

import org.fao.unredd.portal.PersistenceException;
import org.junit.Test;

public class CreateCommentTest {

	private static final String validGeometry = "POINT(0 0)";
	private static final String validComment = "boh";
	private static final String validEmail = "nombre@dominio.com";
	private static final String validSRID = "900913";
	private Feedback feedback;

	@Test
	public void testMissingParameters() throws CannotSendMailException,
			PersistenceException {
		feedback = new Feedback(mock(FeedbackPersistence.class),
				mock(Mailer.class));
		testMandatoryParameter(null, validSRID, validComment, validEmail);
		testMandatoryParameter(validGeometry, null, validComment, validEmail);
		testMandatoryParameter(validGeometry, validSRID, null, validEmail);
		testMandatoryParameter(validGeometry, validSRID, validComment, null);
	}

	private void testMandatoryParameter(String geom, String srid,
			String comment, String email) throws CannotSendMailException,
			PersistenceException {
		try {
			feedback.insertNew(geom, srid, comment, email);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testInvalidMailNoInsert() throws Exception {
		FeedbackPersistence persistence = mock(FeedbackPersistence.class);
		Mailer mailer = mock(Mailer.class);
		doThrow(new MessagingException()).when(mailer).sendMail(anyString(),
				anyString());
		feedback = new Feedback(persistence, mailer);

		try {
			feedback.insertNew(validGeometry, validSRID, validComment,
					"sindominio@");
			fail();
		} catch (Exception e) {
		}
		verify(persistence, never()).insert(anyString(), anyString(),
				anyString(), anyString(), anyString());
	}

	@Test
	public void testInsert() throws Exception {
		FeedbackPersistence persistence = mock(FeedbackPersistence.class);
		feedback = new Feedback(persistence, mock(Mailer.class));
		feedback.insertNew(validGeometry, validSRID, validComment, validEmail);
		verify(persistence, times(1)).insert(eq(validGeometry), eq(validSRID),
				eq(validComment), eq(validEmail), anyString());
	}

	@Test
	public void testDifferentVerificationCodes() throws Exception {
		feedback = new Feedback(mock(FeedbackPersistence.class),
				mock(Mailer.class));
		assertTrue(feedback.insertNew(validGeometry, validSRID, validComment,
				validEmail) != feedback.insertNew("POINT(1 1)", validSRID,
				validComment, validEmail));
	}

	@Test
	public void testInsertCleansOutOfDate() throws Exception {
		FeedbackPersistence persistence = mock(FeedbackPersistence.class);
		feedback = new Feedback(persistence, mock(Mailer.class));
		feedback.insertNew(validGeometry, validSRID, validComment, validEmail);
		verify(persistence, times(1)).cleanOutOfDate();
	}
}
