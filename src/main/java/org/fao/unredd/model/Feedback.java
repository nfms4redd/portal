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
package org.fao.unredd.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Feedback {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String layerName;

	private String userName;

	private String userEMail;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLayerName() {
		return layerName;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEMail() {
		return userEMail;
	}

	public void setUserEMail(String userEMail) {
		this.userEMail = userEMail;
	}

}
