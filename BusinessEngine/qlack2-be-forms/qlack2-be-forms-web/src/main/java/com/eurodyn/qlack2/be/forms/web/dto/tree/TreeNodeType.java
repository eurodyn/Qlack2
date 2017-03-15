package com.eurodyn.qlack2.be.forms.web.dto.tree;

public enum TreeNodeType {
	FORM ("Forms", "form"),
	CATEGORY ("Categories", "category");

	private String title;

	private String type;

	private TreeNodeType(String title, String type) {
		this.title = title;
		this.type = type;
	}

	public String title() {
		return title;
	}

	public String type() {
		return type;
	}

}
