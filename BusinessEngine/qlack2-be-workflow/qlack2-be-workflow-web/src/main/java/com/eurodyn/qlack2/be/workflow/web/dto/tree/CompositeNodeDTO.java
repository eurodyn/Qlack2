package com.eurodyn.qlack2.be.workflow.web.dto.tree;

import java.util.ArrayList;
import java.util.List;

public class CompositeNodeDTO extends TreeNodeDTO {

	public static final String WORKFLOWS = "Workflows";
	public static final String CATEGORIES = "Categories";
	private final String type;

	private List<TreeNodeDTO> nodes;

	public CompositeNodeDTO(String name) {
		this(null, name, null);
	}

	public CompositeNodeDTO(String id, String name, String type) {
		super(id, name);
		this.type = type;
		this.nodes = new ArrayList<>();
	}

	public void add(TreeNodeDTO child) {
		nodes.add(child);
	}

	public void remove(TreeNodeDTO child) {
		nodes.remove(child);
	}

	public TreeNodeDTO get(int index) {
		return nodes.get(index);
	}

	public CompositeNodeDTO getComposite(int index) {
		TreeNodeDTO node = get(index);
		if (node instanceof CompositeNodeDTO) {
			return (CompositeNodeDTO) node;
		}
		else {
			throw new IllegalArgumentException("The requested node is not composite");
		}
	}

	public List<TreeNodeDTO> getNodes() {
		return nodes;
	}

	public String getType() {
		return type;
	}

}
