/**
 * nfms4redd Portal Interface - http://nfms4redd.org/
 *
 * (C) 2012, FAO Forestry Department (http://www.fao.org/forestry/)
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */
package org.fao.unredd.layers.bd;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.NoSuchIndicatorException;
import org.fao.unredd.layers.Output;
import org.fao.unredd.layers.Outputs;
import org.fao.unredd.portal.DBUtils;
import org.fao.unredd.portal.PersistenceException;

/**
 * Concrete implementation of the {@link Layer} interface based on databases
 * 
 * @author manureta
 * 
 */
public class DBLayer implements Layer {
	private String qName;
	public Outputs var_outputs;
	public ArrayList<Output> tempoutputs;

	public DBLayer(String layerName) throws PersistenceException {
		// TODO Auto-generated constructor stub
		String[] workspaceAndName = layerName.split(Pattern.quote(":"));
		if (workspaceAndName.length != 2) {
			throw new IllegalArgumentException(
					"The layer name must have the form workspaceName:layerName");
		}
		this.qName = layerName;

		DBUtils.processConnection("unredd-portal",
				new DBUtils.ReturningDBProcessor<Boolean>() {
					@Override
					public Boolean process(Connection connection)
							throws SQLException {
						boolean ret = false;

						PreparedStatement statement = connection
								.prepareStatement("select * from indicators.indicators_metadata WHERE layer_name=?");
						statement.setString(1, qName);
						ResultSet resultSet = statement.executeQuery();

						ArrayList<Output> temp = null;
						temp = new ArrayList<Output>();
						while (resultSet.next()) {
							// Cargar los datos para este layer de todos los
							// OutputDescriptors (diferentes graficos)
							int id = resultSet.getInt("id");
							String name = resultSet.getString("name");
							String division_field_id = resultSet
									.getString("division_field_id");
							String title = resultSet.getString("title");
							Output output = new Output("" + id, name,
									division_field_id, title);
							// Ver de agregar estos meta datos al Output.
							output.setSubtitle(resultSet.getString("subtitle"));
							output.setDescription(resultSet
									.getString("description"));
							output.setY_label(resultSet.getString("y_label"));
							output.setUnits(resultSet.getString("units"));
							output.setTooltipsdecimals(resultSet
									.getInt("tooltipsdecimals"));
							output.setLayer_name(resultSet
									.getString("layer_name"));
							output.setTable_name_data(resultSet
									.getString("table_name_data"));
							output.setDivision_field_id(resultSet
									.getString("division_field_id"));
							output.setGraphicType(resultSet
									.getString("graphic_type"));
							// TODO: Agregar un metodo al output para obtener
							// los datos de
							// un determinado Feautre-id

							temp.add(output);

						}
						var_outputs = new Outputs(temp);
						resultSet.close();
						statement.close();
						connection.close();
						return ret;

					}
				});

	}

	@Override
	public Outputs getOutputs() throws IOException, SQLException {
		// TODO Auto-generated method stub
		/*
		 * OutputDescriptor descriptor = new OutputDescriptor(""+this.id,
		 * this.name, this.division_field_id); return new Outputs(descriptor);
		 */
		return this.var_outputs;
	}

	@Override
	public Output getOutput(String outputId) throws NoSuchIndicatorException {

		// Declaramos el Iterador e imprimimos los Elementos del ArrayList
		Iterator<Output> outputsIterator = this.var_outputs.iterator();
		while (outputsIterator.hasNext()) {
			Output elemento = outputsIterator.next();
			if (elemento.getId().equals(outputId)) {
				return elemento;
			}
		}
		return null;
	}

}
