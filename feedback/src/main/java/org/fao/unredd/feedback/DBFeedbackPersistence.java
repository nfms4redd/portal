package org.fao.unredd.feedback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.fao.unredd.portal.DBUtils;
import org.fao.unredd.portal.PersistenceException;

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
				statement.setTimestamp(4, new Timestamp(new Date().getTime()));
				statement.setString(5, email);
				statement.setString(6, verificationCode);
				statement.execute();
				statement.close();
			}
		});
	}

	@Override
	public void cleanOutOfDate() throws PersistenceException {
		DBUtils.processConnection("unredd-portal", new DBUtils.DBProcessor() {
			@Override
			public void process(Connection connection) throws SQLException {
				PreparedStatement statement = connection
						.prepareStatement("DELETE FROM "
								+ tableName
								+ " WHERE verification_code IS NOT NULL"
								+ " AND date < (current_timestamp - interval '5 days')");
				statement.execute();

				statement.close();
			}
		});
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

	@Override
	public boolean existsUnverified(final String verificationCode)
			throws PersistenceException {
		return DBUtils.processConnection("unredd-portal",
				new DBUtils.ReturningDBProcessor<Boolean>() {
					@Override
					public Boolean process(Connection connection)
							throws SQLException {
						PreparedStatement statement = connection
								.prepareStatement("SELECT count(*) FROM "
										+ tableName
										+ " WHERE verification_code = ?");
						statement.setString(1, verificationCode);
						ResultSet resultSet = statement.executeQuery();
						resultSet.next();
						int verificationCodeCount = resultSet.getInt(1);
						statement.close();

						return verificationCodeCount == 1;
					}
				});
	}

	@Override
	public void verify(final String verificationCode)
			throws PersistenceException {
		DBUtils.processConnection("unredd-portal", new DBUtils.DBProcessor() {
			@Override
			public void process(Connection connection) throws SQLException {
				PreparedStatement statement = connection
						.prepareStatement("UPDATE TABLE " + tableName
								+ " SET verification_code = NULL "
								+ "WHERE verification_code = ?");
				statement.setString(1, verificationCode);
				statement.execute();

				statement.close();
			}
		});
	}

}
