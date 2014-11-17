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
package org.fao.unredd.layers;

import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.postgresql.util.PSQLException;

/**
 * One of the outputs a layer can have.
 * 
 * @author manureta
 */
public class Output extends OutputDescriptor {

	private String title;
	private String subtitle;
	private String description;
	private String y_label;
	private String units;
	private int tooltipsdecimals;
	private String layer_name;
	private String table_name_data;
	private String division_field_id;

	private ArrayList<String> series = null;
	private ArrayList<String> labels = null;
	private ArrayList<ArrayList<String>> values = null;

	public Output(String id, String name, String fieldId) {
		super(id, name, fieldId);
	}

	public Output(String id, String name, String fieldId, String title) {
		super(id, name, fieldId);
		this.setTitle(title);

	}

	public ArrayList<ArrayList<String>> getData(String objectid) {
		// String[][] ret = {{"1","2","3"},{"2","3","4"},{"2","4","5"}};
		if (this.values == null) {
			this.cargarDatos(objectid);
		}
		return this.values;
	}

	private void addValues(ArrayList<String> array2ArrayList) {
		// TODO Auto-generated method stub

		this.values.add(array2ArrayList);

	}

	public List<String> getSeries(String objectid) {
		// String[] ret = {"TF","OT","OTF"};
		if (this.series == null) {
			this.cargarDatos(objectid);
		}
		return this.series;
	}

	private void setSeries(ArrayList<String> series) {
		// TODO Auto-generated method stub
		this.series = series;
	}

	private void addSerie(String string) {
		// TODO Auto-generated method stub
		this.series.add(string);
	}

	public List<String> getLabels(String objectid) {
		if (this.labels == null) {
			this.cargarDatos(objectid);
		}
		return this.labels;
	}

	private void setLabels(ArrayList<String> labels) {
		// TODO Auto-generated method stub
		this.labels = labels;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getY_label() {
		return y_label;
	}

	public void setY_label(String y_label) {
		this.y_label = y_label;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public int getTooltipsdecimals() {
		return tooltipsdecimals;
	}

	public void setTooltipsdecimals(int tooltipsdecimals) {
		this.tooltipsdecimals = tooltipsdecimals;
	}

	public String getLayer_name() {
		return layer_name;
	}

	public void setLayer_name(String layer_name) {
		this.layer_name = layer_name;
	}

	public String getTable_name_data() {
		return table_name_data;
	}

	public void setTable_name_data(String table_name_data) {
		this.table_name_data = table_name_data;
	}

	public String getDivision_field_id() {
		return division_field_id;
	}

	public void setDivision_field_id(String division_field_id) {
		this.division_field_id = division_field_id;
	}

	public String getGraphicType() {
		return "3d";
	}

	public String getHover() {
		return "hover";
	}

	public String getFooter() {
		return "MR";
	}

	private void cargarDatos(String objectid) {
		try {
			InitialContext context = new InitialContext();
			DataSource dataSource = (DataSource) context
					.lookup("java:/comp/env/jdbc/app");
			Connection connection = dataSource.getConnection();
			Statement statement = connection.createStatement();
			ResultSet result = statement
			// .executeQuery("SELECT "+this.getDivision_field_id()+",class,array_agg(fecha_result) labels,array_agg(ha) data_values FROM "+this.getTable_name_data()+" "
			// +
			// "WHERE "+this.getDivision_field_id()+" = '"+objectid+"' GROUP BY "+this.getDivision_field_id()+",class");

					.executeQuery("SELECT "
//							+ this.getDivision_field_id()
							+ "division_id "
							+ ",class,array_agg(fecha_result) labels,array_agg(ha) data_values FROM "
							+ "(SELECT " 
//							+ this.getDivision_field_id()
							+ "division_id "							
							+ ",class,fecha_result, ha " + "FROM "
							+ this.getTable_name_data() + " " + "WHERE "
//							+ this.getDivision_field_id()
							+ "division_id "
							+ " = '" + objectid
							+ "'" + "ORDER BY fecha_result asc"
							+ " ) foo	GROUP BY "+ "division_id " //+ this.getDivision_field_id()
							+ ",class ");
			this.series = new ArrayList<String>();
			this.labels = new ArrayList<String>();
			this.values = new ArrayList<ArrayList<String>>();

			while (result.next()) {
				String clases = result.getString("class");
				this.addSerie(clases);

				this.setLabels(Array2ArrayListDates(result.getArray("labels")));
				this.addValues(Array2ArrayList(result.getArray("data_values")));

			}
			result.close();
			statement.close();
			connection.close();
		} catch (NamingException e) {
			e.getMessage();
			// return null;
			// throw new SQLException("Cannot find the database", e);
		} catch (PSQLException e) {
			e.getMessage();
			// TODO MAnejar errores sql, no conecta, permiso denegado, loguear
			// estos errores
		}

		catch (Exception e) {
			e.getMessage();
			// Ejemplo debug, esto no deberia pasar..

			this.labels.add("1999");
			this.labels.add("2000");
			this.labels.add("2001");
			this.series.add("OT");
			this.series.add("TF");
			this.series.add("OTNF");

		}

	}

	private ArrayList<String> Array2ArrayList(Array array) {
		ArrayList<String> ret = null;
		ret = new ArrayList<String>();
		try {
			Float[] tmparray = (Float[]) array.getArray();
			for (int i = 0; i < tmparray.length; i++) {
				ret.add(tmparray[i].toString());
			}
		} catch (PSQLException e) {
			e.getMessage();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	private ArrayList<String> Array2ArrayListDates(Array array) {
		ArrayList<String> ret = null;
		ret = new ArrayList<String>();
		try {
			Date[] tmparray = (Date[]) array.getArray();
			for (int i = 0; i < tmparray.length; i++) {
				ret.add(tmparray[i].toString());
			}
		} catch (PSQLException e) {
			e.getMessage();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
}
