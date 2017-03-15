package com.eurodyn.qlack2.be.workflow.api.request.workflow;

import java.util.ArrayList;
import java.util.List;

import com.eurodyn.qlack2.be.workflow.api.request.version.UpdateWorkflowVersionRequest;
import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateWorkflowRequest extends QSignedRequest {

	private String id;

	private String name;

	private String description;

	private boolean active;

	private List<String> categoryIds = new ArrayList<>();
	private UpdateWorkflowVersionRequest versionRequest = new UpdateWorkflowVersionRequest();

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<String> getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(List<String> categoryIds) {
		this.categoryIds = categoryIds;
	}
	
	public UpdateWorkflowVersionRequest getVersionRequest() {
		return versionRequest;
	}

	public void setVersionRequest(UpdateWorkflowVersionRequest versionRequest) {
		this.versionRequest = versionRequest;
	}

}
