package com.eurodyn.qlack2.be.rules.web.dto.tree;

import java.util.ArrayList;
import java.util.List;

public class CompositeNode extends TreeNode {

	public static final String TYPE_WORKING_SETS = "WorkingSets";
	public static final String TYPE_RULES = "Rules";
	public static final String TYPE_DATA_MODELS = "DataModels";
	public static final String TYPE_LIBRARIES = "Libraries";
	public static final String TYPE_CATEGORIES = "Categories";

	public static final String NAME_WORKING_SETS = "Working sets";
	public static final String NAME_RULES = "Rules";
	public static final String NAME_DATA_MODELS = "Data models";
	public static final String NAME_LIBRARIES = "Libraries";
	public static final String NAME_CATEGORIES = "Categories";

	private List<TreeNode> items;

	public CompositeNode(String type, String name) {
		super(type, name);
		this.items = new ArrayList<>();
	}

	// --

	public boolean isComposite() {
		return true;
	}

	public void add(TreeNode child) {
		items.add(child);
	}

	public void remove(TreeNode child) {
		items.remove(child);
	}

	public TreeNode get(int index) {
		return items.get(index);
	}

	public List<TreeNode> getItems() {
		return items;
	}

}
