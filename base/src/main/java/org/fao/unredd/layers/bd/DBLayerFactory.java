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
import java.sql.Statement;
import java.util.regex.Pattern;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.LayerFactory;
import org.postgresql.util.PSQLException;

/**
 * Databases based implementation of {@link LayerFactory}
 * 
 * @author manureta
 */
public class DBLayerFactory implements LayerFactory {


	private String qName;

	public DBLayerFactory(String layerName) throws IOException {
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
	public boolean exists(String layerName) {
		// TODO Auto-generated method stub
		boolean ret = false;
		try {
			InitialContext context = new InitialContext();
			DataSource dataSource = (DataSource) context
					.lookup("java:/comp/env/jdbc/app");
			Connection connection = dataSource.getConnection();
			Statement statement = connection.createStatement();
			ResultSet result = statement
					.executeQuery("select count(*) count from indicators.indicators_metadata WHERE layer_name='"+layerName+"'");
			if (result.next()) {
				 if (result.getInt("count")>0){
						ret=true;
				 }
			} else {
				ret=false;
			}
			result.close();
			statement.close();
			connection.close();
			} catch (NamingException e) {
				return false;
				//throw new SQLException("Cannot find the database", e);
			}
		 catch (PSQLException e) {
			 //TODO MAnejar errores sql, no conecta, permiso denegado, loguear estos errores
		//Nothing, return false	
			 e.getMessage();
		}

		catch (Exception e) {
				 e.getMessage();
			//Nothing, return false	
			}
		return ret;
		}

	@Override
	public Layer newLayer(String layerName) throws IOException {
		// TODO Auto-generated method stub
		Layer nuevaLayer = new DBLayer(layerName);
		return nuevaLayer;
	}
}
