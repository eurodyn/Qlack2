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


/**
 *
 * @author European Dynamics SA
 */
public class MessageDTO extends BaseDTO {
    private static final long serialVersionUID = -3779638136967675611L;
    private String fromID;
    private String toID;
    private String message;
    private Long date;
    private String roomID;

    /**
     * @return the fromID
     */
    public String getFromID() {
        return fromID;
    }

    /**
     * @param fromID the fromID to set
     */
    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the date
     */
    public Long getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Long date) {
        this.date = date;
    }

    /**
     * @return the roomID
     */
    public String getRoomID() {
        return roomID;
    }

    /**
     * @param roomID the roomID to set
     */
    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    /**
     * @return the toID
     */
    public String getToID() {
        return toID;
    }

    /**
     * @param toID the toID to set
     */
    public void setToID(String toID) {
        this.toID = toID;
    }
}
