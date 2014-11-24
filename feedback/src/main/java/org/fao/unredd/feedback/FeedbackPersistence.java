package org.fao.unredd.feedback;

import org.fao.unredd.portal.PersistenceException;

public interface FeedbackPersistence {

	void insert(String geom, String srid, String comment, String email,
			String verificationCode) throws PersistenceException;

	void cleanOutOfDate() throws PersistenceException;

	void createTable() throws PersistenceException;

}
