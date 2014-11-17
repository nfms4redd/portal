package org.fao.unredd.feedback;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBFeedbackPersistence implements FeedbackPersistence {

	private String tableName;

	public DBFeedbackPersistence(String tableName) {
		this.tableName = tableName;
	}

	@Override
	public void insert(final String geom, final String srid,
			final String comment, final String email,
			final String verificationCode) throws PersistenceException {
		DBUtils.processConnection("unredd-portal", new DBUtils.DBProcessor() {

			@Override
			public void process(Connection connection) throws SQLException {
				PreparedStatement statement = connection
						.prepareStatement("INSERT INTO "
								+ tableName
								+ "(geometry, comment, date, email, verification_code, validated, notified) "
								+ "VALUES"
								+ "(ST_GeomFromText(?, ?), ?, ?, ?, ?, false, false)");
				statement.setString(1, geom);
				statement.setInt(2, Integer.parseInt(srid));
				statement.setString(3, comment);
				statement.setDate(4, new Date(new java.util.Date().getTime()));
				statement.setString(5, email);
				statement.setString(6, verificationCode);
				statement.execute();
				statement.close();
			}
		});
	}

	@Override
	public void cleanOutOfDate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void createTable() throws PersistenceException {
		DBUtils.processConnection("unredd-portal", new DBUtils.DBProcessor() {
			@Override
			public void process(Connection connection) throws SQLException {
				PreparedStatement statement = connection
						.prepareStatement("CREATE TABLE IF NOT EXISTS "
								+ tableName + " ("//
								+ "geometry geometry('GEOMETRY', 900913),"//
								+ "comment varchar NOT NULL,"//
								+ "date timestamp NOT NULL,"//
								+ "email varchar NOT NULL,"//
								+ "verification_code varchar,"//
								+ "validated boolean NOT NULL DEFAULT false,"//
								+ "notified boolean NOT NULL DEFAULT false"//
								+ ")");
				statement.execute();

				statement.close();
			}
		});
	}

}
