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

import java.util.ArrayList;
import java.util.List;

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

	public Output(String id, String name, String fieldId) {
		super(id, name, fieldId);
	}
	public Output(String id, String name, String fieldId,String title) {
		super(id, name, fieldId);
		this.setTitle(title);
		
	}
	
	public String[][] getData(){
		String[][] ret = {{"1","2","3"},{"2","3","4"},{"2","4","5"}};
		return ret;
		
	}
	public String[] getSeries(){
		String[] ret = {"TF","OT","OTF"};
		return ret;
	}
	public List<String> getLabels(){
		List<String> ret =  new ArrayList<String>();
		ret.add("1999");
		ret.add("2000");
		ret.add("2001");
		return ret;		
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
	
	public String getGraphicType(){
		return "3d";
	}

	public String getHover(){
		return "hover";
	}

	public String getFooter(){
		return "MR";
	}

}
