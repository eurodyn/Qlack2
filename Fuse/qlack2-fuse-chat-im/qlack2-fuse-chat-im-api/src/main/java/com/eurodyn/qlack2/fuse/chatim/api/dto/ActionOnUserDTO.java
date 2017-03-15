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


public class ActionOnUserDTO extends BaseDTO {

    private static final long serialVersionUID = 6894162657070927503L;
    private String userId;
    private String actionName;
    private String actionDescription;
    private String roomId;
    private String reason;
    private String actionId;
    private long createdOn;
    private long actionPeriod;

    /**
     * @return the actionOnUserId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param actionOnUserId the actionOnUserId to set
     */
    public void setUserId(String actionOnUserId) {
        this.userId = actionOnUserId;
    }

    /**
     * @return the actionName
     */
    public String getActionName() {
        return actionName;
    }

    /**
     * @param actionName the actionName to set
     */
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    /**
     * @return the actionDescription
     */
    public String getActionDescription() {
        return actionDescription;
    }

    /**
     * @param actionDescription the actionDescription to set
     */
    public void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
    }

    /**
     * @return the roomId
     */
    public String getRoomId() {
        return roomId;
    }

    /**
     * @param roomId the roomId to set
     */
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    /**
     * @return the action
     */
    public String getActionId() {
        return actionId;
    }

    /**
     * @param action the action to set
     */
    public void setActionId(String action) {
        this.actionId = actionId;
    }

    /**
     * @return the createdOn
     */
    public long getCreatedOn() {
        return createdOn;
    }

    /**
     * @param createdOn the createdOn to set
     */
    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }

    /**
     * @return the actionPeriod
     */
    public long getActionPeriod() {
        return actionPeriod;
    }

    /**
     * @param actionPeriod the actionPeriod to set
     */
    public void setActionPeriod(long actionPeriod) {
        this.actionPeriod = actionPeriod;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }
}


