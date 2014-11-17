package org.fao.unredd.feedback;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBUtils {

	public static void processConnection(String resourceName,
			DBProcessor processor) throws PersistenceException {
		InitialContext context;
		DataSource dataSource;
		try {
			context = new InitialContext();
			dataSource = (DataSource) context.lookup("java:/comp/env/jdbc/"
					+ resourceName);
		} catch (NamingException e) {
			throw new PersistenceException("Cannot obtain Datasource", e);
		}
		try {
			Connection connection = dataSource.getConnection();
			processor.process(connection);
			connection.close();
		} catch (SQLException e) {
			throw new PersistenceException("Database error", e);
		}
		try {
			context.close();
		} catch (NamingException e) {
			// ignore
		}

	}

	public interface DBProcessor {

		void process(Connection connection) throws SQLException;

	}

}
