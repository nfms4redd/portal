package org.fao.unredd.feedback;


public interface FeedbackPersistence {

	void insert(String geom, String srid, String comment, String email,
			String verificationCode) throws PersistenceException;

	void cleanOutOfDate();

}
