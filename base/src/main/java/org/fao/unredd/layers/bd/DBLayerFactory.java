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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.LayerFactory;
import org.fao.unredd.portal.DBUtils;
import org.fao.unredd.portal.PersistenceException;

/**
 * Databases based implementation of {@link LayerFactory}
 * 
 * @author manureta
 * 
 */
public class DBLayerFactory implements LayerFactory {

	private String qName;

	public DBLayerFactory(String layerName) {
		String[] workspaceAndName = layerName.split(Pattern.quote(":"));
		if (workspaceAndName.length != 2) {
			throw new IllegalArgumentException(
					"The layer name must have the form workspaceName:layerName");
		}
		this.qName = layerName;

	}

	public String getQualifiedName() {
		return qName;
	}

	@Override
	public boolean exists(final String layerName) {
		try {
			return DBUtils.processConnection("unredd-portal",
					new DBUtils.ReturningDBProcessor<Boolean>() {
						@Override
						public Boolean process(Connection connection)
								throws SQLException {
							boolean ret = false;

							PreparedStatement statement = connection
									.prepareStatement("select count(*) count from indicators.indicators_metadata WHERE layer_name=?");
							statement.setString(1, layerName);
							ResultSet resultSet = statement.executeQuery();
							if (resultSet.next()) {
								if (resultSet.getInt("count") > 0) {
									ret = true;
								}
							} else {
								ret = false;
							}
							resultSet.close();
							statement.close();
							connection.close();
							return ret;

						}
					});
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Layer newLayer(String layerName) {
		// TODO Auto-generated method stub
		Layer nuevaLayer = null;
		try {
			nuevaLayer = new DBLayer(layerName);
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nuevaLayer;
	}

	public void createTable() throws PersistenceException {
		DBUtils.processConnection("unredd-portal", new DBUtils.DBProcessor() {
			@Override
			public void process(Connection connection) throws SQLException {
				PreparedStatement statement = connection
						.prepareStatement("CREATE TABLE IF NOT EXISTS indicators.indicators_metadata"
								+ " ( "
								+ " id integer NOT NULL DEFAULT nextval('indicators.indicators_metadata_id_seq'::regclass),"
								+ " name character varying,"
								+ " title character varying,"
								+ " subtitle character varying,"
								+ " description character varying,"
								+ " y_label character varying,"
								+ " units character varying,"
								+ " tooltipsdecimals integer,"
								+ " layer_name character varying, -- Nombre de la capa del portal para la cual se visualizara el indicador"
								+ " table_name_division character varying, -- Tabla con divisiones/regiones para el calculo de las estadisticas"
								+ " division_field_id character varying, -- Campo identificador de la tabla de divisiones, debe coincidir con el de la capa del portal en geoserver para ser visualizado"
								+ " class_table_name character varying, -- Tabla con clasificaciones"
								+ " class_field_name character varying, -- Campo de tabla de clasificaciones a utilizar"
								+ " date_field_name character varying, -- Campo de fecha en caso de tabla multitemporal"
								+ " table_name_data character varying, -- Nombre de la tabla de destino de los datos estadisticos calculados"
								+ " CONSTRAINT indicators_metadata_pkey PRIMARY KEY (id)"
								+ " )" + " WITH (" + " OIDS=FALSE" + " )");
				statement.execute();

				statement.close();
			}
		});
	}
}
