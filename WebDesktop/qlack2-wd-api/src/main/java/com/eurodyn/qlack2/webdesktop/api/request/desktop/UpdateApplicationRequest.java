package com.eurodyn.qlack2.webdesktop.api.request.desktop;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;
import com.eurodyn.qlack2.webdesktop.api.dto.ApplicationInfo;


public class UpdateApplicationRequest extends QSignedRequest {
	private ApplicationInfo appInfo;

	public UpdateApplicationRequest(ApplicationInfo appInfo) {
		this.appInfo = appInfo;
	}

	public ApplicationInfo getAppInfo() {
		return appInfo;
	}

	public void setAppInfo(ApplicationInfo appInfo) {
		this.appInfo = appInfo;
	}
}
