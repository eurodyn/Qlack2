package com.eurodyn.qlack2.be.workflow.web.dto.tree;


public class LeafNodeDTO extends TreeNodeDTO {

	public static final String WORKFLOW = "workflow";
	public static final String CATEGORY = "category";

	private final String type;

	public LeafNodeDTO(String id, String name, String type) {
		super(id, name);
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
