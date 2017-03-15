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
package com.eurodyn.qlack2.fuse.aaa.api.dto;

import java.io.Serializable;
import java.util.Set;

/**
 * This is transfer object for AaaSession entity.
 * @author European Dynamics SA
 */
public class SessionDTO implements Serializable {
    private static final long serialVersionUID = 4826730405358156679L;

    private String id;
    private String userId;
    private long createdOn;
    private Long terminatedOn;
    private String applicationSessionID;
    private Set<SessionAttributeDTO> attributes;


    public long getCreatedOn() {
		return createdOn;
	}


	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}


	public Long getTerminatedOn() {
		return terminatedOn;
	}


	public void setTerminatedOn(Long terminatedOn) {
		this.terminatedOn = terminatedOn;
	}


	public String getUserId() {
        return userId;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }


    public Set<SessionAttributeDTO> getAttributes() {
        return attributes;
    }


    public void setAttributes(Set<SessionAttributeDTO> attributes) {
        this.attributes = attributes;
    }

    /**
     * @return the applicationSessionID
     */
    public String getApplicationSessionID() {
        return applicationSessionID;
    }

    /**
     * @param applicationSessionID the applicationSessionID to set
     */
    public void setApplicationSessionID(String applicationSessionID) {
        this.applicationSessionID = applicationSessionID;
    }


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}
}