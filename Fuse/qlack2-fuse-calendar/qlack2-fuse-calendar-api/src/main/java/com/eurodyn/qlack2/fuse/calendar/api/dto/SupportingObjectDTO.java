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
package com.eurodyn.qlack2.fuse.calendar.api.dto;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author European Dynamics SA
 */
public class SupportingObjectDTO extends BaseDTO implements Serializable {
    private static final long serialVersionUID = -2114774564414430158L;

    private String categoryId;
    private String itemId;
    private String objectId;
    private String link;
    private String filename;
    private String mimetype;
    private byte[] objectData;
    private Date createdOn;
    private String createdBy;
    private Date lastModifiedOn;
    private String lastModifiedBy;


    public String getCategoryId() {
        return categoryId;
    }


    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }


    public String getFilename() {
        return filename;
    }


    public void setFilename(String filename) {
        this.filename = filename;
    }


    public String getItemId() {
        return itemId;
    }


    public void setItemId(String itemId) {
        this.itemId = itemId;
    }


    public String getMimetype() {
        return mimetype;
    }


    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }


    public byte[] getObjectData() {
        return objectData;
    }


    public void setObjectData(byte[] objectData) {
        this.objectData = objectData;
    }


    public String getObjectId() {
        return objectId;
    }


    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }


    public String getCreatedBy() {
        return createdBy;
    }


    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }


    public Date getCreatedOn() {
        return createdOn;
    }


    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }


    public String getLastModifiedBy() {
        return lastModifiedBy;
    }


    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }


    public Date getLastModifiedOn() {
        return lastModifiedOn;
    }


    public void setLastModifiedOn(Date lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }


    public String getLink() {
        return link;
    }


    public void setLink(String link) {
        this.link = link;
    }
}
