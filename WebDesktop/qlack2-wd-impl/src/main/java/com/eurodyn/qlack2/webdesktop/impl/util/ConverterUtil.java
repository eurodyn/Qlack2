package com.eurodyn.qlack2.webdesktop.impl.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.eurodyn.qlack2.fuse.aaa.api.dto.GroupDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.ApplicationInfo;
import com.eurodyn.qlack2.webdesktop.api.dto.ApplicationInfo.Identification;
import com.eurodyn.qlack2.webdesktop.api.dto.ApplicationInfo.Instantiation;
import com.eurodyn.qlack2.webdesktop.api.dto.ApplicationInfo.Menu;
import com.eurodyn.qlack2.webdesktop.api.dto.ApplicationInfo.Window;
import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserGroupDTO;
import com.eurodyn.qlack2.webdesktop.impl.model.Application;

public class ConverterUtil {

	public static ApplicationInfo applicationToApplicationInfo(Application entity) {
		ApplicationInfo dto = new ApplicationInfo();
		dto.setActive(entity.isActive());

		dto.setIdentification(new Identification());
		dto.getIdentification().setUniqueId(entity.getAppUuid());
		dto.getIdentification().setTitleKey(entity.getTitleKey());
		dto.getIdentification().setDescriptionKey(entity.getDescriptionKey());
		dto.getIdentification().setVersion(entity.getVersion());

		dto.setInstantiation(new Instantiation());
		dto.getInstantiation().setPath(entity.getPath());
		dto.getInstantiation().setIndex(entity.getIndex());
		dto.getInstantiation().setMultipleInstances(entity.isMultipleInstances());
		dto.getInstantiation().setRestrictAccess(entity.isRestrictAccess());
		dto.getInstantiation().setTranslationsGroup(entity.getTranslationsGroup());

		dto.setMenu(new Menu());
		dto.getMenu().setIcon(entity.getIcon());
		dto.getMenu().setIconSmall(entity.getIconSmall());
		dto.getMenu().setBgColor(entity.getBgColor());
		dto.getMenu().setSystem(entity.isSystem());
		dto.getMenu().setShowTitle(entity.isShowTitle());

		dto.setWindow(new Window());
		dto.getWindow().setHeight(entity.getHeight());
		dto.getWindow().setMinHeight(entity.getMinHeight());
		dto.getWindow().setWidth(entity.getWidth());
		dto.getWindow().setMinWidth(entity.getMinWidth());
		dto.getWindow().setClosable(entity.isClosable());
		dto.getWindow().setDraggable(entity.isDraggable());
		dto.getWindow().setMaximizable(entity.isMaximizable());
		dto.getWindow().setMinimizable(entity.isMinimizable());
		dto.getWindow().setResizable(entity.isResizable());

		return dto;
	}

	public static Application applicationInfoToApplication(ApplicationInfo dto) {
		Application entity = new Application();
		entity.setActive(dto.isActive());

		entity.setAppUuid(dto.getIdentification().getUniqueId());
		entity.setTitleKey(dto.getIdentification().getTitleKey());
		entity.setDescriptionKey(dto.getIdentification().getDescriptionKey());
		entity.setVersion(dto.getIdentification().getVersion());

		entity.setPath(dto.getInstantiation().getPath());
		entity.setIndex(dto.getInstantiation().getIndex());
		entity.setMultipleInstances(dto.getInstantiation().getMultipleInstances());
		entity.setRestrictAccess(dto.getInstantiation().getRestrictAccess());
		entity.setTranslationsGroup(dto.getInstantiation().getTranslationsGroup());

		entity.setIcon(dto.getMenu().getIcon());
		entity.setIconSmall(dto.getMenu().getIconSmall());
		entity.setBgColor(dto.getMenu().getBgColor());
		entity.setSystem(dto.getMenu().isSystem());
		entity.setShowTitle(dto.getMenu().isShowTitle());
		
		entity.setHeight(dto.getWindow().getHeight());
		entity.setMinHeight(dto.getWindow().getMinHeight());
		entity.setWidth(dto.getWindow().getWidth());
		entity.setMinWidth(dto.getWindow().getMinWidth());
		entity.setClosable(dto.getWindow().isClosable());
		entity.setDraggable(dto.getWindow().isDraggable());
		entity.setMaximizable(dto.getWindow().isMaximizable());
		entity.setMinimizable(dto.getWindow().isMinimizable());
		entity.setResizable(dto.getWindow().isResizable());

		return entity;
	}

	private static class UserGroupComparator implements Comparator<UserGroupDTO> {
		@Override
		public int compare(UserGroupDTO o1, UserGroupDTO o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}

	public static UserGroupDTO groupDTOToUserGroupDTO(GroupDTO groupDTO) {
		if (groupDTO == null) {
			return null;
		}

		UserGroupDTO userGroupDTO = new UserGroupDTO();
		userGroupDTO.setId(groupDTO.getId());
		userGroupDTO.setName(groupDTO.getName());
		userGroupDTO.setDescription(groupDTO.getDescription());

		if (groupDTO.getParent() != null) {
			userGroupDTO.setParentGroup(groupDTOToUserGroupDTO(groupDTO.getParent()));
		}
		if (groupDTO.getChildren() != null) {
			userGroupDTO.setChildGroups(new ArrayList<UserGroupDTO>());
			for (GroupDTO child : groupDTO.getChildren()) {
				userGroupDTO.getChildGroups().add(groupDTOToUserGroupDTO(child));
			}
			Collections.sort(userGroupDTO.getChildGroups(), new UserGroupComparator());
		}

		return userGroupDTO;
	}

	public static List<UserGroupDTO> groupDTOToUserGroupDTOList(List<GroupDTO> groupDTOs) {
		if (groupDTOs == null) {
			return null;
		}

		List<UserGroupDTO> userGroupDTOs = new ArrayList<>();
		for (GroupDTO group : groupDTOs) {
			userGroupDTOs.add(groupDTOToUserGroupDTO(group));
		}
		return userGroupDTOs;
	}

	public static UserDTO aaaUserDTOToUserDTO(com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO aaaUser) {
		if (aaaUser == null) {
			return null;
		}

		UserDTO user = new UserDTO();
		user.setId(aaaUser.getId());
		user.setUsername(aaaUser.getUsername());
		user.setFirstName(aaaUser.getAttributeData(Constants.USER_FIRST_NAME));
		user.setLastName(aaaUser.getAttributeData(Constants.USER_LAST_NAME));
		user.setEmail(aaaUser.getAttributeData(Constants.USER_EMAIL));
		if (aaaUser.getStatus() == Constants.USER_STATUS_ACTIVE) {
			user.setActive(true);
		} else {
			user.setActive(false);
		}
		user.setSuperadmin(aaaUser.isSuperadmin());
		return user;
	}

	public static List<UserDTO> aaaUserDTOToUserDTOList(List<com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO> aaaUsers) {
		if (aaaUsers == null) {
			return null;
		}

		List<UserDTO> users = new ArrayList<>();
		for (com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO user : aaaUsers) {
			users.add(aaaUserDTOToUserDTO(user));
		}
		return users;
	}
}
