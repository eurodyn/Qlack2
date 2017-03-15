package com.eurodyn.qlack2.be.rules.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import com.eurodyn.qlack2.be.rules.api.ConfigService;
import com.eurodyn.qlack2.be.rules.api.exception.QInvalidOperationException;
import com.eurodyn.qlack2.be.rules.api.request.EmptyRequest;
import com.eurodyn.qlack2.be.rules.api.request.config.AddManagedSubjectRequest;
import com.eurodyn.qlack2.be.rules.api.request.config.GetManagedGroupsRequest;
import com.eurodyn.qlack2.be.rules.api.request.config.GetManagedUsersRequest;
import com.eurodyn.qlack2.be.rules.api.request.config.GetSecureOperationsRequest;
import com.eurodyn.qlack2.be.rules.api.request.config.RemoveManagedSubjectRequest;
import com.eurodyn.qlack2.be.rules.api.request.config.SaveSecureOperationsRequest;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditSecureOperationDTO;
import com.eurodyn.qlack2.be.rules.impl.model.DataModel;
import com.eurodyn.qlack2.be.rules.impl.model.Library;
import com.eurodyn.qlack2.be.rules.impl.model.Rule;
import com.eurodyn.qlack2.be.rules.impl.model.WorkingSet;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.LEVEL;
import com.eurodyn.qlack2.be.rules.impl.util.SecureOperation;
import com.eurodyn.qlack2.fuse.auditclient.api.AuditClientService;
import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicket;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.webdesktop.api.DesktopGroupService;
import com.eurodyn.qlack2.webdesktop.api.DesktopUserService;
import com.eurodyn.qlack2.webdesktop.api.SecurityService;
import com.eurodyn.qlack2.webdesktop.api.dto.SecureOperationAccessDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserGroupDTO;
import com.eurodyn.qlack2.webdesktop.api.request.security.AllowSecureOperationForGroupRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.AllowSecureOperationForUserRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.DenySecureOperationForGroupRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.DenySecureOperationForUserRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.GetAllowedGroupsForOperationRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.GetAllowedUsersForOperationRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.RemoveSecureOperationFromGroupRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.RemoveSecureOperationFromUserRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.RequirePermittedRequest;
import com.eurodyn.qlack2.webdesktop.api.request.user.GetUsersRequest;
import com.eurodyn.qlack2.webdesktop.api.request.user.IsUserRequest;

public class ConfigServiceImpl implements ConfigService {
	private static final Logger LOGGER = Logger.getLogger(ConfigServiceImpl.class.getName());

	@SuppressWarnings("unused")
	private IDMService idmService;
	private SecurityService security;
	private DesktopUserService desktopUserService;
	private DesktopGroupService desktopGroupService;
	private AuditClientService audit;
	private EntityManager em;

	public void setIdmService(IDMService idmService) {
		this.idmService = idmService;
	}

	public void setSecurity(SecurityService security) {
		this.security = security;
	}

	public void setDesktopUserService(DesktopUserService desktopUserService) {
		this.desktopUserService = desktopUserService;
	}

	public void setDesktopGroupService(DesktopGroupService desktopGroupService) {
		this.desktopGroupService = desktopGroupService;
	}

	public void setAudit(AuditClientService audit) {
		this.audit = audit;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	private boolean isUser(String subjectId, SignedTicket signedTicket) {
		IsUserRequest wdReq = new IsUserRequest(subjectId);
		return desktopUserService.isUser(wdReq);
	}

	// --

	@Override
	@ValidateTicket
	public List<SecureOperationAccessDTO> getSecureOperations(GetSecureOperationsRequest sreq) {
		SignedTicket ticket = sreq.getSignedTicket();
		security.requirePermitted(new RequirePermittedRequest(ticket, SecureOperation.RUL_CONFIGURE.toString()));

		List<SecureOperationAccessDTO> retVal = null;
		if (isUser(sreq.getSubjectId(), ticket)) {
			retVal = getSecureOperationsForUser(sreq);
		} else {
			retVal = getSecureOperationsForGroup(sreq);
		}

		AuditSecureOperationDTO auditDTO = new AuditSecureOperationDTO();
		auditDTO.setResourceId(sreq.getResourceId());
		auditDTO.setSubjectId(sreq.getSubjectId());
		auditDTO.setOperations(retVal);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.VIEW.toString(), GROUP.SECURE_OPERATIONS.toString(),
				null, ticket.getUserID(), auditDTO);

		return retVal;
	}

	private List<SecureOperationAccessDTO> getSecureOperationsForUser(GetSecureOperationsRequest sreq) {
		com.eurodyn.qlack2.webdesktop.api.request.security.GetSecureOperationsForUserRequest wdReq =
				new com.eurodyn.qlack2.webdesktop.api.request.security.GetSecureOperationsForUserRequest();
		wdReq.setSignedTicket(sreq.getSignedTicket());
		wdReq.setUserId(sreq.getSubjectId());
		wdReq.setResourceObjectId(sreq.getResourceId());
		wdReq.setOperations(getSecureOperationsBasedOnResource(sreq.getResourceId()));
		return security.getSecureOperationsForUser(wdReq);
	}

	private List<SecureOperationAccessDTO> getSecureOperationsForGroup(GetSecureOperationsRequest sreq) {
		com.eurodyn.qlack2.webdesktop.api.request.security.GetSecureOperationsForGroupRequest wdReq =
				new com.eurodyn.qlack2.webdesktop.api.request.security.GetSecureOperationsForGroupRequest();
		wdReq.setSignedTicket(sreq.getSignedTicket());
		wdReq.setGroupId(sreq.getSubjectId());
		wdReq.setResourceObjectId(sreq.getResourceId());
		wdReq.setOperations(getSecureOperationsBasedOnResource(sreq.getResourceId()));
		return security.getSecureOperationsForGroup(wdReq);
	}

	private List<String> getSecureOperationsBasedOnResource(String resourceId) {
		List<String> operations = new ArrayList<>();
		if (resourceId == null) {
			for (SecureOperation operation : SecureOperation.class.getEnumConstants()) {
				if (operation.isGeneric()) {
					operations.add(operation.toString());
				}
			}
		} else {
			boolean isRule = (Rule.findById(em, resourceId) != null);
			boolean isDataModel = (DataModel.findById(em, resourceId) != null);
			boolean isLibrary = (Library.findById(em, resourceId) != null);
			boolean isWorkingSet = (WorkingSet.findById(em, resourceId) != null);
			boolean isProject = !isRule && !isDataModel && !isLibrary && !isWorkingSet;

			for (SecureOperation operation : SecureOperation.class.getEnumConstants()) {
				if ((isRule && operation.isOnRule())
						|| (isDataModel && operation.isOnDataModel())
						|| (isLibrary && operation.isOnLibrary())
						|| (isWorkingSet && operation.isOnWorkingSet())
						|| (isProject && operation.isOnProject())) {
					operations.add(operation.toString());
				}
			}
		}
		return operations;
	}

	@Override
	@ValidateTicket
	public void saveSecureOperations(SaveSecureOperationsRequest sreq) {
		SignedTicket ticket = sreq.getSignedTicket();
		security.requirePermitted(new RequirePermittedRequest(ticket, SecureOperation.RUL_CONFIGURE.toString()));

		if (isUser(sreq.getSubjectId(), ticket)) {
			saveSecureOperationsForUser(sreq);
		} else {
			saveSecureOperationsForGroup(sreq);
		}

		AuditSecureOperationDTO auditDTO = new AuditSecureOperationDTO();
		auditDTO.setResourceId(sreq.getResourceId());
		auditDTO.setSubjectId(sreq.getSubjectId());
		auditDTO.setOperations(sreq.getOperations());
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.UPDATE.toString(), GROUP.SECURE_OPERATIONS.toString(),
				null, ticket.getUserID(), auditDTO);
	}

	private void saveSecureOperationsForUser(SaveSecureOperationsRequest sreq) {
		LOGGER.log(Level.FINE, "Saving secure operations for user with ID {0}", sreq.getSubjectId());

		for (SecureOperationAccessDTO access : sreq.getOperations()) {
			if (!SecureOperation.contains(access.getOperation())) {
				LOGGER.log(
						Level.SEVERE,
						"Operation {0} is not managed by the Forms Manager application",
						access.getOperation());
				throw new QInvalidOperationException("Operation "
						+ access.getOperation()
						+ " is not managed by the Forms Manager application");
			}
			if (access.getAccess() == null) {
				RemoveSecureOperationFromUserRequest wdReq = new RemoveSecureOperationFromUserRequest();
				wdReq.setSignedTicket(sreq.getSignedTicket());
				wdReq.setUserId(sreq.getSubjectId());
				wdReq.setOperationName(access.getOperation());
				wdReq.setResourceObjectId(sreq.getResourceId());
				security.removeSecureOperationFromUser(wdReq);
			} else if (access.getAccess()) {
				AllowSecureOperationForUserRequest wdReq = new AllowSecureOperationForUserRequest();
				wdReq.setSignedTicket(sreq.getSignedTicket());
				wdReq.setUserId(sreq.getSubjectId());
				wdReq.setOperationName(access.getOperation());
				wdReq.setResourceObjectId(sreq.getResourceId());
				security.allowSecureOperationForUser(wdReq);
			} else {
				DenySecureOperationForUserRequest wdReq = new DenySecureOperationForUserRequest();
				wdReq.setSignedTicket(sreq.getSignedTicket());
				wdReq.setUserId(sreq.getSubjectId());
				wdReq.setOperationName(access.getOperation());
				wdReq.setResourceObjectId(sreq.getResourceId());
				security.denySecureOperationForUser(wdReq);
			}
		}
	}

	private void saveSecureOperationsForGroup(SaveSecureOperationsRequest sreq) {
		LOGGER.log(Level.FINE, "Saving secure operations for group with ID {0}", sreq.getSubjectId());

		for (SecureOperationAccessDTO access : sreq.getOperations()) {
			if (!SecureOperation.contains(access.getOperation())) {
				LOGGER.log(
						Level.SEVERE,
						"Operation {0} is not managed by the Forms Manager application",
						access.getOperation());
				throw new QInvalidOperationException("Operation "
						+ access.getOperation()
						+ " is not managed by the Forms Manager application");
			}
			if (access.getAccess() == null) {
				RemoveSecureOperationFromGroupRequest wdReq = new RemoveSecureOperationFromGroupRequest();
				wdReq.setSignedTicket(sreq.getSignedTicket());
				wdReq.setGroupId(sreq.getSubjectId());
				wdReq.setOperationName(access.getOperation());
				wdReq.setResourceObjectId(sreq.getResourceId());
				security.removeSecureOperationFromGroup(wdReq);
			} else if (access.getAccess()) {
				AllowSecureOperationForGroupRequest wdReq = new AllowSecureOperationForGroupRequest();
				wdReq.setSignedTicket(sreq.getSignedTicket());
				wdReq.setGroupId(sreq.getSubjectId());
				wdReq.setOperationName(access.getOperation());
				wdReq.setResourceObjectId(sreq.getResourceId());
				security.allowSecureOperationForGroup(wdReq);
			} else {
				DenySecureOperationForGroupRequest wdReq = new DenySecureOperationForGroupRequest();
				wdReq.setSignedTicket(sreq.getSignedTicket());
				wdReq.setGroupId(sreq.getSubjectId());
				wdReq.setOperationName(access.getOperation());
				wdReq.setResourceObjectId(sreq.getResourceId());
				security.denySecureOperationForGroup(wdReq);
			}
		}
	}

	// --

	@Override
	@ValidateTicket
	public void addManagedSubject(AddManagedSubjectRequest sreq) {
		SignedTicket ticket = sreq.getSignedTicket();
		security.requirePermitted(new RequirePermittedRequest(ticket, SecureOperation.RUL_CONFIGURE.toString()));

		if (isUser(sreq.getSubjectId(), ticket)) {
			addManagedUser(sreq);
		} else {
			addManagedGroup(sreq);
		}
	}

	private void addManagedUser(AddManagedSubjectRequest sreq) {
		LOGGER.log(Level.INFO, "Adding security managed user with ID {0}", sreq.getSubjectId());
		AllowSecureOperationForUserRequest wdReq = new AllowSecureOperationForUserRequest();
		wdReq.setSignedTicket(sreq.getSignedTicket());
		wdReq.setUserId(sreq.getSubjectId());
		wdReq.setOperationName(SecureOperation.RUL_MANAGED.toString());
		wdReq.setResourceObjectId(sreq.getResourceId());
		security.allowSecureOperationForUser(wdReq);
	}

	private void addManagedGroup(AddManagedSubjectRequest sreq) {
		LOGGER.log(Level.INFO, "Adding security managed group with ID {0}", sreq.getSubjectId());
		AllowSecureOperationForGroupRequest wdReq = new AllowSecureOperationForGroupRequest();
		wdReq.setSignedTicket(sreq.getSignedTicket());
		wdReq.setGroupId(sreq.getSubjectId());
		wdReq.setOperationName(SecureOperation.RUL_MANAGED.toString());
		wdReq.setResourceObjectId(sreq.getResourceId());
		security.allowSecureOperationForGroup(wdReq);
	}

	@Override
	@ValidateTicket
	public void removeManagedSubject(RemoveManagedSubjectRequest sreq) {
		SignedTicket ticket = sreq.getSignedTicket();
		security.requirePermitted(new RequirePermittedRequest(ticket, SecureOperation.RUL_CONFIGURE.toString()));

		if (isUser(sreq.getSubjectId(), ticket)) {
			removeManagedUser(sreq);
		} else {
			removeManagedGroup(sreq);
		}
	}

	private void removeManagedUser(RemoveManagedSubjectRequest sreq) {
		LOGGER.log(Level.INFO, "Removing security managed user with ID {0}", sreq.getSubjectId());

		// First remove all managed operations from the user
		List<String> operations = getSecureOperationsBasedOnResource(sreq.getResourceId());
		AuditSecureOperationDTO auditDTO = new AuditSecureOperationDTO();
		auditDTO.setResourceId(sreq.getResourceId());
		auditDTO.setSubjectId(sreq.getSubjectId());
		auditDTO.setOperations(new ArrayList<SecureOperationAccessDTO>());
		for (String operation : operations) {
			RemoveSecureOperationFromUserRequest wdReq = new RemoveSecureOperationFromUserRequest();
			wdReq.setSignedTicket(sreq.getSignedTicket());
			wdReq.setUserId(sreq.getSubjectId());
			wdReq.setOperationName(operation);
			wdReq.setResourceObjectId(sreq.getResourceId());
			security.removeSecureOperationFromUser(wdReq);

			SecureOperationAccessDTO auditAccess = new SecureOperationAccessDTO(operation, false);
			auditDTO.getOperations().add(auditAccess);
		}

		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.UPDATE.toString(), GROUP.SECURE_OPERATIONS.toString(),
				null, sreq.getSignedTicket().getUserID(), auditDTO);

		// Before removing the "MANAGED" operation to make them unmanaged
		RemoveSecureOperationFromUserRequest wdReq = new RemoveSecureOperationFromUserRequest();
		wdReq.setSignedTicket(sreq.getSignedTicket());
		wdReq.setUserId(sreq.getSubjectId());
		wdReq.setOperationName(SecureOperation.RUL_MANAGED.toString());
		wdReq.setResourceObjectId(sreq.getResourceId());
		security.removeSecureOperationFromUser(wdReq);
	}

	private void removeManagedGroup(RemoveManagedSubjectRequest sreq) {
		LOGGER.log(Level.INFO, "Removing security managed group with ID {0}", sreq.getSubjectId());

		// First remove all managed operations from the groups
		List<String> operations = getSecureOperationsBasedOnResource(sreq.getResourceId());
		AuditSecureOperationDTO auditDTO = new AuditSecureOperationDTO();
		auditDTO.setResourceId(sreq.getResourceId());
		auditDTO.setSubjectId(sreq.getSubjectId());
		auditDTO.setOperations(new ArrayList<SecureOperationAccessDTO>());
		for (String operation : operations) {
			RemoveSecureOperationFromGroupRequest wdReq = new RemoveSecureOperationFromGroupRequest();
			wdReq.setSignedTicket(sreq.getSignedTicket());
			wdReq.setGroupId(sreq.getSubjectId());
			wdReq.setOperationName(operation);
			wdReq.setResourceObjectId(sreq.getResourceId());
			security.removeSecureOperationFromGroup(wdReq);

			SecureOperationAccessDTO auditAccess = new SecureOperationAccessDTO(operation, false);
			auditDTO.getOperations().add(auditAccess);
		}

		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.UPDATE.toString(), GROUP.SECURE_OPERATIONS.toString(),
				null, sreq.getSignedTicket().getUserID(), auditDTO);

		// Before removing the "MANAGED" operation to make it unmanaged
		RemoveSecureOperationFromGroupRequest wdReq = new RemoveSecureOperationFromGroupRequest();
		wdReq.setSignedTicket(sreq.getSignedTicket());
		wdReq.setGroupId(sreq.getSubjectId());
		wdReq.setOperationName(SecureOperation.RUL_MANAGED.toString());
		wdReq.setResourceObjectId(sreq.getResourceId());
		security.removeSecureOperationFromGroup(wdReq);
	}

	@Override
	@ValidateTicket
	public Set<UserDTO> getManagedUsers(GetManagedUsersRequest sreq) {
		SignedTicket ticket = sreq.getSignedTicket();
		security.requirePermitted(new RequirePermittedRequest(ticket, SecureOperation.RUL_CONFIGURE.toString()));

		GetAllowedUsersForOperationRequest wdReq = new GetAllowedUsersForOperationRequest();
		wdReq.setSignedTicket(ticket);
		wdReq.setOperationName(SecureOperation.RUL_MANAGED.toString());
		wdReq.setResourceObjectId(sreq.getResourceId());
		wdReq.setCheckUserGroups(false);
		return security.getAllowedUsersForOperation(wdReq);
	}

	@Override
	@ValidateTicket
	public Set<UserGroupDTO> getManagedGroups(GetManagedGroupsRequest sreq) {
		SignedTicket ticket = sreq.getSignedTicket();
		security.requirePermitted(new RequirePermittedRequest(ticket, SecureOperation.RUL_CONFIGURE.toString()));

		GetAllowedGroupsForOperationRequest wdReq = new GetAllowedGroupsForOperationRequest();
		wdReq.setSignedTicket(ticket);
		wdReq.setOperationName(SecureOperation.RUL_MANAGED.toString());
		wdReq.setResourceObjectId(sreq.getResourceId());
		wdReq.setCheckAncestors(false);
		wdReq.setIncludeRelatives(sreq.isIncludeRelatives());
		wdReq.setIncludeUsers(sreq.isIncludeUsers());
		return security.getAllowedGroupsForOperation(wdReq);
	}

	// --

	@Override
	@ValidateTicket
	public List<UserDTO> getUsers(EmptyRequest sreq) {
		SignedTicket ticket = sreq.getSignedTicket();
		security.requirePermitted(new RequirePermittedRequest(ticket, SecureOperation.RUL_CONFIGURE.toString()));

		GetUsersRequest wdReq = new GetUsersRequest();
		wdReq.setSignedTicket(ticket);
		return desktopUserService.getUsers(wdReq);
	}

	@Override
	@ValidateTicket
	public List<UserGroupDTO> getGroups(EmptyRequest sreq) {
		SignedTicket ticket = sreq.getSignedTicket();
		security.requirePermitted(new RequirePermittedRequest(ticket, SecureOperation.RUL_CONFIGURE.toString()));

		com.eurodyn.qlack2.webdesktop.api.request.EmptySignedRequest wdReq =
				new com.eurodyn.qlack2.webdesktop.api.request.EmptySignedRequest();
		wdReq.setSignedTicket(ticket);
		return desktopGroupService.getDomainsAsTree(wdReq);
	}

	@Override
	@ValidateTicket
	public List<UserGroupDTO> getDomains(EmptyRequest sreq) {
		SignedTicket ticket = sreq.getSignedTicket();
		security.requirePermitted(new RequirePermittedRequest(ticket, SecureOperation.RUL_CONFIGURE.toString()));

		com.eurodyn.qlack2.webdesktop.api.request.EmptySignedRequest wdReq =
				new com.eurodyn.qlack2.webdesktop.api.request.EmptySignedRequest();
		wdReq.setSignedTicket(ticket);
		return desktopGroupService.getDomains(wdReq);
	}
}
