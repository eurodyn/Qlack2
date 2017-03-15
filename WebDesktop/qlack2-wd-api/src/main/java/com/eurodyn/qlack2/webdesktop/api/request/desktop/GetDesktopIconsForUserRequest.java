package com.eurodyn.qlack2.webdesktop.api.request.desktop;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;


public class GetDesktopIconsForUserRequest extends QSignedRequest {
	private String desktopOwnerID;

	public GetDesktopIconsForUserRequest(String desktopOwnerID) {
		this.desktopOwnerID = desktopOwnerID;
	}

	public String getDesktopOwnerID() {
		return desktopOwnerID;
	}

	public void setDesktopOwnerID(String desktopOwnerID) {
		this.desktopOwnerID = desktopOwnerID;
	}
}
