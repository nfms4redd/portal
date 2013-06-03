package org.fao.unredd.layers;

public class Output {

	private String id;
	private String name;
	private String contentType;
	private String content;

	public Output(String id, String name, String contentType, String content) {
		super();
		this.id = id;
		this.name = name;
		this.contentType = contentType;
		this.content = content;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String toJSON() {
		return "{" + "\"id\":\"" + id + "\"," + //
				"\"name\":\"" + name + "\"" + "}";
	}

	public String getContentType() {
		return contentType;
	}

	public String getContents() {
		return content;
	}
}
