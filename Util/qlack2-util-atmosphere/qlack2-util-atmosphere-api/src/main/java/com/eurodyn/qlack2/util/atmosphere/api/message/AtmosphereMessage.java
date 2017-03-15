package com.eurodyn.qlack2.util.atmosphere.api.message;

import java.io.Serializable;

public class AtmosphereMessage implements Serializable {
	private static final long serialVersionUID = -6503350713619858016L;
	private Object message;
	private String fromUserID;
	private String toUserID;
	private String topic;

	public AtmosphereMessage() {
	}

	public AtmosphereMessage(String topic, Object message) {
		super();
		this.topic = topic;
		this.setMessage(message);
	}

	public String getFromUserID() {
		return fromUserID;
	}

	public void setFromUserID(String fromUserID) {
		this.fromUserID = fromUserID;
	}

	public String getToUserID() {
		return toUserID;
	}

	public void setToUserID(String toUserID) {
		this.toUserID = toUserID;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

}
