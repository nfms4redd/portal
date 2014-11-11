package org.fao.unredd.feedback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBFeedbackPersistence implements FeedbackPersistence {

	@Override
	public void insert(String geom, String srid, String comment, String email,
			String verificationCode) throws PersistenceException {
		InitialContext context;
		DataSource dataSource;
		try {
			context = new InitialContext();
			dataSource = (DataSource) context
					.lookup("java:/comp/env/jdbc/unredd-portal");
		} catch (NamingException e) {
			throw new PersistenceException("Cannot obtain Datasource", e);
		}
		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement statement = connection
					.prepareStatement("INSERT INTO comments "
							+ "(geometry, comment, date, email, verification_code, validated, notified) "
							+ "VALUES"
							+ "(GeomFromText(?, ?), ?, ?, ?, ?, false, false)");
			statement.setString(1, geom);
			statement.setString(2, srid);
			statement.setString(3, comment);
			statement.setString(4, new SimpleDateFormat("yyyy-MM-dd hh:mm:SS")
					.format(new Date()));
			statement.setString(5, email);
			statement.setString(6, verificationCode);
			statement.execute();

			statement.close();
			connection.close();
		} catch (SQLException e) {
			throw new PersistenceException("Cannot insert new message", e);
		}
		try {
			context.close();
		} catch (NamingException e) {
			// ignore
		}
	}

	@Override
	public void cleanOutOfDate() {
		// TODO Auto-generated method stub

	}

}
