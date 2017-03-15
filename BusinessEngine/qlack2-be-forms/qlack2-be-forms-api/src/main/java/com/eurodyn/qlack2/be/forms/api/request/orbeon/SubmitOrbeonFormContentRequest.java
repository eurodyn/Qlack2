package com.eurodyn.qlack2.be.forms.api.request.orbeon;

import java.util.List;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class SubmitOrbeonFormContentRequest extends QSignedRequest {
	private String form;

	private List<String> validationConditions;

	private List<byte[]> facts;

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public List<String> getValidationConditions() {
		return validationConditions;
	}

	public void setValidationConditions(List<String> validationConditions) {
		this.validationConditions = validationConditions;
	}

	public List<byte[]> getFacts() {
		return facts;
	}

	public void setFacts(List<byte[]> facts) {
		this.facts = facts;
	}

}
