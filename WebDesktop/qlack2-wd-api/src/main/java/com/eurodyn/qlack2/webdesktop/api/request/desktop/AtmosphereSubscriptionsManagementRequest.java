package com.eurodyn.qlack2.webdesktop.api.request.desktop;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class AtmosphereSubscriptionsManagementRequest extends QSignedRequest {
	public static enum TYPE {
		subscribe, unsubscribe
	}
	private TYPE requestType;
	private String topic;

	public AtmosphereSubscriptionsManagementRequest() {

	}

	public TYPE getRequestType() {
		return requestType;
	}
	public void setRequestType(TYPE requestType) {
		this.requestType = requestType;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}


}
