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
public class NotificationDTO extends BaseDTO {
    private static final long serialVersionUID = -7042953165751072960L;

    private String fromUserID;
    private String toUserID;
    private long createdOn;
    private byte status;
    private String title;
    private String description;
    private String link;
    private String type;
    private String customIconURL;
    /* Setting this to true injects an "autoBalloon: true" parameter in the realtime message generated
     * for this notification event. This allows a handler for this event to automatically display a
     * balloon notification (with the text of this notification event). Note that if you want to display
     * a detailed balloon with links etc. you should actually set this to false and write a JS handler
     * for the actual event that generated this notification (e.g. if the action was that a message
     * was accepted on a forum, you should write a JS handler to catch the FORUM_MESSAGE_ACCEPTED event
     * and manually create and display a balloon notification there).
     */
    private boolean showAutoBalloon = false;

    /**
     * @return the fromUserID
     */
    public String getFromUserID() {
        return fromUserID;
    }

    /**
     * @param fromUserID the fromUserID to set
     */
    public void setFromUserID(String fromUserID) {
        this.fromUserID = fromUserID;
    }

    /**
     * @return the toUserID
     */
    public String getToUserID() {
        return toUserID;
    }

    /**
     * @param toUserID the toUserID to set
     */
    public void setToUserID(String toUserID) {
        this.toUserID = toUserID;
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
     * @return the status
     */
    public byte getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(byte status) {
        this.status = status;
    }

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
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the link
     */
    public String getLink() {
        return link;
    }

    /**
     * @param link the link to set
     */
    public void setLink(String link) {
        this.link = link;
    }
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the customIconURL
     */
    public String getCustomIconURL() {
        return customIconURL;
    }

    /**
     * @param customIconURL the customIconURL to set
     */
    public void setCustomIconURL(String customIconURL) {
        this.customIconURL = customIconURL;
    }

    /**
     * @return the showAutoBalloon
     */
    public boolean isShowAutoBalloon() {
        return showAutoBalloon;
    }

    /**
     * @param showAutoBalloon the showAutoBalloon to set
     */
    public void setShowAutoBalloon(boolean showAutoBalloon) {
        this.showAutoBalloon = showAutoBalloon;
    }

}
