package com.eurodyn.qlack2.be.rules.impl.util;

public class JarDataModelVersion {

	private String className;

	private byte[] bytes;

	private long lastModified;

	// -- Accessors

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
}
