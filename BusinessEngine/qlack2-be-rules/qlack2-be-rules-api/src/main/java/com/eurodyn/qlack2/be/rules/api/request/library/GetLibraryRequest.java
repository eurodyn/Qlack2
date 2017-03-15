package com.eurodyn.qlack2.be.rules.api.request.library;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetLibraryRequest extends QSignedRequest {

	private String projectId;

	private String libraryId;

	public GetLibraryRequest() {
		super();
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getLibraryId() {
		return libraryId;
	}

	public void setLibraryId(String libraryId) {
		this.libraryId = libraryId;
	}

}
