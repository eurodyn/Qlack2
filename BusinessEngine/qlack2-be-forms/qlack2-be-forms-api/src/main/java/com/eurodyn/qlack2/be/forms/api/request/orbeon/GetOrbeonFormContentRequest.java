package com.eurodyn.qlack2.be.forms.api.request.orbeon;

import java.util.List;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetOrbeonFormContentRequest extends QSignedRequest {
	private String application;

	private String form;

	private String locale;

	private String data;

	private List<byte[]> facts;

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public List<byte[]> getFacts() {
		return facts;
	}

	public void setFacts(List<byte[]> facts) {
		this.facts = facts;
	}

}
