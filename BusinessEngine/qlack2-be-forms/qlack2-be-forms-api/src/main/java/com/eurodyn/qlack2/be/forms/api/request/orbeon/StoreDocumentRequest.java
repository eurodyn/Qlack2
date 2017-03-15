package com.eurodyn.qlack2.be.forms.api.request.orbeon;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class StoreDocumentRequest extends QSignedRequest {
	private String documentId;

	private String content;

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
