package com.eurodyn.qlack2.be.rules.api.request.workingset.version;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class CreateWorkingSetVersionRequest extends QSignedRequest {

	private String workingSetId;

	private String name;
	private String description;
	private String basedOnId;

	// -- Accessors

	public String getWorkingSetId() {
		return workingSetId;
	}

	public void setWorkingSetId(String workingSetId) {
		this.workingSetId = workingSetId;
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
