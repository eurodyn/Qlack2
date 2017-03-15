package com.eurodyn.qlack2.be.rules.api.request.library.version;
import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class UpdateLibraryVersionRequest extends QSignedRequest {

	private String id;

	private String description;
	
	private byte[] contentJar;

	// -- Accessors

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
