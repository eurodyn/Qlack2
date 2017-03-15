package com.eurodyn.qlack2.webdesktop.impl.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.ops4j.pax.cdi.api.OsgiService;

import com.eurodyn.qlack2.fuse.aaa.api.UserGroupService;
import com.eurodyn.qlack2.fuse.aaa.api.UserService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.idm.api.exception.QAuthorisationException;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.webdesktop.api.SecurityService;
import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserGroupDTO;
import com.eurodyn.qlack2.webdesktop.api.exception.IllegalGroupActionException;
import com.eurodyn.qlack2.webdesktop.api.exception.IllegalUserActionException;
import com.eurodyn.qlack2.webdesktop.api.exception.InvalidUserActionException;
import com.eurodyn.qlack2.webdesktop.api.request.group.CreateGroupRequest;
import com.eurodyn.qlack2.webdesktop.api.request.group.DeleteGroupRequest;
import com.eurodyn.qlack2.webdesktop.api.request.group.MoveGroupRequest;
import com.eurodyn.qlack2.webdesktop.api.request.group.UpdateGroupRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.RequirePermittedRequest;
import com.eurodyn.qlack2.webdesktop.api.request.user.CreateUserRequest;
import com.eurodyn.qlack2.webdesktop.api.request.user.DeleteUserRequest;
import com.eurodyn.qlack2.webdesktop.api.request.user.UpdateUserRequest;
import com.eurodyn.qlack2.webdesktop.api.util.Constants;

@Singleton
public class DomainUtil {

	@OsgiService @Inject
	private UserService userService;
	@OsgiService @Inject
	private UserGroupService groupService;
	@Inject
	private SecurityService security;

//	public void setUserService(UserService userService) {
//		this.userService = userService;
//	}
//
//	public void setGroupService(UserGroupService groupService) {
//		this.groupService = groupService;
//	}
//
//	public void setSecurity(SecurityService security) {
//		this.security = security;
//	}

	// --

	private String getGroupDomainId(String groupId) {
		GroupDTO group = groupService.getGroupByID(groupId, false);
		while (group.getParent() != null) {
			group = group.getParent();
		}
		return group.getId();
	}

	private String getUserDomainId(String userId) {
		Set<String> userGroups = groupService.getUserGroupsIds(userId);

		// A user can be a member of only one domain so we just use
		// a random user group in order to find the user's domain
		return getGroupDomainId(userGroups.iterator().next());
	}

	private boolean allGroupsInDomain(String domainId, List<String> groupIds) {
		for (String groupId : groupIds) {
			String groupDomainId = getGroupDomainId(groupId);
			if (!groupDomainId.equals(domainId)) {
				return false;
			}
		}
		return true;
	}

	private boolean allUsersInDomain(String domainId, List<String> userIds) {
		for (String userId : userIds) {
			String userDomainId = getUserDomainId(userId);
			if (!userDomainId.equals(domainId)) {
				return false;
			}
		}
		return true;
	}

	// --

	public void checkCanViewGroup(SignedTicket ticket, String groupId) throws QAuthorisationException {
		String adminId = ticket.getUserID();
		boolean adminIsSuper = userService.isSuperadmin(adminId);
		if (adminIsSuper) {
			return;
		}
		else {
			String adminDomainId = this.getUserDomainId(adminId);

			String domainId = this.getGroupDomainId(groupId);
			if (adminDomainId.equals(domainId)) {
				return;
			}
			else {
				throw new QAuthorisationException(
						ticket.getUserID(),
						ticket.toString(),
						Constants.OP_MANAGE_GROUPS,
						groupId);
			}
		}
	}

	public List<UserGroupDTO> filterGroups(SignedTicket ticket, List<UserGroupDTO> groups) {
		return filterGroups(ticket, groups, new ArrayList<UserGroupDTO>());
	}

	public Set<UserGroupDTO> filterGroups(SignedTicket ticket, Set<UserGroupDTO> groups) {
		return filterGroups(ticket, groups, new HashSet<UserGroupDTO>());
	}

	private <C extends Collection<UserGroupDTO>> C filterGroups(SignedTicket ticket, C groups, C empty) {
		String adminId = ticket.getUserID();
		boolean adminIsSuper = userService.isSuperadmin(adminId);
		if (adminIsSuper) {
			return groups;
		}
		else {
			String adminDomainId = this.getUserDomainId(adminId);

			C viewableGroups = empty;
			for (UserGroupDTO group : groups) {
				String domainId = this.getGroupDomainId(group.getId());
				if (adminDomainId.equals(domainId)) {
					viewableGroups.add(group);
				}
			}
			return viewableGroups;
		}
	}

	// --

	public void checkCanViewUser(SignedTicket ticket, String userId) throws QAuthorisationException {
		String adminId = ticket.getUserID();
		boolean adminIsSuper = userService.isSuperadmin(adminId);
		if (adminIsSuper) {
			return;
		}
		else {
			String adminDomainId = this.getUserDomainId(adminId);

			String domainId = this.getUserDomainId(userId);
			if (adminDomainId.equals(domainId)) {
				return;
			}
			else {
				throw new QAuthorisationException(
						ticket.getUserID(),
						ticket.toString(),
						Constants.OP_MANAGE_USERS,
						userId);
			}
		}
	}

	public List<UserDTO> filterUsers(SignedTicket ticket, List<UserDTO> users) {
		return filterUsers(ticket, users, new ArrayList<UserDTO>());
	}

	public Set<UserDTO> filterUsers(SignedTicket ticket, Set<UserDTO> users) {
		return filterUsers(ticket, users, new HashSet<UserDTO>());
	}

	private <C extends Collection<UserDTO>> C filterUsers(SignedTicket ticket, C users, C empty) {
		String adminId = ticket.getUserID();
		boolean adminIsSuper = userService.isSuperadmin(adminId);
		if (adminIsSuper) {
			return users;
		}
		else {
			String adminDomainId = this.getUserDomainId(adminId);

			C viewableUsers = empty;
			for (UserDTO user : users) {
				String userId = user.getId();
				boolean isSuperAdmin = userService.isSuperadmin(userId);
				if (isSuperAdmin)
					continue;

				String domainId = this.getUserDomainId(userId);
				if (adminDomainId.equals(domainId)) {
					viewableUsers.add(user);
				}
			}
			return viewableUsers;
		}
	}

	// --

	public void validateCreateGroupRequest(CreateGroupRequest sreq) throws IllegalGroupActionException {
		SignedTicket ticket = sreq.getSignedTicket();
		security.requirePermitted(new RequirePermittedRequest(ticket, Constants.OP_MANAGE_GROUPS));

		String adminId = ticket.getUserID();
		boolean adminIsSuper = userService.isSuperadmin(adminId);

		if (adminIsSuper) {
			// nothing
		}
		else {
			String domainId = this.getUserDomainId(adminId);
			String parentGroupId = sreq.getParentGroupId();

			validateCreateGroupRequest(domainId, parentGroupId);
		}
	}

	public void validateDeleteGroupRequest(DeleteGroupRequest sreq) throws IllegalGroupActionException {
		SignedTicket ticket = sreq.getSignedTicket();
		security.requirePermitted(new RequirePermittedRequest(ticket, Constants.OP_MANAGE_GROUPS));

		String adminId = ticket.getUserID();
		boolean adminIsSuper = userService.isSuperadmin(adminId);
		if (adminIsSuper) {
			// nothing
		}
		else {
			String domainId = this.getUserDomainId(adminId);
			String groupId = sreq.getId();

			validateManageGroupRequest(domainId, groupId);
		}
	}

	public void validateMoveGroupRequest(MoveGroupRequest sreq) throws IllegalGroupActionException {
		SignedTicket ticket = sreq.getSignedTicket();
		security.requirePermitted(new RequirePermittedRequest(ticket, Constants.OP_MANAGE_GROUPS));

		String adminId = ticket.getUserID();
		boolean adminIsSuper = userService.isSuperadmin(adminId);
		if (adminIsSuper) {
			// nothing
		}
		else {
			String domainId = this.getUserDomainId(adminId);
			String groupId = sreq.getId();
			String newParentGroupId = sreq.getNewParentId();

			validateManageGroupRequest(domainId, groupId);
			validateCreateGroupRequest(domainId, newParentGroupId);
		}
	}

	public void validateUpdateGroupRequest(UpdateGroupRequest sreq) throws IllegalGroupActionException {
		SignedTicket ticket = sreq.getSignedTicket();
		security.requirePermitted(new RequirePermittedRequest(ticket, Constants.OP_MANAGE_GROUPS));

		String adminId = ticket.getUserID();
		boolean adminIsSuper = userService.isSuperadmin(adminId);
		if (adminIsSuper) {
			// nothing
		}
		else {
			String domainId = this.getUserDomainId(adminId);
			String groupId = sreq.getId();

			// This disallows management of domain also
			validateManageGroupRequest(domainId, groupId);

			// Since we disallow management of domain, we allow any user to be removed from the group
			// and we only check that new users do belong to the domain.
			List<String> userIds = sreq.getUserIds();
			boolean allUsersInDomain = this.allUsersInDomain(domainId, userIds);
			if (!allUsersInDomain) {
				throw IllegalGroupActionException.forCannotManageForeignUsers();
			}
		}
	}

	private void validateCreateGroupRequest(String domainId, String parentGroupId) throws IllegalGroupActionException {
		if (parentGroupId == null) {
			throw IllegalGroupActionException.forCannotManageDomain();
		}
		else {
			String parentGroupDomainId = this.getGroupDomainId(parentGroupId);
			if (!parentGroupDomainId.equals(domainId)) {
				throw IllegalGroupActionException.forCannotManageForeignGroup();
			}
		}
	}

	private void validateManageGroupRequest(String domainId, String groupId) throws IllegalGroupActionException {
		if (groupId.equals(domainId)) {
			throw IllegalGroupActionException.forCannotManageDomain();
		}
		else {
			String groupDomainId = this.getGroupDomainId(groupId);
			if (!groupDomainId.equals(domainId)) {
				throw IllegalGroupActionException.forCannotManageForeignGroup();
			}
		}
	}

	// --

	public void validateCreateUserRequest(CreateUserRequest sreq) throws IllegalUserActionException, InvalidUserActionException {
		SignedTicket ticket = sreq.getSignedTicket();
		security.requirePermitted(new RequirePermittedRequest(ticket, Constants.OP_MANAGE_USERS));

		String adminId = ticket.getUserID();
		boolean adminIsSuper = userService.isSuperadmin(adminId);

		if (adminIsSuper) {
			boolean isSuperAdmin = sreq.isSuperadmin();
			List<String> groupIds = sreq.getGroupIds();

			if (isSuperAdmin) {
				// check created user is super-admin with no groups
				if (!groupIds.isEmpty()) {
					throw IllegalUserActionException.forSuperAdminInGroups();
				}
			}
			else {
				validateUserRequestBySuperAdmin(groupIds);
			}
		}
		else {
			boolean isSuperAdmin = sreq.isSuperadmin();
			List<String> groupIds = sreq.getGroupIds();

			// check that super-admin flag is not set
			if (isSuperAdmin) {
				throw IllegalUserActionException.forCannotManageSuperAdmin();
			}

			String domainId = this.getUserDomainId(adminId);

			validateUserRequestByDomainAdmin(domainId, groupIds);
		}
	}

	public void validateUpdateUserRequest(UpdateUserRequest sreq) throws IllegalUserActionException, InvalidUserActionException {
		SignedTicket ticket = sreq.getSignedTicket();
		security.requirePermitted(new RequirePermittedRequest(ticket, Constants.OP_MANAGE_USERS));

		String adminId = ticket.getUserID();
		boolean adminIsSuper = userService.isSuperadmin(adminId);

		if (adminIsSuper) {
			boolean isSuperAdmin = sreq.isSuperadmin();
			List<String> groupIds = sreq.getGroupIds();

			if (isSuperAdmin) {
				// check updated user is super-admin with no groups
				if (!groupIds.isEmpty()) {
					throw IllegalUserActionException.forSuperAdminInGroups();
				}
			}
			else {
				validateUserRequestBySuperAdmin(groupIds);
			}
		}
		else {
			String userId = sreq.getUserId();
			boolean userIsSuperAdmin = userService.isSuperadmin(userId);

			// check that updated user is not super-admin
			if (userIsSuperAdmin) {
				throw IllegalUserActionException.forCannotManageSuperAdmin();
			}

			boolean isSuperAdmin = sreq.isSuperadmin();
			List<String> groupIds = sreq.getGroupIds();

			// check that super-admin flag is not set
			if (isSuperAdmin) {
				throw IllegalUserActionException.forCannotManageSuperAdmin();
			}

			String userDomainId = this.getUserDomainId(userId);
			String domainId = this.getUserDomainId(adminId);

			// check user belongs to admin domain
			if (!userDomainId.equals(domainId)) {
				throw IllegalUserActionException.forCannotManageForeignDomain();
			}

			validateUserRequestByDomainAdmin(domainId, groupIds);
		}
	}

	public void validateDeleteUserRequest(DeleteUserRequest sreq) throws IllegalUserActionException {
		SignedTicket ticket = sreq.getSignedTicket();
		security.requirePermitted(new RequirePermittedRequest(ticket, Constants.OP_MANAGE_USERS));

		String adminId = ticket.getUserID();
		boolean adminIsSuper = userService.isSuperadmin(adminId);

		if (adminIsSuper) {
			// nothing
		}
		else {
			String userId = sreq.getUserId();
			boolean userIsSuperAdmin = userService.isSuperadmin(userId);

			// check that deleted user is not super-admin
			if (userIsSuperAdmin) {
				throw IllegalUserActionException.forCannotManageSuperAdmin();
			}

			String userDomainId = this.getUserDomainId(userId);
			String domainId = this.getUserDomainId(adminId);

			// check user belongs to admin domain
			if (!userDomainId.equals(domainId)) {
				throw IllegalUserActionException.forCannotManageForeignDomain();
			}
		}
	}

	private void validateUserRequestBySuperAdmin(List<String> groupIds) throws InvalidUserActionException {

		// check groups contain at least one group
		if (groupIds.isEmpty()) {
			throw InvalidUserActionException.forSingleDomain();
		}

		// get domain of that group
		String groupId = groupIds.get(0);
		String domainId = this.getGroupDomainId(groupId);

		// check groups contain group domain
		if (!groupIds.contains(domainId)) {
			throw InvalidUserActionException.forSingleDomain();
		}

		// check all groups belong to same domain
		boolean allGroupsInDomain = this.allGroupsInDomain(domainId, groupIds);
		if (!allGroupsInDomain) {
			throw InvalidUserActionException.forSingleDomain();
		}
	}

	private void validateUserRequestByDomainAdmin(String domainId, List<String> groupIds) throws InvalidUserActionException {

		// check groups contain admin domain
		if (!groupIds.contains(domainId)) {
			throw InvalidUserActionException.forDomainAdminDomain();
		}

		// check all groups belong to admin domain
		boolean allGroupsInDomain = this.allGroupsInDomain(domainId, groupIds);
		if (!allGroupsInDomain) {
			throw InvalidUserActionException.forSingleDomain();
		}
	}

}
