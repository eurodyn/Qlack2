package com.eurodyn.qlack2.be.workflow.api;

import java.util.List;

import com.eurodyn.qlack2.be.workflow.api.dto.WorkflowDTO;
import com.eurodyn.qlack2.be.workflow.api.request.workflow.CreateWorkflowRequest;
import com.eurodyn.qlack2.be.workflow.api.request.workflow.DeleteWorkflowRequest;
import com.eurodyn.qlack2.be.workflow.api.request.workflow.GetWorkflowByNameRequest;
import com.eurodyn.qlack2.be.workflow.api.request.workflow.GetWorkflowRequest;
import com.eurodyn.qlack2.be.workflow.api.request.workflow.GetWorkflowsRequest;
import com.eurodyn.qlack2.be.workflow.api.request.workflow.UpdateWorkflowRequest;
import com.eurodyn.qlack2.fuse.idm.api.exception.QAuthorisationException;
import com.eurodyn.qlack2.fuse.idm.api.exception.QInvalidTicketException;

public interface WorkflowService {
	
	List<WorkflowDTO> getWorkflows(GetWorkflowsRequest request) throws QInvalidTicketException, QAuthorisationException;

	WorkflowDTO getWorkflow(GetWorkflowRequest request) throws QInvalidTicketException, QAuthorisationException;
	
	WorkflowDTO getWorkflowByName(GetWorkflowByNameRequest request) throws QInvalidTicketException, QAuthorisationException;

	String createWorkflow(CreateWorkflowRequest request) throws QInvalidTicketException, QAuthorisationException;

	void updateWorkflow(UpdateWorkflowRequest request) throws QInvalidTicketException, QAuthorisationException;

	void deleteWorkflow(DeleteWorkflowRequest request) throws QInvalidTicketException, QAuthorisationException;

}
