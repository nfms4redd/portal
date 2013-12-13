package org.fao.unredd.dbconf;

import java.io.IOException;
import java.io.OutputStream;

import org.fao.unredd.layers.Output;

public class DBOutput implements Output {

	private String contentType;
	private byte[] content;

	public DBOutput(String contentType, byte[] content) {
		this.contentType = contentType;
		this.content = content;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public void writeOutput(OutputStream os) throws IOException {
		os.write(new String(content, "utf-8").getBytes());
	}

}
