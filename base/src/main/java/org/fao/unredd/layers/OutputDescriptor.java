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

/**
 * Describes one of the outputs a layer can have.
 * 
 * @author fergonco, manureta
 * 
 */
public class OutputDescriptor {

	/**
	 * A unique id that is used by the client when requesting a specific output
	 */
	private String id;

	/**
	 * The name of the output
	 */
	private String name;

	/**
	 * The fieldId that identifies the objects in the layer for whom this output
	 * contains information
	 */

	private String fieldId;

	/**
	 * The name of the output
	 */
	private String title;

	public OutputDescriptor(String id, String name, String fieldId, String title) {
		super();
		this.id = id;
		this.name = name;
		this.fieldId = fieldId;
		this.title = title;
	}

	public String toJSON() {
		return "{" + "\"id\":\"" + id + "\"," + //
				"\"name\":\"" + name + "\"," + //
				"\"title\":\"" + title + "\"," + //
				"\"fieldId\":\"" + fieldId + "\"" + "}";
	}

	public String getId() {
		return id;
	}

	public String getFieldId() {
		return fieldId;
	}

	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

}
