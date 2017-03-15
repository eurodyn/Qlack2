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
package com.eurodyn.qlack2.fuse.forum.api;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.forum.api.dto.ForumDTO;
import com.eurodyn.qlack2.fuse.forum.api.exception.QForumException;

import java.util.List;

/**
 *
 * @author European Dynamics SA.
 */
public interface ForumService {

    /**
     * Creates a new empty forum in the system
     * @param forum The details of the new forum to be created. The following
     * fields are mandatory:<br>
     * - title<br>
     * - createdBy<br>
     * Moreover, the forum's title should be unique (no other forum with the same
     * title may exist in the system).<br>
     * This method does not take into account the pending messages and topics properties
     * of the ForumDTO object being passed to it.
     * @return The newly created forum
     * @throws QForumException If a forum with the title specified
     * already exists.
     */
    public ForumDTO createForum(ForumDTO forum) throws QForumException;

    /**
     * Updates a specific forum.
     * @param forum The forum to update. The forum id should
     * not be null since it is used to identify the forum to be updated. The following
     * properties of the ForumDTO object are taken into account and modified in the respective
     * forum:<br>
     * - title<br>
     * - description<br>
     * - moderated<br>
     * - logo
     * @throws QForumException If a forum with the specified id
     * does not exist, if the a forum with the new title
     * specified already exists, if the specified forum is
     * disabled and thus cannot be edited, if the caller attempts to
     * modify the moderated property of the forum to not-supported, while the forum
     * has topics in which pending messages exist.
     *
     */
    public void updateForum(ForumDTO forum)
            throws QForumException;

    /**
     * Deletes a specific forum
     * @param forumId The id of the forum to delete
     * @throws QForumException If a forum with the specified id
     * does not exist.
     */
    public void deleteForum(String forumId) throws QForumException;

    /**
     * Retrieves the details of a forum
     * @param forumId The id of the forum the details of which to retrieve. For each of the
     * @param statistcs Calculates statistics about the total topics, pending topics, total messages
     * and pending messages. Set to false if you are not interested to display such values.
     * forum's topics this method sets the following properties of the corresponding FrmTopic
     * object: <br>
     * - title<br>
     * - lastMessageDate<br>
     * - lastMessageAuthorId
     * @return A ForumDTO object holding the forum's details
     * @throws QForumException If the specified forum is disabled
     * and so it's details cannot be retrieved.
     */
    public ForumDTO getForumById(String forumId, boolean statistcs)
            throws QForumException;

    /**
     * Lists the forums available in the system.
     * @param includeLocked Whether the locked forums should be included in the result list
     * @param archived Whether archived or not archived forums will be retrieved. If this parameter is true then
     * the method returns the archived forums, if it is false it returns the not archived ones. Id this parameter is
     * null then all forums are returned (both archived and not archived ones).
     * @return A list of the forums available in the system. This method does not set
     * the topics or the pendingMessages properties of the ForumDTO objects being returned.
     */
    public List<ForumDTO> listForums(boolean includeLocked, Boolean archived);

    /**
     * Lists the forums available in the system.
     * @param includeLocked Whether the locked forums should be included in the result list
     * @param archived Whether archived or not archived forums will be retrieved. If this parameter is true then
     * the method returns the archived forums, if it is false it returns the not archived ones. Id this parameter is
     * null then all forums are returned (both archived and not archived ones).
     * @param pagingParams Paging support parameters
     * @return A list of the forums available in the system. This method does not set
     * the topics or the pendingMessages properties of the ForumDTO objects being returned.
     */
    public List<ForumDTO> listForums(boolean includeLocked, Boolean archived, PagingParams pagingParams);

    /**
     * Disables a forum
     * @param forumId The id of the forum to disable
     * @return True if the forum was successfully disabled, false otherwise (if the
     * forum is already disabled)
     * @throws QForumException If a forum with the provided id does not
     * exist in the system
     */
    public boolean lockForum(String forumId) throws QForumException;

    /**
     * Enables a disabled forum
     * @param forumId The id of the forum to enable
     * @return True if the forum was successfully enabled, false otherwise (if the
     * forum is already enabled)
     * @throws QForumException If a forum with the provided id does not
     * exist in the system
     */
    public boolean unlockForum(String forumId) throws QForumException;

    /**
     * Retrieves the status of a specific forum
     * @param forumId The id of the forum whose status will be retrieved
     * @return The forum's status
     * @throws QForumException If a forum with the provided id does not
     * exist in the system
     */
    public short getForumStatus(String forumId) throws QForumException;

    /**
     * Archive the forum with the specific id
     * @param forumId The id of the forum which is to be archived.
     * @return boolean value
     * @throws QForumException If a forum with the provided id does not
     * exist in the system
     */
    public boolean archiveForum(String forumId)throws QForumException;

    /**
     * Un-Archive the forum with the specific id
     * @param forumId The id of the forum which is to be un-archived.
     * @return boolean value
     * @throws QForumException If a forum with the provided id does not
     * exist in the system
     */
    public boolean unarchiveForum(String forumId)throws QForumException;
}
