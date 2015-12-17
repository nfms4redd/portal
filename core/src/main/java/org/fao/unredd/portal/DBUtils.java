package org.fao.unredd.portal;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBUtils {

	public static void processConnection(String resourceName,
			final DBProcessor processor) throws PersistenceException {
		processConnection(resourceName, new ReturningDBProcessor<Integer>() {

			@Override
			public Integer process(Connection connection) throws SQLException {
				processor.process(connection);
				return 0;
			}
		});
	}

	public static <T> T processConnection(String resourceName,
			ReturningDBProcessor<T> processor) throws PersistenceException {
		InitialContext context;
		DataSource dataSource;
		try {
			context = new InitialContext();
			dataSource = (DataSource) context.lookup("java:/comp/env/jdbc/"
					+ resourceName);
		} catch (NamingException e) {
			throw new PersistenceException("Cannot obtain Datasource", e);
		}
		T ret = null;
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			ret = processor.process(connection);
		} catch (SQLException e) {
			throw new PersistenceException("Database error", e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
		try {
			context.close();
		} catch (NamingException e) {
			// ignore
		}

		return ret;
	}

	public interface DBProcessor {

		void process(Connection connection) throws SQLException;

	}

	public interface ReturningDBProcessor<T> {

		T process(Connection connection) throws SQLException;

	}

}
