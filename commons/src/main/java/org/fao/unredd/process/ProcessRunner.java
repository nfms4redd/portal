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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

/**
 * Thread that runs an external process redirecting the streams.
 */
public class ProcessRunner {

	private String[] cmd;
	private OutputStream out;
	private InputStream in;

	public ProcessRunner(String... command) throws ProcessExecutionException {
		this(new ByteArrayInputStream(new byte[0]),
				new ByteArrayOutputStream(), command);
	}

	public ProcessRunner(InputStream stdInput, OutputStream stdOutput,
			String... command) {
		this.cmd = command;
		this.out = stdOutput;
		this.in = stdInput;
	}

	public void run() throws ProcessExecutionException {
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		try {
			Process process = Runtime.getRuntime().exec(cmd);

			StreamPipe input = new StreamPipe(process.getInputStream(), out);
			StreamPipe error = new StreamPipe(process.getErrorStream(), err);
			StreamPipe output = new StreamPipe(in, process.getOutputStream());

			output.start();
			input.start();
			error.start();
			int returnCode = process.waitFor();
			input.join();
			error.join();
			output.join();

			if (returnCode != 0) {
				throw new ProcessReturnCodeException();
			}

			output.throwExceptionIfAny();
			input.throwExceptionIfAny();
			error.throwExceptionIfAny();
			byte[] errorBytes = err.toByteArray();
			if (errorBytes.length > 0) {
				throw getExecutionException(cmd, errorBytes);
			}
		} catch (ProcessReturnCodeException e) {
			throw getExecutionException(cmd, err.toByteArray());
		} catch (IOException e) {
			throw new ProcessExecutionException(cmd, e);
		} catch (InterruptedException e) {
			throw new ProcessExecutionException(cmd, e);
		}
	}

	private static ProcessExecutionException getExecutionException(
			String[] command, byte[] errorBytes) {
		String errorMessage = new String(errorBytes);
		return new ProcessExecutionException(command, errorMessage);
	}

	/**
	 * Thread that pipes two streams.
	 */
	private class StreamPipe extends Thread {
		private InputStream input;
		private OutputStream output;
		private IOException exception;

		/**
		 * Creates a new thread.
		 * 
		 * @param input
		 *            The source stream.
		 * @param output
		 *            The destination stream.
		 */
		public StreamPipe(InputStream input, OutputStream output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public void run() {
			try {
				IOUtils.copy(input, output);
			} catch (IOException e) {
				exception = e;
			} finally {
				try {
					output.close();
				} catch (IOException e) {
				}
			}
		}

		public void throwExceptionIfAny() throws IOException {
			if (exception != null) {
				throw exception;
			}
		}
	}
}
