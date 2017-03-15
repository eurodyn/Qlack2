package com.eurodyn.qlack2.webdesktop.apps.appmanagement.api.request;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetGroupApplicationsRequest extends QSignedRequest {
	private String groupKeyname;

	public GetGroupApplicationsRequest(String groupKeyname) {
		this.groupKeyname = groupKeyname;
	}

	public String getGroupKeyname() {
		return groupKeyname;
	}

	public void setGroupKeyname(String groupKeyname) {
		this.groupKeyname = groupKeyname;
	}
}
