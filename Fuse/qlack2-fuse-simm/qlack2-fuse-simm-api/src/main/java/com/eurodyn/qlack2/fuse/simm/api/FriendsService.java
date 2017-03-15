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
import com.eurodyn.qlack2.fuse.simm.api.dto.FriendDTO;
import com.eurodyn.qlack2.fuse.simm.api.exception.QSIMMException;

/**
 *
 * @author European Dynamics SA
 */
public interface FriendsService {

    /**
     * This method request for friendship for provided User ID. This method leave confirmed on value null.
     * And while confirmation confirmed on updated.
     * @param userID User ID
     * @param friendID friendID
     * @throws QSIMMException Throws exception if provided userID is null or
     * provided friendID is null
     */
    void requestFriendship(String userID, String friendID) throws QSIMMException;

    /**
     * This method request for friendship with many friend for provided User ID. This method leave confirmed on value null.
     * @param userID User ID
     * @param friendsIDs
     * @throws QSIMMException Throws exception if provided userID is null or
     * provided friendID is null
     */
    void requestFriendships(String userID, String[] friendsIDs) throws QSIMMException;

    /**
     * This method remove (Remove from database) friendship for provided User ID.
     * @param userID User ID
     * @param friendID
     * @throws QSIMMException Throws exception if provided userID is null or
     * provided friendID is null
     */
    void rejectFriendship(String userID, String friendID) throws QSIMMException;

    /**
     * This method remove (Remove from database) friendship with many friend for provided User ID.
     * @param userID User ID
     * @param friendsIDs
     * @throws QSIMMException Throws exception if provided userID is null or
     * provided friendID is null
     */
    void rejectFriendships(String userID, String[] friendsIDs) throws QSIMMException;

    /**
     * This method update confirmation of friendship by updating field confirmed on with current date .
     * @param userID User ID
     * @param friendID
     * @return
     * @throws QSIMMException Throws exception if provided userID is null or
     * provided friendID is null
     */
    FriendDTO acceptFriendship(String userID, String friendID) throws QSIMMException;

    /**
     * This method update confirmation (with many friends) of friendship by updating field confirmed on with current date .
     * @param userID User ID
     * @param friendsIDs
     * @return
     * @throws QSIMMException Throws exception if provided userID is null or
     * provided friendID is null
     */
    FriendDTO[] acceptFriendships(String userID, String[] friendsIDs) throws QSIMMException;

    /**
     * This method returns all friends.
     * @param userID User ID
     * @param pp
     * @return array of FriendDTO
     * @throws QSIMMException Throws exception if provided userID is null
     */
    FriendDTO[] getFriends(String userID, PagingParams pp) throws QSIMMException;

    public FriendDTO[] getEstablishedFriends(String userID, PagingParams paging) throws QSIMMException;

    /**
     * This method returns all friends IDs.
     * @param userID User ID
     * @return array of FriendDTO
     * @throws QSIMMException Throws exception if provided userID is null
     */
    String[] getFriendsIDs(String userID) throws QSIMMException;
    
    
    /**
     * This method returns the total number of friends.
     * @param userID User ID
     * @return int of FriendDTO
     * @throws QSIMMException Throws exception if provided userID is null
     */
    long getAllFriends(String userID) throws QSIMMException;

    /**
     * This method returns all friends IDs.
     * @param userID User ID
     * @return array of FriendDTO
     * @throws QSIMMException Throws exception if provided userID is null
     */
    public FriendDTO[] getFriendsOwnRequest(String userID) throws QSIMMException;
    /**
     * This method returns all the pending friends requests other users have made to you (i.e.
     * other users asking you to add them as a friend but for which you haven't replied yet).
     * @param userID User ID
     * @return array of FriendDTO
     * @throws QSIMMException Throws exception if provided userID is null
     */
    public FriendDTO[] getFriendsRemoteRequest(String userID) throws QSIMMException;
}
