package com.eurodyn.qlack2.be.rules.api.request.workingset.version;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class ImportWorkingSetVersionRequest extends QSignedRequest {

	private String workingSetId;
	private byte[] xml;

	// -- Accessors

	public String getWorkingSetId() {
		return workingSetId;
	}

	public void setWorkingSetId(String workingSetId) {
		this.workingSetId = workingSetId;
	}

	public byte[] getXml() {
		return xml;
	}

	public void setXml(byte[] xml) {
		this.xml = xml;
	}

}
