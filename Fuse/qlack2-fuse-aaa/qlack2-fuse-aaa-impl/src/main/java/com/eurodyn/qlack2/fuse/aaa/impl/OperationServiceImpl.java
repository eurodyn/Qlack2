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

import com.eurodyn.qlack2.fuse.aaa.api.dto.GroupHasOperationDTO;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import com.eurodyn.qlack2.fuse.aaa.api.OperationService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.OperationDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.ResourceDTO;
import com.eurodyn.qlack2.fuse.aaa.api.exception.QDynamicOperationException;
import com.eurodyn.qlack2.fuse.aaa.impl.model.Group;
import com.eurodyn.qlack2.fuse.aaa.impl.model.GroupHasOperation;
import com.eurodyn.qlack2.fuse.aaa.impl.model.OpTemplate;
import com.eurodyn.qlack2.fuse.aaa.impl.model.OpTemplateHasOperation;
import com.eurodyn.qlack2.fuse.aaa.impl.model.Operation;
import com.eurodyn.qlack2.fuse.aaa.impl.model.Resource;
import com.eurodyn.qlack2.fuse.aaa.impl.model.User;
import com.eurodyn.qlack2.fuse.aaa.impl.model.UserHasOperation;
import com.eurodyn.qlack2.fuse.aaa.impl.util.ConverterUtil;

import bsh.EvalError;
import bsh.Interpreter;

/**
 *
 * @author European Dynamics SA
 */
@Transactional
@Singleton
@OsgiServiceProvider(classes = { OperationService.class })
public class OperationServiceImpl implements OperationService {
    private static final Logger LOGGER = Logger.getLogger(OperationServiceImpl.class.getName());
    @PersistenceContext(unitName = "fuse-aaa")
    private EntityManager em;

    private boolean prioritisePositive;

	public void setPrioritisePositive(boolean prioritisePositive) {
		this.prioritisePositive = prioritisePositive;
	}

	@Override
	public String createOperation(OperationDTO operationDTO) {
		Operation operation = new Operation();
		operation.setDescription(operationDTO.getDescription());
		operation.setDynamicCode(operationDTO.getDynamicCode());
		operation.setDynamic(operationDTO.isDynamic());
		operation.setName(operationDTO.getName());
		em.persist(operation);

		return operation.getId();
	}

	@Override
	public void updateOperation(OperationDTO operationDTO) {
		Operation operation = Operation.find(operationDTO.getId(), em);
		operation.setName(operationDTO.getName());
		operation.setDescription(operationDTO.getDescription());
		operation.setDynamic(operationDTO.isDynamic());
		operation.setDynamicCode(operationDTO.getDynamicCode());
	}

	@Override
	public void deleteOperation(String operationID) {
		em.remove(Operation.find(operationID, em));
	}
	
	@Override
	public List<OperationDTO> getAllOperations() {
		return ConverterUtil.operationToOperationDTOList(Operation.findAll(em));
	}

	@Override
	public OperationDTO getOperationByName(String operationName) {
		return ConverterUtil.operationToOperationDTO(Operation.findByName(operationName, em));
	}

	@Override
	public void addOperationToUser(String userID, String operationName, boolean isDeny) {
		UserHasOperation uho = UserHasOperation.findByUserIDAndOperationName(userID, operationName, em);
		if (uho != null) {
			uho.setDeny(isDeny);
		}
		else {
			User user = User.find(userID, em);
			Operation operation = Operation.findByName(operationName, em);
			uho = new UserHasOperation();
			uho.setDeny(isDeny);
			user.addUserHasOperation(uho);
			operation.addUserHasOperation(uho);
			em.persist(uho);
		}
	}

	@Override
	public void addOperationToUser(String userID, String operationName, String resourceID, boolean isDeny) {
		UserHasOperation uho = UserHasOperation.findByUserAndResourceIDAndOperationName(
				userID, operationName, resourceID, em);
		if (uho != null) {
			uho.setDeny(isDeny);
		}
		else {
			User user = User.find(userID, em);
			Operation operation = Operation.findByName(operationName, em);
			Resource resource = Resource.find(resourceID, em);
			uho = new UserHasOperation();
			uho.setDeny(isDeny);
			user.addUserHasOperation(uho);
			operation.addUserHasOperation(uho);
			resource.addUserHasOperation(uho);
			em.persist(uho);
		}
	}

	@Override
	public void addOperationsToUserFromTemplateID(String userID, String templateID) {
		OpTemplate template = OpTemplate.find(templateID, em);
		addOperationsToUserFromTemplate(userID, template);
	}

	@Override
	public void addOperationsToUserFromTemplateName(String userID, String templateName) {
		OpTemplate template = OpTemplate.findByName(templateName, em);
		addOperationsToUserFromTemplate(userID, template);
	}

	private void addOperationsToUserFromTemplate(String userID, OpTemplate template) {
		for (OpTemplateHasOperation tho : template.getOpTemplateHasOperations()) {
			if (tho.getResource() == null) {
				addOperationToUser(userID, tho.getOperation().getName(), tho.isDeny());
			} else {
				addOperationToUser(userID, tho.getOperation().getName(), tho.getResource().getId(), tho.isDeny());
			}
		}
	}

	@Override
	public void addOperationToGroup(String groupID, String operationName, boolean isDeny) {
		GroupHasOperation gho = GroupHasOperation.findByGroupIDAndOperationName(groupID, operationName, em);
		if (gho != null) {
			gho.setDeny(isDeny);
		}
		else {
			Group group = Group.find(groupID, em);
			Operation operation = Operation.findByName(operationName, em);
			gho = new GroupHasOperation();
			gho.setDeny(isDeny);
			group.addGroupHasOperation(gho);
			operation.addGroupHasOperation(gho);
			em.persist(gho);
		}
	}

	@Override
	public void addOperationToGroup(String groupID, String operationName, String resourceID, boolean isDeny) {
		GroupHasOperation gho = GroupHasOperation.findByGroupAndResourceIDAndOperationName(
				groupID, operationName, resourceID, em);
		if (gho != null) {
			gho.setDeny(isDeny);
		}
		else {
			Group group = Group.find(groupID, em);
			Operation operation = Operation.findByName(operationName, em);
			Resource resource = Resource.find(resourceID, em);
			gho = new GroupHasOperation();
			gho.setDeny(isDeny);
			group.addGroupHasOperation(gho);
			operation.addGroupHasOperation(gho);
			resource.addGroupHasOperation(gho);
			em.persist(gho);
		}
	}

	@Override
	public void addOperationsToGroupFromTemplateID(String groupID, String templateID) {
		OpTemplate template = OpTemplate.find(templateID, em);
		addOperationsToGroupFromTemplate(groupID, template);
	}

	@Override
	public void addOperationsToGroupFromTemplateName(String groupID, String templateName) {
		OpTemplate template = OpTemplate.findByName(templateName, em);
		addOperationsToGroupFromTemplate(groupID, template);
	}

	private void addOperationsToGroupFromTemplate(String groupID, OpTemplate template) {
		for (OpTemplateHasOperation tho : template.getOpTemplateHasOperations()) {
			if (tho.getResource() == null) {
				addOperationToGroup(groupID, tho.getOperation().getName(), tho.isDeny());
			} else {
				addOperationToGroup(groupID, tho.getOperation().getName(), tho.getResource().getId(), tho.isDeny());
			}
		}
	}

	@Override
	public void removeOperationFromUser(String userID, String operationName) {
		UserHasOperation uho = UserHasOperation.findByUserIDAndOperationName(userID, operationName, em);
		if (uho != null) {
			em.remove(uho);
		}
	}

	@Override
	public void removeOperationFromUser(String userID, String operationName, String resourceID) {
		UserHasOperation uho = UserHasOperation.findByUserAndResourceIDAndOperationName(userID, operationName, resourceID, em);
		if (uho != null) {
			em.remove(uho);
		}
	}

	@Override
	public void removeOperationFromGroup(String groupID, String operationName) {
		GroupHasOperation gho = GroupHasOperation.findByGroupIDAndOperationName(groupID, operationName, em);
		if (gho != null) {
			em.remove(gho);
		}
	}

	@Override
	public void removeOperationFromGroup(String groupID, String operationName, String resourceID) {
		GroupHasOperation gho = GroupHasOperation.findByGroupAndResourceIDAndOperationName(groupID, operationName, resourceID, em);
		if (gho != null) {
			em.remove(gho);
		}
	}

	@Override
	public Boolean isPermitted(String userId, String operationName) {
		return isPermitted(userId, operationName, null);
	}

	@Override
	public Boolean isPermitted(String userId, String operationName, String resourceObjectID) {
		LOGGER.log(
				Level.FINEST,
				"Checking permissions for user ''{0}'', operation ''{1}'' and resource object ID ''{2}''.",
				new String[] { userId, operationName, resourceObjectID });
		User user = User.find(userId, em);

		// If the user is a superadmin then the operation is permitted
		// by definition
		if (user.isSuperadmin()) {
			return true;
		}

		Operation operation = Operation.findByName(operationName, em);
		String resourceId = (resourceObjectID == null) ? null : Resource.findByObjectID(resourceObjectID, em).getId();

		Boolean retVal = null;
		UserHasOperation uho = (resourceId == null)
				? UserHasOperation.findByUserIDAndOperationName(userId, operationName, em)
				: UserHasOperation.findByUserAndResourceIDAndOperationName(userId, operationName, resourceId, em);

		// Check the user's permission on the operation
		if (uho != null) {
			// First check whether this is a dynamic operation.
			if (operation.isDynamic()) {
				retVal = evaluateDynamicOperation(operation, userId, null,
						resourceObjectID);
			}
			else {
				retVal = !uho.isDeny();
			}
		}
		// If no user permission on the operation exists check the permissions for the user groups.
		else {
			List<Group> userGroups = user.getGroups();
			for (Group group : userGroups) {
				Boolean groupPermission;
				groupPermission = isPermittedForGroup(group.getId(), operationName, resourceObjectID);
				if (groupPermission != null) {
					// Assign the permission we got for the group to the return value only if
					// a. We haven't found another permission for this user so far
					// b. The groupPermission is true and we are prioritising positive permissions or
					// the groupPermission is false and we are prioritising negative permissions.
					if ((retVal == null) || (groupPermission == prioritisePositive)) {
						retVal = groupPermission;
					}

				}
			}
		}

		return retVal;
	}

	@Override
	public Boolean isPermittedForGroup(String groupID, String operationName) {
		return isPermittedForGroup(groupID, operationName, null);
	}

	@Override
	public Boolean isPermittedForGroupByResource(String groupID, String operationName, String resourceName) {
    LOGGER.log(Level.FINEST,
      "Checking permissions for group {0}, operation {1} and resource with object ID {2}.",
      new String[] { groupID, operationName, resourceName});

    Group group = Group.find(groupID, em);
    Operation operation = Operation.findByName(operationName, em);

    Boolean retVal = null;
    GroupHasOperation gho = GroupHasOperation.findByGroupIDAndOperationNameAndResourceName(groupID, operationName, resourceName, em);
    if (gho != null) {
      retVal = !gho.isDeny();
    }
    else if (group.getParent() != null) {
      // If this group is not assigned the operation check the group's
      // parents until a result is found or until no other parent exists.
      retVal = isPermittedForGroup(group.getParent().getId(), operationName, resourceName);
    }

    return retVal;
  }

	@Override
	public Boolean isPermittedForGroup(String groupID, String operationName, String resourceObjectID) {
		LOGGER.log(Level.FINEST,
				"Checking permissions for group {0}, operation {1} and resource with object ID {2}.",
				new String[] { groupID, operationName, resourceObjectID});

		Group group = Group.find(groupID, em);
		Operation operation = Operation.findByName(operationName, em);
		String resourceId = (resourceObjectID == null) ? null : Resource.findByObjectID(resourceObjectID, em).getId();

		Boolean retVal = null;
		GroupHasOperation gho = (resourceId == null)
				? GroupHasOperation.findByGroupIDAndOperationName(groupID, operationName, em)
				: GroupHasOperation.findByGroupAndResourceIDAndOperationName(groupID, operationName, resourceId, em);
		if (gho != null) {
			// First check whether this is a dynamic operation.
			if (operation.isDynamic()) {
				retVal = evaluateDynamicOperation(operation, null, groupID,
						resourceObjectID);
			}
			else {
				retVal = !gho.isDeny();
			}
		}
		else if (group.getParent() != null) {
			// If this group is not assigned the operation check the group's
			// parents until a result is found or until no other parent exists.
			retVal = isPermittedForGroup(group.getParent().getId(), operationName, resourceObjectID);
		}

		return retVal;
	}

	private Set<String> getUsersForOperation(String operationName,
			String resourceObjectID, boolean checkUserGroups, boolean getAllowed) {
		Set<String> allUsers = User.getNormalUserIds(em);
		// Superadmin users are allowed the operation by default
		Set<String> returnedUsers = new HashSet<>();
		if (getAllowed) {
			returnedUsers = User.getSuperadminUserIds(em);
		} else {
			for (String superadminId : User.getSuperadminUserIds(em)) {
				allUsers.remove(superadminId);
			}
		}

		String resourceId = null;
		if (resourceObjectID != null) {
			resourceId = Resource.findByObjectID(resourceObjectID, em).getId();
		}

		// First check the permissions of users themselves
		List<UserHasOperation> uhoList;
		if (resourceId == null) {
			uhoList = UserHasOperation.findByOperationName(operationName, em);
		} else {
			uhoList = UserHasOperation.findByResourceIDAndOperationName(operationName, resourceId, em);
		}
		for (UserHasOperation uho : uhoList) {
			allUsers.remove(uho.getUser().getId());

			// Check if operation is dynamic and if yes evaluate the operation
			if (uho.getOperation().isDynamic()) {
				Boolean dynamicResult = evaluateDynamicOperation(uho.getOperation(),
						uho.getUser().getId(), null, resourceObjectID);
				if ((dynamicResult != null) && (dynamicResult.booleanValue() == getAllowed)) {
						returnedUsers.add(uho.getUser().getId());
				}
			}
			else if (!uho.isDeny() == getAllowed) {
				returnedUsers.add(uho.getUser().getId());
			}
		}

		// Then iterate over the remaining users to check group permissions
		if (checkUserGroups) {
			// Using Iterator to iterate over allUsers in order to avoid
			// ConcurrentModificationException caused by user removal in the for loop
			Iterator<String> userIt = allUsers.iterator();
			while (userIt.hasNext()) {
				String userId = userIt.next();
				List<Group> userGroups = User.find(userId, em).getGroups();
				Boolean userPermission = null;
				for (Group group : userGroups) {
					Boolean groupPermission;
					if (resourceObjectID == null) {
						groupPermission = isPermittedForGroup(group.getId(), operationName);
					} else {
						groupPermission = isPermittedForGroup(group.getId(), operationName, resourceObjectID);
					}
					// We have the following cases depending on the group permission:
					// a. If it was positive and we are prioritising positive permissions the user
					// is allowed and we end the check for this user. The user will be added to
					// the returned users if getAllowed == true.
					// b. If it was negative and we are prioritising negative permissions the user
					// is not allowed and we end the check for this user. The user will be added to
					// the returned users if getAllowed == false.
					// c. In all other cases we wait until the rest of the user groups are checked
					// before we make a final decision. For this reason we assign the groupPermission
					// to the userPermission variable to be checked after group check is finished.
					if (groupPermission != null) {
						userIt.remove();
						if (groupPermission.booleanValue() == prioritisePositive) {
							if (groupPermission.booleanValue() == getAllowed) {
								returnedUsers.add(userId);
							}
							userPermission = null;
							break;
						} else {
							userPermission = groupPermission;
						}
					}
				}
				if ((userPermission != null) && (userPermission.booleanValue() == getAllowed)) {
					returnedUsers.add(userId);
				}
			}
		}

		return returnedUsers;
	}

	@Override
	public Set<String> getAllowedUsersForOperation(String operationName,
			boolean checkUserGroups) {
		return getUsersForOperation(operationName, null, checkUserGroups, true);
	}

	@Override
	public Set<String> getAllowedUsersForOperation(String operationName, String resourceObjectID,
			boolean checkUserGroups) {
		return getUsersForOperation(operationName, resourceObjectID, checkUserGroups, true);
	}

	@Override
	public Set<String> getBlockedUsersForOperation(String operationName,
			boolean checkUserGroups) {
		return getUsersForOperation(operationName, null, checkUserGroups, false);
	}

	@Override
	public Set<String> getBlockedUsersForOperation(String operationName, String resourceObjectID,
			boolean checkUserGroups) {
		return getUsersForOperation(operationName, resourceObjectID, checkUserGroups, false);
	}

	private Set<String> getGroupsForOperation(String operationName, String resourceObjectID,
			boolean checkAncestors, boolean getAllowed) {
		Set<String> allGroups = Group.getAllGroupIds(em);
		Set<String> returnedGroups = new HashSet<>();

		String resourceId = null;
		if (resourceObjectID != null) {
			resourceId = Resource.findByObjectID(resourceObjectID, em).getId();
		}
		List<GroupHasOperation> ghoList;
		if (resourceId == null) {
			ghoList = GroupHasOperation.findByOperationName(operationName, em);
		} else {
			ghoList = GroupHasOperation.findByResourceIDAndOperationName(
				operationName, resourceId, em);
		}
		for (GroupHasOperation gho : ghoList) {
			allGroups.remove(gho.getGroup().getId());

			// Check if operation is dynamic and if yes evaluate the operation
			if (gho.getOperation().isDynamic()) {
				Boolean dynamicResult = evaluateDynamicOperation(gho.getOperation(),
						null, gho.getGroup().getId(), null);
				if ((dynamicResult != null) && (dynamicResult.booleanValue() == getAllowed)) {
					returnedGroups.add(gho.getGroup().getId());
				}
			}
			else if (!gho.isDeny() == getAllowed) {
				returnedGroups.add(gho.getGroup().getId());
			}
		}

		// Check the ancestors of the remaining groups if so requested
		if (checkAncestors) {
			for (String groupId : allGroups) {
				Boolean groupPermission;
				if (resourceObjectID == null) {
					groupPermission = isPermittedForGroup(groupId, operationName);
				} else {
					groupPermission = isPermittedForGroup(groupId, operationName, resourceObjectID);
				}
				if ((groupPermission != null) && (groupPermission.booleanValue() == getAllowed)) {
					returnedGroups.add(groupId);
				}
			}
		}

		return returnedGroups;
	}

	@Override
	public Set<String> getAllowedGroupsForOperation(String operationName, boolean checkAncestors) {
		return getGroupsForOperation(operationName, null, checkAncestors, true);
	}

	@Override
	public Set<String> getAllowedGroupsForOperation(String operationName,
			String resourceObjectID, boolean checkAncestors) {
		return getGroupsForOperation(operationName, resourceObjectID, checkAncestors, true);
	}

	@Override
	public Set<String> getBlockedGroupsForOperation(String operationName, boolean checkAncestors) {
		return getGroupsForOperation(operationName, null, checkAncestors, false);
	}

	@Override
	public Set<String> getBlockedGroupsForOperation(String operationName,
			String resourceObjectID, boolean checkAncestors) {
		return getGroupsForOperation(operationName, resourceObjectID, checkAncestors, false);
	}

	private Boolean evaluateDynamicOperation(Operation operation,
			String userID, String groupID, String resourceObjectID) {
		LOGGER.log(Level.FINEST, "Evaluating dynamic operation ''{0}''.",
				operation.getName());

		Boolean retVal;
		String algorithm = operation.getDynamicCode();
		// Create a BeanShell interpreter for this operation.
		Interpreter i = new Interpreter();
		// Pass parameters to the algorithm.
		try {
			i.set("userID", userID);
			i.set("groupID", groupID);
			i.set("resourceObjectID", resourceObjectID);
			i.set("entitymanager", em);
			i.eval(algorithm);
			retVal = ((Boolean) i.get("retVal")).booleanValue();
		} catch (EvalError ex) {
			// Catching the EvalError in order to convert it to
			// a RuntimeException which will also rollback the transaction.
			throw new QDynamicOperationException(
					"Error evaluating dynamic operation '"
							+ operation.getName() + "'.");
		}

		return retVal;
	}

	@Override
	public Set<String> getPermittedOperationsForUser(String userID, boolean checkUserGroups) {
		User user = User.find(userID, em);
		return getOperationsForUser(user, null, checkUserGroups);
	}

	@Override
	public Set<String> getPermittedOperationsForUser(String userID, String resourceObjectID,
			boolean checkUserGroups) {
		User user = User.find(userID, em);
		Resource resource = Resource.findByObjectID(resourceObjectID, em);
		return getOperationsForUser(user, resource, checkUserGroups);
	}

	private Set<String> getOperationsForUser(User user, Resource resource,
			boolean checkUserGroups) {
		Set<String> allowedOperations = new HashSet<>();
		Set<String> deniedOperations = new HashSet<>();

		// If the user is a superadmin then they are allowed all operations
		if (user.isSuperadmin()) {
			for (Operation operation : Operation.findAll(em)) {
				allowedOperations.add(operation.getName());
			}
		} else {
			// Check operations attributed to the user
			for (UserHasOperation uho : user.getUserHasOperations()) {
				if (uho.getResource() == resource) {
					if ((uho.getOperation().isDynamic() && evaluateDynamicOperation(
								uho.getOperation(), user.getId(), null, resource.getObjectId()))
							|| (!uho.getOperation().isDynamic() && !uho.isDeny())) {
						allowedOperations.add(uho.getOperation().getName());
					} else if ((uho.getOperation().isDynamic() && !evaluateDynamicOperation(
								uho.getOperation(), user.getId(), null, resource.getObjectId()))
							|| (!uho.getOperation().isDynamic() && uho.isDeny())) {
						deniedOperations.add(uho.getOperation().getId());
					}
				}
			}
			if (checkUserGroups) {
				// Then check operations the user may have via their groups
				Set<String> allowedGroupOperations = new HashSet<>();
				Set<String> deniedGroupOperations = new HashSet<>();
				// First get all the operations allowed or denied through the user groups
				for (Group group : user.getGroups()) {
					while (group != null) {
						allowedGroupOperations.addAll(getOperationsForGroup(group, resource, true));
						deniedGroupOperations.addAll(getOperationsForGroup(group, resource, false));
						group = group.getParent();
					}
				}
				// And then check for each allowed operation if it is explicitly denied
				// to the user or if it is denied through another group (only if prioritisePositive == false)
				for (String groupOperation : allowedGroupOperations) {
					if (!deniedOperations.contains(groupOperation)
							&& (prioritisePositive || (!deniedGroupOperations.contains(groupOperation)))) {
						allowedOperations.add(groupOperation);
					}
				}
			}
		}

		return allowedOperations;
	}

	private Set<String> getOperationsForGroup(Group group, Resource resource, boolean allowed) {
		Set<String> retVal = new HashSet<>();
		for (GroupHasOperation gho : group.getGroupHasOperations()) {
			if (gho.getResource() == resource) {
				String resourceObjectID = (resource != null) ? resource.getObjectId() : null;
				if ((gho.getOperation().isDynamic() &&
						(evaluateDynamicOperation(gho.getOperation(), null, group.getId(), resourceObjectID) == allowed))
					|| (!gho.getOperation().isDynamic() && (!gho.isDeny() == allowed))){
					retVal.add(gho.getOperation().getName());
				}
			}
		}
		return retVal;
	}
	
	@Override
	public Set<ResourceDTO> getResourceForOperation(String userID, String operationName, boolean getAllowed) {
		return getResourceForOperation(userID, operationName, getAllowed, false);
	}
	
    @Override
    public Set<ResourceDTO> getResourceForOperation(String userID, String operationName, boolean getAllowed, boolean checkUserGroups) {
      Set<ResourceDTO> resourceDTOList = new HashSet<>();
      User user = User.find(userID, em);
      for (UserHasOperation uho : user.getUserHasOperations()) {
          if(uho.isDeny()!= getAllowed && uho.getOperation().getName().equals(operationName)){
              resourceDTOList.add(ConverterUtil.resourceToResourceDTO(Resource.find(uho.getResource().getId(), em)));
          }
      }
      /* also the resources of the groups the user belongs to should be retrieved */
      if (checkUserGroups) {
        for (Group group : user.getGroups()) {
          for (GroupHasOperation gho : group.getGroupHasOperations()) {
            if (gho.isDeny() != getAllowed && gho.getOperation().getName().equals(operationName)) {
              resourceDTOList.add(ConverterUtil.resourceToResourceDTO(Resource.find(gho.getResource().getId(), em)));
            }
          }
        }
      }
      return resourceDTOList;
    }

	@Override
	public OperationDTO getOperationByID(String operationID) {
		Operation o = Operation.find(operationID, em);
		if (o != null) {
			return ConverterUtil.operationToOperationDTO(o);
		} else {
			return null;
		}
	}
	
	@Override
	public List<String> getGroupIDsByOperationAndUser(String operationName, String userId){
		return null;
	}

	@Override
	public List<GroupHasOperationDTO> getGroupOperations(String groupName) {
		List<GroupHasOperation> entities = GroupHasOperation.findByGroupName(groupName, em);
		return ConverterUtil.groupHasOperationToGroupHasOperationDTO(entities);
	}

	@Override
	public List<GroupHasOperationDTO> getGroupOperations(List<String> groupNames) {
		List<GroupHasOperation> entities = GroupHasOperation.findByGroupName(groupNames, em);
		return ConverterUtil.groupHasOperationToGroupHasOperationDTO(entities);
	}
}