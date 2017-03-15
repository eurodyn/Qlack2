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
public class RoomDTO extends BaseDTO {
    private static final long serialVersionUID = -8477931974367308197L;

    private String title;
    private Long createdOn;
    private String createdByUserID;
    private String targetCommunityID;

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the createdOn
     */
    public Long getCreatedOn() {
        return createdOn;
    }

    /**
     * @param createdOn the createdOn to set
     */
    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }

    /**
     * @return the createdByUserID
     */
    public String getCreatedByUserID() {
        return createdByUserID;
    }

    /**
     * @param createdByUserID the createdByUserID to set
     */
    public void setCreatedByUserID(String createdByUserID) {
        this.createdByUserID = createdByUserID;
    }

    /**
     * @return the targetCommunityID
     */
    public String getTargetCommunityID() {
        return targetCommunityID;
    }

    /**
     * @param targetCommunityID the targetCommunityID to set
     */
    public void setTargetCommunityID(String targetCommunityID) {
        this.targetCommunityID = targetCommunityID;
    }
}
