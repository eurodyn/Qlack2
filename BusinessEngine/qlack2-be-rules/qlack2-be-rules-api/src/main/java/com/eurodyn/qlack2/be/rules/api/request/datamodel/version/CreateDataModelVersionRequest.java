package com.eurodyn.qlack2.be.rules.api.request.datamodel.version;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class CreateDataModelVersionRequest extends QSignedRequest {

	private String modelId;

	private String name;
	private String description;
	private String basedOnId;

	// -- Accessors

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBasedOnId() {
		return basedOnId;
	}

	public void setBasedOnId(String basedOnId) {
		this.basedOnId = basedOnId;
	}

}
