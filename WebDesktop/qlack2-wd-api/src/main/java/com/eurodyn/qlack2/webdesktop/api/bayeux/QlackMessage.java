package com.eurodyn.qlack2.webdesktop.api.bayeux;


public class QlackMessage {
	public static final String CHANNEL_PRIVATE = "/service/private";
	public static final String CHANNEL_PUBLIC = "/public";
	private String channel;
	private String session;
	private String service;
	private String handler;
	private boolean lazy;
	private Object payload;
	
	private QlackMessage() {
	}
	
	private QlackMessage(String service, String handler, String channel, 
			String session, boolean lazy, Object payload) {
		this.service = service;
		this.handler = handler;
		this.channel = channel;
		this.session = session;
		this.lazy = lazy;
		this.payload = payload;
	}

	public String getChannel() {
		return channel;
	}

	public String getSession() {
		return session;
	}

	public String getService() {
		return service;
	}

	public String getHandler() {
		return handler;
	}

	public boolean isLazy() {
		return lazy;
	}

	public Object getPayload() {
		return payload;
	}	

//	public void publish() throws JsonGenerationException, JsonMappingException, IOException {		
//		Mutable msg = server.newMessage();
//		msg.setChannel(getChannel());
//		msg.setLazy(isLazy());
//		ObjectMapper mapper = new ObjectMapper();
//		msg.setData(mapper.writeValueAsString(this));
//		
//		LocalSession localSession = server.newLocalSession(session);
//		localSession.handshake();
//		server.getChannel(channel).publish(localSession, msg);
//		localSession.disconnect();
//	}
	
	public static class Builder {		
		private String service;
		private String handler;
		private String channel;
		private String session = "server";
		private boolean lazy = false;
		private Object data;
		
		private Builder() {
		}
		
		public Builder handler(String handler) {
			this.handler = handler;
			return this;
		}
		
		public Builder isLazy() {
			lazy = true;
			return this;
		}
		
		public Builder session(String session) {
			this.session = session;
			return this;
		}
		
		public Builder service(String service) {
			this.service = service;
			return this;
		}

		public Builder channel(String channel) {
			this.channel = channel;
			return this;
		}
		
		public Builder data(Object data) {
			this.data = data;
			return this;
		}

		public QlackMessage build() {
			return new QlackMessage(service, handler, channel, session, lazy,
					data);
		}
	}	
	
}
