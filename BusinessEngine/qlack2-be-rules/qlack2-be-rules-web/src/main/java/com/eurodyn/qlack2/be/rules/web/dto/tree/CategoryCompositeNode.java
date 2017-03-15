package com.eurodyn.qlack2.be.rules.web.dto.tree;

public class CategoryCompositeNode extends CompositeNode {

	private final String categoryId;

	public CategoryCompositeNode(String categoryId, String name) {
		super("CompositeCategory", name);
		this.categoryId = categoryId;
	}

	public String getCategoryId() {
		return categoryId;
	}

}
