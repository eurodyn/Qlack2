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
package com.eurodyn.qlack2.fuse.calendar.impl.util;

import com.eurodyn.qlack2.fuse.calendar.api.dto.CalendarDTO;
import com.eurodyn.qlack2.fuse.calendar.api.dto.CalendarItemDTO;
import com.eurodyn.qlack2.fuse.calendar.api.dto.ParticipantDTO;
import com.eurodyn.qlack2.fuse.calendar.api.dto.SupportingObjectDTO;
import com.eurodyn.qlack2.fuse.calendar.impl.model.CalCalendar;
import com.eurodyn.qlack2.fuse.calendar.impl.model.CalItem;
import com.eurodyn.qlack2.fuse.calendar.impl.model.CalParticipant;
import com.eurodyn.qlack2.fuse.calendar.impl.model.CalSupportingObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 *
 * @author European Dynamics SA
 */
public class ConverterUtil {


    /**
     * Converts a CalendarDTO object to a CalCalendar object
     * @param dto The object to convert
     * @return The CalCalendar object
     */
    public static CalCalendar convertToCalendarEntity(CalendarDTO dto) {
        if (dto == null) {
            return null;
        }

        CalCalendar entity = new CalCalendar();

        entity.setId(dto.getId());
        entity.setOwnerId(dto.getOwnerId());
        if (dto.getCreatedOn() != null) {
            entity.setCreatedOn(dto.getCreatedOn().getTime());
        }
        entity.setLastModifiedBy(dto.getLastModifiedBy());
        if (dto.getLastModifiedOn() != null) {
            entity.setLastModifiedOn(dto.getLastModifiedOn().getTime());
        }
        entity.setActive(dto.isActive());

        return entity;
    }


    /**
     * Converts a collection of CalendarDTO objects to a list of CalCalendar objects
     * @param dtos The list to convert
     * @return The CalCalendar object list
     */
    public static List<CalCalendar> convertToCalendarEntityList(Collection<CalendarDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        List<CalCalendar> entities = new ArrayList<CalCalendar>();
        for (CalendarDTO dto : dtos) {
            entities.add(convertToCalendarEntity(dto));
        }
        return entities;
    }


    /**
     * Converts a CalCanedar object to a CalendarDTO object
     * @param entity The object to convert
     * @return The CalendarDTO object
     */
    public static CalendarDTO convertToCalendarDTO(CalCalendar entity) {
        if (entity == null) {
            return null;
        }

        CalendarDTO dto = new CalendarDTO();

        dto.setId(entity.getId());
        dto.setOwnerId(entity.getOwnerId());
        dto.setCreatedOn(new Date(entity.getCreatedOn()));
        dto.setLastModifiedBy(entity.getLastModifiedBy());
        dto.setLastModifiedOn(new Date(entity.getLastModifiedOn()));
        dto.setActive(entity.isActive());

        return dto;
    }


    /**
     * Converts a collection of CalCalendar objects to a list of CalendarDTO objects
     * @param entities The list to convert
     * @return The CalendarDTO object list
     */
    public static List<CalendarDTO> convertToCalendarDTOList(Collection<CalCalendar> entities) {
        if (entities == null) {
            return null;
        }
        List<CalendarDTO> dtos = new ArrayList<CalendarDTO>();
        for (CalCalendar entity : entities) {
            dtos.add(convertToCalendarDTO(entity));
        }
        return dtos;
    }


    /**
     * Converts a CalendarItemDTO object to a CalItem object. Please note that
     * this method does not set the participants, supporting objects and calendar
     * of the resulting CalItem object.
     * @param dto The object to convert
     * @return The CalItem object
     */
    public static CalItem convertToItemEntity(CalendarItemDTO dto) {
        if (dto == null) {
            return null;
        }

        CalItem entity = new CalItem();

        entity.setId(dto.getId());
        if (dto.getAllDay() != null) {
            entity.setAllDay(dto.getAllDay());
        }
        entity.setCategoryId(dto.getCategoryId());
        entity.setContactId(dto.getContactId());
        entity.setCreatedBy(dto.getCreatedBy());
        if (dto.getCreatedOn() != null) {
            entity.setCreatedOn(dto.getCreatedOn().getTime());
        }
        entity.setDescription(dto.getDescription());
        if (dto.getEndTime() != null) {
            entity.setEndTime(dto.getEndTime().getTime());
        }
        entity.setLastModifiedBy(dto.getLastModifiedBy());
        if (dto.getLastModifiedOn() != null) {
            entity.setLastModifiedOn(dto.getLastModifiedOn().getTime());
        }
        entity.setLocation(dto.getLocation());
        entity.setName(dto.getName());
        if (dto.getStartTime() != null) {
            entity.setStartTime(dto.getStartTime().getTime());
        }

        return entity;
    }


    /**
     * Converts a collection of CalendarItemDTO objects to a list of CalItem objects. Please note that
     * this method does not set the participants, supporting objects, calendar and category
     * of the resulting CalItem objects.
     * @param dtos The list to convert
     * @return The CalItem object list
     */
    public static List<CalItem> convertToItemEntityList(Collection<CalendarItemDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        List<CalItem> entities = new ArrayList<CalItem>();
        for (CalendarItemDTO dto : dtos) {
            entities.add(convertToItemEntity(dto));
        }
        return entities;
    }


    /**
     * Converts a CalItem object to a CalendarItemDTO object.
     * @param entity The object to convert
     * @return The CalendarItemDTO object
     */
    public static CalendarItemDTO convertToItemDTO(CalItem entity) {
        if (entity == null) {
            return null;
        }

        CalendarItemDTO dto = new CalendarItemDTO();

        dto.setId(entity.getId());
        dto.setAllDay(entity.isAllDay());
        if (entity.getCalendarId() != null) {
            dto.setCalendarId(entity.getCalendarId().getId());
        }
        dto.setCategoryId(entity.getCategoryId());
        dto.setContactId(entity.getContactId());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedOn(new Date(entity.getCreatedOn()));
        dto.setDescription(entity.getDescription());
        dto.setEndTime(new Date(entity.getEndTime()));
        dto.setLastModifiedBy(entity.getLastModifiedBy());
        dto.setLastModifiedOn(new Date(entity.getLastModifiedOn()));
        dto.setLocation(entity.getLocation());
        dto.setName(entity.getName());
        dto.setObjects(convertToSupportingObjectDTOList(entity.getCalSupportingObjects()));
        dto.setParticipants(convertToParticipantDTOList(entity.getCalParticipants()));
        dto.setStartTime(new Date(entity.getStartTime()));

        return dto;
    }


    /**
     * Converts a collection of CalItem objects to a list of CalendarItemDTO objects
     * @param entities The list to convert
     * @return The CalendarItemDTO object list
     */
    public static List<CalendarItemDTO> convertToItemDTOList(Collection<CalItem> entities) {
        if (entities == null) {
            return null;
        }
        List<CalendarItemDTO> dtos = new ArrayList<CalendarItemDTO>();
        for (CalItem entity : entities) {
            dtos.add(convertToItemDTO(entity));
        }
        return dtos;
    }


    /**
     * Converts a ParticipantDTO object to a CalParticipant object. Please note that
     * this method does not set the calendar item of the resulting object.
     * @param dto The object to convert
     * @return The CalParticipant object
     */
    public static CalParticipant convertToParticipantEntity(ParticipantDTO dto) {
        if (dto == null) {
            return null;
        }

        CalParticipant entity = new CalParticipant();

        entity.setId(dto.getId());
        entity.setParticipantId(dto.getParticipantId());
        entity.setStatus(dto.getStatus());

        return entity;
    }


    /**
     * Converts a CalParticipant object to a ParticipantDTO object.
     * @param entity The object to convert
     * @return The ParticipantDTO object
     */
    public static ParticipantDTO convertToParticipantDTO(CalParticipant entity) {
        if (entity == null) {
            return null;
        }

        ParticipantDTO dto = new ParticipantDTO();

        dto.setId(entity.getId());
        if (entity.getItemId() != null) {
            dto.setItemId(entity.getItemId().getId());
        }
        dto.setParticipantId(entity.getParticipantId());
        dto.setStatus(entity.getStatus());

        return dto;
    }


    /**
     * Converts a collection of CalParticipant objects to a list of ParticipantDTO objects
     * @param entities The list to convert
     * @return The ParticipantDTO object list
     */
    public static List<ParticipantDTO> convertToParticipantDTOList(Collection<CalParticipant> entities) {
        if (entities == null) {
            return null;
        }
        List<ParticipantDTO> dtos = new ArrayList<ParticipantDTO>();
        for (CalParticipant entity : entities) {
            dtos.add(convertToParticipantDTO(entity));
        }
        return dtos;
    }


    /**
     * Converts a SupportingObjectDTO object to a CalSupportingObject object. Please note that
     * this method does not set the calendar item of the resulting object.
     * @param dto The object to convert
     * @return The CalSupportingObject object
     */
    public static CalSupportingObject convertToSupportingObjectEntity(SupportingObjectDTO dto) {
        if (dto == null) {
            return null;
        }

        CalSupportingObject entity = new CalSupportingObject();

        entity.setId(dto.getId());
        entity.setSupportingObjectCategoryId(dto.getCategoryId());
        entity.setFilename(dto.getFilename());
        entity.setMimetype(dto.getMimetype());
        entity.setObjectData(dto.getObjectData());
        entity.setObjectId(dto.getObjectId());
        entity.setLink(dto.getLink());
        if (dto.getCreatedOn() != null) {
            entity.setCreatedOn(dto.getCreatedOn().getTime());
        }
        entity.setCreatedBy(dto.getCreatedBy());
        if (dto.getLastModifiedOn() != null) {
            entity.setLastModifiedOn(dto.getLastModifiedOn().getTime());
        }
        entity.setLastModifiedBy(dto.getLastModifiedBy());

        return entity;
    }


    /**
     * Converts a CalSupportingObject object to a SupportingObjectDTO object.
     * @param entity The object to convert
     * @return The CalSupportingObject object
     */
    public static SupportingObjectDTO convertToSupportingObjectDTO(CalSupportingObject entity) {
        if (entity == null) {
            return null;
        }

        SupportingObjectDTO dto = new SupportingObjectDTO();

        dto.setId(entity.getId());
        dto.setCategoryId(entity.getSupportingObjectCategoryId());
        dto.setFilename(entity.getFilename());
        if (entity.getItemId() != null) {
            dto.setItemId(entity.getItemId().getId());
        }
        dto.setMimetype(entity.getMimetype());
        dto.setObjectData(entity.getObjectData());
        dto.setObjectId(entity.getObjectId());
        dto.setLink(entity.getLink());
        dto.setCreatedOn(new Date(entity.getCreatedOn()));
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setLastModifiedOn(new Date(entity.getLastModifiedOn()));
        dto.setLastModifiedBy(entity.getLastModifiedBy());

        return dto;
    }


    /**
     * Converts a collection of CalSupportingObject objects to a list of SupportingObjectDTO objects
     * @param entities The list to convert
     * @return The SupportingObjectDTO object list
     */
    public static List<SupportingObjectDTO> convertToSupportingObjectDTOList(Collection<CalSupportingObject> entities) {
        if (entities == null) {
            return null;
        }
        List<SupportingObjectDTO> dtos = new ArrayList<SupportingObjectDTO>();
        for (CalSupportingObject entity : entities) {
            dtos.add(convertToSupportingObjectDTO(entity));
        }
        return dtos;
    }

}
