package com.eurodyn.qlack2.be.rules.web.dto.tree;

public class LeafNode extends TreeNode {

	public static final String TYPE_WORKING_SET = "WorkingSet";
	public static final String TYPE_RULE = "Rule";
	public static final String TYPE_DATA_MODEL = "DataModel";
	public static final String TYPE_LIBRARY = "Library";
	public static final String TYPE_CATEGORY = "Category";

	private final String id;

	public LeafNode(String type, String name, String id) {
		super(type, name);
		this.id = id;
	}

	public String getId() {
		return id;
	}

}
