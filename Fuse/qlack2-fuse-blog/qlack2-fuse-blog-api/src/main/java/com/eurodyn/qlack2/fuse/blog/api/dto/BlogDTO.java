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
 * Data Transfer object for BLOG.
 * @author European Dynamics SA.
 */
public class BlogDTO extends BlogBaseDTO {

    private static final long serialVersionUID = 8445433765070818897L;
    private String name;
    private String userId;
    private byte[] picture;
    private Boolean isRss;
    private String language;
    private String layoutId;
    private List<FlagBlogDTO> flags;
    private List<BlogCategoryDTO> categories;
    private List<BlogTagDTO> tags;

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
     * @return the picturePath
     */
    public byte[] getPicture() {
        return picture;
    }

    /**
     * @param picture
     */
    public void setPicture(byte[] picture) {
        this.picture = picture;
    }


    /**
     *
     * @return isRss
     */
    public Boolean getIsRss() {
        return isRss;
    }

    /**
     *
     * @param isRss rss value
     */
    public void setIsRss(Boolean isRss) {
        this.isRss = isRss;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the flags
     */
    public List<FlagBlogDTO> getFlags() {
        return flags;
    }

    /**
     * @param flags the flags to set
     */
    public void setFlags(List<FlagBlogDTO> flags) {
        this.flags = flags;
    }

    /**
     *
     * @return the layoutId
     */
    public String getLayoutId() {
        return layoutId;
    }

    /**
     *
     * @param layoutId
     */
    public void setLayoutId(String layoutId) {
        this.layoutId = layoutId;
    }

    /**
     * @return the categories
     */
    public List<BlogCategoryDTO> getCategories() {
        return categories;
    }

    /**
     * @param categories the categories to set
     */
    public void setCategories(List<BlogCategoryDTO> categories) {
        this.categories = categories;
    }

    /**
     * @return the tags
     */
    public List<BlogTagDTO> getTags() {
        return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(List<BlogTagDTO> tags) {
        this.tags = tags;
    }
}
