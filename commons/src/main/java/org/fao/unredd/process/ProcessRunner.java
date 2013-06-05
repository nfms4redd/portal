package org.fao.unredd.process;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
	private boolean close;

	public ProcessRunner(File input, File output, String... command)
			throws ProcessExecutionException, IOException {
		FileInputStream stdInput = new FileInputStream(input);
		FileOutputStream stdOutput = new FileOutputStream(output);
		initialize(stdInput, stdOutput, command);
		close = true;
	}

	public ProcessRunner(String... command) throws ProcessExecutionException {
		ByteArrayInputStream stdInput = new ByteArrayInputStream(new byte[0]);
		ByteArrayOutputStream stdOutput = new ByteArrayOutputStream();
		initialize(stdInput, stdOutput, command);
	}

	private void initialize(InputStream stdInput, OutputStream stdOutput,
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

			/*
			 * Hack for unconventional oft-stat behavior. Remove once oft-stat
			 * is fixed.
			 */
			if (!cmd[0].equals("oft-stat")) {
				if (returnCode != 0) {
					throw new ProcessReturnCodeException();
				}
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
		} finally {
			if (close) {
				try {
					out.close();
				} catch (IOException e) {
				}
				try {
					in.close();
				} catch (IOException e) {
				}
			}
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
