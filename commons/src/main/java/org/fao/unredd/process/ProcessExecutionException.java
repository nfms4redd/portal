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
package org.fao.unredd.process;

import org.apache.commons.lang.StringUtils;

/**
 * The native process failed
 * 
 * @author fergonco
 */
public class ProcessExecutionException extends Exception {
	private static final long serialVersionUID = 1L;

	public ProcessExecutionException(String[] command) {
		super(StringUtils.join(command, " "));
	}

	public ProcessExecutionException(String[] command, String errorMessage) {
		super(StringUtils.join(command, " ") + "\n" + errorMessage);
	}

	public ProcessExecutionException(String[] command, Exception e) {
		super(StringUtils.join(command, " "), e);
	}

	public ProcessExecutionException(String message) {
		super(message);
	}
}
