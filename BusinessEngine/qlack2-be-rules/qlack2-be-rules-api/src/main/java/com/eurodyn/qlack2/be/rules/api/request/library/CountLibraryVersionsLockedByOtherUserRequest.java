package com.eurodyn.qlack2.be.rules.api.request.library;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class CountLibraryVersionsLockedByOtherUserRequest extends QSignedRequest {
	private String libraryId;

	public String getLibraryId() {
		return libraryId;
	}

	public void setLibraryId(String libraryId) {
		this.libraryId = libraryId;
	}

}
