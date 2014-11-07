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
public class DBLayerFactory implements Layer, LayerFactory {

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

	@Override
	public Outputs getOutputs() throws SQLException {
		// Buscar indicadores para la capa en la BD...
		ArrayList<OutputDescriptor> outputDescriptors = new ArrayList<OutputDescriptor>();
		try {
			InitialContext context = new InitialContext();
			DataSource dataSource = (DataSource) context
					.lookup("java:/comp/env/jdbc/app");
			Connection connection = dataSource.getConnection();
			Statement statement = connection.createStatement();
			ResultSet result = statement
					.executeQuery("select * from indicators.indicators_metadata WHERE layer_id='"+this.qName+"'");

//			PrintWriter writer = resp.getWriter();

			if (result.next()) {
				outputDescriptors.add(new OutputDescriptor(result.getString("id"),result.getString("title"),result.getString("division_field_id")));

/* CODIGO PARA TRAER LOS DATOS DEL INDICADOR *********************************************************************** 
				//				writer.println("Number of results: " + result.getInt(1));
				try {
					ResultSet result_data = statement
										.executeQuery("SELECT division_id,class,array_agg(fecha_result),array_agg(ha) FROM stb_cobertura "
						 + "WHERE division_id = '"+this.qName+"' GROUP BY division_id,class");
				} catch (SQLException e) {
					throw new ServletException("Cannot find the database", e);
				}
	*/			
			} else {
//				writer.print("No results");
			}
			result.close();
			statement.close();
			connection.close();
		} catch (NamingException e) {
			throw new SQLException("Cannot find the database", e);
		} catch (SQLException e) {
			throw new SQLException("Cannot find the database", e);
		}
//.executeQuery("SELECT division_id,class,array_agg(fecha_result),array_agg(ha) FROM stb_cobertura "
// + "WHERE division_id = '"+this.qName+"' GROUP BY division_id,class");

		return new Outputs(outputDescriptors);
	}

	private ResultSet getMetadataProperties()
			throws SQLException {
		ResultSet ret = null;
		try {
			InitialContext context = new InitialContext();
			DataSource dataSource = (DataSource) context
					.lookup("java:/comp/env/jdbc/app");
			Connection connection = dataSource.getConnection();
			Statement statement = connection.createStatement();
			ResultSet result = statement
					.executeQuery("select * count from indicators.indicators_metadata WHERE layer_id='"+this.qName+"'");
			if (result.next()) {
				 		ret=result;
				 
			} else {
				ret=null;
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
		Array metadata[]=null;
		return metadata;
	}


	@Override
	public Array getOutput(String outputId) throws NoSuchIndicatorException,
			IOException {
	//TODO
		Array ret = null;// = "nada";
		try {
			InitialContext context = new InitialContext();
			DataSource dataSource = (DataSource) context
					.lookup("java:/comp/env/jdbc/app");
			Connection connection = dataSource.getConnection();
			Statement statement = connection.createStatement();
			ResultSet result = statement
					.executeQuery("SELECT division_id,class,array_agg(fecha_result),array_agg(ha) FROM stb_cobertura "
					 + "WHERE division_id = '"+this.qName+"' GROUP BY division_id,class");

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
		
		return (Layer) new DBLayerFactory(layerName);
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
					.executeQuery("select count(*) count from indicators.indicators_metadata WHERE layer_id='"+layerName+"'");
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
		}

		catch (Exception e) {
				 e.getMessage();
			//Nothing, return false	
			}
		return ret;
		}
}
