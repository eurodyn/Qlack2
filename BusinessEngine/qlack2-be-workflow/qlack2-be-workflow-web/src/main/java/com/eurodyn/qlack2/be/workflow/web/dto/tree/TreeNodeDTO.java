package com.eurodyn.qlack2.be.workflow.web.dto.tree;

public abstract class TreeNodeDTO {

	protected final String id;
	protected final String name;

	public TreeNodeDTO(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
