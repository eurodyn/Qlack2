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

import com.eurodyn.qlack2.fuse.componentlibrary.api.exception.QComponentLibraryException;

import java.util.List;


/**
 * An interface for EJBs providing services to manage the end-user interacting with a gadget.
 * @author European Dynamics SA
 */
public interface UserInteractionService {

    /**
     * Adds the gadget to the user's homepage.
     * @param gadgetID The ID of the gadget to add.
     * @param userID The ID of the user adding the gadget.
     * @param notification Specifies whether the module should notify the gadget of the addition.
     * @throws QComponentLibraryException
     */
    void addGadgetToHomepage(String gadgetID, String userID, boolean notification) throws QComponentLibraryException;

    /**
     * Removes a gadget from the user's homepage.
     * @param gadgetID The ID of the gadget to be removed.
     * @param userID The ID of the user removing the gadget.
     * @param notification Specifies whether the module should notify the gadget of the removal.
     * @return String The ID of the gadget subscription.
     */
    String removeGadgetFromHomepage(String gadgetID, String userID, boolean notification) throws QComponentLibraryException;

    /**
     * Adds the gadget to the group's homepage.
     * @param gadgetID The ID of the gadget to add.
     * @param groupID The ID of the group adding the gadget.
     * @param notification Specifies whether the module should notify the gadget of the addition.
     * @throws QComponentLibraryException
     */
    void addGadgetToGroupPage(String gadgetID, String groupID, boolean notification) throws QComponentLibraryException;

    /**
     * Removes a gadget from the group's homepage.
     * @param gadgetID The ID of the gadget to be removed.
     * @param groupID The ID of the group removing the gadget.
     * @param notification Specifies whether the module should notify the gadget of the removal.
     */
    void removeGadgetFromGroupPage(String gadgetID, String groupID, boolean notification) throws QComponentLibraryException;

    /**
     * Re-orders the gadgets for a user or gorup.
     * @param orderOfGadgetIDs A list of gadget IDs specifying the new order of the gadgets.
     * @param userID The user ID (or group ID) which has the gadgets on his homepage.
     */
    void reorderGadgets(List<String> orderOfGadgetIDs, String userID);


    /**
     * Sets the state of a particular gadget for a specific user. Note that the module is totally
     * unaware of what the state actual represents; this is left to the end-application.
     * @param gadgetID The ID of the gadgets who's state is going to be changed.
     * @param state The new state for the gadget.
     * @param userID The ID of the user having the gadget.
     */
    void setState(String gadgetID, int state, String userID);
}