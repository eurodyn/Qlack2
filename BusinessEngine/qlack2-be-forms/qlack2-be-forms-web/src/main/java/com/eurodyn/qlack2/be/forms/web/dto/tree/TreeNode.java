package com.eurodyn.qlack2.be.forms.web.dto.tree;

public abstract class TreeNode {
	protected final String id;
	protected final String name;

	public TreeNode(String id, String name) {
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
