package com.eurodyn.qlack2.be.rules.api.request.rule.version;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class ImportRuleVersionRequest extends QSignedRequest {

	private String ruleId;
	private byte[] xml;

	// -- Accessors

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public byte[] getXml() {
		return xml;
	}

	public void setXml(byte[] xml) {
		this.xml = xml;
	}

}
