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
import java.util.TreeSet;

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

	private ArrayList<String> serieNames = null;
	private ArrayList<TreeSet<MeasurePoint>> values = null;

	private class MeasurePoint implements Comparable<MeasurePoint> {
		private Date date;
		private String value;

		public MeasurePoint(Date dateLabel, String value) {
			this.date = dateLabel;
			this.value = value;
		}

		@Override
		public int compareTo(MeasurePoint that) {
			return this.date.compareTo(that.date);
		}

		/**
		 * @return an instance of MeasurePoint in the same date but with no data
		 */
		public MeasurePoint createNull() {
			return new MeasurePoint(date, "null");
		}

	}

	public Output(String id, String name, String fieldId, String title) {
		super(id, name, fieldId, title);
	}

	public ArrayList<ArrayList<String>> getData(String objectid) {
		// String[][] ret = {{"1","2","3"},{"2","3","4"},{"2","4","5"}};
		if (this.values == null) {
			this.cargarDatos(objectid);
		}
		ArrayList<ArrayList<String>> ret = new ArrayList<ArrayList<String>>();
		for (TreeSet<MeasurePoint> serie : values) {
			ArrayList<String> serieLabels = new ArrayList<String>();
			for (MeasurePoint measurePoint : serie) {
				serieLabels.add(measurePoint.value);
			}
			ret.add(serieLabels);
		}
		return ret;
	}

	public List<String> getSeries(String objectid) {
		// String[] ret = {"TF","OT","OTF"};
		if (this.serieNames == null) {
			this.cargarDatos(objectid);
		}
		return this.serieNames;
	}

	private void setSeries(ArrayList<String> series) {
		// TODO Auto-generated method stub
		this.serieNames = series;
	}

	private void addSerie(String string) {
		// TODO Auto-generated method stub
		this.serieNames.add(string);
	}

	public List<String> getLabels(String objectid) {
		if (this.values == null) {
			this.cargarDatos(objectid);
		}

		List<String> labels = new ArrayList<String>();
		TreeSet<MeasurePoint> serie = values.get(0);
		for (MeasurePoint measurePoint : serie) {
			labels.add(measurePoint.date.toString());
		}
		return labels;
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

							serieNames = new ArrayList<String>();
							values = new ArrayList<TreeSet<MeasurePoint>>();

							while (resultSet.next()) {
								String clases = resultSet.getString("class");
								addSerie(clases);

								addSerie(Array2ArrayListDates(resultSet
										.getArray("labels")),
										Array2ArrayList(resultSet
												.getArray("data_values")));

							}
							fillNulls();

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

	/**
	 * adds null measure points to all series so that they all have the same
	 * dates
	 */
	private void fillNulls() {
		for (TreeSet<MeasurePoint> serie : values) {
			for (MeasurePoint measurePoint : serie) {
				for (TreeSet<MeasurePoint> serieToFill : values) {
					if (serieToFill == serie) {
						continue;
					}
					if (!serieToFill.contains(measurePoint)) {
						serieToFill.add(measurePoint.createNull());
					}
				}
			}
		}
	}

	private void addSerie(ArrayList<Date> dates, ArrayList<String> values) {
		TreeSet<MeasurePoint> serie = new TreeSet<Output.MeasurePoint>();
		for (int i = 0; i < dates.size(); i++) {
			serie.add(new MeasurePoint(dates.get(i), values.get(i)));
		}
		this.values.add(serie);
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

	private ArrayList<Date> Array2ArrayListDates(Array array) {
		ArrayList<Date> ret = null;
		ret = new ArrayList<Date>();
		try {
			Date[] tmparray = (Date[]) array.getArray();
			for (int i = 0; i < tmparray.length; i++) {
				ret.add(tmparray[i]);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
}
