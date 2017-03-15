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
import java.util.List;

/**
 *
 * @author European Dynamics SA.
 */
public class ForumDTO implements Serializable {
    private static final long serialVersionUID = 3982298747171643941L;

    private String id;
    private String title;
    private String description;
    private byte[] logo;
    private String createdBy;
    private long createdOn;
    private short status;
    private Short moderated;
    private List<TopicDTO> topics;

    private long messagesPending;
    private long messagesAccepted;
    private long messagesRejected;
    private long topicsPending;
    private long topicsAccepted;
    private long topicsRejected;

    private boolean archived;

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
     * @return the createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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
    public Short getModerated() {
        return moderated;
    }

    /**
     * @param moderated the moderated to set
     */
    public void setModerated(Short moderated) {
        this.moderated = moderated;
    }

    /**
     * @return the topics
     */
    public List<TopicDTO> getTopics() {
        return topics;
    }

    /**
     * @param topics the topics to set
     */
    public void setTopics(List<TopicDTO> topics) {
        this.topics = topics;
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


    public boolean isArchived() {
        return archived;
    }


    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    /**
     * @return the messagesPending
     */
    public long getMessagesPending() {
        return messagesPending;
    }

    /**
     * @param messagesPending the messagesPending to set
     */
    public void setMessagesPending(long messagesPending) {
        this.messagesPending = messagesPending;
    }

    /**
     * @return the messagesAccepted
     */
    public long getMessagesAccepted() {
        return messagesAccepted;
    }

    /**
     * @param messagesAccepted the messagesAccepted to set
     */
    public void setMessagesAccepted(long messagesAccepted) {
        this.messagesAccepted = messagesAccepted;
    }

    /**
     * @return the messagesRejected
     */
    public long getMessagesRejected() {
        return messagesRejected;
    }

    /**
     * @param messagesRejected the messagesRejected to set
     */
    public void setMessagesRejected(long messagesRejected) {
        this.messagesRejected = messagesRejected;
    }

    /**
     * @return the topicsPending
     */
    public long getTopicsPending() {
        return topicsPending;
    }

    /**
     * @param topicsPending the topicsPending to set
     */
    public void setTopicsPending(long topicsPending) {
        this.topicsPending = topicsPending;
    }

    /**
     * @return the topicsAccepted
     */
    public long getTopicsAccepted() {
        return topicsAccepted;
    }

    /**
     * @param topicsAccepted the topicsAccepted to set
     */
    public void setTopicsAccepted(long topicsAccepted) {
        this.topicsAccepted = topicsAccepted;
    }

    /**
     * @return the topicsRejected
     */
    public long getTopicsRejected() {
        return topicsRejected;
    }

    /**
     * @param topicsRejected the topicsRejected to set
     */
    public void setTopicsRejected(long topicsRejected) {
        this.topicsRejected = topicsRejected;
    }

}
