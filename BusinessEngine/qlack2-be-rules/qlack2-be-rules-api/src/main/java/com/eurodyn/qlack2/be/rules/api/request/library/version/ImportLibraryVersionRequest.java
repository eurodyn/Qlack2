package com.eurodyn.qlack2.be.rules.api.request.library.version;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class ImportLibraryVersionRequest extends QSignedRequest {

	private String libraryId;
	private byte[] xml;

	// -- Accessors

	public String getLibraryId() {
		return libraryId;
	}

	public void setLibraryId(String libraryId) {
		this.libraryId = libraryId;
	}

	public byte[] getXml() {
		return xml;
	}

	public void setXml(byte[] xml) {
		this.xml = xml;
	}

}
