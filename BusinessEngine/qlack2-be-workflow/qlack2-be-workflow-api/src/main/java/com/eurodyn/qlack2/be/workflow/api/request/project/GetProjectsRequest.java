package com.eurodyn.qlack2.be.workflow.api.request.project;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class GetProjectsRequest extends QSignedRequest {

	private String sort;
	private String order;
	private Integer start;
	private Integer size;

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

}
