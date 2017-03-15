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
package com.eurodyn.qlack2.fuse.clipboard.api.dto;

import java.util.List;

/**
 *
 * @author European Dynamics SA.
 */
public class ClipboardEntryDTO extends BaseDTO {
    private static final long serialVersionUID = -4494919946177999086L;

    private String ownerId;
    private String objectId;
    private String title;
    private String description;
    private String typeId;
    private long createdOn;
    private List<ClipboardMetaDTO> metadata;


    public long getCreatedOn() {
        return createdOn;
    }


    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public List<ClipboardMetaDTO> getMetadata() {
        return metadata;
    }


    public void setMetadata(List<ClipboardMetaDTO> metadata) {
        this.metadata = metadata;
    }


    public String getObjectId() {
        return objectId;
    }


    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }


    public String getOwnerId() {
        return ownerId;
    }


    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getTypeId() {
        return typeId;
    }


    public void setTypeId(String type) {
        this.typeId = type;
    }
}
