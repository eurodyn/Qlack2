package com.eurodyn.qlack2.be.workflow.api.request.version;
import java.util.List;

import com.eurodyn.qlack2.be.workflow.api.dto.ConditionDTO;
import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateWorkflowVersionRequest extends QSignedRequest {

	private String id;

	private String name;

	private String description;

	//private String basedOn;
	
	private String content;
	
	private List<ConditionDTO> versionConditions;

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

	/*public String getBasedOn() {
		return basedOn;
	}

	public void setBasedOn(String basedOn) {
		this.basedOn = basedOn;
	} */
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public List<ConditionDTO> getVersionConditions() {
		return versionConditions;
	}

	public void setVersionConditions(List<ConditionDTO> versionConditions) {
		this.versionConditions = versionConditions;
	}

}
