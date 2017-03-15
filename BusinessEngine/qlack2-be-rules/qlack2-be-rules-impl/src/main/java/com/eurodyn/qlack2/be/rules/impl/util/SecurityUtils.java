package com.eurodyn.qlack2.be.rules.impl.util;

import com.eurodyn.qlack2.be.rules.impl.model.DataModel;
import com.eurodyn.qlack2.be.rules.impl.model.Library;
import com.eurodyn.qlack2.be.rules.impl.model.Rule;
import com.eurodyn.qlack2.be.rules.impl.model.WorkingSet;
import com.eurodyn.qlack2.fuse.idm.api.exception.QAuthorisationException;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.webdesktop.api.SecurityService;
import com.eurodyn.qlack2.webdesktop.api.request.security.IsPermittedRequest;

public class SecurityUtils {

	private SecurityService securityService;

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	private void require(SignedTicket ticket, String op, String resourceObjectId) {
		String ticketId = ticket.getTicketID();
		String userId = ticket.getUserID();

		Boolean isPermitted = securityService.isPermitted(
				new IsPermittedRequest(ticket, op, resourceObjectId));
		if (isPermitted == null || isPermitted == false) {
			throw new QAuthorisationException(userId, ticketId, op, resourceObjectId);
		}
	}

	// -- Categories

	public void checkCanManageCategory(SignedTicket ticket, String projectId) {
		require(ticket, RuleConstants.OP_RUL_MANAGE_CATEGORY, projectId);
	}

	// -- Runtime (Working Sets)

	public void checkCanExecuteWorkingSet(SignedTicket ticket, WorkingSet resource) throws QAuthorisationException {
		String ticketId = ticket.getTicketID();
		String userId = ticket.getUserID();

		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canExecuteResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_EXECUTE_WORKING_SET, resourceId));
		if (canExecuteResource != null) {
			if (!canExecuteResource) {
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_EXECUTE_WORKING_SET, resourceId);
			}
		} else {
			Boolean canExecuteProject = securityService.isPermitted(
					new IsPermittedRequest(ticket, RuleConstants.OP_RUL_EXECUTE_WORKING_SET, projectId));
			if (canExecuteProject == null || !canExecuteProject) {
				// throw exception for resource which has precedence, not for project
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_EXECUTE_WORKING_SET, resourceId);
			}
		}
	}

	// -- Working Sets

	public boolean canViewWorkingSet(SignedTicket ticket, WorkingSet resource) {
		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canViewResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_VIEW_WORKING_SET, resourceId));
		if (canViewResource != null && canViewResource) {
			return true;
		}

		Boolean canViewProject = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_VIEW_WORKING_SET, projectId));
		if (canViewProject != null && canViewProject) {
			return true;
		}

		return false;
	}

	public void checkCanViewWorkingSet(SignedTicket ticket, WorkingSet resource) throws QAuthorisationException {
		String ticketId = ticket.getTicketID();
		String userId = ticket.getUserID();

		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canViewResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_VIEW_WORKING_SET, resourceId));
		if (canViewResource != null) {
			if (!canViewResource) {
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_VIEW_WORKING_SET, resourceId);
			}
		} else {
			Boolean canViewProject = securityService.isPermitted(
					new IsPermittedRequest(ticket, RuleConstants.OP_RUL_VIEW_WORKING_SET, projectId));
			if (canViewProject == null || !canViewProject) {
				// throw exception for resource which has precedence, not for project
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_VIEW_WORKING_SET, resourceId);
			}
		}
	}

	public void checkCanCreateWorkingSet(SignedTicket ticket, String projectId) {
		require(ticket, RuleConstants.OP_RUL_VIEW_WORKING_SET, projectId);
		require(ticket, RuleConstants.OP_RUL_MANAGE_WORKING_SET, projectId);
	}

	public void checkCanUpdateWorkingSet(SignedTicket ticket, WorkingSet resource) throws QAuthorisationException {
		String ticketId = ticket.getTicketID();
		String userId = ticket.getUserID();

		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canManageResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_MANAGE_WORKING_SET, resourceId));
		if (canManageResource != null) {
			if (!canManageResource) {
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_MANAGE_WORKING_SET, resourceId);
			}
		} else {
			Boolean canManageProject = securityService.isPermitted(
					new IsPermittedRequest(ticket, RuleConstants.OP_RUL_MANAGE_WORKING_SET, projectId));
			if (canManageProject == null || !canManageProject) {
				// throw exception for resource which has precedence, not for project
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_MANAGE_WORKING_SET, resourceId);
			}
		}
	}

	public void checkCanLockWorkingSetVersion(SignedTicket ticket, WorkingSet resource) throws QAuthorisationException {
		String ticketId = ticket.getTicketID();
		String userId = ticket.getUserID();

		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canLockResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_LOCK_WORKING_SET, resourceId));
		if (canLockResource != null) {
			if (!canLockResource) {
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_LOCK_WORKING_SET, resourceId);
			}
		} else {
			Boolean canLockProject = securityService.isPermitted(
					new IsPermittedRequest(ticket, RuleConstants.OP_RUL_LOCK_WORKING_SET, projectId));
			if (canLockProject == null || !canLockProject) {
				// throw exception for resource which has precedence, not for project
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_LOCK_WORKING_SET, resourceId);
			}
		}
	}

	public boolean canUnlockAnyWorkingSetVersion(SignedTicket ticket, WorkingSet resource) {
		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canUnlockAnyResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_UNLOCK_ANY_WORKING_SET, resourceId));
		if (canUnlockAnyResource != null && canUnlockAnyResource) {
			return true;
		}

		Boolean canUnlockAnyProject = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_UNLOCK_ANY_WORKING_SET, projectId));
		if (canUnlockAnyProject != null && canUnlockAnyProject) {
			return true;
		}

		return false;
	}

	public void checkCanUnlockAnyWorkingSetVersion(SignedTicket ticket, WorkingSet resource) throws QAuthorisationException {
		String ticketId = ticket.getTicketID();
		String userId = ticket.getUserID();

		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canUnlockAnyResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_UNLOCK_ANY_WORKING_SET, resourceId));
		if (canUnlockAnyResource != null) {
			if (!canUnlockAnyResource) {
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_UNLOCK_ANY_WORKING_SET, resourceId);
			}
		} else {
			Boolean canUnlockAnyProject = securityService.isPermitted(
					new IsPermittedRequest(ticket, RuleConstants.OP_RUL_UNLOCK_ANY_WORKING_SET, projectId));
			if (canUnlockAnyProject == null || !canUnlockAnyProject) {
				// throw exception for resource which has precedence, not for project
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_UNLOCK_ANY_WORKING_SET, resourceId);
			}
		}
	}

	public void checkCanUnlockWorkingSetVersion(SignedTicket ticket, WorkingSet resource) throws QAuthorisationException {
		try {
			checkCanLockWorkingSetVersion(ticket, resource);
		} catch (QAuthorisationException lockEx) {
			try {
				checkCanUnlockAnyWorkingSetVersion(ticket, resource);
			} catch (QAuthorisationException unlockAnyEx) {
				// throw exception for lock, not for unlock_any
				throw lockEx;
			}
		}
	}

	// -- Rules

	public boolean canViewRule(SignedTicket ticket, Rule resource) {
		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canViewResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_VIEW_RULE, resourceId));
		if (canViewResource != null && canViewResource) {
			return true;
		}

		Boolean canViewProject = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_VIEW_RULE, projectId));
		if (canViewProject != null && canViewProject) {
			return true;
		}

		return false;
	}

	public void checkCanViewRule(SignedTicket ticket, Rule resource) throws QAuthorisationException {
		String ticketId = ticket.getTicketID();
		String userId = ticket.getUserID();

		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canViewResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_VIEW_RULE, resourceId));
		if (canViewResource != null) {
			if (!canViewResource) {
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_VIEW_RULE, resourceId);
			}
		} else {
			Boolean canViewProject = securityService.isPermitted(
					new IsPermittedRequest(ticket, RuleConstants.OP_RUL_VIEW_RULE, projectId));
			if (canViewProject == null || !canViewProject) {
				// throw exception for resource which has precedence, not for project
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_VIEW_RULE, resourceId);
			}
		}
	}

	public void checkCanCreateRule(SignedTicket ticket, String projectId) {
		require(ticket, RuleConstants.OP_RUL_VIEW_RULE, projectId);
		require(ticket, RuleConstants.OP_RUL_MANAGE_RULE, projectId);
	}

	public boolean canUpdateRule(SignedTicket ticket, Rule resource) {
		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canManageResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_MANAGE_RULE, resourceId));
		if (canManageResource != null && canManageResource) {
			return true;
		}

		Boolean canManageProject = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_MANAGE_RULE, projectId));
		if (canManageProject != null && canManageProject) {
			return true;
		}

		return false;
	}

	public void checkCanUpdateRule(SignedTicket ticket, Rule resource) throws QAuthorisationException {
		String ticketId = ticket.getTicketID();
		String userId = ticket.getUserID();

		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canManageResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_MANAGE_RULE, resourceId));
		if (canManageResource != null) {
			if (!canManageResource) {
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_MANAGE_RULE, resourceId);
			}
		} else {
			Boolean canManageProject = securityService.isPermitted(
					new IsPermittedRequest(ticket, RuleConstants.OP_RUL_MANAGE_RULE, projectId));
			if (canManageProject == null || !canManageProject) {
				// throw exception for resource which has precedence, not for project
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_MANAGE_RULE, resourceId);
			}
		}
	}

	public void checkCanLockRuleVersion(SignedTicket ticket, Rule resource) throws QAuthorisationException {
		String ticketId = ticket.getTicketID();
		String userId = ticket.getUserID();

		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canLockResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_LOCK_RULE, resourceId));
		if (canLockResource != null) {
			if (!canLockResource) {
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_LOCK_RULE, resourceId);
			}
		} else {
			Boolean canLockProject = securityService.isPermitted(
					new IsPermittedRequest(ticket, RuleConstants.OP_RUL_LOCK_RULE, projectId));
			if (canLockProject == null || !canLockProject) {
				// throw exception for resource which has precedence, not for project
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_LOCK_RULE, resourceId);
			}
		}
	}

	public boolean canUnlockAnyRuleVersion(SignedTicket ticket, Rule resource) {
		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canUnlockAnyResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_UNLOCK_ANY_RULE, resourceId));
		if (canUnlockAnyResource != null && canUnlockAnyResource) {
			return true;
		}

		Boolean canUnlockAnyProject = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_UNLOCK_ANY_RULE, projectId));
		if (canUnlockAnyProject != null && canUnlockAnyProject) {
			return true;
		}

		return false;
	}

	public void checkCanUnlockAnyRuleVersion(SignedTicket ticket, Rule resource) throws QAuthorisationException {
		String ticketId = ticket.getTicketID();
		String userId = ticket.getUserID();

		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canUnlockAnyResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_UNLOCK_ANY_RULE, resourceId));
		if (canUnlockAnyResource != null) {
			if (!canUnlockAnyResource) {
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_UNLOCK_ANY_RULE, resourceId);
			}
		} else {
			Boolean canUnlockAnyProject = securityService.isPermitted(
					new IsPermittedRequest(ticket, RuleConstants.OP_RUL_UNLOCK_ANY_RULE, projectId));
			if (canUnlockAnyProject == null || !canUnlockAnyProject) {
				// throw exception for resource which has precedence, not for project
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_UNLOCK_ANY_RULE, resourceId);
			}
		}
	}

	public void checkCanUnlockRuleVersion(SignedTicket ticket, Rule resource) throws QAuthorisationException {
		try {
			checkCanLockRuleVersion(ticket, resource);
		} catch (QAuthorisationException lockEx) {
			try {
				checkCanUnlockAnyRuleVersion(ticket, resource);
			} catch (QAuthorisationException unlockAnyEx) {
				// throw exception for lock, not for unlock_any
				throw lockEx;
			}
		}
	}

	// -- Data Models

	public boolean canViewDataModel(SignedTicket ticket, DataModel resource) {
		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canViewResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_VIEW_DATA_MODEL, resourceId));
		if (canViewResource != null && canViewResource) {
			return true;
		}

		Boolean canViewProject = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_VIEW_DATA_MODEL, projectId));
		if (canViewProject != null && canViewProject) {
			return true;
		}

		return false;
	}

	public void checkCanViewDataModel(SignedTicket ticket, DataModel resource) throws QAuthorisationException {
		String ticketId = ticket.getTicketID();
		String userId = ticket.getUserID();

		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canViewResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_VIEW_DATA_MODEL, resourceId));
		if (canViewResource != null) {
			if (!canViewResource) {
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_VIEW_DATA_MODEL, resourceId);
			}
		} else {
			Boolean canViewProject = securityService.isPermitted(
					new IsPermittedRequest(ticket, RuleConstants.OP_RUL_VIEW_DATA_MODEL, projectId));
			if (canViewProject == null || !canViewProject) {
				// throw exception for resource which has precedence, not for project
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_VIEW_DATA_MODEL, resourceId);
			}
		}
	}

	public void checkCanCreateDataModel(SignedTicket ticket, String projectId) {
		require(ticket, RuleConstants.OP_RUL_VIEW_DATA_MODEL, projectId);
		require(ticket, RuleConstants.OP_RUL_MANAGE_DATA_MODEL, projectId);
	}

	public boolean canUpdateDataModel(SignedTicket ticket, DataModel resource) {
		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canManageResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_MANAGE_DATA_MODEL, resourceId));
		if (canManageResource != null && canManageResource) {
			return true;
		}

		Boolean canManageProject = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_MANAGE_DATA_MODEL, projectId));
		if (canManageProject != null && canManageProject) {
			return true;
		}

		return false;
	}

	public void checkCanUpdateDataModel(SignedTicket ticket, DataModel resource) throws QAuthorisationException {
		String ticketId = ticket.getTicketID();
		String userId = ticket.getUserID();

		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canManageResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_MANAGE_DATA_MODEL, resourceId));
		if (canManageResource != null) {
			if (!canManageResource) {
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_MANAGE_DATA_MODEL, resourceId);
			}
		} else {
			Boolean canManageProject = securityService.isPermitted(
					new IsPermittedRequest(ticket, RuleConstants.OP_RUL_MANAGE_DATA_MODEL, projectId));
			if (canManageProject == null || !canManageProject) {
				// throw exception for resource which has precedence, not for project
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_MANAGE_DATA_MODEL, resourceId);
			}
		}
	}

	public void checkCanLockDataModelVersion(SignedTicket ticket, DataModel resource) throws QAuthorisationException {
		String ticketId = ticket.getTicketID();
		String userId = ticket.getUserID();

		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canLockResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_LOCK_DATA_MODEL, resourceId));
		if (canLockResource != null) {
			if (!canLockResource) {
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_LOCK_DATA_MODEL, resourceId);
			}
		} else {
			Boolean canLockProject = securityService.isPermitted(
					new IsPermittedRequest(ticket, RuleConstants.OP_RUL_LOCK_DATA_MODEL, projectId));
			if (canLockProject == null || !canLockProject) {
				// throw exception for resource which has precedence, not for project
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_LOCK_DATA_MODEL, resourceId);
			}
		}
	}

	public boolean canUnlockAnyDataModelVersion(SignedTicket ticket, DataModel resource) {
		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canUnlockAnyResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_UNLOCK_ANY_DATA_MODEL, resourceId));
		if (canUnlockAnyResource != null && canUnlockAnyResource) {
			return true;
		}

		Boolean canUnlockAnyProject = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_UNLOCK_ANY_DATA_MODEL, projectId));
		if (canUnlockAnyProject != null && canUnlockAnyProject) {
			return true;
		}

		return false;
	}

	public void checkCanUnlockAnyDataModelVersion(SignedTicket ticket, DataModel resource) throws QAuthorisationException {
		String ticketId = ticket.getTicketID();
		String userId = ticket.getUserID();

		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canUnlockAnyResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_UNLOCK_ANY_DATA_MODEL, resourceId));
		if (canUnlockAnyResource != null) {
			if (!canUnlockAnyResource) {
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_UNLOCK_ANY_DATA_MODEL, resourceId);
			}
		} else {
			Boolean canUnlockAnyProject = securityService.isPermitted(
					new IsPermittedRequest(ticket, RuleConstants.OP_RUL_UNLOCK_ANY_DATA_MODEL, projectId));
			if (canUnlockAnyProject == null || !canUnlockAnyProject) {
				// throw exception for resource which has precedence, not for project
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_UNLOCK_ANY_DATA_MODEL, resourceId);
			}
		}
	}

	public void checkCanUnlockDataModelVersion(SignedTicket ticket, DataModel resource) throws QAuthorisationException {
		try {
			checkCanLockDataModelVersion(ticket, resource);
		} catch (QAuthorisationException lockEx) {
			try {
				checkCanUnlockAnyDataModelVersion(ticket, resource);
			} catch (QAuthorisationException unlockAnyEx) {
				// throw exception for lock, not for unlock_any
				throw lockEx;
			}
		}
	}

	// -- Libraries

	public boolean canViewLibrary(SignedTicket ticket, Library resource) {
		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canViewResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_VIEW_LIBRARY, resourceId));
		if (canViewResource != null && canViewResource) {
			return true;
		}

		Boolean canViewProject = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_VIEW_LIBRARY, projectId));
		if (canViewProject != null && canViewProject) {
			return true;
		}

		return false;
	}

	public void checkCanViewLibrary(SignedTicket ticket, Library resource) throws QAuthorisationException {
		String ticketId = ticket.getTicketID();
		String userId = ticket.getUserID();

		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canViewResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_VIEW_LIBRARY, resourceId));
		if (canViewResource != null) {
			if (!canViewResource) {
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_VIEW_LIBRARY, resourceId);
			}
		} else {
			Boolean canViewProject = securityService.isPermitted(
					new IsPermittedRequest(ticket, RuleConstants.OP_RUL_VIEW_LIBRARY, projectId));
			if (canViewProject == null || !canViewProject) {
				// throw exception for resource which has precedence, not for project
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_VIEW_LIBRARY, resourceId);
			}
		}
	}

	public void checkCanCreateLibrary(SignedTicket ticket, String projectId) {
		require(ticket, RuleConstants.OP_RUL_VIEW_LIBRARY, projectId);
		require(ticket, RuleConstants.OP_RUL_MANAGE_LIBRARY, projectId);
	}

	public boolean canUpdateLibrary(SignedTicket ticket, Library resource) {
		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canManageResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_MANAGE_LIBRARY, resourceId));
		if (canManageResource != null && canManageResource) {
			return true;
		}

		Boolean canManageProject = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_MANAGE_LIBRARY, projectId));
		if (canManageProject != null && canManageProject) {
			return true;
		}

		return false;
	}

	public void checkCanUpdateLibrary(SignedTicket ticket, Library resource) throws QAuthorisationException {
		String ticketId = ticket.getTicketID();
		String userId = ticket.getUserID();

		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canManageResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_MANAGE_LIBRARY, resourceId));
		if (canManageResource != null) {
			if (!canManageResource) {
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_MANAGE_LIBRARY, resourceId);
			}
		} else {
			Boolean canManageProject = securityService.isPermitted(
					new IsPermittedRequest(ticket, RuleConstants.OP_RUL_MANAGE_LIBRARY, projectId));
			if (canManageProject == null || !canManageProject) {
				// throw exception for resource which has precedence, not for project
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_MANAGE_LIBRARY, resourceId);
			}
		}
	}

	public void checkCanLockLibraryVersion(SignedTicket ticket, Library resource) throws QAuthorisationException {
		String ticketId = ticket.getTicketID();
		String userId = ticket.getUserID();

		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canLockResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_LOCK_LIBRARY, resourceId));
		if (canLockResource != null) {
			if (!canLockResource) {
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_LOCK_LIBRARY, resourceId);
			}
		} else {
			Boolean canLockProject = securityService.isPermitted(
					new IsPermittedRequest(ticket, RuleConstants.OP_RUL_LOCK_LIBRARY, projectId));
			if (canLockProject == null || !canLockProject) {
				// throw exception for resource which has precedence, not for project
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_LOCK_LIBRARY, resourceId);
			}
		}
	}

	public boolean canUnlockAnyLibraryVersion(SignedTicket ticket, Library resource) {
		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canUnlockAnyResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_UNLOCK_ANY_LIBRARY, resourceId));
		if (canUnlockAnyResource != null && canUnlockAnyResource) {
			return true;
		}

		Boolean canUnlockAnyProject = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_UNLOCK_ANY_LIBRARY, projectId));
		if (canUnlockAnyProject != null && canUnlockAnyProject) {
			return true;
		}

		return false;
	}

	public void checkCanUnlockAnyLibraryVersion(SignedTicket ticket, Library resource) throws QAuthorisationException {
		String ticketId = ticket.getTicketID();
		String userId = ticket.getUserID();

		String resourceId = resource.getId();
		String projectId = resource.getProjectId();

		Boolean canUnlockAnyResource = securityService.isPermitted(
				new IsPermittedRequest(ticket, RuleConstants.OP_RUL_UNLOCK_ANY_LIBRARY, resourceId));
		if (canUnlockAnyResource != null) {
			if (!canUnlockAnyResource) {
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_UNLOCK_ANY_LIBRARY, resourceId);
			}
		} else {
			Boolean canUnlockAnyProject = securityService.isPermitted(
					new IsPermittedRequest(ticket, RuleConstants.OP_RUL_UNLOCK_ANY_LIBRARY, projectId));
			if (canUnlockAnyProject == null || !canUnlockAnyProject) {
				// throw exception for resource which has precedence, not for project
				throw new QAuthorisationException(userId, ticketId, RuleConstants.OP_RUL_UNLOCK_ANY_LIBRARY, resourceId);
			}
		}
	}

	public void checkCanUnlockLibraryVersion(SignedTicket ticket, Library resource) throws QAuthorisationException {
		try {
			checkCanLockLibraryVersion(ticket, resource);
		} catch (QAuthorisationException lockEx) {
			try {
				checkCanUnlockAnyLibraryVersion(ticket, resource);
			} catch (QAuthorisationException unlockAnyEx) {
				// throw exception for lock, not for unlock_any
				throw lockEx;
			}
		}
	}

}
