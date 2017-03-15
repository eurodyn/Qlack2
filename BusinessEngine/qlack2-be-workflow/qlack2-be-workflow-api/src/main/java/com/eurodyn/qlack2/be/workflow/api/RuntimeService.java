package com.eurodyn.qlack2.be.workflow.api;

import java.util.List;
import java.util.Map;

import com.eurodyn.qlack2.be.workflow.api.dto.WorkflowInstanceDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.WorkflowRuntimeErrorLogDTO;
import com.eurodyn.qlack2.be.workflow.api.request.runtime.DeleteWorkflowInstancesRequest;
import com.eurodyn.qlack2.be.workflow.api.request.runtime.GetWorkflowInstanceDetailsRequest;
import com.eurodyn.qlack2.be.workflow.api.request.runtime.GetWorkflowRuntimeErrorLogRequest;
import com.eurodyn.qlack2.be.workflow.api.request.runtime.WorkflowInstanceRequest;
import com.eurodyn.qlack2.be.workflow.api.request.runtime.WorkflowTaskActionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.runtime.WorkflowInstanceActionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.runtime.GetWorkflowInstancesRequest;
import com.eurodyn.qlack2.be.workflow.api.request.runtime.WorkflowVariableInstanceRequest;
import com.eurodyn.qlack2.fuse.idm.api.exception.QAuthorisationException;
import com.eurodyn.qlack2.fuse.idm.api.exception.QInvalidTicketException;
import com.eurodyn.qlack2.be.workflow.api.dto.TaskSummaryDTO;
import com.eurodyn.qlack2.be.workflow.api.exception.QInvalidActionException;

public interface RuntimeService {
	
	Long startWorkflowInstance(WorkflowInstanceRequest request) throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;
	
	 Object getVariableInstance(WorkflowVariableInstanceRequest request) throws QInvalidTicketException, QAuthorisationException;
	
	List<WorkflowInstanceDTO> getWorkflowInstances(GetWorkflowInstancesRequest request) throws QInvalidTicketException, QAuthorisationException;
	
	void deleteWorkflowInstancesForWorkflow(DeleteWorkflowInstancesRequest request) throws QInvalidTicketException, QAuthorisationException;
	
	void stopWorkflowInstance(WorkflowInstanceActionRequest request) throws QInvalidTicketException, QAuthorisationException;
	
	void resumeWorkflowInstance(WorkflowInstanceActionRequest request) throws QInvalidTicketException, QAuthorisationException;
	
	void suspendWorkflowInstance(WorkflowInstanceActionRequest request) throws QInvalidTicketException, QAuthorisationException;
	
	void deleteWorkflowInstance(WorkflowInstanceActionRequest request) throws QInvalidTicketException, QAuthorisationException;
	
	TaskSummaryDTO getTaskDetails(WorkflowTaskActionRequest request) throws QInvalidTicketException, QAuthorisationException;
	
	List<TaskSummaryDTO> getTasksAssignedAsPotentialOwner(WorkflowTaskActionRequest request) throws QInvalidTicketException, QAuthorisationException;
	
	void acceptTask(WorkflowTaskActionRequest request) throws QInvalidTicketException, QAuthorisationException;
	
	void startTask(WorkflowTaskActionRequest request) throws QInvalidTicketException, QAuthorisationException;
	
	void completeTask(WorkflowTaskActionRequest request) throws QInvalidTicketException, QAuthorisationException;
	
	List<Long> getTasksByProcessInstanceId(WorkflowInstanceActionRequest request) throws QInvalidTicketException, QAuthorisationException;
	
	List<WorkflowRuntimeErrorLogDTO> getWorkflowErrorAuditLogs(GetWorkflowRuntimeErrorLogRequest request) throws QInvalidTicketException, QAuthorisationException;

	WorkflowInstanceDTO getWorkflowInstanceDetails(GetWorkflowInstanceDetailsRequest req);

	void setVariableInstance(WorkflowVariableInstanceRequest request);
}
