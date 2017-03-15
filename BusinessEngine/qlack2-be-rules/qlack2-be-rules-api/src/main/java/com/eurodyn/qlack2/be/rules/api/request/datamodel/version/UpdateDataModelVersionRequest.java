package com.eurodyn.qlack2.be.rules.api.request.datamodel.version;

import java.util.List;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class UpdateDataModelVersionRequest extends QSignedRequest {

	private String id;

	private String description;

	private String modelPackage;

	private String parentModelVersionId;

	private List<UpdateDataModelFieldRequest> fieldRequests;

	// -- Accessors

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getModelPackage() {
		return modelPackage;
	}

	public void setModelPackage(String modelPackage) {
		this.modelPackage = modelPackage;
	}

	public String getParentModelVersionId() {
		return parentModelVersionId;
	}

	public void setParentModelVersionId(String parentModelVersionId) {
		this.parentModelVersionId = parentModelVersionId;
	}

	public List<UpdateDataModelFieldRequest> getFieldRequests() {
		return fieldRequests;
	}

	public void setFieldRequests(List<UpdateDataModelFieldRequest> fieldRequests) {
		this.fieldRequests = fieldRequests;
	}

}
