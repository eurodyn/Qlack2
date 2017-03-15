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
package com.eurodyn.qlack2.fuse.clipboard.impl.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.eurodyn.qlack2.fuse.clipboard.api.dto.ClipboardEntryDTO;
import com.eurodyn.qlack2.fuse.clipboard.api.dto.ClipboardMetaDTO;
import com.eurodyn.qlack2.fuse.clipboard.impl.model.ClbEntry;
import com.eurodyn.qlack2.fuse.clipboard.impl.model.ClbMetadata;

/**
 *
 * @author European Dynamics SA.
 */
public class ConverterUtil {

    /**
     * Converts a ClipboardEntryDTO object to a ClbEntry object. This method does not
     * set the ClbMetadatas of the ClbEntry object
     * @param dto The object to convert
     * @return The converted object
     */
    public static ClbEntry convertToEntryEntity(ClipboardEntryDTO dto) {
        if (dto == null) {
            return null;
        }
        ClbEntry entity = new ClbEntry();
        entity.setCreatedOn(dto.getCreatedOn());
        entity.setDescription(dto.getDescription());
        entity.setId(dto.getId());
        entity.setObjectId(dto.getObjectId());
        entity.setOwnerId(dto.getOwnerId());
        entity.setTitle(dto.getTitle());
        entity.setTypeId(dto.getTypeId());
        return entity;
    }


    /**
     * Converts a ClbEntry object to a ClipboardEntryDTO object.
     * @param entity The object to convert
     * @return The converted object
     */
    public static ClipboardEntryDTO convertToEntryDTO(ClbEntry entity) {
        if (entity == null) {
            return null;
        }
        ClipboardEntryDTO dto = new ClipboardEntryDTO();
        dto.setCreatedOn(entity.getCreatedOn());
        dto.setDescription(entity.getDescription());
        dto.setId(entity.getId());
        dto.setObjectId(entity.getObjectId());
        dto.setOwnerId(entity.getOwnerId());
        dto.setTitle(entity.getTitle());
        dto.setTypeId(entity.getTypeId());
        dto.setMetadata(convertToMetaDTOList(entity.getClbMetadatas()));
        return dto;
    }


    /**
     * Converts a collection of ClbEntry objects to a list of ClipboardEntryDTO objects.
     * @param entities The objects to convert
     * @return The converted objects
     */
    public static List<ClipboardEntryDTO> convertToEntryDTOList(Collection<ClbEntry> entities) {
        if (entities == null) {
            return null;
        }
        List<ClipboardEntryDTO> dtos = new ArrayList<ClipboardEntryDTO>();
        for (ClbEntry entity : entities) {
            dtos.add(convertToEntryDTO(entity));
        }
        return dtos;
    }


    /**
     * Converts a ClipboardMetaDTO object to a ClbMetadata object. This method does not
     * set the ClbContentId of the ClbMetadata object
     * @param dto The object to convert
     * @return The converted object
     */
    public static ClbMetadata convertToMetaEntity(ClipboardMetaDTO dto) {
        if (dto == null) {
            return null;
        }
        ClbMetadata entity = new ClbMetadata();
        entity.setId(dto.getId());
        entity.setMetaName(dto.getName());
        entity.setMetaValue(dto.getValue());
        return entity;
    }


    /**
     * Converts a ClbMetadata object to a ClipboardMetaDTO object.
     * @param entity The object to convert
     * @return The converted object
     */
    public static ClipboardMetaDTO convertToMetaDTO(ClbMetadata entity) {
        if (entity == null) {
            return null;
        }
        ClipboardMetaDTO dto = new ClipboardMetaDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getMetaName());
        dto.setValue(entity.getMetaValue());
        return dto;
    }


    /**
     * Converts a collection of ClbMetadata objects to a list of ClipboardMetaDTO objects.
     * @param entities The objects to convert
     * @return The converted objects
     */
    public static List<ClipboardMetaDTO> convertToMetaDTOList(Collection<ClbMetadata> entities) {
        if (entities == null) {
            return null;
        }
        List<ClipboardMetaDTO> dtos = new ArrayList<ClipboardMetaDTO>();
        for (ClbMetadata entity : entities) {
            dtos.add(convertToMetaDTO(entity));
        }
        return dtos;
    }
}
