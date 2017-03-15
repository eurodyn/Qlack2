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

/**
 * DTo for Wiki Entry version
 * @author European Dynamics SA
 */
public class WikiEntryVersionDTO extends WikiBaseDTO {

    private static final long serialVersionUID = -2386247768775473541L;
    private String wikEntryId;
    private int entryVersion;
    private String createdBy;
    private Date dtCreated;
    private String comment;
    private String pageContent;

    /**
     * @return the wikEntryId
     */
    public String getWikEntryId() {
        return wikEntryId;
    }

    /**
     * @param wikEntryId the wikEntryId to set
     */
    public void setWikEntryId(String wikEntryId) {
        this.wikEntryId = wikEntryId;
    }

    /**
     * @return the entryVersion
     */
    public int getEntryVersion() {
        return entryVersion;
    }

    /**
     * @param entryVersion the entryVersion to set
     */
    public void setEntryVersion(int entryVersion) {
        this.entryVersion = entryVersion;
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
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
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
    
}
