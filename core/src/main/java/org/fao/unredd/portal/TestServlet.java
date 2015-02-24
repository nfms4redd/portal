package org.fao.unredd.portal;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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
			DataSource ds = (DataSource) getServletContext().getAttribute(
					"datasource.app");

			Connection conn = ds.getConnection();
			Statement statement = conn.createStatement();
			statement.execute("select * from boh;");
			statement.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new StatusServletException(500, "Database access error");
		}
	}
}
