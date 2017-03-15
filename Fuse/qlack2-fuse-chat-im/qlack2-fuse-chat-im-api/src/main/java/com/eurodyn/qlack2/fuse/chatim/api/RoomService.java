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
package com.eurodyn.qlack2.fuse.chatim.api;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.chatim.api.dto.RoomDTO;
import com.eurodyn.qlack2.fuse.chatim.api.dto.RoomPropertyDTO;
import com.eurodyn.qlack2.fuse.chatim.api.dto.RoomUserDTO;
import com.eurodyn.qlack2.fuse.chatim.api.dto.RoomWordFilterDTO;
import com.eurodyn.qlack2.fuse.chatim.api.exception.QChatIMException;

/**
 * An interface for EJBs providing services to manage the chat rooms.
 *
 * @author European Dynamics SA
 */
public interface RoomService {

	/**
	 * Creates a new room in the system.
	 *
	 * @param roomDTO
	 *            The room to be created
	 * @return the Id of the new room
	 * @throws QChatIMException
	 *             If there is an error during the creation of the notification
	 *             for this action.
	 */
	public String createRoom(RoomDTO roomDTO) throws QChatIMException;

	/**
	 * Removes a room from the system.
	 *
	 * @param roomDTO
	 *            The room to be deleted
	 * @throws QChatIMException
	 *             If there is an error during the creation of the notification
	 *             for this action.
	 */
	public void removeRoom(RoomDTO roomDTO) throws QChatIMException;

	/**
	 * Join a room.
	 *
	 * @param roomID
	 *            the Id of the room
	 * @param userID
	 *            the Id of the user
	 * @throws QChatIMException
	 *             If there is an error during the creation of the notification
	 *             for this action.
	 */
	public void joinRoom(String roomID, String userID) throws QChatIMException;

	/**
	 * Get the users of a room.
	 *
	 * @param roomID
	 *            the Id of the room
	 * @return
	 */
	public RoomUserDTO[] getRoomUsers(String roomID);

	/**
	 * Leave a room.
	 *
	 * @param userID
	 *            the Id of the user
	 * @param roomID
	 *            the Id of the room
	 * @throws QChatIMException
	 *             If there is an error during the creation of the notification
	 *             for this action.
	 */
	public void leaveRoom(String userID, String roomID) throws QChatIMException;

	/**
	 * Return the list of the available rooms for groups.
	 *
	 * @param communityIDs
	 * @return
	 */
	public RoomDTO[] listAvailableRoomsForGroups(String[] communityIDs);

	/**
	 * Leave all the rooms.
	 *
	 * @param userID
	 *            the Id of the user
	 */
	public void leaveAllRooms(java.lang.String userID);

	/**
	 * Search chat rooms in the system using a filter of paging parameters.
	 *
	 * @param room
	 *            criteria for search
	 * @param pagingParams
	 *            The paging parameters to use
	 * @return list of rooms
	 */
	public RoomDTO[] searchRooms(RoomDTO room, PagingParams pagingParams);

	/**
	 * Set the chat room property.
	 *
	 * @param roomProperty
	 *            property to be set
	 * @throws QChatIMException
	 *             If there is an error during the creation of the notification
	 *             for this action.
	 */
	public void setRoomProperty(RoomPropertyDTO roomProperty)
			throws QChatIMException;

	/**
	 * Get the chat room property.
	 *
	 * @param roomId
	 *            id of the room
	 * @param propertyName
	 *            name of the property
	 * @return RoomPropertyDTO the room property
	 * @throws QChatIMException
	 *             If there is an error during the creation of the notification
	 *             for this action.
	 */
	public RoomPropertyDTO getRoomProperty(String roomId, String propertyName,
			String recipientUserID) throws QChatIMException;

	/**
	 * Get the chat room statistics like room name,room owner, number of
	 * messages ,number of participants etc
	 *
	 * @param roomId
	 *            Id of the chat room for getting statistics
	 * @return String statistics details for the room as XML string
	 * @throws QChatIMException
	 *             If there is an error during the creation of the notification
	 *             for this action or if the statistics cannot be marshaled to
	 *             xml
	 */
	public String getRoomStatistics(String roomId, String recipientUserID)
			throws QChatIMException;

	/**
	 * Get the room filter.
	 *
	 * @param roomId
	 *            the Id of the room
	 * @param recipientUserID
	 *            the recipient user Id.
	 * @return
	 * @throws QChatIMException
	 *             If there is an error during this action.
	 */
	public RoomWordFilterDTO getRoomFilter(String roomId, String recipientUserID)
			throws QChatIMException;

	/**
	 * Set the room filter.
	 *
	 * @param filter
	 *            the room word filter
	 * @throws QChatIMException
	 *             If there is an error during this action.
	 */
	public void setRoomFilter(RoomWordFilterDTO filter) throws QChatIMException;

	/**
	 * Remove the room filter.
	 *
	 * @param roomId
	 *            the Id of the room
	 * @throws QChatIMException
	 *             If there is an error during this action.
	 */
	public void removeRoomFilter(String roomId) throws QChatIMException;

	/**
	 * Get the time that the user joined the room.
	 *
	 * @param roomID
	 *            the Id of the room
	 * @param userID
	 *            the Id of the user
	 * @return If there is an error during this action.
	 */
	public Long getRoomJoiningTimeForUser(String roomID, String userID);
}
