package com.eurodyn.qlack2.webdesktop.api.request.desktop;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetAppsForGroupRequest extends QSignedRequest {
	private String groupKeyname;
	private Boolean active;

	public GetAppsForGroupRequest(String groupKeyname, Boolean active) {
		this.groupKeyname = groupKeyname;
		this.active = active;
	}

	public String getGroupKeyname() {
		return groupKeyname;
	}

	public void setGroupKeyname(String groupKeyname) {
		this.groupKeyname = groupKeyname;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

}
