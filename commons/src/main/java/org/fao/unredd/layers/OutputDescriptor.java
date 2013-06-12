package org.fao.unredd.layers;

/**
 * Describes one of the outputs a layer can have.
 * 
 * @author fergonco
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

	public OutputDescriptor(String id, String name, String fieldId) {
		super();
		this.id = id;
		this.name = name;
		this.fieldId = fieldId;
	}

	public String toJSON() {
		return "{" + "\"id\":\"" + id + "\"," + //
				"\"name\":\"" + name + "\"," + //
				"\"fieldId\":\"" + fieldId + "\"" + "}";
	}

	public String getId() {
		return id;
	}
}
