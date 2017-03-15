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

import java.util.List;

/**
 * An interface for EJBs providing directory services for gadgets.
 * 
 * @author European Dynamics SA
 */
public interface DirectoryService {

	/**
	 * Returns a list of all the available gadgets registered in the system.
	 * 
	 * @param includeDisabled
	 *            Specifies whether disabled gadgets should be returned or not.
	 * @return An array with all the gadgets found.
	 */
	List<ComponentDTO> listGadgets(boolean includeDisabled);

	/**
	 * ?? Searches in the list of available gadgets for the specified search
	 * term. The search is performed in the 'title' and the 'description' of the
	 * gadget with a 'like' operator. It is the caller's responsibility to
	 * include any "%" characters in the searchTerm if necessary.
	 * 
	 * @param searchTerm
	 *            The term to search for.
	 * @param includeDisabled
	 *            Specifies whether disabled gadgets should be returned or not.
	 * @return An array with all the gadgets found.
	 */
	List<ComponentDTO> searchGadgets(java.lang.String searchTerm,
			boolean includeDisabled);

	/**
	 * Find the gadgets registered for a particular user (or group).
	 * 
	 * @param userID
	 *            The user ID who's gadgets will be returned.
	 * @return Array of GadgetDTO.
	 */
	List<ComponentDTO> getGadgetsForUserID(String userID);

	/**
	 * Find the gadgets registered for a particular user (or group).
	 * 
	 * @param userID
	 *            The user ID who's gadgets will be returned.
	 * @return A String array with the IDs of the gadgets.
	 */
	List<String> getGadgetIDsForUserID(String userID);
}
