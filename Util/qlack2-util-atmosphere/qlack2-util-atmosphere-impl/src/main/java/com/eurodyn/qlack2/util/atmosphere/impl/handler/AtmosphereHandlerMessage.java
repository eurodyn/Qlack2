package com.eurodyn.qlack2.util.atmosphere.impl.handler;

class AtmosphereHandlerMessage {
	public enum MSG_TYPE {SUBSCRIBE, UNSUBSCRIBE};
	private MSG_TYPE messageType;
	private String topic;

	public AtmosphereHandlerMessage() {

	}

	public MSG_TYPE getMessageType() {
		return messageType;
	}
	public void setMessageType(MSG_TYPE messageType) {
		this.messageType = messageType;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
}
