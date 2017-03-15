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

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.wiki.api.dto.WikiEntryDTO;
import com.eurodyn.qlack2.fuse.wiki.api.dto.WikiEntryVersionDTO;
import com.eurodyn.qlack2.fuse.wiki.api.exception.QWikiException;

import javax.jws.WebParam;
import java.util.List;
import java.util.Set;

/**
 * Remote interface for EJB services related to Wiki Entry.
 * @author European Dynamics SA
 */
public interface WikiEntryService {

    /**
     * Creates a wiki entry. If required data is not passed appropriate exceptions are thrown
     * @param entryDTO data for Wiki entry to create
     * @return id of the created entry
     * @throws QWikiException if invalid data or wiki is passed
     */
    String createEntry(@WebParam WikiEntryDTO entryDTO) throws QWikiException;

    /**
     * Creates a wiki entry with its page content
     * @param entryDTO data for Wiki entry to create
     * @return id of the created entry
     * @throws QWikiException if invalid data or wiki is passed
     */
    String createEntryWithPageContent(@WebParam WikiEntryDTO entryDTO, String comment) throws QWikiException;
    
    /**
     * Checks if a wiki has a home page
     * @param wikiId the id of the wiki
     * @throws QWikiException if invalid data or wiki is passed
     */
    Boolean wikiHasHomepage(@WebParam String wikiId) throws QWikiException;
    
    /**
     * Removes the home page for a wikWiki
     * @param wikiId the id of the wiki
     * @throws QWikiException if invalid data or wiki is passed
     */
	void removeHomepageFromWiki(@WebParam String wikiId) throws QWikiException;
    
	/**
     * Edits a wiki entry. If required data is not passed appropriate exceptions are thrown
     * @param entryDTO data for Wiki entry to modify
     * @throws QWikiException  if wiki id passed is invalid or required data passed is invalid or wiki entry does not exist
     * or tag data passed is invalid or if tag already exists.
     */
    void editEntry(@WebParam WikiEntryDTO entryDTO) throws QWikiException;

    /**
     * Deletes a wiki entry. If entry is not found <code>WikiEntryDoesNotExistException</code> is thrown
     * @param entryId id of the entry to delete
     * @throws QWikiException if wiki entry does not exist
     */
    void deleteEntry(@WebParam String entryId) throws QWikiException;

    /**
     * Searches all entries for the search term and applies paging if passed.
     * @param searchTerm search term to search on.
     * @param paging for pagination
     * @return List of entries
     */
    List<WikiEntryDTO> searchEntry(String searchTerm, PagingParams paging);

    /**
     * Get a particular Wiki entry.
     * @param entryID id of the entry to get
     * @return Wiki entry
     */
    WikiEntryDTO getEntryById(String entryID);

    /**
     * Get the home page of the specified wiki.
     * @param wikiId id of the wiki to get its home page
     * @return Wiki entry
     */
    WikiEntryDTO getHomepageForWiki(String wikiId);
    
    /**
     * Get a particular Wiki entry.
     * @param namespace The namespace of the entry to retrieve
     * @return Wiki entry
     */
    WikiEntryDTO getEntryByNamespace(String namespace);

    /**
     * Get related wiki Entries for the tag.
     * @param entryID id of the wiki entry
     * @param tagName name of the tag
     * @return List of the related wiki entries
     * @throws QWikiException if wiki entry does not exist or if wiki tag does not exist
     */
    Set<WikiEntryDTO> getRelatedEntriesForTag(String entryID, String tagName) throws QWikiException;

    /**
     * Gets all the related wiki entries. This method first fetches all the tags associated with the entry.
     * For each tag fetched , gets all associated Entries and finally the whole collection is returned
     * @param entryID the wiki entry id
     * @return list entries having similar tags
     * @throws QWikiException if wiki entry does not exist
     */
    Set<WikiEntryDTO> getRelatedEntries(String entryID) throws QWikiException;

    /**
     * Get the latest entries by last modified date. numberOfrecords parameter is by default set to 5.
     * Caller can pass a parameter and can get the number of latest records accordingly.
     * @param numberOfrecords number of latest entries to fetch
     * @return List of wiki entries limited to numberOfrecords or 5 by default
     */
    List<WikiEntryDTO> getLatestEntries(int numberOfrecords);

    /**
     * Get all the wiki entries for specific Wiki. Entries are ordered by created date .
     * @param wikiId Id of the Wiki
     * @param paging Paging support parameters
     * @return List of entries for the Wiki
     * @throws QWikiException if wiki id passed is invalid
     */
    List<WikiEntryDTO> getAllEntriesForWiki(String wikiId, PagingParams paging)
            throws QWikiException;

    /**
     * Gets the default entry for the wiki.This method gets all the entries for the wiki first
     * and then returns the top record as the default entry.
     * @param wikiId Id of the wiki
     * @return Default Wiki Entry
     * @throws QWikiException if invalid wiki id is passed
     */
    WikiEntryDTO getDefaultEntryForWIki(String wikiId) throws QWikiException;

    /**
     * Lock a wiki entry
     * @param entryID Id of the wiki entry to lock
     * @param userId lock for the user
     * @throws QWikiException if wiki entry does not exist
     */
    void lockEntry(String entryID, String userId) throws QWikiException;

    /**
     * Clear the lock from wiki entry.
     * @param entryID id of the wiki entry to unlock
     * @throws QWikiException if wiki entry does not exist
     */
    void unlockEntry(String entryID);

    /**
     * unlock all the wiki entries.
     */
    void unlockEntries();

    /**
     * Check if entry is Locked.
     * @param entryID
     * @return
     * @throws QWikiException if wiki entry does not exist
     */
    boolean isEntryLocked(String entryID) throws QWikiException;

    /**
     * Get all the entries in a Wiki that are related to a tag id passed.
     * @param wikiId Id of the wiki
     * @param tagId Id of the tag
     * @param pagingParams Paging support parameters
     * @return List of wiki entries related to tag in the wiki
     * @throws QWikiException if invalid wiki id is passed
     */
    List<WikiEntryDTO> getAllEntriesForTag(String wikiId, String tagId, PagingParams pagingParams)
            throws QWikiException;

    /**
     * Get all the versions for a particular wiki Entry. Versions are in descending order with latest version is first in list.
     * @param entryId Id of the wiki Entry
     * @param pagingParams Paging support parameters
     * @return List of wiki entry versions
     * @throws QWikiException if wiki does not exist
     */
    List<WikiEntryVersionDTO> getAllVersionsForWikiEntry(String entryId, PagingParams pagingParams) throws QWikiException;

    /**
     * Get a specific wiki entry version by id. 
     * @param versionId Id of the wiki Entry Version
     * @return The wiki Entry Version
     */
    WikiEntryVersionDTO getEntryVersionById(String versionId);
    
    /**
     * Get all the wiki entries for provided WikiId. Entries are ordered by name space.
     * @param wikiId Id of the Wiki
     * @return List of entries for the Wiki
     * @throws QWikiException if wiki id passed is invalid
     */
    List<WikiEntryDTO> listAllWikiPages(String wikiId) throws QWikiException;
    
    /**
     * Sets the current entry version for a page. This method DELETES the versions AFTER the specified version, and UPDATES the WikEntry pageContent.
     * @param wikiEntryId Id of the WikiEntry
     * @param wikiEntryVersionNumber The entry version of the WikEntryVersion which will be the current WikEntryVersion
     * @throws QWikiException if wikiEntryVersionNumber or wikiEntry do not exist
     */
    void setCurrentVersionEntry(String wikiEntryId, int wikiEntryVersionNumber) throws QWikiException;
    
    /**
     * Updates the wiki entry entry. This method creates a new wiki entry version, containing the content and 
     * the pertinent description (WikEntryVersion.comment). All other fields remain the same with the previous version
     * 
     * *NOTE* the update page updates the content of the WikEntry and makes the new versions the current one (latest).
     * 
     * @param wikiEntryId Id of the WikiEntry
     * @param wikiEntryVersionComment The comment of the version update
     * @param wikiContent The content of the version update
     * @param userId The id of the user who makes the update
     * @throws QWikiException if wikiEntryVersionNumber or wikiEntry do not exist
     */
    WikiEntryVersionDTO updateEntryVersion(String wikiEntryId, String wikiEntryVersionComment, String wikiContent, String userId) throws QWikiException;

}
