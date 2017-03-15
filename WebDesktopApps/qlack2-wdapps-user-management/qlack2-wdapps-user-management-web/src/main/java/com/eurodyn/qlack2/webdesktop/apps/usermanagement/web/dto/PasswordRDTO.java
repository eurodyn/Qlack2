package com.eurodyn.qlack2.webdesktop.apps.usermanagement.web.dto;

import org.hibernate.validator.constraints.NotEmpty;

public class PasswordRDTO {
	@NotEmpty
	private String password;
	@NotEmpty
	private String verifyPassword;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getVerifyPassword() {
		return verifyPassword;
	}

	public void setVerifyPassword(String verifyPassword) {
		this.verifyPassword = verifyPassword;
	}
}
