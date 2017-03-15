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
package com.eurodyn.qlack2.fuse.wiki.api;

import com.eurodyn.qlack2.fuse.wiki.api.dto.WikiEntryDTO;
import com.eurodyn.qlack2.fuse.wiki.api.dto.WikiTagDTO;
import com.eurodyn.qlack2.fuse.wiki.api.exception.QWikiException;

import java.util.List;
import java.util.Set;

/**
 * An interface for EJBs providing services for Wiki Tags
 * @author European Dynamics SA
 */
public interface WikiTagService {

    /**
     * Creates a tag. While creating the new tag this method makes sure that
     * name is set in DTO. If required fields are missing then InvalidWikiTagException
     * is thrown.
     * @param dto the tag to persist
     * @return WikiTagDTO tag with the id of the newly persisted tag populated in DTO
     * @throws QWikiException if required fields are missing or dto passed is null or if tag with name already exists
     */
    WikiTagDTO createTag(WikiTagDTO dto) throws QWikiException;

    /**
     * Edits a tag. appropriate exceptions are thrown if data passed is invalid.
     * @param dto tag dto to update
     * @throws QWikiException  if invalid data is passed or if tag doesn't exist
     */
    void editTag(WikiTagDTO dto) throws QWikiException;

    /**
     * Remove a wiki tag.
     * @param tagId
     * @throws QWikiException if invalid data is passed or if tag does not exist
     */
    void removeTag(String tagId) throws QWikiException;

    /**
     * find a tag with specific id.
     * @param id of the tag
     * @return Tag DTO
     */
    WikiTagDTO findTag(Object id);

    /**
     * get all the associated entries with the tag. Appropriate exceptions are thrown if tag doesn't exist.
     * @param tagName name of the tag
     * @return The wiki entries related to tag
     * @throws QWikiException if tag is not found in the system
     */
    Set<WikiEntryDTO> getAllAssociatedEntries(String tagName) throws QWikiException;

    /**
     * Find all wiki tags in the system available.
     * @return List of wiki tag dto which exists in the system.
     */
    public List<WikiTagDTO> findAll();

    /**
     * Find wiki tag name for a tag id provided.
     * @param tagId - id of the wiki tag to find the tag name.
     * @return String name of the tag.
     */
    public String getTagName(String tagId);

    /**
     * Find wiki tag from the system for the provided entry id.
     * @param entryId
     * @return A list of wiki tag dto
     * @throws QWikiException Wiki Entry Does Not Exist Exception in the system
     */
    public List<WikiTagDTO> findTagByEntryId(String entryId) throws QWikiException;
}
