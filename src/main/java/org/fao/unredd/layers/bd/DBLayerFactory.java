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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.fao.unredd.layers.Layer;
import org.fao.unredd.layers.LayerFactory;
import org.fao.unredd.layers.NoSuchIndicatorException;
import org.fao.unredd.layers.OutputDescriptor;
import org.fao.unredd.layers.Outputs;
import org.postgresql.util.PSQLException;

/**
 * Abstract {@link Layer} folder based implementation
 * 
 * @author fergonco
 */
public class DBLayerFactory implements LayerFactory {

	private static final String METADATA_FIELD_ID_PROPERTY_NAME = "field-id";
	private static final String METADATA_INDICATOR_NAME_PROPERTY_NAME = "indicator-name";
	private static final String METADATA_PROPERTIES_FILE_NAME = "metadata.properties";
	private static final String OUTPUT_FILE_NAME = "result.xml";
	private static final String OUTPUT = "output";
	private static final String CONFIGURATION = "configuration";
	private static final String WORK = "work";
	private File root;
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

	
	private ResultSet getMetadataProperties()
			throws SQLException {
		ResultSet ret = null;
		ResultSet metadata=null;
		try {
			InitialContext context = new InitialContext();
			DataSource dataSource = (DataSource) context
					.lookup("java:/comp/env/jdbc/app");
			Connection connection = dataSource.getConnection();
			Statement statement = connection.createStatement();
			ResultSet result = statement
					.executeQuery("select * count from indicators.indicators_metadata WHERE layer_id='"+this.qName+"'");
			if (result.next()) {
				 		metadata=result;
				 
			} else {
				metadata=null;
			}
			result.close();
			statement.close();
			connection.close();
			} catch (NamingException e) {
				return null;
				//throw new SQLException("Cannot find the database", e);
			}
		 catch (PSQLException e) {
			 //TODO MAnejar errores sql, no conecta, permiso denegado, loguear estos errores
		//Nothing, return false	
		}

		catch (Exception e) {
				 e.getMessage();
			//Nothing, return false	
			}

		/*		Properties metadata = new Properties();
		File metadataFile = getMetadataFile(outputRoot);
		if (metadataFile.exists()) {
			metadata.load(new FileInputStream(metadataFile));
		}
		*/
		return metadata;
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
