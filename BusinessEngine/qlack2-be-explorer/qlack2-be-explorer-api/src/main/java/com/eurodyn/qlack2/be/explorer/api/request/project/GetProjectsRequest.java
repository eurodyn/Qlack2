package com.eurodyn.qlack2.be.explorer.api.request.project;

import com.eurodyn.qlack2.be.explorer.api.criteria.ProjectListCriteria;
import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetProjectsRequest extends QSignedRequest {
	private ProjectListCriteria criteria;

	public GetProjectsRequest(ProjectListCriteria criteria) {
		this.criteria = criteria;
	}

	public ProjectListCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ProjectListCriteria criteria) {
		this.criteria = criteria;
	}
}
