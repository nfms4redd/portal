package org.fao.unredd.feedback;

import org.fao.unredd.portal.PersistenceException;

public interface FeedbackPersistence {

	void insert(String geom, String srid, String comment, String email,
			String layerName, String layerDate, String verificationCode)
			throws PersistenceException;

	void cleanOutOfDate() throws PersistenceException;

	void createTable() throws PersistenceException;

	boolean existsUnverified(String verificationCode)
			throws PersistenceException;

	void verify(String verificationCode) throws PersistenceException;

	CommentInfo[] getValidatedToNotifyInfo() throws PersistenceException;

	void setNotified(int id) throws PersistenceException;

}
