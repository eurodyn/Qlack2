package com.eurodyn.qlack2.webdesktop.apps.appmanagement.api.request;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;
import com.eurodyn.qlack2.webdesktop.api.dto.ApplicationInfo;


public class UpdateApplicationRequest extends QSignedRequest {
	private ApplicationInfo applicationInfo;

	public UpdateApplicationRequest(ApplicationInfo applicationInfo) {
		this.applicationInfo = applicationInfo;
	}

	public ApplicationInfo getApplicationInfo() {
		return applicationInfo;
	}

	public void setApplicationInfo(ApplicationInfo applicationInfo) {
		this.applicationInfo = applicationInfo;
	}
}
