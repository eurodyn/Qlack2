package com.eurodyn.qlack2.be.client.connectivity;

public class ClientCredentials {
	private String username;
	private String password;
	private String samlv2Token;
	
	@SuppressWarnings("unused")
	private ClientCredentials() {
	}
	
	public ClientCredentials(final String username, final String password) {
		this.username = username;
		this.password = password;
	}
	
	public ClientCredentials(final String samlv2Token) {
		this.samlv2Token = samlv2Token;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getSamlv2Token() {
		return samlv2Token;
	}
	
}
