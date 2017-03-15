package com.eurodyn.qlack2.be.client.connectivity;


public class ServerAddress {
	private final String host;
	private final int port;
	private final String protocol;
	
	public ServerAddress(final String protocol, final String host, int port) {
		this.host = host;
		this.port = port;
		this.protocol = protocol;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
	
	public String getProtocol() {
		return protocol;
	}
	
	public String getAddress() {
		return protocol + "://" + host + ":" + port;
	}
	
}
