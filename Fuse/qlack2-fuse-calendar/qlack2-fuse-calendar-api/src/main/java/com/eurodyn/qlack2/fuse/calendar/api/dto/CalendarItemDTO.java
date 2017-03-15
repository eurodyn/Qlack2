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
package com.eurodyn.qlack2.fuse.calendar.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author European Dynamics SA
 */
public class CalendarItemDTO extends BaseDTO implements Serializable {
    private static final long serialVersionUID = -3422313318640682277L;

    private String categoryId;
    private String calendarId;
    private String name;
    private String description;
    private String location;
    private String contactId;
    private Date startTime;
    private Date endTime;
    private Boolean allDay;
    private Date createdOn;
    private String createdBy;
    private Date lastModifiedOn;
    private String lastModifiedBy;
    private List<ParticipantDTO> participants = new ArrayList<ParticipantDTO>();
    private List<SupportingObjectDTO> objects = new ArrayList<SupportingObjectDTO>();


    public Boolean getAllDay() {
        return allDay;
    }


    public void setAllDay(Boolean allDay) {
        this.allDay = allDay;
    }


    public String getCalendarId() {
        return calendarId;
    }


    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }


    public String getCategoryId() {
        return categoryId;
    }


    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }


    public String getContactId() {
        return contactId;
    }


    public void setContactId(String contactId) {
        this.contactId = contactId;
    }


    public String getCreatedBy() {
        return createdBy;
    }


    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }


    public Date getCreatedOn() {
        return createdOn;
    }


    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public Date getEndTime() {
        return endTime;
    }


    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }


    public String getLastModifiedBy() {
        return lastModifiedBy;
    }


    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }


    public Date getLastModifiedOn() {
        return lastModifiedOn;
    }


    public void setLastModifiedOn(Date lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }


    public String getLocation() {
        return location;
    }


    public void setLocation(String location) {
        this.location = location;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public List<SupportingObjectDTO> getObjects() {
        return objects;
    }


    public void setObjects(List<SupportingObjectDTO> objects) {
        this.objects = objects;
    }


    public List<ParticipantDTO> getParticipants() {
        return participants;
    }


    public void setParticipants(List<ParticipantDTO> participants) {
        this.participants = participants;
    }


    public Date getStartTime() {
        return startTime;
    }


    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
}
