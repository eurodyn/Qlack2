package com.eurodyn.qlack2.webdesktop.impl;

import static com.eurodyn.qlack2.webdesktop.impl.util.Constants.USER_EMAIL;
import static com.eurodyn.qlack2.webdesktop.impl.util.Constants.USER_FIRST_NAME;
import static com.eurodyn.qlack2.webdesktop.impl.util.Constants.USER_LAST_NAME;
import static com.eurodyn.qlack2.webdesktop.impl.util.Constants.USER_STATUS_ACTIVE;
import static com.eurodyn.qlack2.webdesktop.impl.util.Constants.USER_STATUS_INACTIVE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import com.eurodyn.qlack2.fuse.aaa.api.UserGroupService;
import com.eurodyn.qlack2.fuse.aaa.api.UserService;
import com.eurodyn.qlack2.fuse.aaa.api.criteria.UserSearchCriteria;
import com.eurodyn.qlack2.fuse.aaa.api.criteria.UserSearchCriteria.SortColumn;
import com.eurodyn.qlack2.fuse.aaa.api.criteria.UserSearchCriteria.SortType;
import com.eurodyn.qlack2.fuse.aaa.api.criteria.UserSearchCriteria.UserSearchCriteriaBuilder;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserAttributeDTO;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.webdesktop.api.DesktopService;
import com.eurodyn.qlack2.webdesktop.api.DesktopUserService;
import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;
import com.eurodyn.qlack2.webdesktop.api.request.user.CreateUserRequest;
import com.eurodyn.qlack2.webdesktop.api.request.user.DeleteUserRequest;
import com.eurodyn.qlack2.webdesktop.api.request.user.GetUserRequest;
import com.eurodyn.qlack2.webdesktop.api.request.user.GetUserUncheckedRequest;
import com.eurodyn.qlack2.webdesktop.api.request.user.GetUsersRequest;
import com.eurodyn.qlack2.webdesktop.api.request.user.IsUserRequest;
import com.eurodyn.qlack2.webdesktop.api.request.user.UpdateUserRequest;
import com.eurodyn.qlack2.webdesktop.impl.util.ConverterUtil;
import com.eurodyn.qlack2.webdesktop.impl.util.DomainUtil;

@Singleton
@OsgiServiceProvider(classes = {DesktopUserService.class})
@Transactional
public class DesktopUserServiceImpl implements DesktopUserService {
	@OsgiService @Inject
	private UserService userService;
	@OsgiService @Inject
	private UserGroupService groupService;
	@Inject
	private DomainUtil domainUtil;

//	public void setUserService(UserService userService) {
//		this.userService = userService;
//	}
//
//	public void setGroupService(UserGroupService groupService) {
//		this.groupService = groupService;
//	}
//
//	public void setDomainUtil(DomainUtil domainUtil) {
//		this.domainUtil = domainUtil;
//	}

	@Override
	public List<UserDTO> getUsers(GetUsersRequest sreq) {
		UserSearchCriteriaBuilder builder = UserSearchCriteriaBuilder
				.createCriteria()
				.sortByColumn(SortColumn.USERNAME, SortType.ASCENDING);
		if (sreq.getFilter() != null) {
			builder = builder
					.withUsernameLike(sreq.getFilter())
					.withAttributes(UserSearchCriteriaBuilder.or(
									new UserAttributeDTO(USER_FIRST_NAME, sreq.getFilter()),
									new UserAttributeDTO(USER_LAST_NAME, sreq.getFilter())));
		}
		if (sreq.getGroupId() != null) {
			List<String> groupIds = new ArrayList<>();
			groupIds.add(sreq.getGroupId());
			builder = builder.withGroupIdIn(groupIds);
		}

		UserSearchCriteria criteria = builder.build();
		List<com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO> aaaUsers = userService.findUsers(criteria);
		List<UserDTO> users = ConverterUtil.aaaUserDTOToUserDTOList(aaaUsers);

		SignedTicket ticket = sreq.getSignedTicket();
		return domainUtil.filterUsers(ticket, users);
	}

	@Override
	public UserDTO getUser(GetUserRequest sreq) {
		domainUtil.checkCanViewUser(sreq.getSignedTicket(), sreq.getUserId());
		return getUser(sreq.getUserId(), sreq.isIncludeGroups());
	}

	@Override
	public UserDTO getUserUnchecked(GetUserUncheckedRequest sreq) {
		return getUser(sreq.getUserId(), sreq.isIncludeGroups());
	}

	private UserDTO getUser(String userId, boolean includeGroups) {
		com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO aaaUserDto = userService.getUserById(userId);

		UserDTO userDto = ConverterUtil.aaaUserDTOToUserDTO(aaaUserDto);
		if (includeGroups) {
			Set<String> userGroupIds = groupService.getUserGroupsIds(userId);
			List<String> groupIds = new ArrayList<>();
			groupIds.addAll(userGroupIds);
			userDto.setGroups(groupIds);
		}

		return userDto;
	}

	@Override
	public boolean isUser(IsUserRequest sreq) {
		com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO aaaUserDto = userService.getUserById(sreq.getUserId());
		return aaaUserDto != null;
	}

	// --

	@Override
	public String createUser(CreateUserRequest sreq) {
		domainUtil.validateCreateUserRequest(sreq);

		// Create user
		com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO user = new com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO();
		user.setUsername(sreq.getUsername());
		user.setPassword(sreq.getPassword());
		user.setSuperadmin(sreq.isSuperadmin());
		user.setStatus(sreq.isActive() ? USER_STATUS_ACTIVE : USER_STATUS_INACTIVE);

		UserAttributeDTO firstNameAtt = new UserAttributeDTO();
		firstNameAtt.setName(USER_FIRST_NAME);
		firstNameAtt.setData(sreq.getFirstName());
		user.setAttribute(firstNameAtt);

		UserAttributeDTO lastNameAtt = new UserAttributeDTO();
		lastNameAtt.setName(USER_LAST_NAME);
		lastNameAtt.setData(sreq.getLastName());
		user.setAttribute(lastNameAtt);

		UserAttributeDTO emailAtt = new UserAttributeDTO();
		emailAtt.setName(USER_EMAIL);
		emailAtt.setData(sreq.getEmail());
		user.setAttribute(emailAtt);

		String userId = userService.createUser(user);

		// And assign the proper user groups
		for (String groupId : sreq.getGroupIds()) {
			groupService.addUser(userId, groupId);
		}

		return userId;
	}

	@Override
	public void updateUser(UpdateUserRequest sreq) {
		domainUtil.validateUpdateUserRequest(sreq);

		com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO user = new com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO();
		user.setId(sreq.getUserId());
		user.setUsername(sreq.getUsername());
		user.setPassword(sreq.getPassword());
		user.setSuperadmin(sreq.isSuperadmin());
		user.setStatus(sreq.isActive() ? USER_STATUS_ACTIVE : USER_STATUS_INACTIVE);

		UserAttributeDTO firstNameAtt = new UserAttributeDTO();
		firstNameAtt.setName(USER_FIRST_NAME);
		firstNameAtt.setData(sreq.getFirstName());
		user.setAttribute(firstNameAtt);

		UserAttributeDTO lastNameAtt = new UserAttributeDTO();
		lastNameAtt.setName(USER_LAST_NAME);
		lastNameAtt.setData(sreq.getLastName());
		user.setAttribute(lastNameAtt);

		UserAttributeDTO emailAtt = new UserAttributeDTO();
		emailAtt.setName(USER_EMAIL);
		emailAtt.setData(sreq.getEmail());
		user.setAttribute(emailAtt);

		// Update the user password only if it has been set
		userService.updateUser(user, sreq.getPassword() != null);

		// Update user groups
		updateUserGroups(sreq.getUserId(), sreq.getGroupIds());
	}

	private void updateUserGroups(String userId, List<String> groupIds) {
		Set<String> aaaGroupIds = groupService.getUserGroupsIds(userId);

		List<String> oldGroupIds = new ArrayList<String>(aaaGroupIds);
		List<String> newGroupIds = groupIds;
		Collections.sort(oldGroupIds);
		Collections.sort(newGroupIds);
		int oldIndex = 0;
		int newIndex = 0;
		while ((oldIndex < oldGroupIds.size()) && (newIndex < newGroupIds.size())) {
			String oldId = oldGroupIds.get(oldIndex);
			String newId = newGroupIds.get(newIndex);
			if (oldId.compareTo(newId) < 0) {
				groupService.removeUser(userId, oldId);
				oldIndex++;
			} else if (oldId.compareTo(newId) > 0) {
				groupService.addUser(userId, newId);
				newIndex++;
			} else {
				oldIndex++;
				newIndex++;
			}
		}
		for (int i = oldIndex; i < oldGroupIds.size(); i++) {
			groupService.removeUser(userId, oldGroupIds.get(i));
		}
		for (int i = newIndex; i < newGroupIds.size(); i++) {
			groupService.addUser(userId, newGroupIds.get(i));
		}
	}

	@Override
	public void deleteUser(DeleteUserRequest sreq) {
		domainUtil.validateDeleteUserRequest(sreq);

		userService.deleteUser(sreq.getUserId());
	}

}
