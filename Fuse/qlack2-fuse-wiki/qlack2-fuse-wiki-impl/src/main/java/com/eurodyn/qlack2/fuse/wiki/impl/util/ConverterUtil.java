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
package com.eurodyn.qlack2.fuse.wiki.impl.util;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;

import com.eurodyn.qlack2.fuse.wiki.api.dto.WikiDTO;
import com.eurodyn.qlack2.fuse.wiki.api.dto.WikiEntryDTO;
import com.eurodyn.qlack2.fuse.wiki.api.dto.WikiEntryVersionDTO;
import com.eurodyn.qlack2.fuse.wiki.api.dto.WikiTagDTO;
import com.eurodyn.qlack2.fuse.wiki.impl.model.WikEntry;
import com.eurodyn.qlack2.fuse.wiki.impl.model.WikEntryHasTag;
import com.eurodyn.qlack2.fuse.wiki.impl.model.WikEntryVersion;
import com.eurodyn.qlack2.fuse.wiki.impl.model.WikTag;
import com.eurodyn.qlack2.fuse.wiki.impl.model.WikWiki;

/**
 * Converter utility class for Wiki module
 * @author European Dynamics SA
 */
public class ConverterUtil {

    /**
     * Converts a WikiEntryDTO object to a WikEntry object. This method does not set
     * the WikEntry object's versions and tags
     * @param dto
     * @return
     */
    public static WikEntry convertToWikiEntry(WikiEntryDTO dto, EntityManager em) {
        if (dto == null) {
            return null;
        }

        WikEntry entity = new WikEntry();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setDtCreated(dto.getDtCreated() != null ? dto.getDtCreated().getTime() : null);
        entity.setDtLastModified(dto.getDtLastModified() != null ? dto.getDtLastModified().getTime() : null);
        entity.setLastModifiedBy(dto.getLastModifiedBy());
        entity.setLocked(dto.getLock());
        entity.setLockedBy(dto.getLockedBy());
        entity.setNamespace(dto.getNamespace());
        entity.setPageContent(dto.getPageContent());
        entity.setUrl(dto.getUrl());
        entity.setHomepage(dto.isHomepage());
        entity.setWikiId(em.find(WikWiki.class, dto.getWikiId()));

        return entity;
    }

    /**
     * Convert from wiki entry entity to DTO
     * @param entity wiki entry entity
     * @return DTO wiki entry dto
     */
    public static WikiEntryDTO convertToWikiEntryDTO(WikEntry entity) {

        if (entity == null) {
            return null;
        }
        WikiEntryDTO dto = new WikiEntryDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());

        if (entity.getCreatedBy() != null) {
            dto.setCreatedBy(entity.getCreatedBy());
        } else {
            dto.setCreatedBy(null);
        }

        if (entity.getDtCreated() != null) {
            dto.setDtCreated(new Date(entity.getDtCreated()));
        } else {
            dto.setDtCreated(null);
        }

        if (entity.getDtLastModified() != null) {
            dto.setDtLastModified(new Date(entity.getDtLastModified()));
        } else {
            dto.setDtLastModified(null);
        }

        if (entity.getLastModifiedBy() != null) {
            dto.setLastModifiedBy(entity.getLastModifiedBy());
        } else {
            dto.setLastModifiedBy(null);
        }

        if (entity.getLocked() != null) {
            dto.setLock(entity.getLocked());
        } else {
            dto.setLock(null);
        }

        if (entity.getLockedBy() != null) {
            dto.setLockedBy(entity.getLockedBy());
        } else {
            dto.setLockedBy(null);
        }

        if (entity.getPageContent() != null) {
            dto.setPageContent(entity.getPageContent());
        } else {
            dto.setPageContent(null);
        }

        if (entity.getUrl() != null) {
            dto.setUrl(entity.getUrl());
        } else {
            dto.setUrl(null);
        }

        if (entity.isHomepage() != null) {
            dto.setHomepage(entity.isHomepage());
        } else {
            dto.setHomepage(null);
        }
        
        dto.setNamespace(entity.getNamespace());
        dto.setWikiId(entity.getWikiId().getId());
        Set<WikEntryHasTag> entryTags = entity.getWikEntryHasTags();
        Set<WikiTagDTO> tags = new HashSet();
        if (entryTags != null && !entryTags.isEmpty()) {
            for (WikEntryHasTag wikEntryHasTag : entryTags) {
                tags.add(convertToWikiTagDTO(wikEntryHasTag.getWikTagId()));
            }
        }
        dto.setWikTags(tags);
        Set<WikEntryVersion> entryVersions = entity.getWikEntryVersions();
        Set<WikiEntryVersionDTO> versions = new HashSet();
        if (entryVersions != null && !entryVersions.isEmpty()) {
            for (WikEntryVersion version : entryVersions) {
                versions.add(convertToWikiVersionDTO(version));
            }
        }
        dto.setWikVersions(versions);
        return dto;
    }

    /**
     * Convert from wiki entry versin entity to DTO
     * @param entity wiki version entity
     * @return DTO wiki version dto
     */
    public static WikiEntryVersionDTO convertToWikiVersionDTO(WikEntryVersion entity) {

        if (entity == null) {
            return null;
        }
        WikiEntryVersionDTO dto = new WikiEntryVersionDTO();
        dto.setComment(entity.getComment());
        dto.setId(entity.getId());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setDtCreated(new Date(entity.getDtCreated()));
        dto.setEntryVersion(entity.getEntryVersion());
        dto.setWikEntryId(entity.getWikEntryId().getId());
        dto.setPageContent(entity.getPageContent());
        return dto;
    }

    /**
     * Convert from wiki tag entity to DTO
     * @param entity wiki tag entity
     * @return DTO wiki tag dto
     */
    public static WikiTagDTO convertToWikiTagDTO(WikTag entity) {

        if (entity == null) {
            return null;
        }
        WikiTagDTO dto = new WikiTagDTO();
        dto.setDescription(entity.getDescription());
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    /**
     * Convert from wiki entity to DTO
     * @param entity wiki entity
     * @return DTO wiki dto
     */
    public static WikiDTO convertToWikiDTO(WikWiki entity) {

        if (entity == null) {
            return null;
        }
        WikiDTO dto = new WikiDTO();
        dto.setDescription(entity.getDescription());
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLogo(entity.getLogo());
        return dto;
    }

    /**
     * Convert from wiki tag dto to entity objetc
     * @param dto wiki tag dto
     * @return entity wiki tag entity
     */
    public static WikTag convertToTagEntity(WikiTagDTO dto) {

        if (dto == null) {
            return null;
        }
        WikTag entity = new WikTag();
        entity.setDescription(dto.getDescription());
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        return entity;
    }
}
