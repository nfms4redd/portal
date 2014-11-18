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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.NoSuchIndicatorException;
import org.fao.unredd.layers.Output;
import org.fao.unredd.layers.Outputs;
import org.postgresql.util.PSQLException;

/**
 * * Concrete implementation of the {@link Layer} interface based on databases
 * 
 * @author manureta
 */
public class DBLayer implements Layer {
	private String qName;
	public Outputs var_outputs;
	public ArrayList<Output> tempoutputs;

	public DBLayer(String layerName) {
		// TODO Auto-generated constructor stub
		String[] workspaceAndName = layerName.split(Pattern.quote(":"));
		if (workspaceAndName.length != 2) {
			throw new IllegalArgumentException(
					"The layer name must have the form workspaceName:layerName");
		}
		this.qName = layerName;
		try {
			InitialContext context = new InitialContext();
			DataSource dataSource = (DataSource) context
					.lookup("java:/comp/env/jdbc/unredd-portal");
			Connection connection = dataSource.getConnection();
			Statement statement = connection.createStatement();
			ResultSet result = statement
					.executeQuery("select * from indicators.indicators_metadata WHERE layer_name='"
							+ this.qName + "'");
			ArrayList<Output> temp = null;
			temp = new ArrayList<Output>();
			while (result.next()) {
				// Cargar los datos para este layer de todos los
				// OutputDescriptors (diferentes graficos)
				int id = result.getInt("id");
				String name = result.getString("name");
				String division_field_id = result
						.getString("division_field_id");

				Output output = new Output("" + id, name, division_field_id);
				// Ver de agregar estos meta datos al Output.
				output.setTitle(result.getString("title"));
				output.setSubtitle(result.getString("subtitle"));
				output.setDescription(result.getString("description"));
				output.setY_label(result.getString("y_label"));
				output.setUnits(result.getString("units"));
				output.setTooltipsdecimals(result.getInt("tooltipsdecimals"));
				output.setLayer_name(result.getString("layer_name"));
				output.setTable_name_data(result.getString("table_name_data"));
				output.setDivision_field_id(result
						.getString("division_field_id"));
				// TODO: Agregar un metodo al output para obtener los datos de
				// un determinado Feautre-id

				temp.add(output);

			}
			this.var_outputs = new Outputs(temp);
			result.close();
			statement.close();
			connection.close();
		} catch (NamingException e) {
			// return null;
			e.getMessage();
			// throw new SQLException("Cannot find the database", e);
		} catch (PSQLException e) {
			// TODO Manejar errores sql, no conecta, permiso denegado, loguear
			// estos errores
			e.getMessage();
		}

		catch (Exception e) {
			e.getMessage();
			// Nothing, return false
		}

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
