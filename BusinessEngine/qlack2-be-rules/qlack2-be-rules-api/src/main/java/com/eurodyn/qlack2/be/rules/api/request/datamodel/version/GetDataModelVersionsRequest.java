package com.eurodyn.qlack2.be.rules.api.request.datamodel.version;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetDataModelVersionsRequest extends QSignedRequest {

	private String id;
	private String filterCycles;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFilterCycles() {
		return filterCycles;
	}

	public void setFilterCycles(String filterCycles) {
		this.filterCycles = filterCycles;
	}

}
