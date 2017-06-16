package com.eurodyn.qlack2.fuse.search.impl.mappers.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonRawValue;

public class InternalUpdateMappingRequest {

	@JsonInclude(Include.NON_NULL)
	@JsonRawValue
	private String properties;

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}
}
