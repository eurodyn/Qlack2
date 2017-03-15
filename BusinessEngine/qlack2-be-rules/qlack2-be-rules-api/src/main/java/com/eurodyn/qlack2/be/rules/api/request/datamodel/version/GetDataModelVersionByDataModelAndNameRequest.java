package com.eurodyn.qlack2.be.rules.api.request.datamodel.version;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetDataModelVersionByDataModelAndNameRequest extends QSignedRequest {

	private String modelId;
	private String name;

	public String getDataModelId() {
		return modelId;
	}

	public void setDataModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
