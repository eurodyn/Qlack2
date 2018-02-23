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

import com.eurodyn.qlack2.fuse.aaa.api.dto.GroupHasOperationDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.OperationDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.ResourceDTO;
import com.eurodyn.qlack2.fuse.aaa.api.exception.QDynamicOperationException;

import java.util.List;
import java.util.Set;

/**
 * @author European Dynamics SA
 */
public interface OperationService {
	/**
	 * Creates an operation in the system
	 *
	 * @param operationDTO The information of the operation to create
	 * @return The id of the newly created operation
	 */
	String createOperation(OperationDTO operationDTO);

	/**
	 * Updates an operation
	 *
	 * @param operationDTO The details of the operation to update. The operation ID is
	 *                     used to identify the operation while the rest of the operation
	 *                     properties are updated in the database
	 */
	void updateOperation(OperationDTO operationDTO);

	/**
	 * Deletes an operation from the system
	 *
	 * @param operationID The id of the operation to delete
	 */
	void deleteOperation(String operationID);

	/**
	 * Retrieves all operations
	 *
	 * @return The retrieved operations
	 */
	List<OperationDTO> getAllOperations();

	/**
	 * Retrieves an operation by its name
	 *
	 * @param operationName The name of the operation to retrieve
	 * @return The retrieved operation
	 */
	OperationDTO getOperationByName(String operationName);

	/**
	 * Grants a generic operation to a user (ie. an operation not executed on a
	 * specific resource). If the operation is already granted to the user the
	 * value of the isDeny flag is updated.
	 *
	 * @param userID        The ID of the user to grant the operation to
	 * @param operationName The name of the operation
	 * @param isDeny        True if the operation should be denied (negative permission),
	 *                      false otherwise.
	 */
	void addOperationToUser(String userID, String operationName, boolean isDeny);

	/**
	 * Grants an operation to a user on a specific resource. If the operation is
	 * already granted to the user the value of the isDeny flag is updated.
	 *
	 * @param userID        The ID of the user to grant the operation to
	 * @param operationName The name of the operation
	 * @param isDeny        True if the operation should be denied (negative permission),
	 *                      false otherwise.
	 * @param resourceID    The ID of the resource to associate this operation
	 *                      with.
	 */
	void addOperationToUser(String userID, String operationName, String resourceID, boolean isDeny);

	/**
	 * Grants a set of operations to a user based on an operation template
	 *
	 * @param userID     The ID of the user to grant the operations to
	 * @param templateID The ID of the template to use
	 */
	void addOperationsToUserFromTemplateID(String userID, String templateID);

	/**
	 * Grants a set of operations to a user based on an operation template
	 *
	 * @param userID       The ID of the user to grant the operations to
	 * @param templateName The name of the template to use
	 */
	void addOperationsToUserFromTemplateName(String userID, String templateName);

	/**
	 * Grants a generic operation to a group (ie. an operation not executed on a
	 * specific resource). If the operation is already granted to the group the
	 * value of the isDeny flag is updated.
	 *
	 * @param groupID       The ID of the group to grant the operation to
	 * @param operationName The name of the operation
	 * @param isDeny        True if the operation should be denied (negative permission),
	 *                      false otherwise.
	 */
	void addOperationToGroup(String groupID, String operationName, boolean isDeny);

	/**
	 * Grants an operation to a group on a specific resource. If the operation
	 * is already granted to the user the value of the isDeny flag is updated.
	 *
	 * @param groupID       The ID of the group to grant the operation to
	 * @param operationName The name of the operation
	 * @param isDeny        True if the operation should be denied (negative permission),
	 *                      false otherwise.
	 * @param resourceID    The ID of the resource to associate this operation
	 *                      with.
	 */
	void addOperationToGroup(String groupID, String operationName, String resourceID, boolean isDeny);

	/**
	 * Grants a set of operations to a group based on an operation template
	 *
	 * @param groupID    The ID of the group to grant the operations to
	 * @param templateID The ID of the template to use
	 */
	void addOperationsToGroupFromTemplateID(String groupID, String templateID);

	/**
	 * Grants a set of operations to a group based on an operation template
	 *
	 * @param groupID      The ID of the group to grant the operations to
	 * @param templateName The name of the template to use
	 */
	void addOperationsToGroupFromTemplateName(String groupID, String templateName);

	/**
	 * Removes a generic operation granted to a user.
	 *
	 * @param userID        The ID of the user
	 * @param operationName The name of the operation to remove
	 */
	void removeOperationFromUser(String userID, String operationName);

	/**
	 * Removes an operation on a resource granted to a user.
	 *
	 * @param userID        The ID of the user
	 * @param operationName The name of the operation to remove
	 * @param resourceID    The ID of the resource.
	 */
	void removeOperationFromUser(String userID, String operationName, String resourceID);

	/**
	 * Removes a generic operation granted to a group.
	 *
	 * @param groupID       The ID of the group
	 * @param operationName The name of the operation to remove
	 */
	void removeOperationFromGroup(String groupID, String operationName);

	/**
	 * Removes an operation on a resource granted to a group.
	 *
	 * @param groupID       The ID of the group
	 * @param operationName The name of the operation to remove
	 * @param resourceID    The ID of the resource.
	 */
	void removeOperationFromGroup(String groupID, String operationName, String resourceID);

	/**
	 * Checks whether a specific operation, for a specific user is allowed. This
	 * method makes the same checks as method isPermitted(String, String,
	 * String, boolean) with the only difference that checks are performed for a
	 * generic operation and not on a specific resource.
	 *
	 * @param userID        The id of the user for whom to check the operation
	 * @param operationName The name of the operation to check
	 * @return true, if the user is allowed the operation. false, if the user is
	 * denied the operation. null, if there is no information available
	 * to reply accordingly.
	 * @throws QDynamicOperationException If an error occurs while evaluating a dynamic operation.
	 */
	Boolean isPermitted(String userID, String operationName);

	/**
	 * Checks whether a specific operation, for a specific user, for a specific
	 * resource is allowed. The algorithm checks: 1) Whether the user is
	 * assigned the operation. In this case: a. If the operation is dynamic the
	 * dynamic code is executed to decide whether the user is permitted the
	 * operation. b. If the operation is not dynamic the deny flag of the
	 * UserHasOperation entity is used to decide whether the user is permitted
	 * the operation. 2) Whether one or more of the user groups are assigned the
	 * operation. This check is performed by calling the isPermittedForGroup
	 * method which checks the whole group hierarchy recursively until a group
	 * permission is found. In case multiple group permissions are found from
	 * multiple user group hierarchies the prioritisePositive flag is used to
	 * decide which permission should be taken into account (positive or
	 * negative).
	 *
	 * @param userID           The id of the user for whom to check the operation
	 * @param operationName    The name of the operation to check
	 * @param resourceObjectID The object of the resource for which to perform the check
	 * @return true, if the user is allowed the operation. false, if the user is
	 * denied the operation. null, if there is no information available
	 * to reply accordingly.
	 * @throws QDynamicOperationException If an error occurs while evaluating a dynamic operation.
	 */
	Boolean isPermitted(String userID, String operationName, String resourceObjectID);

	/**
	 * Checks whether a specific operation, for a specific group is allowed. The
	 * check is performed by checking whether the specified group is assigned
	 * the specific operation and, if yes, execute the operation code if the
	 * operation is dynamic or check the deny flag of the operation assignment
	 * to decide whether the group is permitted the operation. In case no
	 * assignment for this operation can be found for the group the group's
	 * ancestors are checked recursively until we arrive to a decision.
	 *
	 * @param groupID       The id of the group for which to check the operation
	 * @param operationName The name of the operation to check
	 * @return true, if the group is allowed the operation. false, if the group
	 * is denied the operation. null, if there is no information
	 * available to reply accordingly.
	 * @throws QDynamicOperationException If an error occurs while evaluating a dynamic operation.
	 */
	Boolean isPermittedForGroup(String groupID, String operationName);

	/**
	 * Checks whether a specific operation, for a specific group is allowed on a
	 * specific resource. Makes the same checks as the isPermittedForGroup
	 * method with the different that checks are made on a specific resource and
	 * not for a generic operation.
	 *
	 * @param groupID          The id of the group for which to check the operation
	 * @param operationName    The name of the operation to check
	 * @param resourceObjectID The object of the resource for which to perform the check
	 * @return true, if the group is allowed the operation. false, if the group
	 * is denied the operation. null, if there is no information
	 * available to reply accordingly.
	 * @throws QDynamicOperationException If an error occurs while evaluating a dynamic operation.
	 */
	Boolean isPermittedForGroup(String groupID, String operationName, String resourceObjectID);

	/**
	 * Retrieves the users who can perform a specific operation. This method
	 * will return the same results as if we had executed the isPermitted method
	 * for each individual user registered in the system.
	 *
	 * @param operationName   The name of the operation to check
	 * @param checkUserGroups If true the permissions of each user's groups will also be
	 *                        checked in order to find the users who can perform the
	 *                        specified operation. Otherwise, only permissions assigned
	 *                        explicitly to users will be checked.
	 * @return The IDs of the users who are allowed to perform the operation
	 * @throws QDynamicOperationException If an error occurs while evaluating a dynamic operation.
	 */
	Set<String> getAllowedUsersForOperation(String operationName, boolean checkUserGroups);

	/**
	 * Retrieves the users who can perform a specific operation. This method
	 * will return the same results as if we had executed the isPermitted method
	 * for each individual user registered in the system.
	 *
	 * @param operationName    The name of the operation to check for
	 * @param resourceObjectID The ID of the resource object to check.
	 * @param checkUserGroups  If true the permissions of each user's groups will also be
	 *                         checked in order to find the users who can perform the
	 *                         specified operation. Otherwise, only permissions assigned
	 *                         explicitly to users will be checked.
	 * @return The IDs of the users who are allowed to perform the specified
	 * operation on the specified resource
	 * @throws QDynamicOperationException If an error occurs while evaluating a dynamic operation.
	 */
	Set<String> getAllowedUsersForOperation(String operationName, String resourceObjectID, boolean checkUserGroups);

	/**
	 * Retrieves the users who have been blocked from performing a specific
	 * operation. This method will return the same results as if we had executed
	 * the isPermitted method for each individual user registered in the system.
	 *
	 * @param operationName   The name of the operation to check
	 * @param checkUserGroups If true the permissions of each user's groups will also be
	 *                        checked in order to find the users who can perform the
	 *                        specified operation. Otherwise, only permissions assigned
	 *                        explicitly to users will be checked. belongs to. If true
	 *                        positive permissions will be given priority, otherwise
	 *                        negative permissions will be given priority.
	 * @return The IDs of the users who have been blocked from performing the
	 * operation
	 * @throws QDynamicOperationException If an error occurs while evaluating a dynamic operation.
	 */
	Set<String> getBlockedUsersForOperation(String operationName, boolean checkUserGroups);

	/**
	 * Retrieves the users who have been blocked from performing a specific
	 * operation. This method will return the same results as if we had executed
	 * the isPermitted method for each individual user registered in the system.
	 *
	 * @param operationName    The name of the operation to check for
	 * @param resourceObjectID The ID of the resource object to check.
	 * @param checkUserGroups  If true the permissions of each user's groups will also be
	 *                         checked in order to find the users who can perform the
	 *                         specified operation. Otherwise, only permissions assigned
	 *                         explicitly to users will be checked. belongs to. If true
	 *                         positive permissions will be given priority, otherwise
	 *                         negative permissions will be given priority.
	 * @return The IDs of the users who have been blocked from performing the
	 * specified operation on the specified resource
	 * @throws QDynamicOperationException If an error occurs while evaluating a dynamic operation.
	 */
	Set<String> getBlockedUsersForOperation(String operationName, String resourceObjectID, boolean checkUserGroups);

	/**
	 * Retrieves the groups who can perform a specific operation. This method
	 * will return the same results as if we had executed the isPermitted method
	 * for each individual group registered in the system.
	 *
	 * @param operationName  The name of the operation to check
	 * @param checkAncestors If true the ancestors of each group will also be checked,
	 *                       otherwise only the operations assigned explicitly to each
	 *                       group will be checked.
	 * @return The IDs of the groups who are allowed to perform the operation
	 * @throws QDynamicOperationException If an error occurs while evaluating a dynamic operation.
	 */
	Set<String> getAllowedGroupsForOperation(String operationName, boolean checkAncestors);

	/**
	 * Retrieves the groups who can perform a specific operation on a resource.
	 * This method will return the same results as if we had executed the
	 * isPermitted method for each individual group registered in the system.
	 *
	 * @param operationName    The name of the operation to check
	 * @param resourceObjectID The ID of the resource object to check.
	 * @param checkAncestors   If true the ancestors of each group will also be checked,
	 *                         otherwise only the operations assigned explicitly to each
	 *                         group will be checked.
	 * @return The IDs of the groups who are allowed to perform the operation
	 * @throws QDynamicOperationException If an error occurs while evaluating a dynamic operation.
	 */
	Set<String> getAllowedGroupsForOperation(String operationName, String resourceObjectID, boolean checkAncestors);

	/**
	 * Retrieves the groups who have been blocked form performing a specific
	 * operation. This method will return the same results as if we had executed
	 * the isPermitted method for each individual group registered in the
	 * system.
	 *
	 * @param operationName  The name of the operation to check
	 * @param checkAncestors If true the ancestors of each group will also be checked,
	 *                       otherwise only the operations assigned explicitly to each
	 *                       group will be checked.
	 * @return The IDs of the groups who have been blocked form performing the
	 * operation
	 * @throws QDynamicOperationException If an error occurs while evaluating a dynamic operation.
	 */
	Set<String> getBlockedGroupsForOperation(String operationName, boolean checkAncestors);

	/**
	 * Retrieves the groups who have been blocked form performing a specific
	 * operation on a resource. This method will return the same results as if
	 * we had executed the isPermitted method for each individual group
	 * registered in the system.
	 *
	 * @param operationName    The name of the operation to check
	 * @param resourceObjectID The ID of the resource object to check.
	 * @param checkAncestors   If true the ancestors of each group will also be checked,
	 *                         otherwise only the operations assigned explicitly to each
	 *                         group will be checked.
	 * @return The IDs of the groups who have been blocked form performing the
	 * operation
	 * @throws QDynamicOperationException If an error occurs while evaluating a dynamic operation.
	 */
	Set<String> getBlockedGroupsForOperation(String operationName, String resourceObjectID, boolean checkAncestors);

	/**
	 * Retrieves the operations which are permitted for a specific user.
	 *
	 * @param userID          The ID of the user for whom to retrieve the permitted
	 *                        operations
	 * @param checkUserGroups If true the user's groups will also be checked in order to
	 *                        retrieve the operations permitted for the user belongs to. If
	 *                        true positive permissions will be given priority, otherwise
	 *                        negative permissions will be given priority. This argument is
	 *                        only taken into account if checkUserGroups is true.
	 * @return The names of the operations the user is allowed to perform
	 */
	Set<String> getPermittedOperationsForUser(String userID, boolean checkUserGroups);

	/**
	 * Retrieves the operations which are permitted for a specific user on a
	 * specific resource.
	 *
	 * @param userID           The ID of the user for whom to retrieve the permitted
	 *                         operations
	 * @param resourceObjectID The ObjectID of the resource for which to check.
	 * @param checkUserGroups  If true the user's groups will also be checked in order to
	 *                         retrieve the operations permitted for the user.
	 * @return The names of the operations the user is allowed to perform
	 */
	Set<String> getPermittedOperationsForUser(String userID, String resourceObjectID, boolean checkUserGroups);

	/**
	 * Retrieves the resource DTOs for the given operation of a specific user.
	 *
	 * @param userID        The ID of the user for whom to retrieve the resources
	 * @param operationName The name of the operation for whom to retrieve the resources
	 * @param getAllowed    True if the operation is permitted
	 * @return The resources for the given operation of a specific user
	 */
	Set<ResourceDTO> getResourceForOperation(String userID, String operationName, boolean getAllowed);

    /**
     * Retrieves the resource DTOs for the given operation of a specific user and, if requested, the groups he belongs to.
     *
     * @param userID          The ID of the user for whom to retrieve the resources
     * @param operationName   The name of the operation for whom to retrieve the resources
     * @param getAllowed      True if the operation is permitted
     * @param checkUserGroups True if also the resources of the groups the user belongs to should be retrieved
     * @return The resources for the given operation of a specific user and, if requested, his groups
     */
    Set<ResourceDTO> getResourceForOperation(String userID, String operationName, boolean getAllowed, boolean checkUserGroups);

	/**
	 * Find an operation by ID.
	 *
	 * @param operationID The ID of the operation to lookup.
	 * @return The requested operation as a DTO, or null if the operation did
	 * not exist.
	 */
	OperationDTO getOperationByID(String operationID);

	/**
	 * Gets Groups IDs for given operation which has a user
	 *
	 * @param operationName Operation name
	 * @param userId        user ID
	 * @return A list of the group ids found.
	 */
	List<String> getGroupIDsByOperationAndUser(String operationName, String userId);

	/**
	 * Gets Operations for a given Group
	 *
	 * @param groupName
	 * @return A List with the permitted operations for the given Group
   */
	List<GroupHasOperationDTO> getGroupOperations(String groupName);

	/**
	 * Gets Operations for a list of Groups
	 *
	 * @param groupNames
	 * @return A List with the permitted operation for the given list of Groups
   */
	List<GroupHasOperationDTO> getGroupOperations(List<String> groupNames);

  /**
   * Checks whether a specific operation, for a specific group on a specific resource is allowed.
   * The check is performed by checking whether the specified group is assigned the specific
   * operation and resource, if yes, check the deny flag of the operation assignment to decide
   * whether the group is permitted the operation. In case no assignment for this operation can be
   * found for the group the group's ancestors are checked recursively until we arrive to a
   * decision.
   *
   * @param groupID The id of the group for which to check the operation
   * @param operationName The name of the operation to check
   * @param resourceName The name of the resource to check
   * @return true, if the group is allowed the operation. false, if the group is denied the
   * operation. null, if there is no information available to reply accordingly.
   */
  Boolean isPermittedForGroupByResource(String groupID, String operationName, String resourceName);
}
