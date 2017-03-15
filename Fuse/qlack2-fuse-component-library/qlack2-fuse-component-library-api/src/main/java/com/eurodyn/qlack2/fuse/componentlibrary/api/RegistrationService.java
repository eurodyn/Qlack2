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

import com.eurodyn.qlack2.fuse.componentlibrary.api.dto.ComponentDTO;

/**
 * An interface for EJBs providing registration services for gadgets.
 * @author European Dynamics SA
 */
public interface RegistrationService {

    /**
     * Registers a new gadget with the system, allowing it to be used by end-users.
     * @param gadgetDTO The information describing the gadget.
     * @return Returns the unique key a gadget is assigned to the system. This key is needed in
     * all further communicate from the gadget to the system as an identification token.
     */
    ComponentDTO registerGadget(ComponentDTO gadgetDTO);

    /**
     * Removes a previously registered gadget from the system. All information regarding the gadget including
     * end-user defined configuration for this gadget is permanently deleted.
     * @param gadgetID The ID of the gadget to remove.
     */
    void unregisterGadget(java.lang.String gadgetID);

    /**
     * Updates the information about a gadget. Updatable information includes title, box link, author,
     * description, info link, icon link, page link and configuration page.
     * @param gadgetDTO
     */
    void updateGadget(ComponentDTO gadgetDTO);

    /**
     * Enables a gadget previously registered with the system.
     * @param gadgetID The ID of the gadget to enable.
     */
    void enableGadget(java.lang.String gadgetID);

    /**
     * Disables a gadget previously registered with the system. A disabled gadget is returned in searches,
     * however it can not be displayed to the end-user's homepage.
     * @param gadgetID The ID of the gadget to disable.
     */
    void disableGadget(java.lang.String gadgetID);

    /**
     * Checks if a specific gadget is enabled.
     * @param gadgetID The ID of the gadget to check.
     * @return True, if the gadget is enabled, false otherwise.
     */
    boolean isGadgetEnabled(java.lang.String gadgetID);

    /**
     * Returns the gadgetID associated with a particular user having added the gadget to its homepage. This method
     * is useful to allow the application to internally define which is the gadget accessing the system without such information
     * being explicitly passed.
     * @param gadgetUserKey The user-key associated with the gadgets being added to a homepage. This information is being passed
     * to the gadget from SCP.
     * @return The gadgetID associated with a particular user having added the gadget to its homepage.
     */
    String getGadgetIDFromGadgetUserKey(String gadgetUserKey);

    /**
     * Returns the userID associated with a particular user having added the gadget to its homepage. This method
     * is useful to allow gadgets to externally define who is the user accessing the gadget without such information
     * being explicitly passed during the gadget-display call.
     * @param gadgetUserKey The user-key associated with the gadgets being added to a homepage. This information is being passed
     * to the gadget from the SCP.
     * @return The user ID of the user that has added the respective gadget to its homepage.
     */
    String getUserIDFromGadgetUserKey(String gadgetUserKey);

    /**
     * Checks if a secret key corresponds to a registered gadget. Note that this method does not
     * take into account whether a gadget is active or not.
     * @param gadgetSecretKey The secretKey of the gadget to be checked.
     * @return True if the secret key corresponds to a gadget registered in the system, false otherwise.
     */
    boolean isValidSecretKey(String gadgetSecretKey);

}
