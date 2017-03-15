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

import com.eurodyn.qlack2.fuse.chatim.api.dto.MessageDTO;
import com.eurodyn.qlack2.fuse.chatim.api.exception.QChatIMException;

/**
 *
 * @author European Dynamics SA
 */
public interface IMMessageService {

	/**
	 * Posts an instant message from a user to another. It also sends a
	 * notification about this event.
	 *
	 * @param messageDTO
	 *            The message to post
	 * @return
	 * @throws QChatIMException
	 *             If there is an error during the creation of the notification
	 *             for this action.
	 */
	public String sendMessage(MessageDTO messageDTO) throws QChatIMException;

}