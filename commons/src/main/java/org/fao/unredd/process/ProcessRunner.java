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
	private OutputStream err;
	private InputStream in;

	/**
	 * Creates a new thread for the external process.
	 * 
	 * @param cmd
	 *            An array containing the command to call and its arguments.
	 * @param in
	 *            The stream where the process input must be taken.
	 * @param out
	 *            The stream where the process output must be redirected.
	 * @param err
	 *            The stream where the process error must be redirected.
	 */
	public ProcessRunner(String[] cmd, InputStream in, OutputStream out,
			OutputStream err) {
		this.cmd = cmd;
		this.out = out;
		this.err = err;
		this.in = in;
	}

	public void run() throws IOException, InterruptedException,
			ProcessExecutionException {
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
			throw new ProcessExecutionException(cmd);
		}

		output.throwExceptionIfAny();
		input.throwExceptionIfAny();
		error.throwExceptionIfAny();
	}

	public static void execute(File input, File output, String... command)
			throws IOException, InterruptedException, ProcessExecutionException {
		FileInputStream stdInput = new FileInputStream(input);
		FileOutputStream stdOutput = new FileOutputStream(output);
		execute(stdInput, stdOutput, command);
		stdOutput.close();
		stdInput.close();
	}

	public static void execute(String... command) throws IOException,
			InterruptedException, ProcessExecutionException {
		ByteArrayInputStream stdInput = new ByteArrayInputStream(new byte[0]);
		ByteArrayOutputStream stdOutput = new ByteArrayOutputStream();
		execute(stdInput, stdOutput, command);
	}

	public static void execute(InputStream stdInput, OutputStream stdOutput,
			String... command) throws IOException, InterruptedException,
			ProcessExecutionException {
		ByteArrayOutputStream errorOutputStream = new ByteArrayOutputStream();
		ProcessRunner process = new ProcessRunner(command, stdInput, stdOutput,
				errorOutputStream);
		process.run();
		byte[] errorBytes = errorOutputStream.toByteArray();
		if (errorBytes.length > 0) {
			String errorMessage = new String(errorBytes);
			throw new ProcessExecutionException(errorMessage);
		}
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
				output.close();
			} catch (IOException e) {
				exception = e;
			}
		}

		public void throwExceptionIfAny() throws IOException {
			if (exception != null) {
				throw exception;
			}
		}
	}
}
