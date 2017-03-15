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
package com.eurodyn.qlack2.fuse.simm.api;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.simm.api.dto.SocialGroupAttributeDTO;
import com.eurodyn.qlack2.fuse.simm.api.dto.SocialGroupDTO;
import com.eurodyn.qlack2.fuse.simm.api.exception.QSIMMException;

/**
 * Remote interface for Group management
 * @author European Dynamics SA
 */
public interface SocialGroupService {

    /**
     * Creates Group and returns the original groupDTO populated with
     * the ID of the newly created group.
     * @param groupDTO SocialGroupDTO
     * @return groupDTO
     * @throws QSIMMException Throws exception if provided groupDTO is null
     * or provided group name already exists.
     */
    SocialGroupDTO createGroup(SocialGroupDTO groupDTO) throws QSIMMException;

    /**
     * Retrieves group DTO for provided group ID.
     * @param groupID
     * @return group DTO
     * @throws QSIMMException Throws exception if provided groupDTO is null.
     */
    SocialGroupDTO viewGroup(String groupID) throws QSIMMException;

    /**
     * Updates group for provided Group DTO.
     * @param groupDTO
     * @return groupDTO
     * @throws QSIMMException Throws exception provided groupDTO is null or
     * provided group name already exists.
     */
    SocialGroupDTO updateGroup(SocialGroupDTO groupDTO) throws QSIMMException;

    /**
     * Updates group attributes for provided SocialGroupAttribute DTO
     * @param groupAttributeDTO
     */
    void updateGroupAttribute(SocialGroupAttributeDTO groupAttributeDTO);
    
    
    /**
     * Deletes group for provided group ID, also delete all users that have joined
     * this group from group_user table.
     * @param groupID
     * @throws QSIMMException Throws exception if provided group ID is null
     */
    void deleteGroup(String groupID) throws QSIMMException;

    /**
     * Performs search on the title and the description of the group.
     * @param searchTerm
     * @param paging
     * @param privacy
     * @return GroupDTOs
     * @throws QSIMMException Throws exception if provided searchTerm is null.
     */
    SocialGroupDTO[] searchGroups(String searchTerm, PagingParams paging, byte[] privacy) throws QSIMMException;

    /**
     * Returns Group DTO for provided pagination parameter.
     * @param paging
     * @return GroupDTOs
     */
    SocialGroupDTO[] listGroups(PagingParams paging);

    /**
     * Changes status of Group to suspend (GROUP_STATUS_SUSPEND = 0) for provided group ID.
     * @param groupID
     * @throws QSIMMException Throws exception if provided group ID is null.
     */
    void suspendGroup(String groupID) throws QSIMMException;

    /**
     * Changes status of Group to approved (GROUP_STATUS_APPROVED = 1) for provided group ID.
     * @param groupID
     * @throws QSIMMException Throws exception if provided group ID is null.
     */
    void resumeGroup(String groupID) throws QSIMMException;

    /**
     * To be developed after the mailing module is available, as it should be using
     * the mailing functionality to send the actual notifications.
     */
    void sendGroupInvitation();

    /**
     * Returns users as array of user IDs that belong to provided group ID.
     * @param groupID Group ID
     * @return IDs as array of String
     * @throws QSIMMException Throws exception if provided group ID is null.
     */
    String[] getGroupUsers(String groupID, byte[] status, PagingParams paging) throws QSIMMException;

    /**
     * This method checks whether a group already exist in database with the provided groupName, and groupId
     *
     * @param groupName
     * @param groupId
     * @return true, if the group exists, false otherwise.
     */
    boolean groupNameAlreadyExists(String groupName, String groupId);

    /**
     * This method finds group for a group name.
     *
     * @param groupName groupName
     * @return SocialGroupDTO
     * @throws QSIMMException Throws exception if provided group name is null
     */
    public SocialGroupDTO findGroupByName(String groupName) throws QSIMMException;
    
    /**
     * This method checks whether a group already exist in database with the provided URL and groupId,
     *
     * @param groupURL
     * @param groupId
     * @return true, if the group exists, false otherwise.
     */
    boolean groupURLAlreadyExists(String groupURL, String groupId);
    
    /**
     * Gets a group with the given URL
     * 
     * @param groupURL
     * @return SocialGroupDTO
     */
    SocialGroupDTO findGroupByURL(String groupURL);
}
