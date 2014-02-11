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

import java.io.File;
import java.io.IOException;

public class DBLocation implements Location {

	private String host;
	private String port;
	private String database;
	private String schema;
	private String tableName;
	private String user;

	public DBLocation(String host, String port, String database, String schema,
			String tableName, String user) {
		this.host = host;
		this.port = port;
		this.database = database;
		this.schema = schema;
		this.tableName = tableName;
		this.user = user;
	}

	@Override
	public String getGDALString(PasswordGetter passwordGetter)
			throws IOException {
		return getConnectionInfo() + " password="
				+ passwordGetter.getPassword(getConnectionInfo()) + "\"";
	}

	private String getConnectionInfo() {
		return "PG:\"host=" + host + " port=" + port + " dbname=" + database
				+ " user=" + user;
	}

	@Override
	public String getGDALFeatureName() {
		return schema + "." + tableName;
	}

	@Override
	public File getFile() {
		return null;
	}

	@Override
	public String toString() {
		return getConnectionInfo();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DBLocation) {
			DBLocation that = (DBLocation) obj;
			return this.host.equals(that.host) && this.port.equals(that.port)
					&& this.database.equals(that.database)
					&& this.schema.equals(that.schema)
					&& this.tableName.equals(that.tableName)
					&& this.user.equals(that.user);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return host.hashCode() + port.hashCode() + database.hashCode()
				+ schema.hashCode() + tableName.hashCode() + user.hashCode();
	}

}
