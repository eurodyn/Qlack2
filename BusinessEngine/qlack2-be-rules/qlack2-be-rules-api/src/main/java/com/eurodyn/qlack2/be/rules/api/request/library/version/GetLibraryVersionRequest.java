package com.eurodyn.qlack2.be.rules.api.request.library.version;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetLibraryVersionRequest extends QSignedRequest {

	private String versionId;

	public GetLibraryVersionRequest() {
		super();
	}

	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

}
