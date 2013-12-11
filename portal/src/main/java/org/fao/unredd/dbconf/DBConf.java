package org.fao.unredd.dbconf;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.LayerFactory;
import org.fao.unredd.layers.MosaicLayer;
import org.fao.unredd.portal.ConfigurationException;

public class DBConf implements LayerFactory {

	private DataSource dataSource;

	public DBConf(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Layer newLayer(String layerName) throws IOException {
		return new DBLayer(dataSource, layerName);
	}

	@Override
	public MosaicLayer newMosaicLayer(String layer) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean exists(String layerName) throws ConfigurationException {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			resultSet = statement
					.executeQuery("SELECT name FROM layers WHERE id='"
							+ layerName + "';");
			return resultSet.next();
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
				statement.close();
			} catch (SQLException e1) {
			}
			try {
				connection.close();
			} catch (SQLException e1) {
			}
		}
	}
}
