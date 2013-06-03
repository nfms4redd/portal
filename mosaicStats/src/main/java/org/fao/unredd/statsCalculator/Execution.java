package org.fao.unredd.statsCalculator;

import java.io.File;

public class Execution {
	private File areaRaster;
	private File forestMask;
	private File zones;
	private String id;

	public Execution(File areaRaster, File forestMask, File zones, String id) {
		super();
		this.areaRaster = areaRaster;
		this.forestMask = forestMask;
		this.zones = zones;
		this.id = id;
	}

	public File getAreaRaster() {
		return areaRaster;
	}

	public File getForestMask() {
		return forestMask;
	}

	public String getId() {
		return id;
	}

	public File getZones() {
		return zones;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Execution) {
			Execution that = (Execution) obj;
			return that.areaRaster.equals(this.areaRaster)
					&& that.forestMask.equals(this.forestMask)
					&& that.zones.equals(this.zones) && that.id.equals(this.id);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return areaRaster.hashCode() + forestMask.hashCode() + zones.hashCode()
				+ id.hashCode();
	}
}