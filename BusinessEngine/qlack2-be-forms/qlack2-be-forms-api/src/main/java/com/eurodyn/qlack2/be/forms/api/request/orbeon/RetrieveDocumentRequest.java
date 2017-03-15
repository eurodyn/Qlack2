package com.eurodyn.qlack2.be.forms.api.request.orbeon;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class RetrieveDocumentRequest extends QSignedRequest {
	private String documentId;

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

}
