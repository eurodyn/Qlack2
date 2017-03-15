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
package com.eurodyn.qlack2.fuse.blog.api.dto;

import java.util.Date;

/**
 * Data Transfer object for flagging a BLOG.
 * @author European Dynamics SA.
 */
public class FlagBlogDTO extends BlogBaseDTO {

    private static final long serialVersionUID = -3531373335261274815L;
    private String blogId;
    private String userId;
    private String flagDescription;
    private String flagName;
    private Date dateFlagged;

    /**
     * @return the blogId
     */
    public String getBlogId() {
        return blogId;
    }

    /**
     * @param blogId the blogId to set
     */
    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     *
     * @return Date the date flagged
     */
    public Date getDateFlagged() {
        return dateFlagged;
    }

    /**
     * Set the date flagged
     * @param dateFlagged
     */
    public void setDateFlagged(Long dateFlagged) {
        Date date = new Date(dateFlagged);
        this.dateFlagged = date;
    }

    /**
     * get the description of flag
     * @return String the description of flag
     */
    public String getFlagDescription() {
        return flagDescription;
    }

    /**
     * set the description of flag
     * @param flagDescription the description of flag
     */
    public void setFlagDescription(String flagDescription) {
        this.flagDescription = flagDescription;
    }

    /**
     * get the title of flag
     * @return String  the title of flag
     */
    public String getFlagName() {
        return flagName;
    }

    /**
     * Set the title of flag
     * @param flagName the title of flag
     */
    public void setFlagName(String flagName) {
        this.flagName = flagName;
    }
}
