package com.eurodyn.qlack2.be.rules.web.dto.tree;

public abstract class TreeNode {

	protected final String type;
	protected final String name;

	public TreeNode(String type, String name) {
		this.type = type;
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

}
