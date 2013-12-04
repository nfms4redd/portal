package org.fao.unredd.portal;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

public class TestServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			// TODO put in servlet context?
			InitialContext cxt = new InitialContext();
			DataSource ds = (DataSource) cxt.lookup("java:/comp/env/jdbc/app");

			Connection conn = ds.getConnection();
			Statement statement = conn.createStatement();
			statement.execute("");
			statement.close();
			conn.close();
		} catch (NamingException e) {
			throw new StatusServletExceptionImpl(500,
					"Server configuration error");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new StatusServletExceptionImpl(500, "Database access error");
		}
	}
}
