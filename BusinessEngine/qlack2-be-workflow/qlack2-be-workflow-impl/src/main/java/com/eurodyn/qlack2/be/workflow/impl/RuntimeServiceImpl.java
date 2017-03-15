package com.eurodyn.qlack2.be.workflow.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import com.eurodyn.qlack2.be.rules.api.RulesRuntimeManagementService;
import com.eurodyn.qlack2.be.rules.api.request.runtime.StatelessMultiExecuteRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.WorkingSetRuleVersionPair;
import com.eurodyn.qlack2.be.workflow.api.RuntimeService;
import com.eurodyn.qlack2.be.workflow.api.dto.TaskSummaryDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.WorkflowInstanceDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.WorkflowRuntimeErrorLogDTO;
import com.eurodyn.qlack2.be.workflow.api.exception.QInvalidActionException;
import com.eurodyn.qlack2.be.workflow.api.exception.QInvalidPreconditionsException;
import com.eurodyn.qlack2.be.workflow.api.request.runtime.DeleteWorkflowInstancesRequest;
import com.eurodyn.qlack2.be.workflow.api.request.runtime.GetWorkflowInstanceDetailsRequest;
import com.eurodyn.qlack2.be.workflow.api.request.runtime.GetWorkflowInstancesRequest;
import com.eurodyn.qlack2.be.workflow.api.request.runtime.GetWorkflowRuntimeErrorLogRequest;
import com.eurodyn.qlack2.be.workflow.api.request.runtime.WorkflowInstanceActionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.runtime.WorkflowInstanceRequest;
import com.eurodyn.qlack2.be.workflow.api.request.runtime.WorkflowTaskActionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.runtime.WorkflowVariableInstanceRequest;
import com.eurodyn.qlack2.be.workflow.client.api.rules.PreconditionFact;
import com.eurodyn.qlack2.be.workflow.impl.dto.AuditWorkflowInstanceDTO;
import com.eurodyn.qlack2.be.workflow.impl.model.Condition;
import com.eurodyn.qlack2.be.workflow.impl.model.ConditionType;
import com.eurodyn.qlack2.be.workflow.impl.model.Workflow;
import com.eurodyn.qlack2.be.workflow.impl.model.WorkflowVersion;
import com.eurodyn.qlack2.be.workflow.impl.util.AuditConstants;
import com.eurodyn.qlack2.be.workflow.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.be.workflow.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.be.workflow.impl.util.AuditConstants.LEVEL;
import com.eurodyn.qlack2.be.workflow.impl.util.ConverterUtil;
import com.eurodyn.qlack2.be.workflow.impl.util.RuntimeUtil;
import com.eurodyn.qlack2.be.workflow.impl.util.SecurityUtil;
import com.eurodyn.qlack2.be.workflow.impl.util.WorkflowConstants;
import com.eurodyn.qlack2.fuse.auditclient.api.AuditClientService;
import com.eurodyn.qlack2.fuse.auditing.api.AuditLoggingService;
import com.eurodyn.qlack2.fuse.auditing.api.dto.AuditLogDTO;
import com.eurodyn.qlack2.fuse.auditing.api.dto.SearchDTO;
import com.eurodyn.qlack2.fuse.auditing.api.dto.SortDTO;
import com.eurodyn.qlack2.fuse.auditing.api.enums.AuditLogColumns;
import com.eurodyn.qlack2.fuse.auditing.api.enums.SearchOperator;
import com.eurodyn.qlack2.fuse.auditing.api.enums.SortOperator;
import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicket;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.fuse.workflow.runtime.api.WorkflowRuntimeService;
import com.eurodyn.qlack2.fuse.workflow.runtime.api.dto.ProcessInstanceDesc;

public class RuntimeServiceImpl implements RuntimeService{
	
	private static final Logger LOGGER = Logger.getLogger(RuntimeServiceImpl.class.getName());
	private EntityManager em;
	private IDMService idmService;
	private SecurityUtil securityUtil;
	private WorkflowRuntimeService workflowRuntimeService;
	private ConverterUtil converterUtil;
	private RuntimeUtil runtimeUtil;
	private AuditClientService auditClientService;
	private AuditLoggingService auditLoggingService;
	
	private List<RulesRuntimeManagementService> rulesRuntimeManagementServiceList;

	public void setEm(EntityManager em) {
		this.em = em;
	}
	
	public void setIdmService(IDMService idmService) {
		this.idmService = idmService;
	}
	
	public void setSecurityUtil(SecurityUtil securityUtil) {
		this.securityUtil = securityUtil;
	}
	
	public void setWorkflowRuntimeService(WorkflowRuntimeService workflowRuntimeService) {
		this.workflowRuntimeService = workflowRuntimeService;
	}
	
	public void setRuntimeUtil(RuntimeUtil runtimeUtil) {
		this.runtimeUtil = runtimeUtil;
	}
	
	public void setConverterUtil(ConverterUtil converterUtil) {
		this.converterUtil = converterUtil;
	}
	
	public void setRulesRuntimeManagementServiceList(
			List<RulesRuntimeManagementService> rulesRuntimeManagementServiceList) {
		this.rulesRuntimeManagementServiceList = rulesRuntimeManagementServiceList;
	}
	
	public void setAuditClientService(AuditClientService auditClientService) {
		this.auditClientService = auditClientService;
	}
	
	public void setAuditLoggingService(AuditLoggingService auditLoggingService) {
		this.auditLoggingService = auditLoggingService;
	}
			
	@ValidateTicket
	@Override
	public Long startWorkflowInstance(WorkflowInstanceRequest request) {
		LOGGER.log(Level.FINE, "Starting workflow instance for version with ID {0}", request.getId());
		
		WorkflowVersion version = WorkflowVersion.find(em, request.getId());

		securityUtil.checkWorkflowOperation(request.getSignedTicket(),
				WorkflowConstants.OP_WFL_EXECUTE_RUNTIME, version.getWorkflow().getId(), version.getWorkflow().getProjectId());
		
		if (version.getConditions()!=null && 
				version.getConditions().size() > 0)
		{
			// Execute precondition rules to decide if the workflow should be executed or not
			boolean validPreconditions = executePreconditionRules(version, request.getFacts(), request.getSignedTicket());
			
			if (!validPreconditions)
				throw new QInvalidPreconditionsException("Preconditions are not met. Workflow cannot be executed.");
		}
		
		Long processInstanceId = null;
	
		if (version.getProcessId() != null && version.getContent().length() > 0)
			processInstanceId = workflowRuntimeService.startWorkflowInstance(version.getProcessId(), version.getContent(), request.getParameters());
		else
			throw new QInvalidActionException("Either the BPMN content is null or it does not contain the process id."); 
		
		AuditWorkflowInstanceDTO auditWorkflowInstanceDTO = converterUtil
				.workflowVersionToAuditWorkflowInstanceDTO(version, processInstanceId);
		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(), EVENT.START.toString(), 
				GROUP.WORKFLOW_VERSION_INSTANCE.toString(),
				null, request.getSignedTicket().getUserID(),
				auditWorkflowInstanceDTO);
		
		return processInstanceId;
	}
	
	@ValidateTicket
	@Override
	public void stopWorkflowInstance(WorkflowInstanceActionRequest request) {
		LOGGER.log(Level.FINE, "Stopping workflow instance ID {0}", request.getProcessInstanceId());
		WorkflowVersion version = WorkflowVersion.find(em, request.getId());

		securityUtil.checkWorkflowOperation(request.getSignedTicket(),
				WorkflowConstants.OP_WFL_EXECUTE_RUNTIME, version.getWorkflow().getId(), version.getWorkflow().getProjectId());
		
		workflowRuntimeService.stopWorkflowInstance(request.getProcessInstanceId());
		
		AuditWorkflowInstanceDTO auditWorkflowInstanceDTO = converterUtil
				.workflowVersionToAuditWorkflowInstanceDTO(version, request.getProcessInstanceId());
		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(), EVENT.STOP.toString(), 
				GROUP.WORKFLOW_VERSION_INSTANCE.toString(),
				null, request.getSignedTicket().getUserID(),
				auditWorkflowInstanceDTO);
	}
	
	@ValidateTicket
	@Override
	public void deleteWorkflowInstance(WorkflowInstanceActionRequest request) {
		LOGGER.log(Level.FINE, "Deleting workflow instance ID {0}", request.getProcessInstanceId());
		WorkflowVersion version = WorkflowVersion.find(em, request.getId());

		securityUtil.checkWorkflowOperation(request.getSignedTicket(),
				WorkflowConstants.OP_WFL_EXECUTE_RUNTIME, version.getWorkflow().getId(), version.getWorkflow().getProjectId());
		
		workflowRuntimeService.deleteWorkflowInstance(request.getProcessInstanceId());
		
		AuditWorkflowInstanceDTO auditWorkflowInstanceDTO = converterUtil
				.workflowVersionToAuditWorkflowInstanceDTO(version, request.getProcessInstanceId());
		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(), EVENT.DELETE.toString(), 
				GROUP.WORKFLOW_VERSION_INSTANCE.toString(),
				null, request.getSignedTicket().getUserID(),
				auditWorkflowInstanceDTO);
	}
	
	@ValidateTicket
	@Override
	public void suspendWorkflowInstance(WorkflowInstanceActionRequest request) {
		LOGGER.log(Level.FINE, "Suspending workflow instance ID {0}", request.getProcessInstanceId());
		WorkflowVersion version = WorkflowVersion.find(em, request.getId());

		securityUtil.checkWorkflowOperation(request.getSignedTicket(),
				WorkflowConstants.OP_WFL_EXECUTE_RUNTIME, version.getWorkflow().getId(), version.getWorkflow().getProjectId());
		
		workflowRuntimeService.suspendWorkflowInstance(request.getProcessInstanceId());
		
		AuditWorkflowInstanceDTO auditWorkflowInstanceDTO = converterUtil
				.workflowVersionToAuditWorkflowInstanceDTO(version, request.getProcessInstanceId());
		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(), EVENT.PAUSE.toString(), 
				GROUP.WORKFLOW_VERSION_INSTANCE.toString(),
				null, request.getSignedTicket().getUserID(),
				auditWorkflowInstanceDTO);
	}
	
	@ValidateTicket
	@Override
	public void resumeWorkflowInstance(WorkflowInstanceActionRequest request) {
		LOGGER.log(Level.FINE, "Resuming workflow instance ID {0}", request.getProcessInstanceId());
		WorkflowVersion version = WorkflowVersion.find(em, request.getId());

		securityUtil.checkWorkflowOperation(request.getSignedTicket(),
				WorkflowConstants.OP_WFL_EXECUTE_RUNTIME, version.getWorkflow().getId(), version.getWorkflow().getProjectId());
		
		workflowRuntimeService.resumeWorkflowInstance(request.getProcessInstanceId());
		
		AuditWorkflowInstanceDTO auditWorkflowInstanceDTO = converterUtil
				.workflowVersionToAuditWorkflowInstanceDTO(version, request.getProcessInstanceId());
		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(), EVENT.RESUME.toString(), 
				GROUP.WORKFLOW_VERSION_INSTANCE.toString(),
				null, request.getSignedTicket().getUserID(),
				auditWorkflowInstanceDTO);
	}
	
	@ValidateTicket
	@Override
	public Object getVariableInstance(WorkflowVariableInstanceRequest request) {
		LOGGER.log(Level.FINE, "Getting variable instance for ID {0}", request.getProcessInstanceId());
		WorkflowVersion version = WorkflowVersion.find(em, request.getId());

		securityUtil.checkWorkflowOperation(request.getSignedTicket(),
				WorkflowConstants.OP_WFL_EXECUTE_RUNTIME, version.getWorkflow().getId(), version.getWorkflow().getProjectId());
		
		Object instanceVariable = workflowRuntimeService.getVariableInstance(request.getProcessInstanceId(), request.getVariableName());
		
		AuditWorkflowInstanceDTO auditWorkflowInstanceDTO = converterUtil
				.workflowVersionToAuditWorkflowInstanceDTO(version, request.getProcessInstanceId());
		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(), EVENT.VIEW.toString(), 
				GROUP.WORKFLOW_VERSION_INSTANCE.toString(),
				null, request.getSignedTicket().getUserID(),
				auditWorkflowInstanceDTO);
		
		return instanceVariable;
	}
	
	@ValidateTicket
	@Override
	public void setVariableInstance(WorkflowVariableInstanceRequest request) {
		LOGGER.log(Level.FINE, "setting variable instance for ID {0}", request.getProcessInstanceId());
		WorkflowVersion version = WorkflowVersion.find(em, request.getId());

		securityUtil.checkWorkflowOperation(request.getSignedTicket(),
				WorkflowConstants.OP_WFL_EXECUTE_RUNTIME, version.getWorkflow().getId(), version.getWorkflow().getProjectId());
		
		workflowRuntimeService.setVariableInstance(request.getProcessInstanceId(), request.getVariableName(), request.getVariableData());
		
		AuditWorkflowInstanceDTO auditWorkflowInstanceDTO = converterUtil
				.workflowVersionToAuditWorkflowInstanceDTO(version, request.getProcessInstanceId());
		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(), EVENT.VIEW.toString(), 
				GROUP.WORKFLOW_VERSION_INSTANCE.toString(),
				null, request.getSignedTicket().getUserID(),
				auditWorkflowInstanceDTO);
	}
	
	
	@ValidateTicket
	@Override
	public List<WorkflowInstanceDTO> getWorkflowInstances(GetWorkflowInstancesRequest req)
	{
		LOGGER.log(Level.FINE, "Getting workflow instances for project ID {0}", req.getProjectId());
		List<WorkflowInstanceDTO> workflowInstanceDTOs = new ArrayList<>();
		List<Workflow> myWorkflows = Workflow.findByProjectId(em, req.getProjectId()); 
		for (Workflow workflow : myWorkflows)
		{
			securityUtil.checkWorkflowOperation(req.getSignedTicket(),
					WorkflowConstants.OP_WFL_VIEW_RUNTIME, workflow.getId(), workflow.getProjectId());
			
			for (WorkflowVersion version : workflow.getWorkflowVersions())
			{
				LOGGER.log(Level.FINE, "WorkflowName: " + workflow.getName() + ", VersionName: " + version.getName() + ", ProcessId: " + version.getProcessId());
				if (version.getProcessId() != null)
				{
					List <ProcessInstanceDesc> processInstances = workflowRuntimeService.getProcessInstancesByProcessId(version.getProcessId());
					for (ProcessInstanceDesc instanceLog : processInstances)
					{
						workflowInstanceDTOs.add(converterUtil
								.processInstanceDescToWorkflowInstanceDTO(instanceLog, workflow, version));
						
						AuditWorkflowInstanceDTO auditWorkflowInstanceDTO = converterUtil
								.workflowVersionToAuditWorkflowInstanceDTO(version, instanceLog.getProcessInstanceId());
						auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(), EVENT.VIEW.toString(), 
								GROUP.WORKFLOW_VERSION_INSTANCE.toString(),
								null, req.getSignedTicket().getUserID(),
								auditWorkflowInstanceDTO);
					}
				}
			}
		}
		
		return workflowInstanceDTOs;
	}
	
	@ValidateTicket
	@Override
	public WorkflowInstanceDTO getWorkflowInstanceDetails(GetWorkflowInstanceDetailsRequest req)
	{
		LOGGER.log(Level.FINE, "Getting workflow instance details for instance ID {0}", req.getInstanceId());
		ProcessInstanceDesc processInstance = workflowRuntimeService.getProcessInstanceDetails(req.getInstanceId());
		WorkflowVersion version = WorkflowVersion.findByProcessId(em,  processInstance.getProcessId());
		WorkflowInstanceDTO workflowInstanceDTO = converterUtil
				.processInstanceDescToWorkflowInstanceDTO(processInstance, version.getWorkflow(), version);
		return workflowInstanceDTO;
	}
	
	@ValidateTicket
	@Override
	public void deleteWorkflowInstancesForWorkflow(DeleteWorkflowInstancesRequest req)
	{
		LOGGER.log(Level.FINE, "Getting workflow instances for workflow ID {0}", req.getWorkflowId());
		Workflow workflow = Workflow.find(em, req.getWorkflowId()); 

		securityUtil.checkWorkflowOperation(req.getSignedTicket(),
			WorkflowConstants.OP_WFL_EXECUTE_RUNTIME, workflow.getId(), workflow.getProjectId());
			
		runtimeUtil.deleteWorkflowInstancesForWorkflow(workflow, req.getSignedTicket().getUserID());
	}
	
	@ValidateTicket
	@Override
	public TaskSummaryDTO getTaskDetails(WorkflowTaskActionRequest request)
	{
		LOGGER.log(Level.FINE, "Getting task details for task ID {0}", request.getTaskId());
		WorkflowVersion version = WorkflowVersion.find(em, request.getId());

		securityUtil.checkWorkflowOperation(request.getSignedTicket(),
				WorkflowConstants.OP_WFL_VIEW_RUNTIME, version.getWorkflow().getId(), version.getWorkflow().getProjectId());
		
		return converterUtil.
				taskSummaryToTaskSummaryDTO(workflowRuntimeService.
						getTaskDetails(request.getProcessInstanceId(), request.getTaskId()));
	}
	
	@ValidateTicket
	@Override
	public List<TaskSummaryDTO> getTasksAssignedAsPotentialOwner(WorkflowTaskActionRequest request)
	{
		LOGGER.log(Level.FINE, "Getting Tasks Assigned As Potential Owner for User ID {0}", request.getUserId());
		if (request.getId() != null)
		{		
			WorkflowVersion version = WorkflowVersion.find(em, request.getId());

			securityUtil.checkViewRuntimeWorkflowOperation(request.getSignedTicket(), version.getWorkflow().getId());
		
			return converterUtil.
					taskSummaryToTaskSummaryDTOList(workflowRuntimeService.
							getTasksAssignedAsPotentialOwner(request.getProcessInstanceId(), request.getUserId(), request.getStatusList()));
		}
		else if (request.getProjectId() != null)
		{
			securityUtil.checkViewRuntimeWorkflowOperation(request.getSignedTicket(), request.getProjectId());
		
			return converterUtil.
					taskSummaryToTaskSummaryDTOList(workflowRuntimeService.
							getAllTasksAssignedAsPotentialOwner(request.getUserId(), request.getStatusList()));
		}
		return null;
	}
	
	@ValidateTicket
	@Override
	public void acceptTask(WorkflowTaskActionRequest request)
	{
		LOGGER.log(Level.FINE, "Accepting task ID {0}", request.getTaskId());
		WorkflowVersion version = WorkflowVersion.find(em, request.getId());

		securityUtil.checkWorkflowOperation(request.getSignedTicket(),
				WorkflowConstants.OP_WFL_EXECUTE_RUNTIME, version.getWorkflow().getId(), version.getWorkflow().getProjectId());
		
		workflowRuntimeService.acceptTask(request.getProcessInstanceId(), request.getTaskId(), request.getUserId());
	}
	
	@ValidateTicket
	@Override
	public void startTask(WorkflowTaskActionRequest request)
	{
		LOGGER.log(Level.FINE, "Starting task ID {0}", request.getTaskId());
		WorkflowVersion version = WorkflowVersion.find(em, request.getId());

		securityUtil.checkWorkflowOperation(request.getSignedTicket(),
				WorkflowConstants.OP_WFL_EXECUTE_RUNTIME, version.getWorkflow().getId(), version.getWorkflow().getProjectId());
		
		workflowRuntimeService.startTask(request.getProcessInstanceId(), request.getTaskId(), request.getUserId());
	}
	
	@ValidateTicket
	@Override
	public void completeTask(WorkflowTaskActionRequest request)
	{
		LOGGER.log(Level.FINE, "Completing task ID {0}", request.getTaskId());
		WorkflowVersion version = WorkflowVersion.find(em, request.getId());

		securityUtil.checkWorkflowOperation(request.getSignedTicket(),
				WorkflowConstants.OP_WFL_EXECUTE_RUNTIME, version.getWorkflow().getId(), version.getWorkflow().getProjectId());
		
		workflowRuntimeService.completeTask(request.getProcessInstanceId(), request.getTaskId(), request.getUserId(), request.getData());
	}
	
	@ValidateTicket
	@Override
	public List<Long> getTasksByProcessInstanceId(WorkflowInstanceActionRequest request)
	{
		LOGGER.log(Level.FINE, "Getting tasks for process instance task ID {0}", request.getProcessInstanceId());
		WorkflowVersion version = WorkflowVersion.find(em, request.getId());

		securityUtil.checkWorkflowOperation(request.getSignedTicket(),
				WorkflowConstants.OP_WFL_VIEW_RUNTIME, version.getWorkflow().getId(), version.getWorkflow().getProjectId());
		
		return workflowRuntimeService.getTasksByProcessInstanceId(request.getProcessInstanceId());
	}
	
	@ValidateTicket
	@Override
	public List<WorkflowRuntimeErrorLogDTO> getWorkflowErrorAuditLogs(GetWorkflowRuntimeErrorLogRequest req)
	{
		LOGGER.log(Level.FINE, "Getting workflow error logs for project ID {0}", req.getProjectId());
		
		List<WorkflowRuntimeErrorLogDTO> workflowLogDTOs = new ArrayList<>();
		
		List<Workflow> myWorkflows = Workflow.findByProjectId(em, req.getProjectId()); 
		
		for (Workflow workflow : myWorkflows)
		{
			securityUtil.checkWorkflowOperation(req.getSignedTicket(),
					WorkflowConstants.OP_WFL_VIEW_RUNTIME, workflow.getId(), workflow.getProjectId());
			
			for (WorkflowVersion version : workflow.getWorkflowVersions())
			{
				LOGGER.log(Level.FINE, "WorkflowName: " + workflow.getName() + ", VersionName: " + version.getName() + ", ProcessId: " + version.getProcessId());
				if (version.getProcessId() != null)
				{
					List<SearchDTO> searchList = new ArrayList();
					List<SortDTO> sortList = new ArrayList();
					
					SortDTO sort1 = new SortDTO();
					sort1.setColumn(AuditLogColumns.createdOn);
					sort1.setOperator(SortOperator.DESC);
					sortList.add(sort1);
					
					SearchDTO search1 = new SearchDTO();
					search1.setColumn(AuditLogColumns.groupName);
					search1.setOperator(SearchOperator.EQUAL);
					List <String> values1 = new ArrayList();
					values1.add(AuditConstants.RUNTIME_GROUP.RUNTIME_WORKFLOW.toString());
					search1.setValue(values1);
					searchList.add(search1);
					
					SearchDTO search2 = new SearchDTO();
					search2.setColumn(AuditLogColumns.shortDescription);
					search2.setOperator(SearchOperator.EQUAL);
					List <String> values2 = new ArrayList();
					values2.add(version.getProcessId());
					search2.setValue(values2);
					searchList.add(search2);
					
					List<AuditLogDTO> auditLogs = auditLoggingService.listAuditLogs(searchList, null, null, sortList, null);
					for (AuditLogDTO auditLog: auditLogs)
						workflowLogDTOs.add(converterUtil
								.auditLogDTOToWorkflowRuntimeErrorLogDTO(auditLog, workflow, version));								
				}
			}
		}	
		return workflowLogDTOs;
	}
	
	private boolean executePreconditionRules(WorkflowVersion version, List<byte[]> facts, SignedTicket signedTicket) {
		boolean retVal = false;

		if (rulesRuntimeManagementServiceList.size() == 0) {
			throw new QInvalidActionException(
					"RulesRuntimeManagementService not available");
		}

		List<Condition> conditions = Condition.getConditionsWithoutParent(em, version.getId(), ConditionType.PRECONDITION);
		List<Condition> preconditions = new ArrayList<>();
		sortConditions(conditions, preconditions);

		List<WorkingSetRuleVersionPair> pairs = new ArrayList<>();
		if (preconditions != null) {
			for (Condition condition : preconditions) {
				WorkingSetRuleVersionPair pair = new WorkingSetRuleVersionPair();
				pair.setWorkingSetVersionId(condition.getWorkingSetId());
				pair.setRuleVersionId(condition.getRuleId());
				pairs.add(pair);
			}

			StatelessMultiExecuteRequest statelessMultiExecuteRequest = new StatelessMultiExecuteRequest();
			statelessMultiExecuteRequest.setPairs(pairs);
			statelessMultiExecuteRequest.setFacts(facts);
			statelessMultiExecuteRequest.setSignedTicket(signedTicket);

			List<byte[]> results = rulesRuntimeManagementServiceList.get(0)
					.statelessMultiExecute(statelessMultiExecuteRequest).getFacts();

			if (results != null && !results.isEmpty()) {
				byte[] preconditionFactBytes = results.get(0);
				Object object = deserializeObject(preconditionFactBytes);

				PreconditionFact preconditionFact = (PreconditionFact) object;

				retVal = preconditionFact.isValid();
			} else {
				throw new QInvalidActionException(
						"Output facts cannot be null or empty when executing preconditions");
			}

		}
		return retVal;
	}
	
	private Object deserializeObject(byte[] bytes) {
		// XXX check stream close (inner/outer stream, success/fail path)
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);

			Object object = ois.readObject();

			ois.close();

			return object;
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error while deserializing output fact", e);
			throw new QInvalidActionException(e);
		} catch (ClassNotFoundException e) {
			LOGGER.log(Level.SEVERE, "Error while deserializing output fact", e);
			throw new QInvalidActionException(e);
		}
	}
	
	private void sortConditions(List<Condition> conditions,
			List<Condition> sortedConditions) {
		if (conditions != null) {
			for (Condition condition : conditions) {
				sortedConditions.add(condition);

				if (!condition.getChildren().isEmpty()) {
					sortConditions(condition.getChildren(), sortedConditions);
				}
			}
		}
	}
}
