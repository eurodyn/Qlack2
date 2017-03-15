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
import com.eurodyn.qlack2.fuse.simm.api.dto.SocialGroupDTO;
import com.eurodyn.qlack2.fuse.simm.api.dto.SocialGroupUserDTO;
import com.eurodyn.qlack2.fuse.simm.api.exception.QSIMMException;

import java.util.List;

/**
 * Remote interface for Group User management
 * 
 * @author European Dynamic SA
 */
public interface GroupUserService {

	/**
	 * Joins the user in a group basically it creates mapping between Group and
	 * a user.
	 * 
	 * @param userId
	 *            user ID
	 * @param groupId
	 *            group ID
	 * @return
	 * @throws QSIMMException
	 *             Throws exception if user has already joined the group.
	 */
	SocialGroupDTO requestToJoinGroup(String userId, String groupId) throws QSIMMException;

	/**
	 * Returns array of Group DTOs for provided user ID.
	 * 
	 * @param userID
	 * @param status
	 *            byte array, if status is null then this will return for all
	 *            status
	 * @param paging
	 * @return array of GroupDTOs
	 * @throws QSIMMException
	 *             Throws exception if provided userID is null.
	 */
	SocialGroupDTO[] listGroupsForUser(String userID, String searchTerm, byte[] status, PagingParams paging)
			throws QSIMMException;

	/**
	 * Changes status of user in group as accepted (GROUP_USER_STATUS_ACCEPTED =
	 * 1) to join the group.
	 * 
	 * @param userID
	 *            User ID
	 * @param groupID
	 *            group ID
	 * @throws QSIMMException
	 *             Throws exception if user has not been joined the group.
	 */
	void acceptUserJoin(String userID, String groupID) throws QSIMMException;

	/**
	 * Changes status of user in group as rejected to join the group Following
	 * are the status of rejection: GROUP_USER_STATUS_REJECTED_MODERATOR(3)- If
	 * rejected by the moderator to join the group
	 * GROUP_USER_STATUS_REJECTED_USER(4) - If rejected by the user to join the
	 * group.
	 * 
	 * @param userID
	 *            User ID
	 * @param groupID
	 *            group ID
	 * @throws QSIMMException
	 *             Throws exception if user has not been joined the group.
	 */
	void rejectUserJoin(String userID, String groupID) throws QSIMMException;

	/**
	 * Removes the user for provided group, it physically removes the user from
	 * group.
	 * 
	 * @param userID
	 *            User ID
	 * @param groupID
	 *            group ID
	 * @throws QSIMMException
	 *             Throws exception if user has not been joined the group.
	 */
	void leaveGroup(String userID, String groupID) throws QSIMMException;

	/**
	 * Bans the user for provided group.
	 * 
	 * @param userID
	 * @param groupID
	 * @throws QSIMMException
	 *             Throws exception if user has not been joined the group.
	 */
	void banUser(String userID, String groupID) throws QSIMMException;

	/**
	 * It is not implemented yet.
	 * 
	 * @param userID
	 *            User ID
	 * @param groupID
	 *            group ID
	 */
	void shareGroup(String userID, String groupID);

	/**
	 * This method returns SocialGroupUserDTO for supplied group ID and User ID.
	 * 
	 * @param userID
	 *            User ID
	 * @param groupID
	 *            group ID
	 * @return SocialGroupUserDTO
	 * @throws QSIMMException
	 *             Throws exception if provided userID or groupID is null.
	 */
	SocialGroupUserDTO getGroupUser(String userID, String groupID) throws QSIMMException;

	/**
	 * Returns a list with all the users which are members in the same groups as
	 * the passed-in user
	 * 
	 * @param userID
	 * @return array of userIds
	 * @throws QSIMMException
	 *             Throws exception if Provided userID is null
	 */
	public String[] getMembersForUserGroups(String userID) throws QSIMMException;

	/**
	 * This method returns array of SocialGroupUserDTO for provided User ID.
	 * 
	 * @param userID
	 * @param status
	 *            byte array of status
	 * @param paging
	 * @return array of GroupDTOs
	 * @throws QSIMMException
	 *             Throws exception if Provided userID is null.
	 */
	public SocialGroupUserDTO[] listContactsForUser(String userID, byte[] status, PagingParams paging)
			throws QSIMMException;

	/**
	 * Returns array of SocialGroupUserDTO for provided group ID.
	 * 
	 * @param groupID
	 * @param status
	 *            byte array of status
	 * @param paging
	 * @return array of GroupDTOs
	 * @throws QSIMMException
	 *             Throws exception if provided groupID is null.
	 */
	public SocialGroupUserDTO[] listContactsForGroup(String groupID, byte[] status, PagingParams paging)
			throws QSIMMException;

	/**
	 * This method returns all group users for provided userID and status for
	 * eg. status = 0(GROUP_USER_STATUS_REQUESTED_NEW) implies all the group
	 * invitations to a user that are not yet responded by user.
	 * 
	 * @param userID
	 * @param status
	 * @return array of SocialGroupUserDTO
	 * @throws QSIMMException
	 */
	SocialGroupUserDTO[] getAllContactsForStatus(String userID, byte status) throws QSIMMException;

	/**
	 * Check whether user has been invited
	 *
	 * @param userID
	 * @param groupID
	 * @return
	 * @throws QSIMMException
	 */
	public boolean isInvited(String userID, String groupID) throws QSIMMException;

	/**
	 * Invite user map the user with group and status as invited
	 *
	 * @param userID
	 * @param groupDTO
	 *            The group to which the user is being invited. This method
	 *            takes into account the id property of the group object, as
	 *            well as the srcUserId property in case a JMS message is posted
	 *            for this action
	 */
	public void inviteUser(String userID, SocialGroupDTO groupDTO) throws QSIMMException;

	/**
	 * List all available groups in the system. If the <em>userID</em> param is
	 * provided, user membership information will also be populated.
	 * 
	 * @param userID
	 *            An optional param to indiicate whether results should also
	 *            include whether the user is a member for each group.
	 * @return
	 */
	List<SocialGroupDTO> listAvailableGroups(String userID, boolean isMember);
}
