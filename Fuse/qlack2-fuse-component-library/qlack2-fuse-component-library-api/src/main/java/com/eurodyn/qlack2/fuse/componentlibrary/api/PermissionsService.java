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
package com.eurodyn.qlack2.fuse.componentlibrary.api;

import com.eurodyn.qlack2.fuse.componentlibrary.api.dto.ComponentPermissionDTO;
import com.eurodyn.qlack2.fuse.componentlibrary.api.exception.QComponentLibraryException;

/**
 * An interface for EJBs providing services to manage the permissions a gadget may require.
 * @author European Dynamics SA
 */
public interface PermissionsService {

    /**
     * Requests a list of permissions on behalf of a gadget for a particular user. Note that this method
     * is only registering the actual request. The requested permissions will still need to be granted
     * or (revoked) by {@link #grantPermission(java.lang.String) grantPermission} or
     * {@link #revokePermission(java.lang.String, java.lang.String, java.lang.String) revokePermission}.
     * @param gadgetPermissions A permission to be requested.
     * @param gadgetID The ID of the gadget requesting the permissions.
     * @param userID The ID of the user for which the permission is requested.
     */
    void requestPermissions(java.lang.String[] gadgetPermissions, java.lang.String gadgetID, java.lang.String userID);


    /**
     * Requests a permission on behalf of a gadget for a particular user. Note that this method
     * is only registering the actual request. The requested permission will still need to be granted
     * or (revoked) by {@link #grantPermission(java.lang.String) grantPermission} or
     * {@link #revokePermission(java.lang.String, java.lang.String, java.lang.String) revokePermission}.
     * @param gadgetPermission A list of permissions to request.
     * @param gadgetID The ID of the gadget requesting the permissions.
     * @param userID The ID of the user for which the permissions are requested.
     */
    void requestPermission(java.lang.String gadgetPermission, java.lang.String gadgetID, java.lang.String userID);

    /**
     * Grants a list of permissions on behalf of a gadget for a particular user. This method should be called
     * when the end-user has specifically allowed a gadget the permissions it requested (after the end-user
     * receiving the relevant notification).
     * @param gadgetPermissions A list of permissions to grant.
     * @param gadgetID The ID of the gadget to be granted the permissions.
     * @param userID The ID of the user for which the permissions are granted.
     * @throws QComponentLibraryException When a permission request has not been performed for this particular grant request.
     */
    void grantPermissions(java.lang.String[] gadgetPermissions, java.lang.String gadgetID, java.lang.String userID) throws QComponentLibraryException;

    /**
     * Grants a permission on behalf of a gadget for a particular user. This method should be called
     * when the end-user has specifically allowed a gadget the permissions it requested (after the end-user
     * receiving the relevant notification).
     * @param gadgetPermission A permission to grant.
     * @param gadgetID The ID of the gadget to be granted the permissions.
     * @param userID The ID of the user for which the permissions are granted.
     * @throws QComponentLibraryException When a permission request has not been performed for this particular grant request.
     */
    void grantPermission(java.lang.String gadgetPermission, java.lang.String gadgetID, java.lang.String userID) throws QComponentLibraryException;

    /**
     * Revokes permissions previously granted to a gadget for a particular user. This method is called
     * when the end-user is requesting to remove a particular permission for a gadget (e.g. the end-user may change
     * its mind regarding allowing the gadget to send email notifications).
     * @param gadgetPermissions A list of permissions to revoke.
     * @param gadgetID The ID of the gadget to be revoked the permissions.
     * @param userID The ID of the user for which the permissions are revoked.
     * @throws QComponentLibraryException When a permission request has not been performed for this particular revoket request.
     */
    void revokePermissions(java.lang.String[] gadgetPermissions, java.lang.String gadgetID, java.lang.String userID) throws QComponentLibraryException;

    /**
     * Revokes a permission previously granted to a gadget for a particular user. This method is called
     * when the end-user is requesting to remove a particular permission for a gadget (e.g. the end-user may change
     * its mind regarding allowing the gadget to send email notifications).
     * @param gadgetPermission A list of permissions to revoke.
     * @param gadgetID The ID of the gadget to be revoked the permissions.
     * @param userID The ID of the user for which the permissions are revoked.
     * @throws QComponentLibraryException When a permission request has not been performed for this particular revoke request.
     */
    void revokePermission(java.lang.String gadgetPermission, java.lang.String gadgetID, java.lang.String userID) throws QComponentLibraryException;

    /**
     * Checks if a gadget has a particular permission from a user.
     * @param gadgetPermissions The permissions to check.
     * @param gadgetID The ID of the gadget checking the permission.
     * @param userID The ID of the user for which the permissions is checked.
     * @return
     */
    boolean hasPermission(java.lang.String gadgetPermissions, java.lang.String gadgetID, java.lang.String userID);

    /**
     * Finds permission requests that have been made by gadgets which are still pending.
     * Note that a permission with enabled=0 is not a pending permission, but a permission
     * which has been explicitly not granted by the user (i.e. the gadget requested a permission
     * but the user decided not to grant it).
     * @param userID The user ID for which pending gadget requests will be returned.
     * @return An array with all gadgets' pending permissions.
     */
    ComponentPermissionDTO[] getPendingPermissionRequests(String userID);

    /**
     * Retrieves a permission request by its id
     * @param permissionRequestID The id of the permission request to retrieve
     * @return The permission request
     */
    ComponentPermissionDTO getPermissionRequest(String permissionRequestID);

    /**
     * Grants a permission on behalf of a gadget for a particular user. This method should be called
     * when the end-user has specifically allowed a gadget the permissions it requested (after the end-user
     * receiving the relevant notification).
     * @param permissionRequestID The ID of the permissions request to be granted.
     * @throws QComponentLibraryException When a permission request has not been performed for this particular grant request.
     */
    void grantPermission(String permissionRequestID) throws QComponentLibraryException;

     /**
     * Revokes permissions previously granted to a gadget for a particular user. This method is called
     * when the end-user is requesting to remove a particular permission for a gadget (e.g. the end-user may change
     * its mind regarding allowing the gadget to send email notifications).
     * @param permissionRequestID The ID of the permissions request to be revoked.
     * @throws QComponentLibraryException When a permission request has not been performed for this particular revoke request.
     */
    void revokePermission(String permissionRequestID) throws QComponentLibraryException;

}