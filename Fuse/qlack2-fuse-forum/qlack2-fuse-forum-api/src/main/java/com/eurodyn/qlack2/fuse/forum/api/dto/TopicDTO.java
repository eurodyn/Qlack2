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
package com.eurodyn.qlack2.fuse.forum.api.dto;

import java.io.Serializable;

/**
 *
 * @author European Dynamics SA
 */
public class TopicDTO implements Serializable {

    private static final long serialVersionUID = 689779288048843148L;
    private String id;
    private String creatorId;
    private String forumId;
    private String title;
    private String description;
    private byte[] logo;
    private long createdOn;
    private short status;
    private Boolean moderated;
    private short moderationStatus;
    private long pendingMessages;
    private long acceptedMessages;
    private long lastMessageDate;
    private String lastMessageAuthorId;
    private boolean archived;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the creatorId
     */
    public String getCreatorId() {
        return creatorId;
    }

    /**
     * @param creatorId the creatorId to set
     */
    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    /**
     * @return the forumId
     */
    public String getForumId() {
        return forumId;
    }

    /**
     * @param forumId the forumId to set
     */
    public void setForumId(String forumId) {
        this.forumId = forumId;
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
     * @return the logo
     */
    public byte[] getLogo() {
        return logo;
    }

    /**
     * @param logo the logo to set
     */
    public void setLogo(byte[] logo) {
        this.logo = logo;
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
    public short getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(short status) {
        this.status = status;
    }

    /**
     * @return the moderated
     */
    public Boolean getModerated() {
        return moderated;
    }

    /**
     * @param moderated the moderated to set
     */
    public void setModerated(Boolean moderated) {
        this.moderated = moderated;
    }

    /**
     * @return the moderationStatus
     */
    public short getModerationStatus() {
        return moderationStatus;
    }

    /**
     * @param moderationStatus the moderationStatus to set
     */
    public void setModerationStatus(short moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    /**
     * @return the pendingMessages
     */
    public long getPendingMessages() {
        return pendingMessages;
    }

    /**
     * @param pendingMessages the pendingMessages to set
     */
    public void setPendingMessages(long pendingMessages) {
        this.pendingMessages = pendingMessages;
    }

    /**
     * @return the acceptedMessages
     */
    public long getAcceptedMessages() {
        return acceptedMessages;
    }

    /**
     * @param acceptedMessages the acceptedMessages to set
     */
    public void setAcceptedMessages(long acceptedMessages) {
        this.acceptedMessages = acceptedMessages;
    }

    /**
     * @return the lastMessageDate
     */
    public long getLastMessageDate() {
        return lastMessageDate;
    }

    /**
     * @param lastMessageDate the lastMessageDate to set
     */
    public void setLastMessageDate(long lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    /**
     * @return the lastMessageAuthorId
     */
    public String getLastMessageAuthorId() {
        return lastMessageAuthorId;
    }

    /**
     * @param lastMessageAuthorId the lastMessageAuthorId to set
     */
    public void setLastMessageAuthorId(String lastMessageAuthorId) {
        this.lastMessageAuthorId = lastMessageAuthorId;
    }


    public boolean isArchived() {
        return archived;
    }


    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
