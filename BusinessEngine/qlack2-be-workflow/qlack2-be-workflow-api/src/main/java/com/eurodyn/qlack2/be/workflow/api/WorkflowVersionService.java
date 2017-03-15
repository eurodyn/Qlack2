package com.eurodyn.qlack2.be.workflow.api;

import java.util.List;

import com.eurodyn.qlack2.be.workflow.api.request.version.ExportWorkflowVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.ImportWorkflowVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.EnableTestingVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.dto.WorkflowVersionDTO;
import com.eurodyn.qlack2.be.workflow.api.request.EmptySignedRequest;
import com.eurodyn.qlack2.be.workflow.api.request.workflow.UpdateWorkflowRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.CountWorkflowVersionsLockedByOtherUserRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.CreateWorkflowVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.DeleteWorkflowVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.FinaliseWorkflowVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.GetWorkflowVersionIdByNameRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.GetWorkflowVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.GetWorkflowVersionsRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.LockWorkflowVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.UnlockWorkflowVersionRequest;
import com.eurodyn.qlack2.fuse.idm.api.exception.QAuthorisationException;
import com.eurodyn.qlack2.fuse.idm.api.exception.QInvalidTicketException;
import com.eurodyn.qlack2.be.workflow.api.exception.QInvalidDataException;
import com.eurodyn.qlack2.be.workflow.api.exception.QInvalidActionException;

public interface WorkflowVersionService {

	WorkflowVersionDTO getWorkflowVersion(GetWorkflowVersionRequest request) throws QInvalidTicketException, QAuthorisationException;
	
	List<WorkflowVersionDTO> getWorkflowVersions(GetWorkflowVersionsRequest request) throws QInvalidTicketException, QAuthorisationException;
	
	Long countWorkflowVersionsLockedByOtherUser(CountWorkflowVersionsLockedByOtherUserRequest request) throws QInvalidTicketException, QAuthorisationException;
	
	String createWorkflowVersion(CreateWorkflowVersionRequest request) throws QInvalidTicketException, QAuthorisationException, QInvalidDataException;
	
	String getWorkflowVersionIdByName(GetWorkflowVersionIdByNameRequest request) throws QInvalidTicketException, QAuthorisationException;
	
	void updateWorkflowVersion(UpdateWorkflowRequest request) throws QInvalidTicketException, QAuthorisationException, QInvalidDataException;

	void deleteWorkflowVersion(DeleteWorkflowVersionRequest request) throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;
	
	void lockWorkflowVersion(LockWorkflowVersionRequest request) throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	void unlockWorkflowVersion(UnlockWorkflowVersionRequest request) throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	void finaliseWorkflowVersion(FinaliseWorkflowVersionRequest request) throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;
	
	void enableTestingWorkflowVersion(EnableTestingVersionRequest request) throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;
	
	List<Integer> getConditionTypes(EmptySignedRequest request) throws QInvalidTicketException;
	
	String importWorkflowVersion(ImportWorkflowVersionRequest request) throws QInvalidTicketException;

	byte[] exportWorkflowVersion(ExportWorkflowVersionRequest request) throws QInvalidTicketException;
	
	boolean checkWorkflowVersionCanFinalise(FinaliseWorkflowVersionRequest request) throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;
}
