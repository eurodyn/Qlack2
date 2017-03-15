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
package com.eurodyn.qlack2.fuse.wiki.api.dto;

import java.util.Date;
import java.util.Set;

/**
 * DTO for WIKI Entry Object
 * @author European Dynamics SA
 */
public class WikiEntryDTO extends WikiBaseDTO {

    private static final long serialVersionUID = -2676273373277626728L;
    private String id;
    private String wikiId;
    private String pageContent;
    private String namespace;
    private Date dtLastModified;
    private String lastModifiedBy;
    private Date dtCreated;
    private String createdBy;
    private String url;
    private Boolean lock;
    private String lockedBy;
    private String title;
    private Boolean homepage;
    private Set<WikiTagDTO> wikTags;
    private Set<WikiEntryVersionDTO> wikVersions;

    /**
     * @return the id
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the wikiId
     */
    public String getWikiId() {
        return wikiId;
    }

    /**
     * @param wikiId the wikiId to set
     */
    public void setWikiId(String wikiId) {
        this.wikiId = wikiId;
    }

    /**
     * @return the pageContent
     */
    public String getPageContent() {
        return pageContent;
    }

    /**
     * @param pageContent the pageContent to set
     */
    public void setPageContent(String pageContent) {
        this.pageContent = pageContent;
    }

    /**
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @param namespace the namespace to set
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * @return the dtLastModified
     */
    public Date getDtLastModified() {
        return dtLastModified;
    }

    /**
     * @param dtLastModified the dtLastModified to set
     */
    public void setDtLastModified(Date dtLastModified) {
        this.dtLastModified = dtLastModified;
    }

    /**
     * @return the lastModifiedBy
     */
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    /**
     * @param lastModifiedBy the lastModifiedBy to set
     */
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    /**
     * @return the dtCreated
     */
    public Date getDtCreated() {
        return dtCreated;
    }

    /**
     * @param dtCreated the dtCreated to set
     */
    public void setDtCreated(Date dtCreated) {
        this.dtCreated = dtCreated;
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
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the lock
     */
    public Boolean getLock() {
        return lock;
    }

    /**
     * @param lock the lock to set
     */
    public void setLock(Boolean lock) {
        this.lock = lock;
    }

    /**
     * @return the lockedBy
     */
    public String getLockedBy() {
        return lockedBy;
    }

    /**
     * @param lockedBy the lockedBy to set
     */
    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
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
     * @return if the entry is a home page
     */
    public Boolean isHomepage() {
		return homepage;
	}

    /**
     * @param homepage either the entry is a home page or not
     */
	public void setHomepage(Boolean homepage) {
		this.homepage = homepage;
	}

	/**
     * @return the wikTags
     */
    public Set<WikiTagDTO> getWikTags() {
        return wikTags;
    }

    /**
     * @param wikTags the wikTags to set
     */
    public void setWikTags(Set<WikiTagDTO> wikTags) {
        this.wikTags = wikTags;
    }

    /**
     * @return the wikVersions
     */
    public Set<WikiEntryVersionDTO> getWikVersions() {
        return wikVersions;
    }

    /**
     * @param wikVersions the wikVersions to set
     */
    public void setWikVersions(Set<WikiEntryVersionDTO> wikVersions) {
        this.wikVersions = wikVersions;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WikiEntryDTO other = (WikiEntryDTO) obj;
        if ((this.getId() == null) ? (other.getId() != null) : !this.id.equals(other.id)) {
            return false;
        }
        if ((this.getWikiId() == null) ? (other.getWikiId() != null) : !this.wikiId.equals(other.wikiId)) {
            return false;
        }
        if ((this.getPageContent() == null) ? (other.getPageContent() != null) : !this.pageContent.equals(other.pageContent)) {
            return false;
        }
        if ((this.getNamespace() == null) ? (other.getNamespace() != null) : !this.namespace.equals(other.namespace)) {
            return false;
        }
        if (this.getDtLastModified() != other.getDtLastModified() && (this.getDtLastModified() == null || !this.dtLastModified.equals(other.dtLastModified))) {
            return false;
        }
        if ((this.getLastModifiedBy() == null) ? (other.getLastModifiedBy() != null) : !this.lastModifiedBy.equals(other.lastModifiedBy)) {
            return false;
        }
        if (this.getDtCreated() != other.getDtCreated() && (this.getDtCreated() == null || !this.dtCreated.equals(other.dtCreated))) {
            return false;
        }
        if ((this.getCreatedBy() == null) ? (other.getCreatedBy() != null) : !this.createdBy.equals(other.createdBy)) {
            return false;
        }
        if ((this.getUrl() == null) ? (other.getUrl() != null) : !this.url.equals(other.url)) {
            return false;
        }
        if (this.getLock() != other.getLock() && (this.getLock() == null || !this.lock.equals(other.lock))) {
            return false;
        }
        if ((this.getLockedBy() == null) ? (other.getLockedBy() != null) : !this.lockedBy.equals(other.lockedBy)) {
            return false;
        }
        if ((this.getTitle() == null) ? (other.getTitle() != null) : !this.title.equals(other.title)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.getId() != null ? this.getId().hashCode() : 0);
        hash = 97 * hash + (this.getWikiId() != null ? this.getWikiId().hashCode() : 0);
        hash = 97 * hash + (this.getPageContent() != null ? this.getPageContent().hashCode() : 0);
        hash = 97 * hash + (this.getNamespace() != null ? this.getNamespace().hashCode() : 0);
        hash = 97 * hash + (this.getDtLastModified() != null ? this.getDtLastModified().hashCode() : 0);
        hash = 97 * hash + (this.getLastModifiedBy() != null ? this.getLastModifiedBy().hashCode() : 0);
        hash = 97 * hash + (this.getDtCreated() != null ? this.getDtCreated().hashCode() : 0);
        hash = 97 * hash + (this.getCreatedBy() != null ? this.getCreatedBy().hashCode() : 0);
        hash = 97 * hash + (this.getUrl() != null ? this.getUrl().hashCode() : 0);
        hash = 97 * hash + (this.getLock() != null ? this.getLock().hashCode() : 0);
        hash = 97 * hash + (this.getLockedBy() != null ? this.getLockedBy().hashCode() : 0);
        hash = 97 * hash + (this.getTitle() != null ? this.getTitle().hashCode() : 0);
        return hash;
    }
}
