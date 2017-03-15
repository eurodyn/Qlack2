package com.eurodyn.qlack2.be.forms.web.util;

import java.util.ArrayList;
import java.util.List;

import com.eurodyn.qlack2.be.forms.api.dto.CategoryDTO;
import com.eurodyn.qlack2.be.forms.api.dto.ConditionDTO;
import com.eurodyn.qlack2.be.forms.api.dto.FormDTO;
import com.eurodyn.qlack2.be.forms.api.dto.ProjectDetailsDTO;
import com.eurodyn.qlack2.be.forms.api.dto.TranslationDTO;
import com.eurodyn.qlack2.be.forms.api.request.category.CreateCategoryRequest;
import com.eurodyn.qlack2.be.forms.api.request.category.UpdateCategoryRequest;
import com.eurodyn.qlack2.be.forms.api.request.form.CreateFormRequest;
import com.eurodyn.qlack2.be.forms.api.request.form.UpdateFormRequest;
import com.eurodyn.qlack2.be.forms.web.dto.CategoryRDTO;
import com.eurodyn.qlack2.be.forms.web.dto.ConditionRDTO;
import com.eurodyn.qlack2.be.forms.web.dto.FormDetailsRDTO;
import com.eurodyn.qlack2.be.forms.web.dto.FormRDTO;
import com.eurodyn.qlack2.be.forms.web.dto.TranslationRDTO;
import com.eurodyn.qlack2.be.forms.web.dto.tree.CompositeNode;
import com.eurodyn.qlack2.be.forms.web.dto.tree.LeafNode;
import com.eurodyn.qlack2.be.forms.web.dto.tree.TreeNodeType;

/**
 *
 * Utility class to convert 1.transfer object to entity 2.entity to transfer
 * object.
 *
 * @author European Dynamics SA
 */
public class ConverterUtil {

	public static CreateCategoryRequest categoryRDTOToCreateCategoryRequest(
			CategoryRDTO categoryRDTO) {
		CreateCategoryRequest request = new CreateCategoryRequest();
		if (categoryRDTO == null) {
			return request;
		}

		request.setName(categoryRDTO.getName());
		request.setDescription(categoryRDTO.getDescription());
		request.setProjectId(categoryRDTO.getProjectId());

		return request;
	}

	public static UpdateCategoryRequest categoryRDTOToUpdateCategoryRequest(
			CategoryRDTO categoryRDTO) {
		UpdateCategoryRequest request = new UpdateCategoryRequest();
		if (categoryRDTO == null) {
			return request;
		}

		request.setName(categoryRDTO.getName());
		request.setDescription(categoryRDTO.getDescription());

		return request;
	}

	public static CreateFormRequest formRDTOToCreateFormRequest(
			FormRDTO formRDTO) {
		CreateFormRequest request = new CreateFormRequest();
		if (formRDTO == null) {
			return request;
		}

		request.setName(formRDTO.getName());
		request.setDescription(formRDTO.getDescription());
		request.setActive(formRDTO.isActive());
		request.setCategories(formRDTO.getCategories());
		request.setLocales(formRDTO.getLocales());
		request.setProjectId(formRDTO.getProjectId());

		return request;
	}

	public static UpdateFormRequest formDetailsRDTOToUpdateFormRequest(
			FormDetailsRDTO formDetailsRDTO) {
		UpdateFormRequest request = new UpdateFormRequest();
		if (formDetailsRDTO == null) {
			return request;
		}

		request.setName(formDetailsRDTO.getName());
		request.setDescription(formDetailsRDTO.getDescription());
		request.setActive(formDetailsRDTO.isActive());
		request.setCategories(formDetailsRDTO.getCategories());
		request.setLocales(formDetailsRDTO.getLocales());
		request.setProjectId(formDetailsRDTO.getProjectId());
		request.setVersionId(formDetailsRDTO.getVersionId());
		request.setVersionDescription(formDetailsRDTO.getVersionDescription());
		request.setVersionContent(formDetailsRDTO.getVersionContent());

		return request;
	}

	public static ConditionDTO conditionRDTOToConditionDTO(
			ConditionRDTO conditionRDTO) {
		if (conditionRDTO == null) {
			return null;
		}

		ConditionDTO conditionDTO = new ConditionDTO();
		conditionDTO.setId(conditionRDTO.getId());
		conditionDTO.setName(conditionRDTO.getName());
		conditionDTO.setConditionType(conditionRDTO.getConditionType());
		conditionDTO.setWorkingSetId(conditionRDTO.getWorkingSetId());
		conditionDTO.setRuleId(conditionRDTO.getRuleId());
		conditionDTO
				.setParentCondition(conditionRDTOToConditionDTO(conditionRDTO
						.getParentCondition()));

		return conditionDTO;
	}

	public static List<ConditionDTO> conditionRDTOsToConditionDTOList(
			List<ConditionRDTO> conditionRDTOs) {
		if (conditionRDTOs == null) {
			return null;
		}

		List<ConditionDTO> conditionDTOs = new ArrayList<>();
		for (ConditionRDTO conditionRDTO : conditionRDTOs) {
			conditionDTOs.add(conditionRDTOToConditionDTO(conditionRDTO));
		}

		return conditionDTOs;
	}

	public static TranslationDTO translationRDTOToTranslationDTO(
			TranslationRDTO translationRDTO) {
		if (translationRDTO == null) {
			return null;
		}

		TranslationDTO translationDTO = new TranslationDTO();
		translationDTO.setKeyId(translationRDTO.getKeyId());
		translationDTO.setKey(translationRDTO.getKey());
		translationDTO.setValue(translationRDTO.getValue());
		translationDTO.setLanguage(translationRDTO.getLanguage());

		return translationDTO;
	}

	public static List<TranslationDTO> translationRDTOsToTranslationDTOList(
			List<TranslationRDTO> translationRDTOs) {
		if (translationRDTOs == null) {
			return null;
		}

		List<TranslationDTO> translationDTOs = new ArrayList<>();
		for (TranslationRDTO translationRDTO : translationRDTOs) {
			translationDTOs
					.add(translationRDTOToTranslationDTO(translationRDTO));
		}

		return translationDTOs;
	}

	/**
	 * Converts the resources of a project, i.e. categories and forms to a tree
	 * structure. The first level has the nodes "Forms" and "Categories". The
	 * "Forms" node has as descendant nodes the categories of the project as
	 * well as the forms that do not belong to any category. Each category node
	 * has as descendants the forms that are associated with this category. The
	 * "Categories" node has as descendant nodes the categories of the project.
	 *
	 *
	 * @param project
	 * @return
	 */
	public static CompositeNode convert(ProjectDetailsDTO project) {
		// static project resource nodes
		CompositeNode root = new CompositeNode(project.getId(), project.getName(), "project");

		// forms node
		CompositeNode formsNode = new CompositeNode(TreeNodeType.FORM);

		// form resources under each category
		for (CategoryDTO category : project.getCategories()) {
			// form node for each form of the category
			if (category.getForms() != null && !category.getForms().isEmpty()) {
				// Add a category node only if it has form resources
				CompositeNode categoryNode = new CompositeNode(
						category.getId(), category.getName(),
						TreeNodeType.CATEGORY.type());
				for (FormDTO form : category.getForms()) {
					LeafNode formNode = new LeafNode(form.getId(),
							form.getName(), TreeNodeType.FORM.type());
					categoryNode.add(formNode);
				}

				formsNode.add(categoryNode);
			}
		}

		// uncategorised form resources
		for (FormDTO form : project.getForms()) {
			LeafNode formNode = new LeafNode(form.getId(), form.getName(),
					TreeNodeType.FORM.type());
			formsNode.add(formNode);
		}
		root.add(formsNode);

		// categories node
		CompositeNode categoriesNode = new CompositeNode(TreeNodeType.CATEGORY);

		for (CategoryDTO category : project.getCategories()) {
			LeafNode categoryNode = new LeafNode(category.getId(),
					category.getName(), TreeNodeType.CATEGORY.type());
			categoriesNode.add(categoryNode);
		}
		root.add(categoriesNode);

		return root;
	}

}
