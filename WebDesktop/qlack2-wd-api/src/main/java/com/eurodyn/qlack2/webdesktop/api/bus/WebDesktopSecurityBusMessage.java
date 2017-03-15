package com.eurodyn.qlack2.webdesktop.api.bus;

import com.eurodyn.qlack2.webdesktop.api.util.Constants.SecurityEvent;

public class WebDesktopSecurityBusMessage {	
	private SecurityEvent event;
	private String subjectId;
	private String srcUserId;
	private String operationName;
	private String resourceObjectId;

	public SecurityEvent getEvent() {
		return event;
	}

	public void setEvent(SecurityEvent event) {
		this.event = event;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getSrcUserId() {
		return srcUserId;
	}

	public void setSrcUserId(String srcUserId) {
		this.srcUserId = srcUserId;
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
