package com.eurodyn.qlack2.be.rules.web.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.eurodyn.qlack2.be.rules.api.dto.CategoryDTO;
import com.eurodyn.qlack2.be.rules.api.dto.DataModelDTO;
import com.eurodyn.qlack2.be.rules.api.dto.LibraryDTO;
import com.eurodyn.qlack2.be.rules.api.dto.ProjectDetailsDTO;
import com.eurodyn.qlack2.be.rules.api.dto.RuleDTO;
import com.eurodyn.qlack2.be.rules.api.dto.WorkingSetDTO;
import com.eurodyn.qlack2.be.rules.web.dto.tree.CategoryCompositeNode;
import com.eurodyn.qlack2.be.rules.web.dto.tree.CompositeNode;
import com.eurodyn.qlack2.be.rules.web.dto.tree.LeafNode;
import com.eurodyn.qlack2.be.rules.web.dto.tree.ProjectCompositeNode;
import com.eurodyn.qlack2.be.rules.web.dto.tree.TreeNode;

public class TreeConverter {

	public static final TreeConverter INSTANCE = new TreeConverter();

	private TreeConverter() {
	}

	public CompositeNode convert(ProjectDetailsDTO project) {
		// static project resource nodes
		CompositeNode root = new ProjectCompositeNode(project.getId(), project.getName());

		CompositeNode workingSetsNode = new CompositeNode(CompositeNode.TYPE_WORKING_SETS, CompositeNode.NAME_WORKING_SETS);
		CompositeNode rulesNode = new CompositeNode(CompositeNode.TYPE_RULES, CompositeNode.NAME_RULES);
		CompositeNode modelsNode = new CompositeNode(CompositeNode.TYPE_DATA_MODELS, CompositeNode.NAME_DATA_MODELS);
		CompositeNode librariesNode = new CompositeNode(CompositeNode.TYPE_LIBRARIES, CompositeNode.NAME_LIBRARIES);

		List<CompositeNode> resourceNodes = new ArrayList<>();
		resourceNodes.add(workingSetsNode);
		resourceNodes.add(rulesNode);
		resourceNodes.add(modelsNode);
		resourceNodes.add(librariesNode);

		// categories for project resources
		for (CompositeNode resourceNode : resourceNodes) {
			root.add(resourceNode);
			for (CategoryDTO category : project.getCategories()) {
				CompositeNode categoryNode = new CategoryCompositeNode(category.getId(), category.getName());
				resourceNode.add(categoryNode);
			}
		}

		// categories node
		CompositeNode categoriesNode = new CompositeNode(CompositeNode.TYPE_CATEGORIES, CompositeNode.NAME_CATEGORIES);
		root.add(categoriesNode);

		for (CategoryDTO category : project.getCategories()) {
			LeafNode categoryNode = new LeafNode(LeafNode.TYPE_CATEGORY, category.getName(), category.getId());
			categoriesNode.add(categoryNode);
		}

		// add project resources to categories
		for (WorkingSetDTO set : project.getWorkingSets()) {
			LeafNode setNode = new LeafNode(LeafNode.TYPE_WORKING_SET, set.getName(), set.getId());
			if (set.getCategoryIds().isEmpty()) {
				workingSetsNode.add(setNode);
			}
			for (String categoryId : set.getCategoryIds()) {
				CompositeNode categoryNode = findCategoryNode(workingSetsNode, categoryId);
				categoryNode.add(setNode);
			}
		}

		for (RuleDTO rule : project.getRules()) {
			LeafNode ruleNode = new LeafNode(LeafNode.TYPE_RULE, rule.getName(), rule.getId());
			if (rule.getCategoryIds().isEmpty()) {
				rulesNode.add(ruleNode);
			}
			for (String categoryId : rule.getCategoryIds()) {
				CompositeNode categoryNode = findCategoryNode(rulesNode, categoryId);
				categoryNode.add(ruleNode);
			}
		}

		for (DataModelDTO model : project.getDataModels()) {
			LeafNode modelNode = new LeafNode(LeafNode.TYPE_DATA_MODEL, model.getName(), model.getId());
			if (model.getCategoryIds().isEmpty()) {
				modelsNode.add(modelNode);
			}
			for (String categoryId : model.getCategoryIds()) {
				CompositeNode categoryNode = findCategoryNode(modelsNode, categoryId);
				categoryNode.add(modelNode);
			}
		}

		for (LibraryDTO library : project.getLibraries()) {
			LeafNode libraryNode = new LeafNode(LeafNode.TYPE_LIBRARY, library.getName(), library.getId());
			if (library.getCategoryIds().isEmpty()) {
				librariesNode.add(libraryNode);
			}
			for (String categoryId : library.getCategoryIds()) {
				CompositeNode categoryNode = findCategoryNode(librariesNode, categoryId);
				categoryNode.add(libraryNode);
			}
		}

		// remove empty categories from project resources
		for (CompositeNode resourceNode : resourceNodes) {
			removeEmptyCategories(resourceNode);
		}

		return root;
	}

	private static CompositeNode findCategoryNode(CompositeNode node, String categoryId) {
		for (TreeNode child : node.getItems()) {
			if (child instanceof CategoryCompositeNode) {
				CategoryCompositeNode compositeChild = (CategoryCompositeNode) child;
				if (categoryId.equals(compositeChild.getCategoryId())) {
					return compositeChild;
				}
			}
		}
		throw new IllegalStateException("Cannot find category node");
	}

	private static void removeEmptyCategories(CompositeNode node) {
		Iterator<TreeNode> iter = node.getItems().iterator();
		while (iter.hasNext()) {
			TreeNode child = iter.next();
			if (child instanceof CompositeNode) {
				CompositeNode compositeChild = (CompositeNode) child;
				if (compositeChild.getItems().isEmpty()) {
					iter.remove();
				}
			}
		}
	}

}
