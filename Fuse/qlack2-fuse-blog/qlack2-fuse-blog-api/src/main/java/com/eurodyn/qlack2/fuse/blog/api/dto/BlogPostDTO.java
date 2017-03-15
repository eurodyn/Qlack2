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
import java.util.List;

/**
 * Data Transfer object for POST.
 * @author European Dynamics SA.
 */
public class BlogPostDTO extends BlogBaseDTO {

    private static final long serialVersionUID = -6420161689851873661L;
    private String blogId;
    private String name;
    private String body;
    private boolean commentsEnabled;
    private Date dtposted;
    private boolean archived;
    private boolean published;
    private List<BlogCommentDTO> blgComments;
    private List<String> blgCategories;
    private List<String> blgTags;
    private String trackbackPingUrl;
    private List<String> trackBackPostIds;
    private String excerpt;
    private String userId; // for trak-backing.
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }



    /**
     * @return the dtposted
     */
    public Date getDtposted() {
        return dtposted;
    }

    /**
     * @param dtposted the dtposted to set
     */
    public void setDtposted(Date dtposted) {
        this.dtposted = dtposted;
    }






    /**
     * @return the blgComments
     */
    public List<BlogCommentDTO> getBlgComments() {
        return blgComments;
    }

    /**
     * @param blgComments the blgComments to set
     */
    public void setBlgComments(List<BlogCommentDTO> blgComments) {
        this.blgComments = blgComments;
    }

    /**
     * @return the blgCategories
     */
    public List<String> getBlgCategories() {
        return blgCategories;
    }

    /**
     * @param blgCategories the blgCategories to set
     */
    public void setBlgCategories(List<String> blgCategories) {
        this.blgCategories = blgCategories;
    }

    /**
     * @return the blgTags
     */
    public List<String> getBlgTags() {
        return blgTags;
    }

    /**
     * @param blgTags the blgTags to set
     */
    public void setBlgTags(List<String> blgTags) {
        this.blgTags = blgTags;
    }

    /**
     * @return the trackBackPostIds
     */
    public List<String> getTrackBackPostIds() {
        return trackBackPostIds;
    }

    /**
     * @param trackBackPostIds the trackBackPostIds to set
     */
    public void setTrackBackPostIds(List<String> trackBackPostIds) {
        this.trackBackPostIds = trackBackPostIds;
    }

    /**
     * @return the excerpt
     */
    public String getExcerpt() {
        return excerpt;
    }

    /**
     * @param excerpt the excerpt to set
     */
    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
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
     * @return the trackbackPingUrl
     */
    public String getTrackbackPingUrl() {
        return trackbackPingUrl;
    }

    /**
     * @param trackbackPingUrl the trackbackPingUrl to set
     */
    public void setTrackbackPingUrl(String trackbackPingUrl) {
        this.trackbackPingUrl = trackbackPingUrl;
    }

    /**
     * @return the commentsEnabled
     */
    public boolean isCommentsEnabled() {
        return commentsEnabled;
    }

    /**
     * @param commentsEnabled the commentsEnabled to set
     */
    public void setCommentsEnabled(boolean commentsEnabled) {
        this.commentsEnabled = commentsEnabled;
    }

    /**
     * @return the archived
     */
    public boolean isArchived() {
        return archived;
    }

    /**
     * @param archived the archived to set
     */
    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    /**
     * @return the published
     */
    public boolean isPublished() {
        return published;
    }

    /**
     * @param published the published to set
     */
    public void setPublished(boolean published) {
        this.published = published;
    }
}