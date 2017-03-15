/*
* Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
*
* Licensed under the EUPL, Version 1.1 only (the "License").
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
* https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and
* limitations under the Licence.
*/
package com.eurodyn.qlack2.fuse.aaa.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import com.eurodyn.qlack2.fuse.aaa.api.UserGroupService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.aaa.api.exception.QInvalidGroupHierarchyException;
import com.eurodyn.qlack2.fuse.aaa.impl.model.Group;
import com.eurodyn.qlack2.fuse.aaa.impl.model.User;
import com.eurodyn.qlack2.fuse.aaa.impl.util.ConverterUtil;

/**
 *
 * @author European Dynamics SA
 */
@Transactional
@Singleton
@OsgiServiceProvider(classes = {UserGroupService.class})
public class UserGroupServiceImpl implements UserGroupService {
	@PersistenceContext(unitName = "fuse-aaa")
    private EntityManager em;

	@Override
	public String createGroup(GroupDTO groupDTO) {
		Group group = new Group();
		if(groupDTO.getId() != null) {
			group.setId(groupDTO.getId());
		}
		group.setName(groupDTO.getName());
		group.setDescription(groupDTO.getDescription());
		group.setObjectId(groupDTO.getObjectID());
		if (groupDTO.getParent() != null) {
			group.setParent(Group.find(groupDTO.getParent().getId(), em));
		}
		em.persist(group);

		return group.getId();
	}

	@Override
	public void updateGroup(GroupDTO groupDTO) {
		Group group = Group.find(groupDTO.getId(), em);
		group.setName(groupDTO.getName());
		group.setDescription(groupDTO.getDescription());
		group.setObjectId(groupDTO.getObjectID());
	}

	@Override
	public void deleteGroup(String groupID) {
		em.remove(Group.find(groupID, em));
	}

	@Override
	public void moveGroup(String groupID, String newParentId)
    		throws QInvalidGroupHierarchyException {
		Group group = Group.find(groupID, em);
		Group newParent = Group.find(newParentId, em);

		// Check the moving the group under the new parent will not
		// create a cyclic dependency.
		Group checkedGroup = newParent;
		while (checkedGroup != null) {
			if (checkedGroup.getId().equals(group.getId())) {
				throw new QInvalidGroupHierarchyException("Cannot move group with ID " + groupID
						+ " under group with ID " + newParentId
						+ " since this will create a cyclic dependency between groups.");
			}
			checkedGroup = checkedGroup.getParent();
		}

		group.setParent(newParent);
	}

	@Override
	public GroupDTO getGroupByID(String groupID, boolean lazyRelatives) {
		return ConverterUtil.groupToGroupDTO(Group.find(groupID,  em), lazyRelatives);
	}

	@Override
	public List<GroupDTO> getGroupsByID(Collection<String> groupIds, boolean lazyRelatives) {
		Query query = em.createQuery("SELECT g FROM Group g WHERE g.id in (:groupIds) ORDER BY g.name ASC");
		query.setParameter("groupIds", groupIds);
		return ConverterUtil.groupToGroupDTOList(query.getResultList(), lazyRelatives);
	}

	@Override
	public GroupDTO getGroupByName(String groupName, boolean lazyRelatives) {
		return ConverterUtil.groupToGroupDTO(
				Group.findByName(groupName, em), lazyRelatives);
	}

	@Override
	public GroupDTO getGroupByObjectId(String objectId, boolean lazyRelatives) {
		return ConverterUtil.groupToGroupDTO(
				Group.findByObjectId(objectId, em), lazyRelatives);
	}

	@Override
	public List<GroupDTO> listGroups() {
		Query query = em.createQuery("SELECT g FROM Group g ORDER BY g.name ASC");
		return ConverterUtil.groupToGroupDTOList(
				query.getResultList(), true);
	}

	@Override
	public List<GroupDTO> listGroupsAsTree() {
		Query query = em.createQuery("SELECT g FROM Group g WHERE g.parent IS NULL ORDER BY g.name ASC");
		return ConverterUtil.groupToGroupDTOList(
				query.getResultList(), false);
	}

	@Override
	public GroupDTO getGroupParent(String groupID) {
		Group group = Group.find(groupID, em);
		return ConverterUtil.groupToGroupDTO(group.getParent(), true);
	}

	@Override
	public List<GroupDTO> getGroupChildren(String groupID) {
		Query query = null;
		if (groupID == null) {
			query = em.createQuery("SELECT g FROM Group g WHERE g.parent IS NULL ORDER BY g.name ASC");
		} else {
			query = em.createQuery("SELECT g FROM Group g WHERE g.parent.id = :parentId ORDER BY g.name ASC");
			query.setParameter("parentId", groupID);
		}
		return ConverterUtil.groupToGroupDTOList(
				query.getResultList(), true);

	}

	/**
	 * Returns the users belonging to a given group and (optionally) its hierarchy
	 * @param group The group the users of which to retrieve
	 * @param includeAncestors true if users belonging to ancestors of this group
	 * (the group's parent and its parent's parent, etc.) should be retrieved
	 * @param includeDescendants true if users belonging to descendants of this
	 * group (the group's children and its children's children, etc.) should
	 * be retrieved
	 * @return The IDs of the users belonging to the specified group hierarchy.
	 */
	private Set<String> getGroupHierarchyUsersIds(Group group, boolean includeAncestors,
			boolean includeDescendants) {
		Set<String> retVal = new HashSet<>(group.getUsers().size());
		for (User user : group.getUsers()) {
			retVal.add(user.getId());
		}

		// If children group users should be included iterate over them
		// (and their children recursively) and add their users to
		// the return value. Same for the group parents.
		if (includeDescendants) {
			for (Group child : group.getChildren()) {
				retVal.addAll(getGroupHierarchyUsersIds(child, false, true));
			}
		}
		if ((includeAncestors) && (group.getParent() != null)) {
			retVal.addAll(getGroupHierarchyUsersIds(group.getParent(), true, false));
		}

		return retVal;
	}

	private void addUsers(Collection<String> userIDs, Group group) {
		for (String userID : userIDs) {
			User user = User.find(userID, em);
			// TODO ask how else we do it
			if (group.getUsers() == null){
				group.setUsers(new ArrayList<User>());
			}
			group.getUsers().add(user);
			if (user.getGroups() == null) {
				user.setGroups(new ArrayList<Group>());
			}
			user.getGroups().add(group);
		}
	}

	@Override
	public void addUser(String userID, String groupId) {
		List<String> userIds = new ArrayList<>(1);
		userIds.add(userID);
		addUsers(userIds, groupId);
	}

	@Override
	public void addUsers(Collection<String> userIDs, String groupID) {
		addUsers(userIDs, Group.find(groupID, em));
		
	}

	@Override
	public void addUserByGroupName(String userId, String groupName) {
		List<String> userIds = new ArrayList<>(1);
		userIds.add(userId);
		addUsersByGroupName(userIds, groupName);
	}

	@Override
	public void addUsersByGroupName(Collection<String> userIDs, String groupName) {
		addUsers(userIDs, Group.findByName(groupName, em));
	}

	@Override
	public void removeUser(String userID, String groupID) {
		List<String> userIds = new ArrayList<>(1);
		userIds.add(userID);
		removeUsers(userIds, groupID);
	}

	@Override
	public void removeUsers(Collection<String> userIDs, String groupID) {
		Group group = Group.find(groupID, em);
		for (String userID : userIDs) {
			User user = User.find(userID, em);
			group.getUsers().remove(user);
		}
	}

	@Override
	public Set<String> getGroupUsersIds(String groupID, boolean includeChildren) {
		Group group = Group.find(groupID, em);
		return getGroupHierarchyUsersIds(group, false, includeChildren);
	}

	@Override
	public Set<String> getUserGroupsIds(String userID) {
		User user = User.find(userID, em);
		Set<String> retVal = new HashSet<>();
		if (user.getGroups() != null) {
			for (Group group : user.getGroups()) {
				retVal.add(group.getId());
			}
		}
		return retVal;
	}
}
