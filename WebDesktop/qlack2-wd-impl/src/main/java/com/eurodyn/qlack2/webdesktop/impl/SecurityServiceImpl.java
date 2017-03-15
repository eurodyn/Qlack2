package com.eurodyn.qlack2.webdesktop.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import com.eurodyn.qlack2.fuse.aaa.api.OpTemplateService;
import com.eurodyn.qlack2.fuse.aaa.api.OperationService;
import com.eurodyn.qlack2.fuse.aaa.api.ResourceService;
import com.eurodyn.qlack2.fuse.aaa.api.UserGroupService;
import com.eurodyn.qlack2.fuse.aaa.api.UserService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.OpTemplateDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.OperationAccessDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.ResourceDTO;
import com.eurodyn.qlack2.fuse.idm.api.exception.QAuthorisationException;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.webdesktop.api.SecurityService;
import com.eurodyn.qlack2.webdesktop.api.dto.SecureOperationAccessDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserGroupDTO;
import com.eurodyn.qlack2.webdesktop.api.request.security.AllowSecureOperationForGroupRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.AllowSecureOperationForTemplateRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.AllowSecureOperationForUserRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.CreateSecureResourceRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.DeleteSecureResourceRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.DenySecureOperationForGroupRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.DenySecureOperationForTemplateRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.DenySecureOperationForUserRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.GetAllowedGroupsForOperationRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.GetAllowedUsersForOperationRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.GetSecureOperationsForGroupRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.GetSecureOperationsForTemplateRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.GetSecureOperationsForUserRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.IsPermittedRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.RemoveSecureOperationFromGroupRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.RemoveSecureOperationFromTemplateRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.RemoveSecureOperationFromUserRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.RequirePermittedRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.UpdateSecureResourceRequest;
import com.eurodyn.qlack2.webdesktop.impl.util.ConverterUtil;
import com.eurodyn.qlack2.webdesktop.impl.util.DomainUtil;

@Singleton
@Transactional
@OsgiServiceProvider(classes = { SecurityService.class })
public class SecurityServiceImpl implements SecurityService {
	@OsgiService @Inject
	private ResourceService resourceService;
	@OsgiService @Inject
	private OperationService operationService;
	@OsgiService @Inject
	private UserService userService;
	@OsgiService @Inject
	private UserGroupService groupService;
	@OsgiService @Inject
	private OpTemplateService templateService;
	
	@Inject
	private DomainUtil domainUtil;

//	public void setResourceService(ResourceService resourceService) {
//		this.resourceService = resourceService;
//	}
//
//	public void setOperationService(OperationService operationService) {
//		this.operationService = operationService;
//	}
//
//	public void setUserService(UserService userService) {
//		this.userService = userService;
//	}
//
//	public void setGroupService(UserGroupService groupService) {
//		this.groupService = groupService;
//	}
//
//	public void setTemplateService(OpTemplateService templateService) {
//		this.templateService = templateService;
//	}
//
//	public void setDomainUtil(DomainUtil domainUtil) {
//		this.domainUtil = domainUtil;
//	}

	// --

	@Override
	public String createSecureResource(CreateSecureResourceRequest sreq) {
		ResourceDTO resource = new ResourceDTO();
		resource.setObjectID(sreq.getObjectId());
		resource.setName(sreq.getName());
		resource.setDescription(sreq.getDescription());

		return resourceService.createResource(resource);
	}

	@Override
	public void updateSecureResource(UpdateSecureResourceRequest sreq) {
		ResourceDTO resource = resourceService.getResourceByObjectId(sreq.getObjectId());
		resource.setName(sreq.getName());
		resource.setDescription(sreq.getDescription());

		resourceService.updateResource(resource);
	}

	@Override
	public void deleteSecureResource(DeleteSecureResourceRequest sreq) {
		resourceService.deleteResourceByObjectId(sreq.getResourceObjectId());
	}

	// --

	@Override
	public Boolean isPermitted(IsPermittedRequest sreq) {
		return operationService.isPermitted(
				sreq.getSignedTicket().getUserID(),
				sreq.getOperationName(),
				sreq.getResourceObjectId());
	}

	@Override
	public void requirePermitted(RequirePermittedRequest sreq) {
		if (!isPermitted(new IsPermittedRequest(
				sreq.getSignedTicket(),
				sreq.getOperationName(),
				sreq.getResourceObjectId()))) {
			throw new QAuthorisationException(
					sreq.getSignedTicket().getUserID(),
					sreq.getSignedTicket().toString(),
					sreq.getOperationName(),
					sreq.getResourceObjectId());
		}
	}

	// --

	@Override
	public List<SecureOperationAccessDTO> getSecureOperationsForUser(GetSecureOperationsForUserRequest sreq) {
		domainUtil.checkCanViewUser(sreq.getSignedTicket(), sreq.getUserId());

		List<SecureOperationAccessDTO> retVal = new ArrayList<>();
		for (String operationName : sreq.getOperations()) {
			Boolean access = operationService.isPermitted(
					sreq.getUserId(),
					operationName,
					sreq.getResourceObjectId());
			retVal.add(new SecureOperationAccessDTO(operationName, access));
		}

		return retVal;
	}

	@Override
	public List<SecureOperationAccessDTO> getSecureOperationsForGroup(GetSecureOperationsForGroupRequest sreq) {
		domainUtil.checkCanViewGroup(sreq.getSignedTicket(), sreq.getGroupId());

		List<SecureOperationAccessDTO> retVal = new ArrayList<>();
		for (String operationName : sreq.getOperations()) {
			Boolean access = operationService.isPermittedForGroup(
					sreq.getGroupId(),
					operationName,
					sreq.getResourceObjectId());
			retVal.add(new SecureOperationAccessDTO(operationName, access));
		}

		return retVal;
	}

	@Override
	public void allowSecureOperationForUser(AllowSecureOperationForUserRequest sreq) {
		domainUtil.checkCanViewUser(sreq.getSignedTicket(), sreq.getUserId());

		if (sreq.getResourceObjectId() == null) {
			operationService.addOperationToUser(
					sreq.getUserId(),
					sreq.getOperationName(),
					false);
		} else {
			ResourceDTO resource = resourceService.getResourceByObjectId(sreq.getResourceObjectId());
			operationService.addOperationToUser(
					sreq.getUserId(),
					sreq.getOperationName(),
					resource.getId(),
					false);
		}
	}

	@Override
	public void denySecureOperationForUser(DenySecureOperationForUserRequest sreq) {
		domainUtil.checkCanViewUser(sreq.getSignedTicket(), sreq.getUserId());

		if (sreq.getResourceObjectId() == null) {
			operationService.addOperationToUser(
					sreq.getUserId(),
					sreq.getOperationName(),
					true);
		} else {
			ResourceDTO resource = resourceService.getResourceByObjectId(sreq.getResourceObjectId());
			operationService.addOperationToUser(
					sreq.getUserId(),
					sreq.getOperationName(),
					resource.getId(),
					true);
		}
	}

	@Override
	public void removeSecureOperationFromUser(RemoveSecureOperationFromUserRequest sreq) {
		domainUtil.checkCanViewUser(sreq.getSignedTicket(), sreq.getUserId());

		if (sreq.getResourceObjectId() == null) {
			operationService.removeOperationFromUser(
					sreq.getUserId(),
					sreq.getOperationName());
		} else {
			ResourceDTO resource = resourceService.getResourceByObjectId(sreq.getResourceObjectId());
			operationService.removeOperationFromUser(
					sreq.getUserId(),
					sreq.getOperationName(),
					resource.getId());
		}
	}

	@Override
	public void allowSecureOperationForGroup(AllowSecureOperationForGroupRequest sreq) {
		domainUtil.checkCanViewGroup(sreq.getSignedTicket(), sreq.getGroupId());

		if (sreq.getResourceObjectId() == null) {
			operationService.addOperationToGroup(
					sreq.getGroupId(),
					sreq.getOperationName(),
					false);
		} else {
			ResourceDTO resource = resourceService.getResourceByObjectId(sreq.getResourceObjectId());
			operationService.addOperationToGroup(
					sreq.getGroupId(),
					sreq.getOperationName(),
					resource.getId(),
					false);
		}
	}

	@Override
	public void denySecureOperationForGroup(DenySecureOperationForGroupRequest sreq) {
		domainUtil.checkCanViewGroup(sreq.getSignedTicket(), sreq.getGroupId());

		if (sreq.getResourceObjectId() == null) {
			operationService.addOperationToGroup(
					sreq.getGroupId(),
					sreq.getOperationName(),
					true);
		} else {
			ResourceDTO resource = resourceService.getResourceByObjectId(sreq.getResourceObjectId());
			operationService.addOperationToGroup(
					sreq.getGroupId(),
					sreq.getOperationName(),
					resource.getId(),
					true);
		}
	}

	@Override
	public void removeSecureOperationFromGroup(RemoveSecureOperationFromGroupRequest sreq) {
		domainUtil.checkCanViewGroup(sreq.getSignedTicket(), sreq.getGroupId());

		if (sreq.getResourceObjectId() == null) {
			operationService.removeOperationFromGroup(
					sreq.getGroupId(),
					sreq.getOperationName());
		} else {
			ResourceDTO resource = resourceService.getResourceByObjectId(sreq.getResourceObjectId());
			operationService.removeOperationFromGroup(
					sreq.getGroupId(),
					sreq.getOperationName(),
					resource.getId());
		}
	}

	// --

	@Override
	public Set<UserDTO> getAllowedUsersForOperation(GetAllowedUsersForOperationRequest sreq) {
		Set<String> allowedUserIds = operationService.getAllowedUsersForOperation(
				sreq.getOperationName(),
				sreq.getResourceObjectId(),
				sreq.isCheckUserGroups());

		Set<UserDTO> users = new HashSet<>(allowedUserIds.size());
		for (String userId : allowedUserIds) {
			com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO aaaUser = userService.getUserById(userId);
			UserDTO user = ConverterUtil.aaaUserDTOToUserDTO(aaaUser);
			users.add(user);
		}

		SignedTicket ticket = sreq.getSignedTicket();
		return domainUtil.filterUsers(ticket, users);
	}

	@Override
	public Set<UserGroupDTO> getAllowedGroupsForOperation(GetAllowedGroupsForOperationRequest sreq) {
		Set<String> allowedGroupIds = operationService.getAllowedGroupsForOperation(
				sreq.getOperationName(),
				sreq.getResourceObjectId(),
				sreq.isCheckAncestors());

		Set<UserGroupDTO> groups = new HashSet<>(allowedGroupIds.size());
		for (String groupId : allowedGroupIds) {
			GroupDTO aaaGroup = groupService.getGroupByID(groupId, !sreq.isIncludeRelatives());
			UserGroupDTO group = ConverterUtil.groupDTOToUserGroupDTO(aaaGroup);
			if (sreq.isIncludeUsers()) {
				List<String> groupUsers = new ArrayList<>();
				groupUsers.addAll(groupService.getGroupUsersIds(groupId, false));
				group.setUsers(groupUsers);
			}
			groups.add(group);
		}

		SignedTicket ticket = sreq.getSignedTicket();
		return domainUtil.filterGroups(ticket, groups);
	}

	// --

	@Override
	public Set<SecureOperationAccessDTO> getSecureOperationsForTemplate(GetSecureOperationsForTemplateRequest sreq) {
		OpTemplateDTO template = templateService.getTemplateByName(sreq.getName());

		Set<SecureOperationAccessDTO> retVal = new HashSet<>();
		for (OperationAccessDTO operation : template.getOperations()) {
			String requestObjectId = sreq.getResourceObjectId();
			String operationObjectId = operation.getResource().getObjectID();

			if ((operationObjectId == null && requestObjectId == null) || operationObjectId.equals(requestObjectId)) {
				retVal.add(new SecureOperationAccessDTO(operation.getOperation().getName(), !operation.isDeny()));
			}
		}
		return retVal;
	}

	@Override
	public void allowSecureOperationForTemplate(AllowSecureOperationForTemplateRequest sreq) {
		OpTemplateDTO template = templateService.getTemplateByName(sreq.getTemplateName());
		ResourceDTO resource = resourceService.getResourceByObjectId(sreq.getResourceObjectId());
		templateService.addOperation(
				template.getId(),
				sreq.getOperationName(),
				resource.getId(),
				false);
	}

	@Override
	public void denySecureOperationForTemplate(DenySecureOperationForTemplateRequest sreq) {
		OpTemplateDTO template = templateService.getTemplateByName(sreq.getTemplateName());
		ResourceDTO resource = resourceService.getResourceByObjectId(sreq.getResourceObjectId());
		templateService.addOperation(
				template.getId(),
				sreq.getOperationName(),
				resource.getId(),
				true);
	}

	@Override
	public void removeSecureOperationFromTemplate(RemoveSecureOperationFromTemplateRequest sreq) {
		OpTemplateDTO template = templateService.getTemplateByName(sreq.getTemplateName());
		ResourceDTO resource = resourceService.getResourceByObjectId(sreq.getResourceObjectId());
		templateService.removeOperation(
				template.getId(),
				sreq.getOperationName(),
				resource.getId());
	}

}
