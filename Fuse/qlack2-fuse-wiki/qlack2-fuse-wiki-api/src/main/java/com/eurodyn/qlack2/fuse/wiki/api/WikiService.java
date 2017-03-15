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
import com.eurodyn.qlack2.fuse.wiki.api.dto.WikiDTO;
import com.eurodyn.qlack2.fuse.wiki.api.exception.QWikiException;

import java.util.List;

/**
 * An interface for EJBs providing Services for Wiki
 * @author European Dynamics SA
 */
public interface WikiService {

    /**
     * Create a wiki. If invalid data is passed appropriate exceptions are thrown.
     * @param dto wiki dto
     * @throws QWikiException if data passed in dto is not valid or if wiki already exists with the name.
     */
    String createWiki(WikiDTO dto) throws QWikiException;

    /**
     * Create a wiki with a specified id. If invalid data is passed appropriate exceptions are thrown.
     * @param dto wiki dto
     * @throws QWikiException if data passed in dto is not valid or if wiki already exists with the name.
     */
    String createWikiWithId(WikiDTO dto) throws QWikiException;
    
    /**
     * Edit a wiki. If invalid data is passed appropriate exceptions are thrown.
     * @param dto wiki dto to update
     * @throws QWikiException  if data passed in dto is not valid or if wiki does not exist
     */
    void editWiki(WikiDTO dto) throws QWikiException;

    /**
     * Delete a wiki.If invalid data is passed appropriate exceptions are thrown.
     * @param wikiId wiki to delete
     * @throws QWikiException if data passed in dto is not valid or if wiki does not exist
     */
    void deleteWiki(String wikiId) throws QWikiException;

    /**
     * Get a particular wiki Object by id. if it doesn't exist proper exception is thrown.
     * @param wikiId id of the Wiki object
     * @return WikiDTO wiki dto
     */
    WikiDTO findWiki(String wikiId);

    /**
     * Get all the wiki objects available in system
     * @param pagingParams paging parameter
     * @return A list of all the wiki objects
     */
    List<WikiDTO> getAllWikis(PagingParams pagingParams);

    /**
     * Get the name of wiki from the system
     * @param wikiId - id of the wiki for which name to be retrieved
     * @return String name of the wiki
     * @throws QWikiException - if wiki does not exists in the system
     */
    public String getWikiName(String wikiId) throws QWikiException;

    /**
     * Get the wiki for the provided name.
     * @param wikiName - name of the wiki for retrieving the wiki from the system
     * @return WikiDTO wiki dto
     */
    public WikiDTO findWikiByName(String wikiName);
}
