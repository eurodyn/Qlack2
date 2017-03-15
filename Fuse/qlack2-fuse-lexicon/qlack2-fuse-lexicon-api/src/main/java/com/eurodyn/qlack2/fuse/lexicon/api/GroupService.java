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
package com.eurodyn.qlack2.fuse.lexicon.api;

import java.util.List;
import java.util.Set;

import com.eurodyn.qlack2.fuse.lexicon.api.dto.GroupDTO;

public interface GroupService {
	/**
	 * Creates a new translation group
	 *
	 * @param group
	 *            The details of the group to create. Any keys contained in the
	 *            passed GroupDTO object are ignored. In order to add keys to
	 *            the new group the relevant method of the KeyService should be
	 *            used.
	 * @return The ID of the newly created group.
	 */
	String createGroup(GroupDTO group);

	/**
	 * Updates a translation group
	 *
	 * @param group
	 *            The details of the group to update. The group ID is used to
	 *            identify the group being updated while the rest of the
	 *            GroupDTO fields are used to update the group in the Lexicon
	 *            database.
	 *
	 */
	void updateGroup(GroupDTO group);

	/**
	 * Delete a group
	 *
	 * @param groupID
	 *            The ID of the group to delete.
	 */
	void deleteGroup(String groupID);

	/**
	 * Retrieves the details of a translation group
	 *
	 * @param groupID
	 *            The ID of the group to retrieve.
	 * @return The details of the specified group or null if the group does not
	 *         exist.
	 */
	GroupDTO getGroup(String groupID);

	/**
	 * Retrieves the details of a translation group
	 *
	 * @param groupName
	 *            The name of the group to retrieve.
	 * @return The details of the specified group or null if the group does not
	 *         exist.
	 */
	GroupDTO getGroupByName(String groupName);
	
	/**
	 * Retrieves groups with groupName different from excludedGroupNames
	 *
	 * @param excludedGroupNames
	 *            List of excluded names.
	 * @return The details of the specified groups or null if the groups does not
	 *         exist.
	 */
	Set<GroupDTO> getRemainingGroups(List<String> excludedGroupNames);

	/**
	 * Retrieve all groups available in Lexicon
	 *
	 * @return The available groups.
	 */
	Set<GroupDTO> getGroups();

	/**
	 * Deletes the translation values of a group for a specific languageID
	 *
	 * @param groupID
	 * @param languageID
	 */
	void deleteLanguageTranslations(String groupID, String languageID);

	/**
	 * Deletes the translation values of a group for a specific locale
	 *
	 * @param groupID
	 * @param locale
	 */
	void deleteLanguageTranslationsByLocale(String groupID, String locale);

	/**
	 * Finds the last date in which any of the keys of this group in the
	 * requested locale has been updated. Useful to implement caching.
	 * 
	 * @param groupID The id of the group to check.
	 * @param locale The locale for the group to check.
	 * @return An EPOCH with the last date on which an update was performed,
	 * or the current time if the combination of groupID and locale does not
	 * exist.
	 */
	long getLastUpdateDateForLocale(String groupID, String locale);
}
