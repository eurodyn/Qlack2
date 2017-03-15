package com.eurodyn.qlack2.be.workflow.web.util;

import java.util.ArrayList;
import java.util.List;

import com.eurodyn.qlack2.be.workflow.api.dto.CategoryDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.ConditionDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.ProjectResourcesDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.WorkflowDTO;
import com.eurodyn.qlack2.be.workflow.api.request.category.CreateCategoryRequest;
import com.eurodyn.qlack2.be.workflow.api.request.category.UpdateCategoryRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.UpdateWorkflowVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.workflow.CreateWorkflowRequest;
import com.eurodyn.qlack2.be.workflow.api.request.workflow.UpdateWorkflowRequest;
import com.eurodyn.qlack2.be.workflow.web.dto.CategoryRDTO;
import com.eurodyn.qlack2.be.workflow.web.dto.ConditionRDTO;
import com.eurodyn.qlack2.be.workflow.web.dto.WorkflowDetailsRDTO;
import com.eurodyn.qlack2.be.workflow.web.dto.WorkflowRDTO;
import com.eurodyn.qlack2.be.workflow.web.dto.WorkflowVersionDetailsRDTO;
import com.eurodyn.qlack2.be.workflow.web.dto.tree.CompositeNodeDTO;
import com.eurodyn.qlack2.be.workflow.web.dto.tree.LeafNodeDTO;

/**
 *
 * Utility class to convert 1.transfer object to entity 2.entity to transfer
 * object.
 *
 * @author European Dynamics SA
 */
public class ConverterUtil {

	public static CreateCategoryRequest categoryRDTOToCreateCategoryRequest(CategoryRDTO categoryRDTO) {
		CreateCategoryRequest request = new CreateCategoryRequest();
		if (categoryRDTO == null) {
			return request;
		}

		request.setName(categoryRDTO.getName());
		request.setDescription(categoryRDTO.getDescription());
		request.setProjectId(categoryRDTO.getProjectId());

		return request;
	}

	public static UpdateCategoryRequest categoryRDTOToUpdateCategoryRequest(CategoryRDTO categoryRDTO) {
		UpdateCategoryRequest request = new UpdateCategoryRequest();
		if (categoryRDTO == null) {
			return request;
		}

		request.setName(categoryRDTO.getName());
		request.setDescription(categoryRDTO.getDescription());
		request.setProjectId(categoryRDTO.getProjectId());

		return request;
	}

	public static CreateWorkflowRequest workflowRDTOToCreateWorkflowRequest(WorkflowRDTO workflowRDTO) {
		CreateWorkflowRequest request = new CreateWorkflowRequest();
		if (workflowRDTO == null) {
			return request;
		}

		request.setName(workflowRDTO.getName());
		request.setDescription(workflowRDTO.getDescription());
		request.setActive(workflowRDTO.isActive());
		request.setCategoryIds(workflowRDTO.getCategories());
		request.setProjectId(workflowRDTO.getProjectId());

		return request;
	}

	public static UpdateWorkflowRequest workflowDetailsRDTOToUpdateWorkflowRequest(WorkflowDetailsRDTO workflowDetailsRDTO) {
		UpdateWorkflowRequest request = new UpdateWorkflowRequest();
		if (workflowDetailsRDTO == null) {
			return request;
		}

		request.setName(workflowDetailsRDTO.getName());
		request.setDescription(workflowDetailsRDTO.getDescription());
		request.setActive(workflowDetailsRDTO.isActive());
		request.setCategoryIds(workflowDetailsRDTO.getCategories());

		request.setVersionRequest(workflowVersionDetailsRDTOToUpdateWorkflowVersionRequest(workflowDetailsRDTO.getVersionDetails()));

		return request;
	}
	
	public static UpdateWorkflowVersionRequest workflowVersionDetailsRDTOToUpdateWorkflowVersionRequest(WorkflowVersionDetailsRDTO versionDetailsRDTO) {
		UpdateWorkflowVersionRequest request = new UpdateWorkflowVersionRequest();
		
		if (versionDetailsRDTO == null) {
			return request;
		}

		request.setContent(versionDetailsRDTO.getContent());
		request.setDescription(versionDetailsRDTO.getDescription());
		request.setId(versionDetailsRDTO.getId());
		request.setName(versionDetailsRDTO.getName());
		request.setVersionConditions(conditionRDTOsToConditionDTOList(versionDetailsRDTO.getVersionConditions()));

		return request;
	}

	public static ConditionDTO conditionRDTOToConditionDTO(ConditionRDTO conditionRDTO) {
		if (conditionRDTO == null) {
			return null;
		}

		ConditionDTO conditionDTO = new ConditionDTO();
		conditionDTO.setId(conditionRDTO.getId());
		conditionDTO.setName(conditionRDTO.getName());
		conditionDTO.setConditionType(conditionRDTO.getConditionType());
		conditionDTO.setWorkingSetId(conditionRDTO.getWorkingSetId());
		conditionDTO.setRuleId(conditionRDTO.getRuleId());
		conditionDTO.setParentCondition(conditionRDTOToConditionDTO(conditionRDTO.getParentCondition()));

		return conditionDTO;
	}

	public static List<ConditionDTO> conditionRDTOsToConditionDTOList(List<ConditionRDTO> conditionRDTOs) {
		if (conditionRDTOs == null) {
			return null;
		}

		List<ConditionDTO> conditionDTOs = new ArrayList<>();
		for (ConditionRDTO conditionRDTO : conditionRDTOs) {
			conditionDTOs.add(conditionRDTOToConditionDTO(conditionRDTO));
		}

		return conditionDTOs;
	}
	
	public static CompositeNodeDTO convertResourcesToTree(ProjectResourcesDTO project) {
		CompositeNodeDTO root = new CompositeNodeDTO(project.getId(), project.getName(), "project");
		CompositeNodeDTO workflowsNode = new CompositeNodeDTO(CompositeNodeDTO.WORKFLOWS);
		CompositeNodeDTO categoriesNode = new CompositeNodeDTO(CompositeNodeDTO.CATEGORIES);

		List<CompositeNodeDTO> resourceNodes = new ArrayList<>();
		resourceNodes.add(workflowsNode);

		// categories for project resources
		for (CompositeNodeDTO resourceNode : resourceNodes) {
			root.add(resourceNode);
			for (CategoryDTO category : project.getCategories()) {
				//check empty category - no workflows
				if (category.getWorkflows().size() > 0)
				{
					CompositeNodeDTO categoryNode = new CompositeNodeDTO(category.getId(), category.getName(), "category");
					// put workflows as leaf nodes in categories
					for (WorkflowDTO workflow : category.getWorkflows())
					{
						LeafNodeDTO workflowNode = new LeafNodeDTO(workflow.getId(), workflow.getName(), LeafNodeDTO.WORKFLOW);
						categoryNode.add(workflowNode);
					}
					resourceNode.add(categoryNode);
				}
				LeafNodeDTO categoryNode = new LeafNodeDTO(category.getId(), category.getName(), LeafNodeDTO.CATEGORY);
				categoriesNode.add(categoryNode);
			}
			
			
			for (WorkflowDTO workflow : project.getWorkflows()) {
					LeafNodeDTO workflowNode = new LeafNodeDTO(workflow.getId(), workflow.getName(), LeafNodeDTO.WORKFLOW);
					resourceNode.add(workflowNode);
			}
		}
		
		root.add(categoriesNode);

		return root;
	}

}
