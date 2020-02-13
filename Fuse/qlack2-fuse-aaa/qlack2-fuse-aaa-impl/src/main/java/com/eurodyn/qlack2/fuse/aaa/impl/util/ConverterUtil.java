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
package com.eurodyn.qlack2.fuse.aaa.impl.util;

import com.eurodyn.qlack2.fuse.aaa.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.GroupHasOperationDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.OpTemplateDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.OperationAccessDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.OperationDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.ResourceDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.SessionAttributeDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.SessionDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserAttributeDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;
import com.eurodyn.qlack2.fuse.aaa.impl.model.Group;
import com.eurodyn.qlack2.fuse.aaa.impl.model.GroupHasOperation;
import com.eurodyn.qlack2.fuse.aaa.impl.model.OpTemplate;
import com.eurodyn.qlack2.fuse.aaa.impl.model.OpTemplateHasOperation;
import com.eurodyn.qlack2.fuse.aaa.impl.model.Operation;
import com.eurodyn.qlack2.fuse.aaa.impl.model.Resource;
import com.eurodyn.qlack2.fuse.aaa.impl.model.Session;
import com.eurodyn.qlack2.fuse.aaa.impl.model.SessionAttribute;
import com.eurodyn.qlack2.fuse.aaa.impl.model.User;
import com.eurodyn.qlack2.fuse.aaa.impl.model.UserAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * Utility class to convert
 * 1.transfer object to entity
 * 2.entity to transfer object.
 *
 * @author European Dynamics SA
 */
public class ConverterUtil {

    /**
     * This method is used for converting User entity to UserDTO transfer object.
     * Please note that the password field in the UserDTO object
     * IS NOT set. This is because the password stored in the database
     * is hashed and therefore it does not make sense returning it to the AAA
     * client since this field does not contain any useful value.
     *
     * @param entity Entity Object.
     * @return UserDTO, Transfer object.
     */
    public static UserDTO userToUserDTO(User entity) {
        if (entity == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setDbversion(entity.getDbversion());
        dto.setUsername(entity.getUsername());
        dto.setStatus(entity.getStatus());
        dto.setSuperadmin(entity.isSuperadmin());
        dto.setExternal(entity.isExternal() != null && entity.isExternal());

        Set<UserAttributeDTO> userAttributeDtos = userAttributesToUserAttributeDTOSet(entity.getUserAttributes());
        dto.setUserAttributes(userAttributeDtos);

        return dto;
    }

	public static Set<UserAttributeDTO> userAttributesToUserAttributeDTOSet(Collection<UserAttribute> entities) {
		if (entities == null) {
			return null;
		}

		Set<UserAttributeDTO> dtos = new HashSet<UserAttributeDTO>();
		for (UserAttribute entity : entities) {
			UserAttributeDTO dto = userAttributeToUserAttributeDTO(entity);
			dtos.add(dto);
		}
		return dtos;
	}

	public static UserAttributeDTO userAttributeToUserAttributeDTO(UserAttribute entity) {
		if (entity == null) {
			return null;
		}
		UserAttributeDTO dto = new UserAttributeDTO();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setData(entity.getData());
		dto.setBinData(entity.getBindata());
		dto.setContentType(entity.getContentType());
		dto.setUserId(entity.getUser().getId());
		return dto;
	}

    /**
     * Converts a UserDTO transfer object to User entity.
     * This method does not convert the createdOn and lastModifiedOn attributes
     * since these are set by the UserService whenever needed and not by the
     * parameters passed by callers. Additionally, this method does not set
     * the user password since it needs to be hashed before being stored in the DB.
     * @param dto Transfer object.
     * @return User Entity Object.
     */
    public static User userDTOToUser(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        User entity = new User();
        if (StringUtils.isNotBlank(dto.getId())) {
          entity.setId(dto.getId());
        }
        entity.setDbversion(dto.getDbversion());
        entity.setUsername(dto.getUsername());
        entity.setStatus(dto.getStatus());
        entity.setSuperadmin(dto.isSuperadmin());
        entity.setExternal(dto.isExternal());
        entity.setUserAttributes(userAttributeDTOsToUserAttributeList(
        		dto.getUserAttributes(), entity));
        return entity;
    }


    public static List<UserAttribute> userAttributeDTOsToUserAttributeList(Collection<UserAttributeDTO> dtos, User user) {
    	if (dtos == null) {
    		return null;
    	}

    	List<UserAttribute> entities = new ArrayList<>();
    	for (UserAttributeDTO dto : dtos) {
    		UserAttribute entity = new UserAttribute();
    		entity.setUser(user);
    		entity.setName(dto.getName());
    		entity.setData(dto.getData());
    		entity.setBindata(dto.getBinData());
    		entity.setContentType(dto.getContentType());
            entities.add(entity);
    	}
    	return entities;
    }


    public static SessionDTO sessionToSessionDTO(Session entity) {
	    if (entity == null) {
	        return null;
	    }

	    SessionDTO dto = new SessionDTO();
	    dto.setId(entity.getId());
	    dto.setUserId(entity.getUser().getId());
	    dto.setCreatedOn(entity.getCreatedOn());
	    dto.setTerminatedOn(entity.getTerminatedOn());
	    dto.setAttributes(sessionAttributesToSessionAttributeDTOSet(
	    		entity.getSessionAttributes()));
	    dto.setApplicationSessionID(entity.getApplicationSessionId());

	    return dto;
	}

	public static SessionAttributeDTO sessionAttributeToSessionAttributeDTO(SessionAttribute entity) {
	    if (entity == null) {
	        return null;
	    }

	    SessionAttributeDTO dto = new SessionAttributeDTO();
	    dto.setId(entity.getId());
	    dto.setName(entity.getName());
	    dto.setValue(entity.getValue());
	    dto.setSessionId(entity.getSession().getId());
	    return dto;
	}

	public static Set<SessionAttributeDTO> sessionAttributesToSessionAttributeDTOSet(Collection<SessionAttribute> entities) {
	    if (entities == null) {
	        return null;
	    }
	    Set<SessionAttributeDTO> dtos = new HashSet<SessionAttributeDTO>();
	    for (SessionAttribute attribute : entities) {
	        dtos.add(sessionAttributeToSessionAttributeDTO(attribute));
	    }
	    return dtos;
	}

	public static Session sessionDTOToSession(SessionDTO dto, EntityManager em) {
		if (dto == null) {
			return null;
		}

		Session entity = new Session();
		entity.setCreatedOn(dto.getCreatedOn());
		entity.setTerminatedOn(dto.getTerminatedOn());
		entity.setApplicationSessionId(dto.getApplicationSessionID());
		entity.setUser(User.find(dto.getUserId(), em));
		entity.setSessionAttributes(sessionAttributeDTOsToSessionAttributeList(
        		dto.getAttributes(), entity));
		return entity;
	}

    public static List<SessionAttribute> sessionAttributeDTOsToSessionAttributeList(Collection<SessionAttributeDTO> dtos, Session session) {
    	if (dtos == null) {
    		return null;
    	}

    	List<SessionAttribute> entities = new ArrayList<>();
    	for (SessionAttributeDTO dto : dtos) {
    		SessionAttribute entity = new SessionAttribute();
    		entity.setSession(session);
    		entity.setName(dto.getName());
    		entity.setValue(dto.getValue());
            entities.add(entity);
    	}
    	return entities;
    }

    public static GroupDTO groupToGroupDTO(Group entity, boolean lazyRelatives) {
    	return groupToGroupDTO(entity, lazyRelatives, lazyRelatives);
    }

    /*
     * Separate lazy parent from lazy children in order to allow specifying lazyChildren = true
     * when getting a node's parent since in any other case we will get a stack overflow.
     * For consistency reasons we also specify lazyParent = true when getting a node's children
     */
	private static GroupDTO groupToGroupDTO(Group entity, boolean lazyParent, boolean lazyChildren) {
	    if (entity == null) {
	        return null;
	    }

	    GroupDTO dto = new GroupDTO();
	    dto.setId(entity.getId());
	    dto.setDescription(entity.getDescription());
	    dto.setName(entity.getName());
	    dto.setObjectID(entity.getObjectId());

	    if (!lazyParent) {
    		dto.setParent(groupToGroupDTO(entity.getParent(), false, true));
	    }
	    if (!lazyChildren) {
    		dto.setChildren(new HashSet<GroupDTO>());
    		for (Group child : entity.getChildren()) {
    			dto.getChildren().add(groupToGroupDTO(child, true, false));
    		}
	    }

	    return dto;
	}

	public static List<GroupDTO> groupToGroupDTOList(Collection<Group> groups, boolean lazyRelatives) {
	    if (groups == null) {
	        return null;
	    }

	    List<GroupDTO> retVal = new ArrayList<>(groups.size());

	    // Use a HashSet as index in order to provide efficient indexing of
	    // processed groups in the case where we need to re-use them as
	    // other groups' relatives (if lazyRelatives = false).
	    Map<String, GroupDTO> groupIndex = new HashMap<>();
	    for (Group group : groups) {
	    	// If the group has already been processed as a parent/child of another
	    	// group then use the GroupDTO instance already created; otherwise create
	    	// a new GroupDTO instance for this group.
	    	GroupDTO dto = groupIndex.get(group.getId());
    		if (dto == null) {
    			// Do not handle lazyRelatives in the groupToGroupDTO method since
    			// we will handle them in this method in order to only create one
    			// GroupDTO instance for each group and use this instance wherever
    			// the group is referenced (as parent, child, etc.).
    			dto = groupToGroupDTO(group, true);
    		}

    		if (!lazyRelatives) {
    			if (!groupIndex.containsKey(dto.getId())) {
    				groupIndex.put(dto.getId(), dto);
    			}

    			// If the group has a parent check if it has already been processed in
    			// order to use the already existing instance, otherwise process it
    			// and add it to the groupIndex to be used later. Same for the group
    			// children.
    			if (group.getParent() != null) {
    				GroupDTO parent = groupIndex.get(group.getParent().getId());
    				if (parent == null) {
    					parent = groupToGroupDTO(group.getParent(), false);
    					groupIndex.put(parent.getId(), parent);
    				}
        			dto.setParent(parent);
        		}
        		dto.setChildren(new HashSet<GroupDTO>());
        		for (Group child : group.getChildren()) {
        			GroupDTO childDTO = groupIndex.get(child.getId());
    				if (childDTO == null) {
    					childDTO = groupToGroupDTO(child, false);
    					groupIndex.put(childDTO.getId(), childDTO);
    				}
        			dto.getChildren().add(childDTO);
        		}
    		}

	        retVal.add(dto);
	    }

	    return retVal;
	}


	public static OperationDTO operationToOperationDTO(Operation entity) {
		if (entity == null) {
			return null;
		}

        OperationDTO dto = new OperationDTO();
        dto.setDescription(entity.getDescription());
        dto.setDynamic(entity.isDynamic());
        dto.setDynamicCode(entity.getDynamicCode());
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }
	
	public static List<OperationDTO> operationToOperationDTOList(List<Operation> entities) {
		if (entities == null) {
			return null;
		}

		List<OperationDTO> dtos = new ArrayList<>(entities.size());
		for (Operation entity : entities) {
			dtos.add(operationToOperationDTO(entity));
		}
		return dtos;
    }


	public static OpTemplateDTO opTemplateToOpTemplateDTO(OpTemplate entity) {
		if (entity == null) {
			return null;
		}

		OpTemplateDTO dto = new OpTemplateDTO();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setDescription(entity.getDescription());
		dto.setOperations(opTemplateHasOperationsToOperationAccessDTOSet(entity.getOpTemplateHasOperations()));
		return dto;
	}


	public static OperationAccessDTO opTemplateHasOperationToOperationAccessDTO(OpTemplateHasOperation entity) {
    	if (entity == null) {
    		return null;
    	}

    	OperationAccessDTO dto = new OperationAccessDTO();
    	dto.setOperation(operationToOperationDTO(entity.getOperation()));
    	dto.setResource(resourceToResourceDTO(entity.getResource()));
    	dto.setDeny(entity.isDeny());
        return dto;
    }


    public static Set<OperationAccessDTO> opTemplateHasOperationsToOperationAccessDTOSet(
    		Collection<OpTemplateHasOperation> entities) {
    	if (entities == null) {
    		return null;
    	}

    	Set<OperationAccessDTO> dtos = new HashSet<>();
    	for (OpTemplateHasOperation entity : entities) {
    		dtos.add(opTemplateHasOperationToOperationAccessDTO(entity));
    	}
    	return dtos;
    }


    public static ResourceDTO resourceToResourceDTO(Resource entity) {
        if (entity == null) {
            return null;
        }

        ResourceDTO dto = new ResourceDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setObjectID(entity.getObjectId());
        return dto;
    }

    public static List<ResourceDTO> resourceToResourceDTO(List<Resource> entities) {
      if (entities.isEmpty()) {
        return null;
      }
      List<ResourceDTO> dtoList = new ArrayList<>();
      for (Resource entity : entities) {
        dtoList.add(resourceToResourceDTO(entity));
      }
      return dtoList;
    }

	public static List<GroupHasOperationDTO> groupHasOperationToGroupHasOperationDTO(
			List<GroupHasOperation> entities) {
		if (entities == null) {
			return null;
		}

		List<GroupHasOperationDTO> dtos = new ArrayList<>();
		for (GroupHasOperation entity : entities) {
			dtos.add(groupHasOperationToGroupHasOperationDTO(entity));
		}
		return dtos;
	}

	public static GroupHasOperationDTO groupHasOperationToGroupHasOperationDTO(
			GroupHasOperation entity) {
		if (entity == null) {
			return null;
		}
		GroupHasOperationDTO dto = new GroupHasOperationDTO();
		dto.setId(entity.getId());
		dto.setGroupDTO(groupToGroupDTO(entity.getGroup(), false));
		dto.setResourceDTO(resourceToResourceDTO(entity.getResource()));
		dto.setOperationDTO(operationToOperationDTO(entity.getOperation()));
		return dto;
	}
}
