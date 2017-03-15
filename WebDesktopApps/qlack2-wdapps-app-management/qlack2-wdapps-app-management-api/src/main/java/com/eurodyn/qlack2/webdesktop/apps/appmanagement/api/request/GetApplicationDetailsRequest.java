package com.eurodyn.qlack2.webdesktop.apps.appmanagement.api.request;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;


public class GetApplicationDetailsRequest extends QSignedRequest {
	private String appUUID;

	public GetApplicationDetailsRequest(String appUUID) {
		this.appUUID = appUUID;
	}

	public String getAppUUID() {
		return appUUID;
	}

	public void setAppUUID(String appUUID) {
		this.appUUID = appUUID;
	}
}
