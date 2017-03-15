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
package com.eurodyn.qlack2.fuse.clipboard.api;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.clipboard.api.dto.ClipboardEntryDTO;
import com.eurodyn.qlack2.fuse.clipboard.api.dto.ClipboardMetaDTO;
import com.eurodyn.qlack2.fuse.clipboard.api.exception.QClipboardException;

import java.util.List;

/**
 *
 * @author European Dynamics SA
 */
public interface ClipboardService {

	/**
	 * Creates a new clipboard entry
	 *
	 * @param entry
	 *            The information of the entry to add
	 * @return The newly created entry
	 */
	ClipboardEntryDTO createEntry(ClipboardEntryDTO entry);

	/**
	 * Updates a clipboard entry
	 *
	 * @param entry
	 *            The information of the entry to update. This method uses the
	 *            entry id to identify the entry to update and updates the
	 *            following attributes of the entry:<br>
	 *            - title<br>
	 *            - description<br>
	 *            - type
	 */
	void updateEntry(ClipboardEntryDTO entry) throws QClipboardException;

	/**
	 * Deletes a clipboard entry
	 *
	 * @param entry
	 *            The information of the entry to delete. The entry id is used
	 *            to identify the entry to delete
	 */
	void deleteEntry(ClipboardEntryDTO entry) throws QClipboardException;

	/**
	 * Deletes a list of clipboard entries
	 *
	 * @param entryIds
	 *            The ids of the entries to delete
	 */
	void deleteEntries(String entryIds[]);

	/**
	 * Deletes all the entries of a specific owner
	 *
	 * @param ownerId
	 *            The owner whose entries to delete
	 */
	void deleteEntries(String ownerId);

	/**
	 * Retrieves a clipboard entry
	 *
	 * @param id
	 *            The id of the entry to retrieve
	 * @return The retrieved clipboard entry
	 */
	ClipboardEntryDTO getEntry(String id) throws QClipboardException;

	/**
	 * Retrieves the clipboard entries of a specific owner
	 *
	 * @param ownerId
	 *            The owner of the entries to retrieve
	 * @param paging
	 *            The paging parameters to use
	 * @return The retrieved clipboard entries
	 */
	List<ClipboardEntryDTO> getEntries(String ownerId, PagingParams paging);

	/**
	 * Retrieves the clipboard entries of a specific owner having a specific
	 * type
	 *
	 * @param ownerId
	 *            The owner of the entries to retrieve
	 * @param typeId
	 *            The type of the entries to retrieve
	 * @param paging
	 *            The paging parameters to use
	 * @return The retrieved clipboard entries
	 */
	List<ClipboardEntryDTO> getEntries(String ownerId, String typeId,
			PagingParams paging);

	/**
	 * Adds a metadatum to a clipboard entry
	 *
	 * @param entryId
	 *            The id of the entry to add the metadatum to
	 * @param meta
	 *            The metadatum to add
	 * @return The newly added metadatum
	 */
	ClipboardMetaDTO addEntryMeta(String entryId, ClipboardMetaDTO meta)
			throws QClipboardException;

	/**
	 * Updates a metadatum of a clipboard entry
	 *
	 * @param meta
	 *            The info of the metadatum to update. The id is used to
	 *            identify the metadatum, while the name and value attributes
	 *            are used to update the respective attributes of the metadatum
	 */
	void updateEntryMeta(ClipboardMetaDTO meta) throws QClipboardException;

	/**
	 * Removes a metadatum from a clipboard entry
	 *
	 * @param meta
	 *            The information of the metadatum to delete. The meta id is
	 *            used to identify the metadatum to delete
	 */
	void removeEntryMeta(ClipboardMetaDTO meta) throws QClipboardException;
}
