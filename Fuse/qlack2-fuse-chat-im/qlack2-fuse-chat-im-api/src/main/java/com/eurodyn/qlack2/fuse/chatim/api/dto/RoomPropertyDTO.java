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
public class RoomPropertyDTO extends BaseDTO {

    private static final long serialVersionUID = 4235771727150771632L;
    private String roomId;
    private String propertyName;
    private String propertyValue;

    /**
     * Default Constructor
     */
    public RoomPropertyDTO() {
    }

    public RoomPropertyDTO(String roomId, String propertyName, String propertyValue) {
        this.roomId = roomId;
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }
}
