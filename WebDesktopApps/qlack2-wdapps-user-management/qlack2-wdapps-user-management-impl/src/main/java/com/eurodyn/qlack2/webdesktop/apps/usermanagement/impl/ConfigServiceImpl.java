package com.eurodyn.qlack2.webdesktop.apps.usermanagement.impl;

import com.eurodyn.qlack2.fuse.auditing.api.AuditClientService;
import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicket;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.webdesktop.api.DesktopUserService;
import com.eurodyn.qlack2.webdesktop.api.SecurityService;
import com.eurodyn.qlack2.webdesktop.api.dto.SecureOperationAccessDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserGroupDTO;
import com.eurodyn.qlack2.webdesktop.api.request.security.*;
import com.eurodyn.qlack2.webdesktop.api.request.user.IsUserRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.ConfigService;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.exception.QInvalidOperationException;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.EmptySignedRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.config.*;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.impl.dto.SecureOperationAuditDTO;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.impl.util.AuditConstants.LEVEL;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.impl.util.SecureOperation;
import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Transactional
@OsgiServiceProvider(classes = {ConfigService.class})
public class ConfigServiceImpl implements ConfigService {
	private static final Logger LOGGER = Logger.getLogger(ConfigServiceImpl.class.getName());

	@OsgiService
	@Inject
	private IDMService idmService;

	@OsgiService
	@Inject
	private SecurityService security;

	@OsgiService
	@Inject
	private DesktopUserService desktopUserService;

	@OsgiService
	@Inject
	private AuditClientService audit;

	private boolean isUser(String subjectId, SignedTicket signedTicket) {
		IsUserRequest wdReq = new IsUserRequest(subjectId);
		return desktopUserService.isUser(wdReq);
	}

	@Override
	@ValidateTicket
	public List<SecureOperationAccessDTO> getSecureOperations(GetSecureOperationsRequest sreq) {
		SignedTicket ticket = sreq.getSignedTicket();
		security.requirePermitted(new RequirePermittedRequest(ticket, SecureOperation.USERMANAGEMENT_CONFIGURE.toString()));

		List<SecureOperationAccessDTO> retVal = null;
		if (isUser(sreq.getSubjectId(), ticket)) {
			retVal = getSecureOperationsForUser(sreq);
		} else {
			retVal = getSecureOperationsForGroup(sreq);
		}

		SecureOperationAuditDTO auditDTO = new SecureOperationAuditDTO();
		auditDTO.setSubjectId(sreq.getSubjectId());
		auditDTO.setOperations(retVal);
		audit.audit(LEVEL.WD_USERMANAGEMENT.toString(), EVENT.VIEW.toString(), GROUP.SECURE_OPERATIONS.toString(),
				null, ticket.getUserID(), auditDTO);

		return retVal;
	}

	private List<SecureOperationAccessDTO> getSecureOperationsForUser(GetSecureOperationsRequest sreq) {
		com.eurodyn.qlack2.webdesktop.api.request.security.GetSecureOperationsForUserRequest wdReq =
				new com.eurodyn.qlack2.webdesktop.api.request.security.GetSecureOperationsForUserRequest();
		wdReq.setSignedTicket(sreq.getSignedTicket());
		wdReq.setUserId(sreq.getSubjectId());
		wdReq.setOperations(getSecureOperations());
		return security.getSecureOperationsForUser(wdReq);
	}

	private List<SecureOperationAccessDTO> getSecureOperationsForGroup(GetSecureOperationsRequest sreq) {
		com.eurodyn.qlack2.webdesktop.api.request.security.GetSecureOperationsForGroupRequest wdReq =
				new com.eurodyn.qlack2.webdesktop.api.request.security.GetSecureOperationsForGroupRequest();
		wdReq.setSignedTicket(sreq.getSignedTicket());
		wdReq.setGroupId(sreq.getSubjectId());
		wdReq.setOperations(getSecureOperations());
		return security.getSecureOperationsForGroup(wdReq);
	}

	private List<String> getSecureOperations() {
		List<String> operations = new ArrayList<>();
		for (SecureOperation operation : SecureOperation.class.getEnumConstants()) {
			if (operation.isUiManaged()) {
				operations.add(operation.toString());
			}
		}
		return operations;
	}

	@Override
	@ValidateTicket
	public void saveSecureOperations(SaveSecureOperationsRequest sreq) {
		SignedTicket ticket = sreq.getSignedTicket();
		security.requirePermitted(new RequirePermittedRequest(ticket, SecureOperation.USERMANAGEMENT_CONFIGURE.toString()));

		if (isUser(sreq.getSubjectId(), ticket)) {
			saveSecureOperationsForUser(sreq);
		} else {
			saveSecureOperationsForGroup(sreq);
		}

		SecureOperationAuditDTO auditDTO = new SecureOperationAuditDTO();
		auditDTO.setSubjectId(sreq.getSubjectId());
		auditDTO.setOperations(sreq.getOperations());
		audit.audit(LEVEL.WD_USERMANAGEMENT.toString(), EVENT.UPDATE.toString(), GROUP.SECURE_OPERATIONS.toString(),
				null, ticket.getUserID(), auditDTO);
	}

	private void saveSecureOperationsForUser(SaveSecureOperationsRequest sreq) {
		LOGGER.log(Level.FINE, "Saving secure operations for user with ID {0}", sreq.getSubjectId());

		for (SecureOperationAccessDTO access : sreq.getOperations()) {
			if (!SecureOperation.contains(access.getOperation())) {
				throw new QInvalidOperationException("Operation "
						+ access.getOperation()
						+ " is not managed by the User Management application");
			}
			if (access.getAccess() == null) {
				RemoveSecureOperationFromUserRequest wdReq = new RemoveSecureOperationFromUserRequest();
				wdReq.setSignedTicket(sreq.getSignedTicket());
				wdReq.setUserId(sreq.getSubjectId());
				wdReq.setOperationName(access.getOperation());
				security.removeSecureOperationFromUser(wdReq);
			} else if (access.getAccess()) {
				AllowSecureOperationForUserRequest wdReq = new AllowSecureOperationForUserRequest();
				wdReq.setSignedTicket(sreq.getSignedTicket());
				wdReq.setUserId(sreq.getSubjectId());
				wdReq.setOperationName(access.getOperation());
				security.allowSecureOperationForUser(wdReq);
			} else {
				DenySecureOperationForUserRequest wdReq = new DenySecureOperationForUserRequest();
				wdReq.setSignedTicket(sreq.getSignedTicket());
				wdReq.setUserId(sreq.getSubjectId());
				wdReq.setOperationName(access.getOperation());
				security.denySecureOperationForUser(wdReq);
			}
		}
	}

	private void saveSecureOperationsForGroup(SaveSecureOperationsRequest sreq) {
		LOGGER.log(Level.FINE, "Saving secure operations for group with ID {0}", sreq.getSubjectId());

		for (SecureOperationAccessDTO access : sreq.getOperations()) {
			if (!SecureOperation.contains(access.getOperation())) {
				throw new QInvalidOperationException("Operation "
						+ access.getOperation()
						+ " is not managed by the User Management application");
			}
			if (access.getAccess() == null) {
				RemoveSecureOperationFromGroupRequest wdReq = new RemoveSecureOperationFromGroupRequest();
				wdReq.setSignedTicket(sreq.getSignedTicket());
				wdReq.setGroupId(sreq.getSubjectId());
				wdReq.setOperationName(access.getOperation());
				security.removeSecureOperationFromGroup(wdReq);
			} else if (access.getAccess()) {
				AllowSecureOperationForGroupRequest wdReq = new AllowSecureOperationForGroupRequest();
				wdReq.setSignedTicket(sreq.getSignedTicket());
				wdReq.setGroupId(sreq.getSubjectId());
				wdReq.setOperationName(access.getOperation());
				security.allowSecureOperationForGroup(wdReq);
			} else {
				DenySecureOperationForGroupRequest wdReq = new DenySecureOperationForGroupRequest();
				wdReq.setSignedTicket(sreq.getSignedTicket());
				wdReq.setGroupId(sreq.getSubjectId());
				wdReq.setOperationName(access.getOperation());
				security.denySecureOperationForGroup(wdReq);
			}
		}
	}

	// --

	@Override
	@ValidateTicket
	public void addManagedSubject(AddManagedSubjectRequest sreq) {
		SignedTicket ticket = sreq.getSignedTicket();
		security.requirePermitted(new RequirePermittedRequest(ticket, SecureOperation.USERMANAGEMENT_CONFIGURE.toString()));

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
		wdReq.setOperationName(SecureOperation.USERMANAGEMENT_MANAGED.toString());
		security.allowSecureOperationForUser(wdReq);
	}

	private void addManagedGroup(AddManagedSubjectRequest sreq) {
		LOGGER.log(Level.INFO, "Adding security managed group with ID {0}", sreq.getSubjectId());
		AllowSecureOperationForGroupRequest wdReq = new AllowSecureOperationForGroupRequest();
		wdReq.setSignedTicket(sreq.getSignedTicket());
		wdReq.setGroupId(sreq.getSubjectId());
		wdReq.setOperationName(SecureOperation.USERMANAGEMENT_MANAGED.toString());
		security.allowSecureOperationForGroup(wdReq);
	}

	@Override
	@ValidateTicket
	public void removeManagedSubject(RemoveManagedSubjectRequest sreq) {
		SignedTicket ticket = sreq.getSignedTicket();
		security.requirePermitted(new RequirePermittedRequest(ticket, SecureOperation.USERMANAGEMENT_CONFIGURE.toString()));

		if (isUser(sreq.getSubjectId(), ticket)) {
			removeManagedUser(sreq);
		} else {
			removeManagedGroup(sreq);
		}
	}

	private void removeManagedUser(RemoveManagedSubjectRequest sreq) {
		LOGGER.log(Level.INFO, "Removing security managed user with ID {0}", sreq.getSubjectId());

		SignedTicket ticket = sreq.getSignedTicket();
		security.requirePermitted(new RequirePermittedRequest(ticket, SecureOperation.USERMANAGEMENT_CONFIGURE.toString()));

		SecureOperationAuditDTO auditDTO = new SecureOperationAuditDTO();
		auditDTO.setSubjectId(sreq.getSubjectId());
		auditDTO.setOperations(new ArrayList<SecureOperationAccessDTO>());

		// First remove all managed operations from the user
		for (SecureOperation operation : SecureOperation.class.getEnumConstants()) {
			if (operation.isUiManaged()) {
				RemoveSecureOperationFromUserRequest wdReq = new RemoveSecureOperationFromUserRequest();
				wdReq.setSignedTicket(sreq.getSignedTicket());
				wdReq.setUserId(sreq.getSubjectId());
				wdReq.setOperationName(operation.toString());
				security.removeSecureOperationFromUser(wdReq);

				SecureOperationAccessDTO auditAccess = new SecureOperationAccessDTO(operation.toString(), false);
				auditDTO.getOperations().add(auditAccess);
			}
		}

		audit.audit(LEVEL.WD_USERMANAGEMENT.toString(), EVENT.UPDATE.toString(), GROUP.SECURE_OPERATIONS.toString(),
				null, ticket.getUserID(), auditDTO);

		// Before removing the "MANAGED" operation to make them unmanaged
		RemoveSecureOperationFromUserRequest wdReq = new RemoveSecureOperationFromUserRequest();
		wdReq.setSignedTicket(sreq.getSignedTicket());
		wdReq.setUserId(sreq.getSubjectId());
		wdReq.setOperationName(SecureOperation.USERMANAGEMENT_MANAGED.toString());
		security.removeSecureOperationFromUser(wdReq);
	}

	private void removeManagedGroup(RemoveManagedSubjectRequest sreq) {
		LOGGER.log(Level.INFO, "Removing security managed group with ID {0}", sreq.getSubjectId());

		SignedTicket ticket = sreq.getSignedTicket();
		security.requirePermitted(new RequirePermittedRequest(ticket, SecureOperation.USERMANAGEMENT_CONFIGURE.toString()));

		SecureOperationAuditDTO auditDTO = new SecureOperationAuditDTO();
		auditDTO.setSubjectId(sreq.getSubjectId());
		auditDTO.setOperations(new ArrayList<SecureOperationAccessDTO>());

		// First remove all managed operations from the groups
		for (SecureOperation operation : SecureOperation.class.getEnumConstants()) {
			if (operation.isUiManaged()) {
				RemoveSecureOperationFromGroupRequest wdReq = new RemoveSecureOperationFromGroupRequest();
				wdReq.setSignedTicket(sreq.getSignedTicket());
				wdReq.setGroupId(sreq.getSubjectId());
				wdReq.setOperationName(operation.toString());
				security.removeSecureOperationFromGroup(wdReq);

				SecureOperationAccessDTO auditAccess = new SecureOperationAccessDTO(operation.toString(), false);
				auditDTO.getOperations().add(auditAccess);
			}
		}

		audit.audit(LEVEL.WD_USERMANAGEMENT.toString(), EVENT.UPDATE.toString(), GROUP.SECURE_OPERATIONS.toString(),
				null, ticket.getUserID(), auditDTO);

		// Before removing the "MANAGED" operation to make it unmanaged
		RemoveSecureOperationFromGroupRequest wdReq = new RemoveSecureOperationFromGroupRequest();
		wdReq.setSignedTicket(sreq.getSignedTicket());
		wdReq.setGroupId(sreq.getSubjectId());
		wdReq.setOperationName(SecureOperation.USERMANAGEMENT_MANAGED.toString());
		security.removeSecureOperationFromGroup(wdReq);
	}

	@Override
	@ValidateTicket
	public Set<UserDTO> getManagedUsers(EmptySignedRequest sreq) {
		SignedTicket ticket = sreq.getSignedTicket();
		security.requirePermitted(new RequirePermittedRequest(ticket, SecureOperation.USERMANAGEMENT_CONFIGURE.toString()));

		GetAllowedUsersForOperationRequest wdReq = new GetAllowedUsersForOperationRequest();
		wdReq.setSignedTicket(ticket);
		wdReq.setOperationName(SecureOperation.USERMANAGEMENT_MANAGED.toString());
		wdReq.setCheckUserGroups(false);
		return security.getAllowedUsersForOperation(wdReq);
	}

	@Override
	@ValidateTicket
	public Set<UserGroupDTO> getManagedGroups(GetManagedGroupsRequest sreq) {
		SignedTicket ticket = sreq.getSignedTicket();
		security.requirePermitted(new RequirePermittedRequest(ticket, SecureOperation.USERMANAGEMENT_CONFIGURE.toString()));

		GetAllowedGroupsForOperationRequest wdReq = new GetAllowedGroupsForOperationRequest();
		wdReq.setSignedTicket(ticket);
		wdReq.setOperationName(SecureOperation.USERMANAGEMENT_MANAGED.toString());
		wdReq.setCheckAncestors(false);
		wdReq.setIncludeRelatives(sreq.isIncludeRelatives());
		wdReq.setIncludeUsers(sreq.isIncludeUsers());
		return security.getAllowedGroupsForOperation(wdReq);
	}

}
