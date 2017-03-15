package com.eurodyn.qlack2.be.rules.api.request.config;

import java.util.Map;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class SaveConfigParamsRequest extends QSignedRequest {
	private Map<String, String> params;

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}
}
