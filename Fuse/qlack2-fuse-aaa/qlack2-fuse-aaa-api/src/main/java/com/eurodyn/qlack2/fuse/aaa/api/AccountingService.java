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

import com.eurodyn.qlack2.fuse.aaa.api.dto.SessionAttributeDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.SessionDTO;

import java.util.Collection;
import java.util.Set;

/**
 *
 * @author European Dynamics SA
 */
public interface AccountingService {

    /**
     * Creates a new session for a specific user
     * @param session The information of the session to create. If a not-zero createdOn value
     * is passed for this session then it will be used as the session's creation time,
     * otherwise the system's current time will be used instead.
     * @return The ID of the newly created session
     */
    String createSession(SessionDTO session);

    /**
     * Terminates a user's session.
     * @param sessionID The information of the session to terminate. The session
     * ID is used to identify the session to be terminated.
     */
    void terminateSession(String sessionID);

    /**
     * Retrieves a user session, identified by its ID
     * @param sessionID The ID of the session to retrieve
     * @return The details of the requested session or null if a session
     * with the given ID does not exist.
     */
    SessionDTO getSession(String sessionID);

    /**
	 * Retrieves the duration of a specific user.
	 * @param sessionID The ID of the session the duration of which will be retrieved.
	 * @return The login time of the specified session (in milliseconds) or null if the session is still active.
	 */
	Long getSessionDuration(String sessionID);

	/**
     * Get the time of a user's last log in
     * @param userID The ID of the user whose last log in time to retrieve
     * @return The timestamp of the user's last login or null if the user has
     * never been logged in (no sessions exist for this user).
     */
    Long getUserLastLogIn(String userID);

    /**
     * Get the time of a user's last log out
     * @param userID The ID of the user whose last log out time to retrieve
     * @return The timestamp of the user's last logout or null if the user has
     * never been logged out (no terminated sessions exist for this user).
     */
    Long getUserLastLogOut(String userID);

    /**
     * Retrieves the duration of a user's last <b>terminated</b> session
     * @param userID The ID of the user whose last session to retrieve
     * @return The duration of the user's last terminated session in milliseconds
     * or null if no terminated sessions exist for this user.
     */
    Long getUserLastLogInDuration(String userID);

    /**
     * Get the number of times a user has been logged in (ie. the number
     * of sessions existing for a user).
     * @param userID  The ID of the user for whom to retrieve the information.
     * @return The number of times the user has been logged in the system.
     */
    long getNoOfTimesUserLoggedIn(String userID);

    /**
     * Retrieves the users which are currently logged in (the have a not-terminated session) 
     * among those passed to the method.
     * @param userIDs The users for which to check whether they are logged in or not.
     * @return The IDs of the logged in users. Please note that this method does not
     * check if the user IDs passed to it actually exist.
     */
    Set<String> filterOnlineUsers(Collection<String> userIDs);

    /**
	 * Updates a session attribute, optionally creating if it does not 
	 * already exist,
	 * @param attribute The details of the attribute to update. The attribute
	 * is identified by using the attribute name and the sessionID.
	 * @param createIfMissing If true and an attribute with the specified name does
	 * not exist for the specified session a new attribute is created. If false, an 
	 * exception is thrown if the attribute does not exist. Please note that
     * if the attribute does not exist and the createIfMissing flag is set to false a
     * NullPointerException will be thrown.
	 */
	void updateAttribute(SessionAttributeDTO attribute, boolean createIfMissing);


	/**
     * Updates attributes information for one or more sessions, optionally creating
     * missing attributes.
     * @param attributes The list of attributes to update. Do not forget to
     * correctly set the 'sessionId' on each of the attributes to be updated.
     * @param createIfMissing If true, missing attributes are created. If false,
     * an exception is thrown if one or more attributes do not exist. Please note that
     * if an attribute does not exist and the createIfMissing flag is set to false a
     * NullPointerException will be thrown. In this case none of the
     * passed attributes are updated.
     */
    void updateAttributes(Collection<SessionAttributeDTO> attributes, boolean createIfMissing);


    /**
     * Deletes a session attribute
     * @param sessionID The ID of the session owner of the attribute to delete.
     * @param attributeName The name of the attribute to delete.
     */
    void deleteAttribute(String sessionID, String attributeName);


    /**
     * Retrieves a session attribute given the sessionID and the attribute name.
     * @param sessionID The ID of the session owner of the attribute to retrieve
     * @param attributeName The name of the attribute to retrieve
     * @return The retrieved attribute or null if the specified attribute does not
     * exist for the specified session
     */
    SessionAttributeDTO getAttribute(String sessionID, String attributeName);
    
    
    /**
	 * Retrieves the IDs of the session having a specified attribute.	 
	 * @param sessionIDs The IDs of the sessions among which to check. If no session IDs
	 * are specified then the search is performed among all sessions.
	 * @param attributeName The name of the attribute to check for
	 * @param attributeValue The value of the attribute to check for
	 * @return The IDs of the session among those specified having the given
	 * attribute.
	 */
	Set<String> getSessionIDsForAttribute(Collection<String> sessionIDs, String attributeName,
	                                String attributeValue);

}
