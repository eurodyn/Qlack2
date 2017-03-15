package com.eurodyn.qlack2.be.forms.web.dto.tree;

public class LeafNode extends TreeNode {

	private final String type;

	public LeafNode(String id, String name, String type) {
		super(id, name);
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
