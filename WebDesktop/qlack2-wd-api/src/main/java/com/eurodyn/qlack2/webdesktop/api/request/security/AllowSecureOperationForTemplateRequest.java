package com.eurodyn.qlack2.webdesktop.api.request.security;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class AllowSecureOperationForTemplateRequest extends QSignedRequest {
	private String templateName;
	private String operationName;
	private String resourceObjectId;

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getResourceObjectId() {
		return resourceObjectId;
	}

	public void setResourceObjectId(String resourceObjectId) {
		this.resourceObjectId = resourceObjectId;
	}
}
