package com.eurodyn.qlack2.be.workflow.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class XMLWorkflowVersionDTO {

	private String name;
	private String description;
	private String content;
	private String processId;
	private XMLConditionsDTO conditions;

	public XMLWorkflowVersionDTO() {
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
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}
	
	public XMLConditionsDTO getConditions() {
        return this.conditions;
	}
	
	public void setConditions(XMLConditionsDTO conditions) {
		this.conditions = conditions;
	}
}
