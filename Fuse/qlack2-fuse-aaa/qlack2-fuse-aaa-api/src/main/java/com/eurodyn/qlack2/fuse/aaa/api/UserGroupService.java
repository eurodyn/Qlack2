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
package com.eurodyn.qlack2.fuse.aaa.api;

import com.eurodyn.qlack2.fuse.aaa.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.aaa.api.exception.QInvalidGroupHierarchyException;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Manage access control groups.
 * AAA groups have a hierarchical structure in which every group can have
 * a parent and one or more children. When a user becomes member of a group
 * they are implicitly considered also a member of all the group's parents.
 * For example in the hierarchy:
 * Organisation
 * |
 * | (has child)
 * |
 * Department
 * |
 * | (has child)
 * |
 * Team
 * a user who is a member of the group "Team" is also (implicitly)
 * a member of groups "Department" and "Organisation"
 *
 * @author European Dynamics SA
 */
public interface UserGroupService {

  /**
   * Creates a new access control group
   *
   * @param groupDTO The information of the group to create
   * @return The ID of the newly created group
   */
  String createGroup(GroupDTO groupDTO);


  /**
   * Updates the details of a user group. This method only
   * updates the group's name, description and objectId. In order to update
   * the group's parent use the moveGroup method instead.
   *
   * @param groupDTO The details of the group to update.
   */
  void updateGroup(GroupDTO groupDTO);


  /**
   * Deletes an existing group. Please note that if this group has
   * children its children will also be deleted.
   *
   * @param groupID The ID of the group to delete
   */
  void deleteGroup(String groupID);


  /**
   * Moves a group assigning it to a new parent group
   *
   * @param groupID The ID of the group to move
   * @param newParentId The ID of the new parent group. Id this parameter
   * is left null then the group will be moved to the top level of the group
   * hierarchy (it will have no parent).
   * @throws QInvalidGroupHierarchyException If the group cannot be assigned
   * to the specified parent without creating an invalid group hierarchy. An
   * invalid hierarchy is created when a cyclic dependency exists between groups,
   * for example A is the parent of B, B is the parent of C, C is the parent of A.
   */
  void moveGroup(String groupID, String newParentId)
    throws QInvalidGroupHierarchyException;


  /**
   * Retrieves the information of a group identified by its ID
   *
   * @param groupID The ID of the group to retrieve
   * @param lazyRelatives If true the parent and children attributes
   * of the returned group will not be fetched. If false the whole
   * hierarchy to which this group is contained will be returned.
   * @return The details of the requested group or null
   * if a group with the specified ID does not exist.
   */
  GroupDTO getGroupByID(String groupID, boolean lazyRelatives);

  /**
   * Retrieves the information of a list of groups identified by their ID
   *
   * @param groupIDs The IDs of the groups to retrieve
   * @param lazyRelatives If true the parent and children attributes
   * of the returned group will not be fetched. If false the whole
   * hierarchy to which this group is contained will be returned.
   * @return The specified groups ordered by group name ascending. Please
   * note that any groupIDs passed to this method which do not correspond
   * to existing groups will be ignored, in which case the size of list returned
   * by this method will be less than the size of the collection of IDs passed
   * to it.
   */
  List<GroupDTO> getGroupsByID(Collection<String> groupIDs, boolean lazyRelatives);

  /**
   * Retrieves the information of a group identified by its name
   *
   * @param groupName The name of the group to retrieve
   * @param lazyRelatives If true the parent and children attributes
   * of the returned group will not be fetched. If false the whole
   * hierarchy to which this group is contained will be returned.
   * @return The details of the requested group or null
   * if a group with the specified name does not exist.
   */
  GroupDTO getGroupByName(String groupName, boolean lazyRelatives);


  /**
   * Retrieves the information of multiple groups identified by their name.
   * @param groupNames The names of the group to retrieve.
   * @param lazyRelatives If true the parent and children attributes
   * of the returned group will not be fetched. If false the whole
   * hierarchy to which this group is contained will be returned.
   * @return The details of the requested groups or an empty list if no groups found.
   */
  List<GroupDTO> getGroupByNames(List<String> groupNames, boolean lazyRelatives);

  /**
   * Retrieves the information of a group identified by its objectId
   *
   * @param objectID The objectID of the group to retrieve
   * @param lazyRelatives If true the parent and children attributes
   * of the returned group will not be fetched. If false the whole
   * hierarchy to which this group is contained will be returned.
   * @return The details of the requested group or null
   * if a group with the specified objectID does not exist.
   */
  GroupDTO getGroupByObjectId(String objectID, boolean lazyRelatives);


  /**
   * Retrieves all the groups registered in the system. The parent
   * and children attributes of the returned groups are not fetched.
   *
   * @return A list of all the groups registered in the system
   * ordered by group name ascending
   */
  List<GroupDTO> listGroups();


  /**
   * Retrieves all the groups registered in the system as a hierarchy
   * tree.
   *
   * @return A list of the groups registered in the system in the top
   * level of the group hierarchy (the groups having no parents). Through
   * the children attributes of these groups all other groups registered
   * in the system can be accessed. The retrieved groups are ordered
   * by name ascending.
   */
  List<GroupDTO> listGroupsAsTree();

  /**
   * Retrieves the parent group of a group. The parent and children groups
   * of the retrieved (parent) group are not fetched.
   *
   * @param groupID The group the parent of which to retrieve
   * @return The details of the specified group's parent or null of the
   * specified group is at the top of the group hierarchy.
   */
  GroupDTO getGroupParent(String groupID);

  /**
   * Retrieves the children groups of a group. The parent an children groups
   * of the retrieved (children) groups are not fetched.
   *
   * @param groupID The group the children of which to retrieve
   * @return The details of the specified group's children ordered by group name ascending
   */
  List<GroupDTO> getGroupChildren(String groupID);


  /**
   * Adds a user to a group
   *
   * @param userID The id of the user to add
   * @param groupID The id of the group in which the user will be added
   */
  void addUser(String userID, String groupID);

  /**
   * Adds a set of users to a group
   *
   * @param userIDs The ids of the users to add
   * @param groupID The id of the group in which the users will be added
   */
  void addUsers(Collection<String> userIDs, String groupID);

  /**
   * Adds a user to a group. The group is identified by its name
   *
   * @param userID The id of the user to add
   * @param groupName The name of the group in which the user will be added
   */
  void addUserByGroupName(String userID, String groupName);

  /**
   * Adds a set of users to a group. The group is identified by its name
   *
   * @param userIDs The IDs of the users to add
   * @param groupName The name of the group in which the users will be added
   */
  void addUsersByGroupName(Collection<String> userIDs, String groupName);


  /**
   * Removes a user form a group
   *
   * @param userID The id of the user to remove
   * @param groupID The id of the group
   */
  void removeUser(String userID, String groupID);

  /**
   * Removes a set of users from a group
   *
   * @param userIDs The ids of the users to remove
   * @param groupID The id of the group
   */
  void removeUsers(Collection<String> userIDs, String groupID);


  /**
   * Retrieves the IDs of the users who are members of a specific group
   *
   * @param groupID The ID of the group whose members to retrieve
   * @param includeChildren If true this method will also return users which are
   * member of children of this group and thus are also implicitly
   * members of this group. If false only the users which have been given
   * membership to the group explicitly will be returned.
   * @return The IDs of the retrieved users
   */
  Set<String> getGroupUsersIds(String groupID, boolean includeChildren);


  /**
   * Retrieves the IDs of the groups a certain user belongs to. Please note
   * that this method only returns groups to which the user has been explicitly
   * added, ie. it will not return parents of the user's group to which the
   * user is also implicitly a member.
   *
   * @param userID The id of the user whose groups to retrieve
   * @return The ids of the retrieved groups
   */
  Set<String> getUserGroupsIds(String userID);

  /**
   * Retrieves the names of the users who are members of specific groups
   *
   * @param groupIDs The ids of the groups whose members to retrieve
   * @return The names of the retrieved users
   */
  Set<String> getGroupUsersNames(Collection<String> groupIDs);
}
