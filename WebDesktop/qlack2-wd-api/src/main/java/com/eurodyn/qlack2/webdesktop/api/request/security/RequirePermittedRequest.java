package com.eurodyn.qlack2.webdesktop.api.request.security;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;

public class RequirePermittedRequest extends QSignedRequest {
	private String operationName;
	private String resourceObjectId;

	public RequirePermittedRequest(SignedTicket signedTicket, String operationName) {
		this.setSignedTicket(signedTicket);
		this.operationName = operationName;
	}

	public RequirePermittedRequest(SignedTicket signedTicket, String operationName, String resourceObjectId) {
		this.setSignedTicket(signedTicket);
		this.operationName = operationName;
		this.resourceObjectId = resourceObjectId;
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
