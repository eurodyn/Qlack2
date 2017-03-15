package com.eurodyn.qlack2.be.forms.api.request.orbeon;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class StoreAttachmentRequest extends QSignedRequest {

	private String documentId;

	private String attachmentName;

	private byte[] content;

	private String contentType;

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

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
