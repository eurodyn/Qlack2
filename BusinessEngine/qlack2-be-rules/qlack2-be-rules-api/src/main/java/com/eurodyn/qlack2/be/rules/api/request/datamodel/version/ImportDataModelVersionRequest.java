package com.eurodyn.qlack2.be.rules.api.request.datamodel.version;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class ImportDataModelVersionRequest extends QSignedRequest {

	private String dataModelId;
	private byte[] xml;

	// -- Accessors

	public String getDataModelId() {
		return dataModelId;
	}

	public void setDataModelId(String dataModelId) {
		this.dataModelId = dataModelId;
	}

	public byte[] getXml() {
		return xml;
	}

	public void setXml(byte[] xml) {
		this.xml = xml;
	}

}
