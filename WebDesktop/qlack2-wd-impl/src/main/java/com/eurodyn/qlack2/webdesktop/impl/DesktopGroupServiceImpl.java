package com.eurodyn.qlack2.webdesktop.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import com.eurodyn.qlack2.fuse.aaa.api.ResourceService;
import com.eurodyn.qlack2.fuse.aaa.api.UserGroupService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.ResourceDTO;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.webdesktop.api.DesktopGroupService;
import com.eurodyn.qlack2.webdesktop.api.dto.UserGroupDTO;
import com.eurodyn.qlack2.webdesktop.api.request.EmptySignedRequest;
import com.eurodyn.qlack2.webdesktop.api.request.group.CreateGroupRequest;
import com.eurodyn.qlack2.webdesktop.api.request.group.DeleteGroupRequest;
import com.eurodyn.qlack2.webdesktop.api.request.group.GetGroupRequest;
import com.eurodyn.qlack2.webdesktop.api.request.group.MoveGroupRequest;
import com.eurodyn.qlack2.webdesktop.api.request.group.UpdateGroupRequest;
import com.eurodyn.qlack2.webdesktop.impl.util.ConverterUtil;
import com.eurodyn.qlack2.webdesktop.impl.util.DomainUtil;

@Singleton
@OsgiServiceProvider(classes = { DesktopGroupService.class })
@Transactional
public class DesktopGroupServiceImpl implements DesktopGroupService {
	@OsgiService @Inject
	private UserGroupService groupService;
	@Inject
	private DomainUtil domainUtil;
	@OsgiService @Inject
	private ResourceService resourceService;

	@Override
	public List<UserGroupDTO> getDomainsAsTree(EmptySignedRequest sreq) {
		List<GroupDTO> aaaDomains = groupService.listGroupsAsTree();
		List<UserGroupDTO> domains = ConverterUtil.groupDTOToUserGroupDTOList(aaaDomains);

		SignedTicket ticket = sreq.getSignedTicket();
		return domainUtil.filterGroups(ticket, domains);
	}

	@Override
	public List<UserGroupDTO> getDomains(EmptySignedRequest sreq) {
		List<GroupDTO> aaaDomains = groupService.getGroupChildren(null);
		List<UserGroupDTO> domains = ConverterUtil.groupDTOToUserGroupDTOList(aaaDomains);

		SignedTicket ticket = sreq.getSignedTicket();
		return domainUtil.filterGroups(ticket, domains);
	}

	@Override
	public UserGroupDTO getGroup(GetGroupRequest sreq) {
		domainUtil.checkCanViewGroup(sreq.getSignedTicket(), sreq.getGroupId());

		GroupDTO aaaGroup = groupService.getGroupByID(sreq.getGroupId(), !sreq.isIncludeRelatives());
		UserGroupDTO group = ConverterUtil.groupDTOToUserGroupDTO(aaaGroup);
		if (sreq.isIncludeUsers()) {
			List<String> groupUsers = new ArrayList<>();
			Set<String> groupUserIds = groupService.getGroupUsersIds(sreq.getGroupId(), false);
			groupUsers.addAll(groupUserIds);
			group.setUsers(groupUsers);
		}
		return group;
	}

	// --

	@Override
	public String createGroup(CreateGroupRequest sreq) {
		domainUtil.validateCreateGroupRequest(sreq);

		GroupDTO group = new GroupDTO();
		group.setName(sreq.getName());
		group.setDescription(sreq.getDescription());
		if (sreq.getParentGroupId() != null) {
			group.setParent(new GroupDTO());
			group.getParent().setId(sreq.getParentGroupId());
		}
		String groupId = groupService.createGroup(group);

		// Create a secure resource for the group in order to allow other apps
		// to use it for permissions / default operation templates
		ResourceDTO resource = new ResourceDTO();
		resource.setName(sreq.getName());
		if (sreq.getParentGroupId() != null) {
			resource.setDescription("User group");
		} else {
			resource.setDescription("User domain");
		}
		resource.setObjectID(groupId);
		resourceService.createResource(resource);

		return groupId;
	}

	@Override
	public void updateGroup(UpdateGroupRequest sreq) {
		domainUtil.validateUpdateGroupRequest(sreq);

		GroupDTO group = new GroupDTO();
		group.setId(sreq.getId());
		group.setName(sreq.getName());
		group.setDescription(sreq.getDescription());
		groupService.updateGroup(group);

		// Update group users
		updateGroupUsers(sreq.getId(), sreq.getUserIds());

		// Update the name of the relevant security resource
		ResourceDTO resource = resourceService.getResourceByObjectId(sreq.getId());
		resource.setName(sreq.getName());
		resourceService.updateResource(resource);
	}

	private void updateGroupUsers(String groupId, List<String> userIds) {
		Set<String> aaaUserIds = groupService.getGroupUsersIds(groupId, false);

		List<String> oldUserIds = new ArrayList<String>(aaaUserIds);
		List<String> newUserIds = userIds;
		Collections.sort(oldUserIds);
		Collections.sort(newUserIds);
		int oldIndex = 0;
		int newIndex = 0;
		while ((oldIndex < oldUserIds.size()) && (newIndex < newUserIds.size())) {
			String oldId = oldUserIds.get(oldIndex);
			String newId = newUserIds.get(newIndex);
			if (oldId.compareTo(newId) < 0) {
				groupService.removeUser(oldId, groupId);
				oldIndex++;
			} else if (oldId.compareTo(newId) > 0) {
				groupService.addUser(newId, groupId);
				newIndex++;
			} else {
				oldIndex++;
				newIndex++;
			}
		}
		for (int i = oldIndex; i < oldUserIds.size(); i++) {
			groupService.removeUser(oldUserIds.get(i), groupId);
		}
		for (int i = newIndex; i < newUserIds.size(); i++) {
			groupService.addUser(newUserIds.get(i), groupId);
		}
	}

	@Override
	public void deleteGroup(DeleteGroupRequest sreq) {
		domainUtil.validateDeleteGroupRequest(sreq);

		groupService.deleteGroup(sreq.getId());

		// Delete the relevant security resource
		resourceService.deleteResourceByObjectId(sreq.getId());
	}

	@Override
	public void moveGroup(MoveGroupRequest sreq) {
		domainUtil.validateMoveGroupRequest(sreq);

		groupService.moveGroup(sreq.getId(), sreq.getNewParentId());
	}
}
