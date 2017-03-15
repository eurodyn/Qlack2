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

import java.util.List;

/**
 * Dashboard data transfer object.
 * @author European Dynamics SA
 */
public class BlogDashboardDTO extends BlogBaseDTO {

    private static final long serialVersionUID = -1282810146180639470L;

    private int allPosts;
    private int archivedPosts;
    private int publishedPosts;
    private int unpublishedPosts;
    private List<BlogCommentDTO> recentComments;
    private List<BlogPostDTO> mostCommentedPosts;

    //will be added in next release
    //private List<BlogPostDTO> mostCommentedFrndPosts;
    /**
     * @return the allPosts
     */
    public int getAllPosts() {
        return allPosts;
    }

    /**
     * @param allPosts the allPosts to set
     */
    public void setAllPosts(int allPosts) {
        this.allPosts = allPosts;
    }

    /**
     * @return the archivedPosts
     */
    public int getArchivedPosts() {
        return archivedPosts;
    }

    /**
     * @param archivedPosts the archivedPosts to set
     */
    public void setArchivedPosts(int archivedPosts) {
        this.archivedPosts = archivedPosts;
    }

    /**
     * @return the publishedPosts
     */
    public int getPublishedPosts() {
        return publishedPosts;
    }

    /**
     * @param publishedPosts the publishedPosts to set
     */
    public void setPublishedPosts(int publishedPosts) {
        this.publishedPosts = publishedPosts;
    }

    /**
     * @return the unpublishedPosts
     */
    public int getUnpublishedPosts() {
        return unpublishedPosts;
    }

    /**
     * @param unpublishedPosts the unpublishedPosts to set
     */
    public void setUnpublishedPosts(int unpublishedPosts) {
        this.unpublishedPosts = unpublishedPosts;
    }

    /**
     * @return the recentComments
     */
    public List<BlogCommentDTO> getRecentComments() {
        return recentComments;
    }

    /**
     * @param recentComments the recentComments to set
     */
    public void setRecentComments(List<BlogCommentDTO> recentComments) {
        this.recentComments = recentComments;
    }

    /**
     * @return the mostCommentedPosts
     */
    public List<BlogPostDTO> getMostCommentedPosts() {
        return mostCommentedPosts;
    }

    /**
     * @param mostCommentedPosts the mostCommentedPosts to set
     */
    public void setMostCommentedPosts(List<BlogPostDTO> mostCommentedPosts) {
        this.mostCommentedPosts = mostCommentedPosts;
    }
}
