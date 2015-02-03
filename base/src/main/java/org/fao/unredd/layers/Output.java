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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.fao.unredd.portal.DBUtils;
import org.fao.unredd.portal.PersistenceException;

/**
 * One of the outputs a layer can have.
 * 
 * @author manureta
 */
public class Output extends OutputDescriptor {

	private String subtitle;
	private String description;
	private String y_label;
	private String units;
	private int tooltipsdecimals;
	private String layer_name;
	private String table_name_data;
	private String division_field_id;
	private String graphic_type;

	private ArrayList<String> series = null;
	private ArrayList<String> labels = null;
	private ArrayList<ArrayList<String>> values = null;

	public Output(String id, String name, String fieldId, String title) {
		super(id, name, fieldId, title);
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

	public void setGraphicType(String graphic_type) {
		this.graphic_type = graphic_type;
	}

	public String getGraphicType() {
		return graphic_type;
	}

	public String getHover() {
		return "hover";
	}

	public String getFooter() {
		return "MR";
	}

	private void cargarDatos(final String objectid) {

		try {
			DBUtils.processConnection("unredd-portal",
					new DBUtils.DBProcessor() {

						@Override
						public void process(Connection connection)
								throws SQLException {

							PreparedStatement statement = connection
									.prepareStatement("SELECT "
											// + this.getDivision_field_id()
											+ "division_id,class,array_agg(fecha_result) labels,array_agg(ha) data_values FROM "
											+ "(SELECT division_id,class,fecha_result, ha "
											+ "FROM "
											+ getTable_name_data()
											+ " WHERE "
											+ " division_id  =  ? "
											+ "ORDER BY fecha_result asc ) foo	GROUP BY "
											+ "division_id,class ");

							statement.setString(1, objectid);
							ResultSet resultSet = statement.executeQuery();

							series = new ArrayList<String>();
							labels = new ArrayList<String>();
							values = new ArrayList<ArrayList<String>>();

							while (resultSet.next()) {
								String clases = resultSet.getString("class");
								addSerie(clases);

								setLabels(Array2ArrayListDates(resultSet
										.getArray("labels")));
								addValues(Array2ArrayList(resultSet
										.getArray("data_values")));

							}
							resultSet.close();
							statement.close();
							connection.close();

						}
					});
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			// TODO if error because not exsist table, create table
			// e.printStackTrace();
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
}
