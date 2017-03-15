package com.eurodyn.qlack2.be.rules.api.request.library.version;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class CreateLibraryVersionRequest extends QSignedRequest {

	private String libraryId;

	private String name;
	private String description;
	private byte[] contentJar;

	public String getLibraryId() {
		return libraryId;
	}

	public void setLibraryId(String libraryId) {
		this.libraryId = libraryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public byte[] getContentJar() {
		return contentJar;
	}

	public void setContentJar(byte[] content) {
		this.contentJar = content;
	}

}
