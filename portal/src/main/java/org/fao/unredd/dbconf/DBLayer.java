package org.fao.unredd.dbconf;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.NoSuchConfigurationException;
import org.fao.unredd.layers.NoSuchIndicatorException;
import org.fao.unredd.layers.OutputDescriptor;
import org.fao.unredd.layers.Outputs;
import org.fao.unredd.portal.ConfigurationException;

public class DBLayer implements Layer {

	private DataSource dataSource;
	private String layerName;

	public DBLayer(DataSource dataSource, String layerName) {
		this.dataSource = dataSource;
		this.layerName = layerName;
	}

	@Override
	public Outputs getOutputs() throws IOException {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			resultSet = statement
					.executeQuery("SELECT id, name FROM indicators WHERE layer_id='"
							+ layerName + "';");
			ArrayList<OutputDescriptor> outputs = new ArrayList<OutputDescriptor>();
			while (resultSet.next()) {
				String indicatorId = resultSet.getString("id");
				String indicatorName = resultSet.getString("name");
				OutputDescriptor descriptor = new OutputDescriptor(indicatorId,
						indicatorName, "no field id");
				outputs.add(descriptor);
			}
			return new Outputs(outputs);
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

	@Override
	public String getOutput(String outputId) throws NoSuchIndicatorException,
			IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setOutput(String id, String outputName, String fieldId,
			String content) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public File getWorkFile(String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getWorkspace() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getQualifiedName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getConfiguration(String id)
			throws NoSuchConfigurationException, IOException {
		throw new UnsupportedOperationException();
	}

}
