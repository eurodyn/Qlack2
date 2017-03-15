package com.eurodyn.qlack2.be.forms.api.request.orbeon;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class RetrieveAttachmentRequest extends QSignedRequest {
	private String documentId;

	private String attachmentName;

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getAttachmentName() {
		return attachmentName;
	}

	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}
}
