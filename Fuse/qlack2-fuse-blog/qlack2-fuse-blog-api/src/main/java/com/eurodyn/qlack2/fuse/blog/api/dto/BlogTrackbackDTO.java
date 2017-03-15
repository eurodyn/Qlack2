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

/**
 * Data Transfer object for Comment.
 * @author European Dynamics SA.
 */
public class BlogTrackbackDTO extends BlogBaseDTO {

    private static final long serialVersionUID = -2166542984743633862L;
    private String trackbackPostId;
    private String blgCommentId;
    private String postId;
    private String postName;
    private String trackbackPingUrl;

    /**
     * @return the trackbackPostId
     */
    public String getTrackbackPostId() {
        return trackbackPostId;
    }

    /**
     * @param trackbackPostId the trackbackPostId to set
     */
    public void setTrackbackPostId(String trackbackPostId) {
        this.trackbackPostId = trackbackPostId;
    }

    /**
     * @return the blgCommentId
     */
    public String getBlgCommentId() {
        return blgCommentId;
    }

    /**
     * @param blgCommentId the blgCommentId to set
     */
    public void setBlgCommentId(String blgCommentId) {
        this.blgCommentId = blgCommentId;
    }

    /**
     * @return the postId
     */
    public String getPostId() {
        return postId;
    }

    /**
     * @param postId the postId to set
     */
    public void setPostId(String postId) {
        this.postId = postId;
    }

    /**
     * @return the postName
     */
    public String getPostName() {
        return postName;
    }

    /**
     * @param postName the postName to set
     */
    public void setPostName(String postName) {
        this.postName = postName;
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
}