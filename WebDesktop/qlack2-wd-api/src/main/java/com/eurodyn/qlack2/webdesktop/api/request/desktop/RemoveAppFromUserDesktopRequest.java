package com.eurodyn.qlack2.webdesktop.api.request.desktop;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class RemoveAppFromUserDesktopRequest extends QSignedRequest {
	private String desktopOwnerID;
	private String appID;

	public RemoveAppFromUserDesktopRequest(String desktopOwnerID, String appID) {
		this.desktopOwnerID = desktopOwnerID;
		this.appID = appID;
	}

	public String getDesktopOwnerID() {
		return desktopOwnerID;
	}

	public void setDesktopOwnerID(String desktopOwnerID) {
		this.desktopOwnerID = desktopOwnerID;
	}

	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}

}
