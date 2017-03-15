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
 *
 * @author European Dynamics SA
 */
public class GroupDTO implements Serializable {
    private static final long serialVersionUID = -5634011610010077294L;
    private String id;
    private String name;
    private String objectID;
    private String description;
    private GroupDTO parent;
    private Set<GroupDTO> children;

	public GroupDTO() {
	}

	public GroupDTO(String id) {
		this.id = id;
	}

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
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
     * @return the objectID
     */
    public String getObjectID() {
        return objectID;
    }

    /**
     * @param objectID the objectID to set
     */
    public void setObjectID(String objectID) {
        this.objectID = objectID;
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

	public GroupDTO getParent() {
		return parent;
	}

	public void setParent(GroupDTO parent) {
		this.parent = parent;
	}

	public Set<GroupDTO> getChildren() {
		return children;
	}

	public void setChildren(Set<GroupDTO> children) {
		this.children = children;
	}
}
