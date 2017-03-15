package com.eurodyn.qlack2.be.rules.impl.dto;

public class AuditRuntimeBaseDTO {
	private String workingSetVersionId;
	private String knowledgeBaseId;

	// -- Accessors

	public String getWorkingSetVersionId() {
		return workingSetVersionId;
	}

	public void setWorkingSetVersionId(String workingSetVersionId) {
		this.workingSetVersionId = workingSetVersionId;
	}

	public String getKnowledgeBaseId() {
		return knowledgeBaseId;
	}

	public void setKnowledgeBaseId(String knowledgeBaseId) {
		this.knowledgeBaseId = knowledgeBaseId;
	}

}
