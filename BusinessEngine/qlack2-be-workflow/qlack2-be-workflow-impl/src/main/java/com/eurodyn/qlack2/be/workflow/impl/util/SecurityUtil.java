package com.eurodyn.qlack2.be.workflow.impl.util;

import com.eurodyn.qlack2.fuse.idm.api.exception.QAuthorisationException;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.webdesktop.api.SecurityService;
import com.eurodyn.qlack2.webdesktop.api.request.security.IsPermittedRequest;

public class SecurityUtil {
	private SecurityService securityService;
	
	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public void checkWorkflowOperation(SignedTicket signedTicket, String operation, String workflowId, String projectId) {
		Boolean isPermitted = securityService.isPermitted(new IsPermittedRequest(
				signedTicket, operation, workflowId));
		if (isPermitted == null) {
			require(signedTicket, operation, projectId);
		} else if (!isPermitted) {
			throw new QAuthorisationException(signedTicket.getUserID(),
					signedTicket.toString(), operation, workflowId);
		}
	}

	public void checkCreateWorkflowOperation(SignedTicket signedTicket, String projectId) {
		require(signedTicket, 
				WorkflowConstants.OP_WFL_VIEW_WORKFLOW, projectId);

		require(signedTicket,
				WorkflowConstants.OP_WFL_MANAGE_WORKFLOW, projectId);
	}
	
	public void checkViewRuntimeWorkflowOperation(SignedTicket signedTicket, String id) {
		require(signedTicket,
				WorkflowConstants.OP_WFL_VIEW_RUNTIME, id);
	}
	
	public void checkManageRuntimeWorkflowOperation(SignedTicket signedTicket, String workflowId) {
		require(signedTicket,
				WorkflowConstants.OP_WFL_VIEW_RUNTIME, workflowId);

		require(signedTicket,
				WorkflowConstants.OP_WFL_EXECUTE_RUNTIME, workflowId);
	}

	public void checkUnlockWorkflowOperation(SignedTicket signedTicket, String workflowId, String projectId) {
	
		String userID = signedTicket.getUserID();

		Boolean isLockOnWorkflowPermitted = securityService.isPermitted(new IsPermittedRequest(signedTicket,
				WorkflowConstants.OP_WFL_LOCK_WORKFLOW, workflowId));

		if (isLockOnWorkflowPermitted == null || !isLockOnWorkflowPermitted) {
			// denied OP_WFL_LOCK_WORKFLOW for workflowId -> check
			// OP_WFL_UNLOCK_ANY_WORKFLOW for workflowId
			Boolean isUnLockAnyOnWorkflowPermitted = securityService.isPermitted(new IsPermittedRequest(
					signedTicket, WorkflowConstants.OP_WFL_UNLOCK_ANY_WORKFLOW, workflowId));

			if (isUnLockAnyOnWorkflowPermitted == null
					|| !isUnLockAnyOnWorkflowPermitted) {
				// denied OP_WFL_UNLOCK_ANY_WORKFLOW for workflowId -> check
				// OP_WFL_LOCK_WORKFLOW for projectId
				Boolean isLockOnProjectPermitted = securityService
						.isPermitted(new IsPermittedRequest(signedTicket, WorkflowConstants.OP_WFL_LOCK_WORKFLOW,
								projectId));

				if (isLockOnProjectPermitted == null
						|| !isLockOnProjectPermitted) {
					// denied OP_WFL_LOCK_WORKFLOW for projectId -> check
					// OP_WFL_UNLOCK_ANY_WORKFLOW for project
					Boolean isUnLockAnyOnProjectPermitted = securityService
							.isPermitted(new IsPermittedRequest(signedTicket,
									WorkflowConstants.OP_WFL_UNLOCK_ANY_WORKFLOW, projectId));

					if (isUnLockAnyOnProjectPermitted == null
							|| !isUnLockAnyOnProjectPermitted) {
						// denied OP_WFL_UNLOCK_ANY_WORKFLOW for projectId
						throw new QAuthorisationException(userID,
								signedTicket.toString(),
								WorkflowConstants.OP_WFL_LOCK_WORKFLOW, workflowId);
					}
				}
			}
		}
	}

	public void checkCategoryOperation(SignedTicket signedTicket, String operation, String projectId) {
		require(signedTicket, operation, projectId);
	}

	public void require(SignedTicket signedTicket,
			String operationName, String resourceObjectID) {
		Boolean isPermitted = securityService.isPermitted(new IsPermittedRequest(signedTicket,
				operationName, resourceObjectID));
		if ((isPermitted == null) || (!isPermitted)) {
			throw new QAuthorisationException(signedTicket.getUserID(), signedTicket.getTicketID(),
					operationName, resourceObjectID);
		}
	}

}
