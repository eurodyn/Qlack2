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
public class RoomUserDTO extends BaseDTO {
    private static final long serialVersionUID = 7719654461451480515L;

    private String userID;
    private Long joinedRoomOn;


    public RoomUserDTO() {
    }

    public RoomUserDTO(String userID, Long joinedRoomOn) {
        this.userID = userID;
        this.joinedRoomOn = joinedRoomOn;
    }

    /**
     * @return the joinedRoomOn
     */
    public Long getJoinedRoomOn() {
        return joinedRoomOn;
    }

    /**
     * @param joinedRoomOn the joinedRoomOn to set
     */
    public void setJoinedRoomOn(Long joinedRoomOn) {
        this.joinedRoomOn = joinedRoomOn;
    }

    /**
     * @return the userID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * @param userID the userID to set
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }
}
