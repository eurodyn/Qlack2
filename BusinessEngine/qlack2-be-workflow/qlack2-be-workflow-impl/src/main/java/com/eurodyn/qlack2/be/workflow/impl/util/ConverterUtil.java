package com.eurodyn.qlack2.be.workflow.impl.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.eurodyn.qlack2.be.explorer.api.dto.ProjectDTO;
import com.eurodyn.qlack2.be.rules.api.RulesService;
import com.eurodyn.qlack2.be.rules.api.WorkingSetsService;
import com.eurodyn.qlack2.be.rules.api.dto.RuleVersionIdentifierDTO;
import com.eurodyn.qlack2.be.rules.api.dto.WorkingSetVersionIdentifierDTO;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.GetRuleVersionIdByNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.GetRuleVersionIdentifierRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.GetWorkingSetVersionIdByNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.GetWorkingSetVersionIdentifierRequest;
import com.eurodyn.qlack2.be.workflow.api.dto.CategoryDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.ConditionDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.RuleDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.TaskSummaryDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.WorkflowDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.WorkflowInstanceDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.WorkflowRuntimeErrorLogDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.WorkflowVersionDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.WorkingSetDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.XMLConditionDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.XMLConditionsDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.XMLWorkflowVersionDTO;
import com.eurodyn.qlack2.be.workflow.impl.dto.AuditCategoryDTO;
import com.eurodyn.qlack2.be.workflow.impl.dto.AuditProjectDTO;
import com.eurodyn.qlack2.be.workflow.impl.dto.AuditWorkflowDTO;
import com.eurodyn.qlack2.be.workflow.impl.dto.AuditWorkflowInstanceDTO;
import com.eurodyn.qlack2.be.workflow.impl.dto.AuditWorkflowVersionDTO;
import com.eurodyn.qlack2.be.workflow.impl.model.Category;
import com.eurodyn.qlack2.be.workflow.impl.model.Condition;
import com.eurodyn.qlack2.be.workflow.impl.model.ConditionType;
import com.eurodyn.qlack2.be.workflow.impl.model.Workflow;
import com.eurodyn.qlack2.be.workflow.impl.model.WorkflowVersion;
import com.eurodyn.qlack2.fuse.auditing.api.dto.AuditLogDTO;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.fuse.workflow.runtime.api.dto.ProcessInstanceDesc;
import com.eurodyn.qlack2.fuse.workflow.runtime.api.dto.TaskSummary;
import com.eurodyn.qlack2.webdesktop.api.DesktopUserService;
import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;
import com.eurodyn.qlack2.webdesktop.api.request.user.GetUserUncheckedRequest;

/**
 *
 * Utility class to convert
 * 1.transfer object to entity
 * 2.entity to transfer object.
 *
 * @author European Dynamics SA
 */
public class ConverterUtil {
	private DesktopUserService desktopUserService;
	private List<WorkingSetsService> workingSetsServiceList;
	private List<RulesService> rulesServiceList;

	public void setDesktopUserService(DesktopUserService desktopUserService) {
		this.desktopUserService = desktopUserService;
	}

	public void setWorkingSetsServiceList(List<WorkingSetsService> workingSetsServiceList) {
		this.workingSetsServiceList = workingSetsServiceList;
	}

	public void setRulesServiceList(List<RulesService> rulesServiceList) {
		this.rulesServiceList = rulesServiceList;
	}

	private UserDTO user(String userId) {
		if (userId != null) {
			GetUserUncheckedRequest request = new GetUserUncheckedRequest(userId, false);
			return desktopUserService.getUserUnchecked(request);
		}
		else {
			return null;
		}
	}

	public CategoryDTO categoryToCategoryDTO(Category entity, boolean includeWorkflows) {

		if (entity == null)
	        return null;

	    CategoryDTO dto = new CategoryDTO();
	    dto.setId(entity.getId());
	    dto.setDescription(entity.getDescription());
	    dto.setName(entity.getName());

		dto.setCreatedBy(user(entity.getCreatedBy()));
		dto.setLastModifiedBy(user(entity.getLastModifiedBy()));

	    dto.setCreatedOn(entity.getCreatedOn());
	    dto.setLastModifiedOn(entity.getLastModifiedOn());

	    if (includeWorkflows) {
	    	List<WorkflowDTO> list = new ArrayList<WorkflowDTO>(workflowToWorkflowDTOList(entity.getWorkflows()));
	    	dto.setWorkflows(list);
	    }

	    return dto;
	}

	public List<CategoryDTO> categoryToCategoryDTOList(List<Category> categories, SignedTicket signedTicket, boolean includeWorkflows) {

		if (categories == null)
	        return null;

		List<CategoryDTO> dtos = new ArrayList<>();
		for (Category category : categories) {
			dtos.add(categoryToCategoryDTO(category, includeWorkflows));
		}
		return dtos;
	}

	public WorkflowInstanceDTO processInstanceDescToWorkflowInstanceDTO(ProcessInstanceDesc instanceLog, Workflow workflow, WorkflowVersion version) {
		WorkflowInstanceDTO dto = new WorkflowInstanceDTO();
		dto.setId(instanceLog.getId());
		dto.setDuration(instanceLog.getDuration());
		dto.setEndDate(instanceLog.getEndDate()!=null ? instanceLog.getEndDate().getTime() : 0);
		dto.setStartDate(instanceLog.getStartDate().getTime());
		dto.setProcessId(instanceLog.getProcessId());
		dto.setProcessInstanceId(instanceLog.getProcessInstanceId());
		dto.setProcessName(instanceLog.getProcessName());
		dto.setStatus(instanceLog.getState());
		dto.setStatusDesc(instanceLog.getStateDesc());
		dto.setVersionName(version.getName());
		dto.setWorkflowName(workflow.getName());
		dto.setVersionId(version.getId());
		dto.setWorkflowId(workflow.getId());
		return dto;
	}

	public TaskSummaryDTO taskSummaryToTaskSummaryDTO(TaskSummary summary) {
		TaskSummaryDTO dto = new TaskSummaryDTO();
		dto.setActivationTime(summary.getActivationTime());
		dto.setActualOwner(summary.getActualOwner());
		dto.setCreatedBy(summary.getCreatedBy());
		dto.setCreatedOn(summary.getCreatedOn());
		dto.setDeploymentId(summary.getDeploymentId());
		dto.setDescription(summary.getDescription());
		dto.setExpirationTime(summary.getExpirationTime());
		dto.setId(summary.getId());
		dto.setName(summary.getName());
		dto.setParentId(summary.getParentId());
		dto.setPriority(summary.getPriority());
		dto.setProcessId(summary.getProcessId());
		dto.setProcessInstanceId(summary.getProcessInstanceId());
		dto.setProcessSessionId(summary.getProcessSessionId());
		dto.setStatus(summary.getStatus());
		dto.setPotentialOwnerGroups(summary.getPotentialOwnerGroups());
		return dto;
	}

	public List<TaskSummaryDTO> taskSummaryToTaskSummaryDTOList(List<TaskSummary> summaries) {

		if (summaries == null)
	        return null;

		List<TaskSummaryDTO> dtos = new ArrayList<>();
		for (TaskSummary taskSummary : summaries) {
			dtos.add(taskSummaryToTaskSummaryDTO(taskSummary));
		}
		return dtos;
	}

	public List<String> categoriesToCategoryIdsList(Collection<Category> categories) {
		if (categories == null) {
			return null;
		}

		List<String> categoryIds = new ArrayList<>();
		for (Category category : categories) {
			categoryIds.add(category.getId());
		}
		return categoryIds;
	}

	public WorkflowDTO workflowToWorkflowDTO(Workflow entity) {

		if (entity == null)
	        return null;

	    WorkflowDTO dto = new WorkflowDTO();
	    dto.setId(entity.getId());
	    dto.setName(entity.getName());
	    dto.setDescription(entity.getDescription());
	    dto.setProjectId(entity.getProjectId());
	    dto.setActive(entity.isActive());

	  	dto.setCreatedBy(user(entity.getCreatedBy()));
	  	dto.setLastModifiedBy(user(entity.getLastModifiedBy()));

	    dto.setCreatedOn(entity.getCreatedOn());
	    dto.setLastModifiedOn(entity.getLastModifiedOn());
    	List<String> list = categoriesToCategoryIdsList(entity.getCategories());
	    dto.setCategories(list);

	    List<WorkflowVersionDTO> versions = new ArrayList<WorkflowVersionDTO>(workflowVersionToWorkflowVersionDTOList(entity.getWorkflowVersions()));
	    dto.setVersions(versions);

	    return dto;
	}

	public List<WorkflowDTO> workflowToWorkflowDTOList(List<Workflow> workflows) {

		if (workflows == null) {
			return null;
		}

		List<WorkflowDTO> dtos = new ArrayList<>();
		for (Workflow workflow : workflows) {
            dtos.add(workflowToWorkflowDTO(workflow));
        }
		return dtos;
	}

	public List<WorkflowVersionDTO> workflowVersionToWorkflowVersionDTOList(List<WorkflowVersion> versions) {

		if (versions == null) {
			return null;
		}

		List<WorkflowVersionDTO> dtos = new ArrayList<>();
		for (WorkflowVersion version : versions) {
            dtos.add(workflowVersionToWorkflowVersionDTO(version));
        }
		return dtos;
	}

	public WorkflowVersionDTO workflowVersionToWorkflowVersionDTO(WorkflowVersion entity) {

		if (entity == null)
	        return null;

	    WorkflowVersionDTO dto = new WorkflowVersionDTO();
	    dto.setId(entity.getId());
	    dto.setName(entity.getName());
	    dto.setDescription(entity.getDescription());
	    dto.setState(entity.getState().ordinal());

	    dto.setContent(entity.getContent());

	    dto.setCreatedBy(user(entity.getCreatedBy()));
	  	dto.setLastModifiedBy(user(entity.getLastModifiedBy()));
		dto.setLockedBy(user(entity.getLockedBy()));

	    dto.setCreatedOn(entity.getCreatedOn());
	    dto.setLastModifiedOn(entity.getLastModifiedOn());
	    dto.setLockedOn(entity.getLockedOn());

	    dto.setEnableTesting(entity.isEnableTesting());
	    dto.setProcessId(entity.getProcessId());

	    List<ConditionDTO> conditions = new ArrayList<ConditionDTO>(conditionToConditionDTOList(entity.getConditions()));
	    dto.setConditions(conditions);
	    return dto;
	}

	public List<ConditionDTO> conditionToConditionDTOList(List<Condition> conditions) {

		if (conditions == null) {
			return null;
		}

		List<ConditionDTO> dtos = new ArrayList<>();
		for (Condition condition : conditions) {
            dtos.add(conditionToConditionDTO(condition));
        }
		return dtos;
	}

	public ConditionDTO conditionToConditionDTO(Condition entity) {

		if (entity == null)
	        return null;

	    ConditionDTO dto = new ConditionDTO();
	    dto.setId(entity.getId());
	    dto.setName(entity.getName());
	    dto.setConditionType(entity.getConditionType().ordinal());
	    dto.setWorkingSetId(entity.getWorkingSetId());
	    dto.setRuleId(entity.getRuleId());
	    dto.setParentCondition(conditionToConditionDTO(entity.getParent()));

	    return dto;
	}

	public List<WorkingSetDTO> workingSetVersionIdentifierDTOsToWorkingSetDTOList(
			Collection<WorkingSetVersionIdentifierDTO> workingSetVersionIdentifierDTOs) {
		if (workingSetVersionIdentifierDTOs == null) {
			return null;
		}

		List<WorkingSetDTO> workingSetDTOs = new ArrayList<>();
		for (WorkingSetVersionIdentifierDTO workingSetVersionIdentifierDTO : workingSetVersionIdentifierDTOs) {
			workingSetDTOs
					.add(workingSetVersionIdentifierDTOToWorkingSetDTO(workingSetVersionIdentifierDTO));
		}
		return workingSetDTOs;
	}

	public WorkingSetDTO workingSetVersionIdentifierDTOToWorkingSetDTO(
			WorkingSetVersionIdentifierDTO workingSetVersionIdentifierDTO) {
		if (workingSetVersionIdentifierDTO == null) {
			return null;
		}

		WorkingSetDTO workingSetDTO = new WorkingSetDTO();
		workingSetDTO.setId(workingSetVersionIdentifierDTO.getId());
		workingSetDTO.setName(workingSetVersionIdentifierDTO.getName());
		workingSetDTO.setWorkingSetName(workingSetVersionIdentifierDTO
				.getWorkingSetName());
		return workingSetDTO;
	}

	public List<RuleDTO> ruleVersionIdentifierDTOsToRuleDTOList(
			Collection<RuleVersionIdentifierDTO> ruleVersionIdentifierDTOs) {
		if (ruleVersionIdentifierDTOs == null) {
			return null;
		}

		List<RuleDTO> ruleDTOs = new ArrayList<>();
		for (RuleVersionIdentifierDTO ruleVersionIdentifierDTO : ruleVersionIdentifierDTOs) {
			ruleDTOs.add(ruleVersionIdentifierDTOToRuleDTO(ruleVersionIdentifierDTO));
		}
		return ruleDTOs;
	}

	public RuleDTO ruleVersionIdentifierDTOToRuleDTO(
			RuleVersionIdentifierDTO ruleVersionIdentifierDTO) {
		if (ruleVersionIdentifierDTO == null) {
			return null;
		}

		RuleDTO ruleDTO = new RuleDTO();
		ruleDTO.setId(ruleVersionIdentifierDTO.getId());
		ruleDTO.setName(ruleVersionIdentifierDTO.getName());
		ruleDTO.setRuleName(ruleVersionIdentifierDTO.getRuleName());
		ruleDTO.setWorkingSetId(ruleVersionIdentifierDTO
				.getWorkingSetVersionId());

		return ruleDTO;

	}

	public AuditCategoryDTO categoryToAuditCategoryDTO(Category category) {
		if (category == null) {
			return null;
		}

		AuditCategoryDTO auditCategoryDTO = new AuditCategoryDTO();
		auditCategoryDTO.setId(category.getId());
		auditCategoryDTO.setName(category.getName());
		auditCategoryDTO.setDescription(category.getDescription());
		return auditCategoryDTO;
	}

	public List<AuditCategoryDTO> categoriesToAuditCategoryDTOList(
			Collection<Category> categories) {
		if (categories == null) {
			return null;
		}

		List<AuditCategoryDTO> auditCategoryDTOs = new ArrayList<>();
		for (Category category : categories) {
			auditCategoryDTOs.add(categoryToAuditCategoryDTO(category));
		}
		return auditCategoryDTOs;
	}

	public AuditProjectDTO projectToAuditProjectDTO(ProjectDTO project) {
		if (project == null) {
			return null;
		}

		AuditProjectDTO auditProjectDTO = new AuditProjectDTO();
		auditProjectDTO.setId(project.getId());
		auditProjectDTO.setName(project.getName());

		return auditProjectDTO;
	}

	public AuditWorkflowDTO workflowToAuditWorkflowDTO(Workflow workflow) {
		if (workflow == null) {
			return null;
		}

		AuditWorkflowDTO workflowDTO = new AuditWorkflowDTO();
		workflowDTO.setId(workflow.getId());
		workflowDTO.setName(workflow.getName());
		workflowDTO.setDescription(workflow.getDescription());
		workflowDTO.setActive(workflow.isActive());

		List<AuditCategoryDTO> auditCategoryDTOs = categoriesToAuditCategoryDTOList(workflow
				.getCategories());
		workflowDTO.setCategories(auditCategoryDTOs);

		List<AuditWorkflowVersionDTO> auditWorkflowVersionDTOs = workflowVersionsToAuditWorkflowVersionDTOList(workflow
				.getWorkflowVersions());
		workflowDTO.setVersions(auditWorkflowVersionDTOs);
		return workflowDTO;
	}

	public AuditWorkflowVersionDTO workflowVersionToAuditWorkflowVersionDetailsDTO(
			WorkflowVersion workflowVersion) {
		if (workflowVersion == null) {
			return null;
		}

		AuditWorkflowVersionDTO workflowVersionDTO = new AuditWorkflowVersionDTO();
		workflowVersionDTO.setId(workflowVersion.getId());
		workflowVersionDTO.setName(workflowVersion.getName());
		workflowVersionDTO.setDescription(workflowVersion.getDescription());
		workflowVersionDTO.setState(workflowVersion.getState().ordinal());
		workflowVersionDTO.setLocked((workflowVersion.getLockedOn() != null));
		workflowVersionDTO.setConditions(conditionToConditionDTOList(workflowVersion.getConditions()));

		return workflowVersionDTO;
	}

	public List<AuditWorkflowVersionDTO> workflowVersionsToAuditWorkflowVersionDTOList(
			Collection<WorkflowVersion> workflowVersions) {
		if (workflowVersions == null) {
			return null;
		}

		List<AuditWorkflowVersionDTO> workflowVersionDTOs = new ArrayList<>();
		for (WorkflowVersion workflowVersion : workflowVersions) {
			workflowVersionDTOs
					.add(workflowVersionToAuditWorkflowVersionDetailsDTO(workflowVersion));
		}
		return workflowVersionDTOs;
	}

	public AuditWorkflowInstanceDTO workflowVersionToAuditWorkflowInstanceDTO(WorkflowVersion version, Long processInstanceId) {
		if (version == null) {
			return null;
		}

		AuditWorkflowInstanceDTO auditWorkflowInstanceDTO = new AuditWorkflowInstanceDTO();
		auditWorkflowInstanceDTO.setVersionId(version.getId());
		auditWorkflowInstanceDTO.setProcessId(version.getProcessId());
		auditWorkflowInstanceDTO.setProcessInstanceId(processInstanceId);
		auditWorkflowInstanceDTO.setWorkflowId(version.getWorkflow().getId());

		return auditWorkflowInstanceDTO;
	}

	public XMLWorkflowVersionDTO workflowVersionToXMLWorkflowVersionDTO(WorkflowVersion entity, SignedTicket signedTicket) {

		if (entity == null)
	        return null;

	    XMLWorkflowVersionDTO dto = new XMLWorkflowVersionDTO();
	    dto.setName(entity.getName());
	    dto.setDescription(entity.getDescription());
	    dto.setContent(entity.getContent());
	    dto.setProcessId(entity.getProcessId());
	    XMLConditionsDTO conditionsList = new XMLConditionsDTO();
	    conditionsList.setConditions(new ArrayList<XMLConditionDTO>(conditionToXMLConditionDTOList(entity.getConditions(), signedTicket)));
	    dto.setConditions(conditionsList);
	    return dto;
	}

	public List<XMLConditionDTO> conditionToXMLConditionDTOList(List<Condition> conditions, SignedTicket signedTicket) {

		if (conditions == null) {
			return null;
		}

		List<XMLConditionDTO> dtos = new ArrayList<>();
		for (Condition condition : conditions) {
            dtos.add(conditionToXMLConditionDTO(condition, signedTicket));
        }
		return dtos;
	}

	public XMLConditionDTO conditionToXMLConditionDTO(Condition entity, SignedTicket signedTicket) {

		if (entity == null)
	        return null;

		if (workingSetsServiceList.size() == 0) {
			return null;
		}

		if (rulesServiceList.size() == 0) {
			return null;
		}

		GetWorkingSetVersionIdentifierRequest workingSetVersionRequest = new GetWorkingSetVersionIdentifierRequest();
		workingSetVersionRequest.setSignedTicket(signedTicket);
		workingSetVersionRequest.setId(entity.getWorkingSetId());
		WorkingSetVersionIdentifierDTO workingSetVersion = workingSetsServiceList.get(0).getWorkingSetVersionIdentifier(workingSetVersionRequest);


		GetRuleVersionIdentifierRequest ruleVersionRequest = new GetRuleVersionIdentifierRequest();
		ruleVersionRequest.setSignedTicket(signedTicket);
		ruleVersionRequest.setId(entity.getRuleId());
		RuleVersionIdentifierDTO ruleVersion = rulesServiceList.get(0).getRuleVersionIdentifier(ruleVersionRequest);

	    XMLConditionDTO dto = new XMLConditionDTO();
	    dto.setName(entity.getName());
	    dto.setConditionType(entity.getConditionType().toString());
	    dto.setWorkingSet(workingSetVersion.getWorkingSetName());
	    dto.setWorkingSetVersion(workingSetVersion.getName());
	    dto.setRule(ruleVersion.getRuleName());
	    dto.setRuleVersion(ruleVersion.getName());
	    dto.setParentCondition(entity.getParent()!=null? entity.getParent().getName() : null);

	    return dto;
	}

	public List<ConditionDTO> XMLConditionDTOToConditionDTOList(List<XMLConditionDTO> conditions, String projectId, SignedTicket signedTicket) {

		if (conditions == null) {
			return null;
		}

		List<ConditionDTO> dtos = new ArrayList<>();
		for (XMLConditionDTO condition : conditions) {
            XMLConditionDTOToConditionDTO(condition, conditions, dtos, projectId, signedTicket);
        }
		return dtos;
	}


	public ConditionDTO XMLConditionDTOToConditionDTO(XMLConditionDTO entity, List<XMLConditionDTO> conditions, List<ConditionDTO> conditionDTOs, String projectId, SignedTicket signedTicket) {

		if (entity == null)
	        return null;

		if (workingSetsServiceList.size() == 0) {
			return null;
		}

		if (rulesServiceList.size() == 0) {
			return null;
		}

		GetWorkingSetVersionIdByNameRequest workingSetVersionRequest = new GetWorkingSetVersionIdByNameRequest();
		workingSetVersionRequest.setSignedTicket(signedTicket);
		workingSetVersionRequest.setProjectId(projectId);
		workingSetVersionRequest.setWorkingSetName(entity.getWorkingSet());
		workingSetVersionRequest.setName(entity.getWorkingSetVersion());
		String workingSetVersion = workingSetsServiceList.get(0).getWorkingSetVersionIdByName(workingSetVersionRequest);

		GetRuleVersionIdByNameRequest ruleVersionRequest = new GetRuleVersionIdByNameRequest();
		ruleVersionRequest.setSignedTicket(signedTicket);
		ruleVersionRequest.setProjectId(projectId);
		ruleVersionRequest.setRuleName(entity.getRule());
		ruleVersionRequest.setName(entity.getRuleVersion());
		String ruleVersion = rulesServiceList.get(0).getRuleVersionIdByName(ruleVersionRequest);

	    ConditionDTO dto = new ConditionDTO();
	    dto.setId(UUID.randomUUID().toString());
	    dto.setName(entity.getName());
	    dto.setConditionType(ConditionType.valueOf(entity.getConditionType()).ordinal());
	    dto.setWorkingSetId(workingSetVersion);
	    dto.setRuleId(ruleVersion);
		if (entity.getParentCondition()!= null &&
				!entity.getParentCondition().equals(""))
				{
					// check if parent has already been created in dtos
					ConditionDTO parentCondition = null;
					for (ConditionDTO pCondition : conditionDTOs)
						if (pCondition.getName().equals(entity.getParentCondition()))
						{
							parentCondition = pCondition;
							break;
						}
					// find parent in XMLDTOs
					if (parentCondition == null)
					{
						for (XMLConditionDTO pCondition : conditions)
							if (pCondition.getName().equals(entity.getParentCondition()))
								parentCondition = XMLConditionDTOToConditionDTO(pCondition, conditions, conditionDTOs, projectId, signedTicket);

					}
					dto.setParentCondition(parentCondition);
				}
	    conditionDTOs.add(dto);
	    return dto;
	}

	public WorkflowRuntimeErrorLogDTO auditLogDTOToWorkflowRuntimeErrorLogDTO(AuditLogDTO auditLog, Workflow workflow, WorkflowVersion version) {
		WorkflowRuntimeErrorLogDTO dto = new WorkflowRuntimeErrorLogDTO();
		dto.setId(auditLog.getId());
		dto.setProcessId(auditLog.getShortDescription());
		dto.setProcessInstanceId(auditLog.getReferenceId());
		dto.setLogDate(auditLog.getCreatedOn().getTime());
		dto.setVersionName(version.getName());
		dto.setWorkflowName(workflow.getName());
		dto.setTraceData(auditLog.getTraceData());
		return dto;
	}
}
