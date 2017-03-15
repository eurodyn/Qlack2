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
public class PostItemDTO extends BaseDTO {
    private static final long serialVersionUID = 177987937702294642L;
    private String homepageID;
    private Long createdOn;
    private String createdByUserID;
    private String createdByUserFullname;
    private byte status;
    private String parentHomepageID;
    private String title;
    private String description;
    private String link;
    private byte[][] binContent;
    private String categoryID;
    private PostItemDTO[] children;
    private String categoryIcon;

    /**
     * @return the homepageID
     */
    public String getHomepageID() {
        return homepageID;
    }

    /**
     * @param homepageID the homepageID to set
     */
    public void setHomepageID(String homepageID) {
        this.homepageID = homepageID;
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
     * @return the parentHomepageID
     */
    public String getParentHomepageID() {
        return parentHomepageID;
    }

    /**
     * @param parentHomepageID the parentHomepageID to set
     */
    public void setParentHomepageID(String parentHomepageID) {
        this.parentHomepageID = parentHomepageID;
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
     * @return the binContent
     */
    public byte[][] getBinContents() {
        return binContent;
    }

    /**
     * @param binContent the binContent to set
     */
    public void setBinContents(byte[][] binContent) {
        this.binContent = binContent;
    }

    /**
     * @return the category
     */
    public String getCategoryID() {
        return categoryID;
    }

    /**
     * @param categoryID the category to set
     */
    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    /**
     * @return the createdByUserFullname
     */
    public String getCreatedByUserFullname() {
        return createdByUserFullname;
    }

    /**
     * @param createdByUserFullname the createdByUserFullname to set
     */
    public void setCreatedByUserFullname(String createdByUserFullname) {
        this.createdByUserFullname = createdByUserFullname;
    }

    /**
     * @return the children
     */
    public PostItemDTO[] getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(PostItemDTO[] children) {
        this.children = children;
    }

    /**
     * @return the categoryIcon
     */
    public String getCategoryIcon() {
        return categoryIcon;
    }

    /**
     * @param categoryIcon the categoryIcon to set
     */
    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }
}
