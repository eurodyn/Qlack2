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
package com.eurodyn.qlack2.fuse.chatim.api.dto;

import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

/**
 * Room Statistics DTO
 * @author European Dynamics SA
 */
public class RoomStatisticsDTO extends BaseDTO {

    private static final long serialVersionUID = 8984631434200244950L;

    private int numberOfUsers;
    private int numberOfEntries;
    private String title;
    private String createdOn;
    private String createdBy;

     @XmlElement
    public int getNumberOfEntries() {
        return numberOfEntries;
    }

    public void setNumberOfEntries(int numberOfEntries) {
        this.numberOfEntries = numberOfEntries;
    }

     @XmlElement
    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    public void setNumberOfUsers(int numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }

    @XmlElement
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlElement
    public String getCreatedOn() {
        return new Date(new Long(createdOn).longValue()).toString();
    }

    public void setCreatedOn(String createdOnTemp) {
        this.createdOn = createdOnTemp;
    }

    @XmlElement
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        String newLine = "<br>";
        StringBuilder builder = new StringBuilder();
        builder.append(newLine);
        builder.append("---------------------------------------------------");
        builder.append(newLine);
        builder.append("Room Name:").append(title).append(newLine);
        builder.append("Room Owner:").append(createdBy).append(newLine);
        builder.append("Room Created on:").append(createdOn).append(newLine);
        builder.append("Number of users:").append(numberOfUsers).append(newLine);
        builder.append("Number of messages:").append(numberOfEntries).append(newLine);
        builder.append("---------------------------------------------------");
        return builder.toString();
    }
}
