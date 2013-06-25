package org.fao.unredd.layers;

import java.io.File;

public class FileLocation implements Location {
	private File file;

	public FileLocation(File file) {
		this.file = file;
	}

	@Override
	public String getGDALString() {
		return file.getAbsolutePath();
	}

	@Override
	public String getGDALFeatureName() {
		String name = file.getName();
		return name.substring(0, name.lastIndexOf('.'));
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public String toString() {
		return "File: " + file.getAbsolutePath();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FileLocation) {
			FileLocation that = (FileLocation) obj;
			return that.file.equals(this.file);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return file.hashCode();
	}
}
