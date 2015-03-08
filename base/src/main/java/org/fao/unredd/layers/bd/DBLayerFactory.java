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

	private String schemaName;

	public DBLayerFactory(String schemaName) {
		super();
		this.schemaName = schemaName;
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
									.prepareStatement("select count(*) count from "
											+ schemaName
											+ ".redd_stats_metadata"
											+ " WHERE layer_name=?");
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
			// TODO if error because not exsist table, create table
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Layer newLayer(String layerName) {
		// TODO Auto-generated method stub
		Layer nuevaLayer = null;
		try {
			nuevaLayer = new DBLayer(schemaName, layerName);
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nuevaLayer;
	}
}
