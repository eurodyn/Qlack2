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

import com.eurodyn.qlack2.fuse.chatim.api.dto.ActionOnUserDTO;
import com.eurodyn.qlack2.fuse.chatim.api.exception.QChatIMException;

/**
 *
 * @author European Dynamics SA
 */
public interface ChatUserService {

	/**
	 * Performs an arbitrary action on a user. This is generic method to perform
	 * an action in a chat room for a specific user (e.g ban user, kick user
	 * etc.). If the same action has already been performed for this user at an
	 * earlier time then this method will update the existing action instead of
	 * creating a new one. This action also creates a notification (subject to
	 * the valus of the realtime.JMS.notifications application property).
	 *
	 * @param roomID
	 *            The room for which the action is performed
	 * @param userID
	 *            The user for whom the action is performed
	 * @param actionID
	 *            he ID of the action to be performed. This is an arbitrary
	 *            value that the caller defines according to its own business
	 *            logic.
	 * @param reason
	 *            A description of why this action has been performed.
	 * @param period
	 *            The duration for which this action is valid. Note that period
	 *            should express the exact date in the future that this action
	 *            is no valid anymore in milliseconds. It is the amount of time
	 *            in msec for which this action is valid. See @isActionActive.
	 * @param userFullname
	 *            The full name of the user for which an action is performed.
	 *            This is used for the realtime message this action will
	 *            generate (it is used to create a notification for this
	 *            action). If you do not need it, you can simply leave it empty.
	 * @return The newly created/updated action
	 * @throws QChatIMException
	 *             If there is an error during the creation of the notification
	 *             for this action.
	 */
	public ActionOnUserDTO performAction(String roomID, String userID,
			String actionID, String reason, long period, String userFullname)
			throws QChatIMException;

	/**
	 * Removes an action performed for a specific user in a chat-room. This
	 * method physically removes the action from the database.
	 *
	 * @param roomID
	 *            The room for which the action was performed
	 * @param userID
	 *            The user for whom the action was performed
	 * @param actionID
	 *            The id of the action to be removed. This is an arbitrary value
	 *            that the caller defines according to its own business logic.
	 * @param userFullname
	 *            The full name of the user for which the action is removed.
	 *            This is used for the realtime message this action will
	 *            generate (creates a notification for this action.) If you do
	 *            not need it, you can simply leave it empty.
	 * @throws QChatIMException
	 *             If there is an error during the creation of the notification
	 *             for this action.
	 */
	public void removeActionPerformed(String roomID, String userID,
			String actionID, String userFullname) throws QChatIMException;

	/**
	 * Checks whether a given action for a specific user on a particular room is
	 * still active. As 'active' actions are considered all actions that their
	 * 'period' value is less than the current time.
	 *
	 * @param roomID
	 *            The ID for which the action was performed.
	 * @param userID
	 *            The ID for the user on which the action was performed.
	 * @param actionID
	 *            The ID of the action to be removed. This is an arbitrary value
	 *            that the caller defines according to its own business logic.
	 * @return true, if the action is still active or false otherwise.
	 */
	public boolean isActionActive(String roomID, String userID, String actionID);
}
