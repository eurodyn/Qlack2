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
package com.eurodyn.qlack2.fuse.simm.api.dto;


/**
 *
 * @author European Dynamics SA
 */
public class SocialGroupUserDTO extends UserDTO {

    // public final static String USER_DETAIL = "userDetail";
    private static final long serialVersionUID = -7021771109917318403L;
    private long joinedOnDate;
    // private List<GroupDTO> groupList;

    public SocialGroupUserDTO(){}
    /**
     *
     * @param userID
     */
    public SocialGroupUserDTO(String userID){
        this.setUserID(userID);
    }

    /**
     * @return groupList List of GroupDTO
     */
//    public List<GroupDTO> getGroupList() {
//        return groupList;
//    }

    /**
     * @param groupList list of GroupDTO
     */
//    public void setGroupList(List<GroupDTO> groupList) {
//        this.groupList = groupList;
//    }

    /**
     * @return the joinedOnDate
     */
    public long getJoinedOnDate() {
        return joinedOnDate;
    }

    /**
     * @param joinedOnDate the joinedOnDate to set
     */
    public void setJoinedOnDate(long joinedOnDate) {
        this.joinedOnDate = joinedOnDate;
    }

}