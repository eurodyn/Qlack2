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

import com.eurodyn.qlack2.fuse.aaa.api.criteria.UserSearchCriteria;
import com.eurodyn.qlack2.fuse.aaa.api.dto.SessionDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserAttributeDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A service providing user-related functionalities. Please note that in all
 * methods that return user data (in the form of UserDTO objects) the user
 * password is not fetched and therefore the password field in the UserDTO
 * object is null. The password field is only used when passing a UserDTO object
 * to this method in order to create/update a user.
 *
 * @author European Dynamics SA
 */
public interface UserService {

  /**
   * Creates a new user. Please note that the user password passed to this
   * method is MD5-hashed before being stored in the AAA database.
   *
   * @param dto The details of the user to create.
   * @return The ID of the newly created user.
   */
  String createUser(UserDTO dto);

  /**
   * Updates an existing user. The updated user fields are the username,
   * (optionally) password, user status, superadmin and (optionally) user
   * attributes. In case the user attributes of the passed UserDTO object are
   * null no user attribute update takes place. Otherwise (if the UserDTO
   * object has not null attributes) the user attributes are modified by
   * adding/updating/removing attributes as necessary in order to exactly
   * match the passed attributes. In case only a few of the user attributes
   * should be modified use the updateAttribute or updateAttributes methods
   * instead.
   *
   * @param dto The details of the user to be updated. The user ID is used to
   * retrieve the user while the other properties are used to
   * update the user data.
   * @param updatePassword If true the user password will also be updated to the value
   * set in the dto object, otherwise it will not be taken into
   * account
   */
  void updateUser(UserDTO dto, boolean updatePassword);

  /**
   * Deletes a user.
   *
   * @param userID The ID of the user to be removed
   */
  void deleteUser(String userID);

  /**
   * Retrieves a user given their ID.
   *
   * @param userID The ID of the user to retrieve
   * @return The user details or null if a user with the specified ID does not
   * exist.
   */
  UserDTO getUserById(String userID);

  /**
   * Retrieves a set of users given their IDs.
   *
   * @param userIDs The IDs of the users to retrieve
   * @return A set of users with the specified IDs. If one or more of the IDs
   * do not correspond to users present in the database then these IDs
   * are ignored and the rest of the users returned. In this case the
   * size of the returned set will be less than the size of the
   * collection of IDs passed to this method.
   */
  Set<UserDTO> getUsersById(Collection<String> userIDs);

  /**
   * Returns a set of users as hash with the user IDs as keys *
   *
   * @param userIDs The IDs of the users to retrieve
   * @return A map containing key (IDs) value (user data) pairs for the
   * requested users. If one or more of the IDs do not correspond to
   * users present in the database then these IDs are ignored and the
   * rest of the users returned. In this case the size of the returned
   * set will be less than the size of the collection of IDs passed to
   * this method.
   */
  Map<String, UserDTO> getUsersByIdAsHash(Collection<String> userIDs);

  /**
   * Retrieves a user by their username.
   *
   * @param username The username to look for.
   * @return The user details or null if a user with the specified username
   * does not exist.
   */
  UserDTO getUserByName(String username);

  /**
   * Updates the status of a user
   *
   * @param userID The ID of the user to update
   * @param status The new status to set for the user
   */
  void updateUserStatus(String userID, byte status);

  /**
   * Retrieves the status of a user
   *
   * @param userID The ID of the user whose status to retrieve
   * @return The status of the specified user
   */
  byte getUserStatus(String userID);

  /**
   * Checks if a user is a superadmin
   *
   * @param userID The ID of the user to check
   * @return true if the user is a superadmin, false otherwise
   */
  boolean isSuperadmin(String userID);

  /**
   * Checks if a user is externally managed (e.g. from LDAP)
   *
   * @param userID The ID of the user to check
   * @return true if the user is externally managed, false otherwise
   */
  boolean isExternal(String userID);

  /**
   * Checks if a user can be authenticated with a specific password.
   *
   * @param username The username of the user trying to be authenticated
   * @param password The password for which to check whether the user can be
   * authenticated
   * @return The ID of the specified user if they can be authenticated with
   * the specified password, null otherwise. Please note that a null
   * result may mean either that a user with the specified username
   * does not exist or that the user exists but they cannot be
   * authenticated with the specified password.
   */
  String canAuthenticate(String username, String password);

  /**
   * Registers a user logging in to the system.
   *
   * @param userID The ID of the user to log in
   * @param applicationSessionID An arbitrary ID to be stored together with the newly created
   * session for this user. This is a convenience argument to allow
   * end-applications to associate AAA sessions with other
   * (usually, HTTP) sessions. If your application does not have a
   * notion of application session IDs you can pass this parameter
   * as 'null'.
   * @param terminateOtherSessions if set to true, all other active sessions for this user will
   * be terminated.
   * @return The information of the specified user
   */
  UserDTO login(String userID, String applicationSessionID,
    boolean terminateOtherSessions);

  /**
   * Registers a user logging out of the system.
   *
   * @param userId The id of the user to log out
   * @param applicationSessionID The application session ID of the user session to terminate.
   * If this parameter is set to null then all active sessions of
   * the specified user will be terminated. Otherwise, only user
   * sessions having the specified application session ID will be
   * terminated.
   */
  void logout(String userId, String applicationSessionID);

  /**
   * Logs out all logged in users and terminates all active user sessions.
   */
  void logoutAll();

  /**
   * Checks if a user is logged in the system.
   *
   * @param userID The ID of the user for whom to check the login status
   * @return A list of the active sessions during the call ordered by creation
   * date, null if no active sessions were found.
   */
  List<SessionDTO> isUserAlreadyLoggedIn(String userID);

  /**
   * UPdates the user's password to a new value.
   *
   * @param username The username for which the password is changed. Note that this
   * is not the user ID as it is stored in the database.
   * @param newPassword The new password to set. If this value is left null or empty
   * the system automatically generates an 8-character-length
   * password.
   * @return The new password that was assigned to this user.
   */
  String updatePassword(String username, String newPassword);


  /**
   * Updates the password of the user by first checking that the user can successfully authenticate
   * with its existing password.
   * @param username The userame of the user to update.
   * @param oldPassword The old/existing password of the user.
   * @param newPassword The new password of the user.
   * @return Returns true if the password was successfully updated, false otherwise.
   */
  boolean updatePassword(String username, String oldPassword, String newPassword);

  /**
   * Checks whether a user belongs to an access control group, identified by
   * its name.
   *
   * @param userID The id of the user to check for
   * @param groupName The name of the group to check for
   * @param includeChildren If true then this method will also check if the user belongs
   * to any children of the specified group thus implicitly also
   * belonging to the specified group. If false only the specified
   * group will be checked.
   * @return True if the specified user belongs to the specified group, false
   * otherwise
   */
  boolean belongsToGroupByName(String userID, String groupName,
    boolean includeChildren);

  /**
   * Updates a user attribute, optionally creating if it does not already
   * exist,
   *
   * @param attribute The details of the attribute to update. The attribute is
   * identified by using the attribute name and the userID.
   * @param createIfMissing If true and an attribute with the specified name does not
   * exist for the specified user a new attribute is created. If
   * false, an exception is thrown if the attribute does not exist.
   */
  void updateAttribute(UserAttributeDTO attribute, boolean createIfMissing);

  /**
   * Updates attributes information for one or more users, optionally creating
   * missing attributes.
   *
   * @param attributes The list of attributes to update. Do not forget to correctly
   * set the 'userId' on each of the attributes to be updated.
   * @param createIfMissing If true, missing attributes are created. If false, an
   * exception is thrown if one or more attributes do not exist.
   */
  void updateAttributes(Collection<UserAttributeDTO> attributes,
    boolean createIfMissing);

  /**
   * Deletes a user attribute
   *
   * @param userID The ID of the user whose attribute to delete.
   * @param attributeName The name of the attribute to delete.
   */
  void deleteAttribute(String userID, String attributeName);

  /**
   * Retrieves a user attribute given the user ID and the attribute name.
   *
   * @param userID The ID of the user owner of the attribute to retrieve
   * @param attributeName The name of the attribute to retrieve
   * @return The retrieved attribute or null if the specified attribute does
   * not exist for the specified user
   */
  UserAttributeDTO getAttribute(String userID, String attributeName);

  /**
   * Retrieves a set of user attributes for a set of user IDs and the attribute name.
   *
   * @param userIDs The IDs of the users owning the attribute to retrieve
   * @param attributeName The name of the attribute to retrieve
   * @return The retrieved attributes or an empty Set if the specified attribute does not exist for
   * the specified users
   */
  Set<UserAttributeDTO> getAttributes(Set<String> userIDs, String attributeName);

  /**
   * Retrieves the IDs of the users having a specified attribute.
   *
   * @param userIDs The IDs of the users among which to check. If no user IDs are
   * specified then the search is performed among all users.
   * @param attributeName The name of the attribute to check for
   * @param attributeValue The value of the attribute to check for
   * @return The IDs of the users among those specified having the given
   * attribute.
   */
  Set<String> getUserIDsForAttribute(Collection<String> userIDs,
    String attributeName, String attributeValue);

  // TODO Revisit the Note.

  /**
   * Search for users registered in the system based on a set of specified
   * criteria. If the criteria are empty then all users will be retrieved.
   * <b>NOTE:</b> In the case of attribute based sorting users not
   * having the specified attribute are NOT included in the search result.
   * This is due to the fact that JPA 2.0 does not support join condition
   * using ON and therefore does not support the following query which should
   * be used to retrieve all users: select distinct * from aaa_user user left
   * outer join aaa_user_attributes att on (user.id=att.aaa_user_id and
   * att.column_name='dob') where ... order by att.column_data asc This should
   * be supported in JPA 2.1 *
   *
   * @param criteria The criteria based on which the search will be performed.
   * If null it will be ignored and all users will be returned.
   * @return The users satisfying the defined search criteria.
   */
  List<UserDTO> findUsers(UserSearchCriteria criteria);

  /**
   * Get the number of users registered in the system database and satisfying
   * specific criteria.
   *
   * @param criteria The criteria based on which the search will be performed.
   * Please note that since this method returns only the user count
   * and not a list of users paging and sorting criteria are
   * ignored.
   * @return The number of users satisfying the specified criteria
   */
  long findUserCount(UserSearchCriteria criteria);

  /**
   * Check attribute's value uniqueness
   *
   * @return True is the attribute value is unique, false otherwise.
   */
  boolean isAttributeValueUnique(String attributeValue, String attributeName, String userID);

}
