package com.eurodyn.qlack2.be.rules.api.request.library.version;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetLibraryVersionIdByNameRequest extends QSignedRequest {
	private String libraryVersionName;

	private String libraryId;

	public String getLibraryVersionName() {
		return libraryVersionName;
	}

	public void setLibraryVersionName(String libraryVersionName) {
		this.libraryVersionName = libraryVersionName;
	}

	public String getLibraryId() {
		return libraryId;
	}

	public void setLibraryId(String libraryId) {
		this.libraryId = libraryId;
	}
}
