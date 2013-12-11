package org.fao.unredd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import org.fao.unredd.dbconf.DBConf;
import org.fao.unredd.layers.LayerFactory;
import org.fao.unredd.portal.Config;
import org.fao.unredd.portal.ConfigurationException;

public class AppContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		String rootPath = servletContext.getRealPath("/");
		String configInitParameter = servletContext
				.getInitParameter("PORTAL_CONFIG_DIR");
		Config config = new Config(rootPath, configInitParameter);
		servletContext.setAttribute("config", config);

		DataSource dataSource = null;
		try {
			InitialContext cxt = new InitialContext();
			dataSource = (DataSource) cxt.lookup("java:/comp/env/jdbc/app");
			servletContext.setAttribute("datasource.app", dataSource);
		} catch (NamingException e) {
			throw new UnsupportedOperationException(
					"Cannot initialize database connection", e);
		}

		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			resultSet = statement
					.executeQuery("SELECT tablename FROM pg_tables WHERE "
							+ "schemaname='public' AND (tablename='layers' OR tablename='indicators');");
			HashSet<String> tableNames = new HashSet<String>();
			tableNames.add("layers");
			tableNames.add("indicators");
			while (resultSet.next()) {
				String tableName = resultSet.getString(1);
				tableNames.remove(tableName);
			}
			if (tableNames.contains("layers")) {
				statement
						.execute("CREATE TABLE layers(id varchar PRIMARY KEY, name varchar);"
								+ "insert into layers values('unredd:drc_provinces', 'Provincias');");
			}
			if (tableNames.contains("indicators")) {
				statement
						.execute("CREATE TABLE indicators(id SERIAL PRIMARY KEY, "
								+ "name varchar NOT NULL, "
								+ "layer_id varchar REFERENCES layers(id), "
								+ "content_type varchar, content bytea);"
								+ "insert into indicators (name, layer_id, content_type) "
								+ "values('Deforestation', 'unredd:drc_provinces', 'text/html')");
			}
		} catch (SQLException e) {
			throw new ConfigurationException("Error accessing the database", e);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e1) {
			}
			try {
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e1) {
			}
			try {
				connection.close();
			} catch (SQLException e1) {
			}
		}

		LayerFactory layerFactory = new DBConf(dataSource);
		servletContext.setAttribute("layer-factory", layerFactory);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
