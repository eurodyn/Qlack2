package com.eurodyn.qlack2.be.rules.api.request.workingset.version;

import java.util.ArrayList;
import java.util.List;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class UpdateWorkingSetVersionRequest extends QSignedRequest {

	private String id;
	private String description;

	private List<String> ruleVersionIds = new ArrayList<>();
	private List<String> dataModelVersionIds = new ArrayList<>();
	private List<String> libraryVersionIds = new ArrayList<>();

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

	public List<String> getRuleVersionIds() {
		return ruleVersionIds;
	}

	public void setRuleVersionIds(List<String> ruleVersionIds) {
		this.ruleVersionIds = ruleVersionIds;
	}

	public List<String> getDataModelVersionIds() {
		return dataModelVersionIds;
	}

	public void setDataModelVersionIds(List<String> dataModelVersionIds) {
		this.dataModelVersionIds = dataModelVersionIds;
	}

	public List<String> getLibraryVersionIds() {
		return libraryVersionIds;
	}

	public void setLibraryVersionIds(List<String> libraryVersionIds) {
		this.libraryVersionIds = libraryVersionIds;
	}

}
