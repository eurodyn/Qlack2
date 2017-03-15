package com.eurodyn.qlack2.be.rules.api.request.datamodel.version;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class UpdateDataModelFieldRequest extends QSignedRequest {

	private String id;
	private String name;
	private String fieldTypeId;
	private String fieldTypeVersionId;

	// -- Accessors

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFieldTypeId() {
		return fieldTypeId;
	}

	public void setFieldTypeId(String fieldTypeId) {
		this.fieldTypeId = fieldTypeId;
	}

	public String getFieldTypeVersionId() {
		return fieldTypeVersionId;
	}

	public void setFieldTypeVersionId(String fieldTypeVersionId) {
		this.fieldTypeVersionId = fieldTypeVersionId;
	}

}
