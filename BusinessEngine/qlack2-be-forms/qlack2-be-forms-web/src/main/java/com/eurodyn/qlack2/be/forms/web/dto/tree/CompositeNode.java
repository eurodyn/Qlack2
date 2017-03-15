package com.eurodyn.qlack2.be.forms.web.dto.tree;

import java.util.ArrayList;
import java.util.List;


public class CompositeNode extends TreeNode {

	private final String type;
	private List<TreeNode> items;

	public CompositeNode(String name) {
		this(null, name, null);
	}

	public CompositeNode(String name, String type) {
		this(null, name, type);
	}

	public CompositeNode(String id, String name, String type) {
		super(id, name);
		this.items = new ArrayList<>();
		this.type = type;
	}

	public CompositeNode(TreeNodeType treeNodeType) {
		this(null, treeNodeType.title(), treeNodeType.type());
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

	public String getType() {
		return type;
	}

	public List<TreeNode> getItems() {
		return items;
	}

	public void setItems(List<TreeNode> items) {
		this.items = items;
	}
}
