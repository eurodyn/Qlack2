package com.eurodyn.qlack2.be.rules.web.dto.tree;

public class ProjectCompositeNode extends CompositeNode {

	private final String projectId;

	public ProjectCompositeNode(String projectId, String name) {
		super("Project", name);
		this.projectId = projectId;
	}

	public String getProjectId() {
		return projectId;
	}

}
