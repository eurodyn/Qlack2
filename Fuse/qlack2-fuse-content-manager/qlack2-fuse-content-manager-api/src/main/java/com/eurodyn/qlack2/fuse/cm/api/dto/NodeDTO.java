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
package com.eurodyn.qlack2.fuse.cm.api.dto;

import java.util.List;
import java.util.Map;

public class NodeDTO {
	private String id;
	private String name;
	private String parentId;
	
	private boolean lockable;
	private boolean versionable;
	
	private long createdOn;
	private String createdBy;
	private long lastModifiedOn;
	private String lastModifiedBy;	
	private boolean locked;
	private Long lockedOn;
	private String lockedBy;
	private Map<String, String> attributes;
	private List <BreadcrumbPartDTO> path;


	/**
	 * @return the path
	 */
	public List<BreadcrumbPartDTO> getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(List<BreadcrumbPartDTO> path) {
		this.path = path;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	/**
	 * @return the lockable
	 */
	public boolean isLockable() {
		return lockable;
	}

	/**
	 * @param lockable the lockable to set
	 */
	public void setLockable(boolean lockable) {
		this.lockable = lockable;
	}

	/**
	 * @return the versionable
	 */
	public boolean isVersionable() {
		return versionable;
	}

	/**
	 * @param versionable the versionable to set
	 */
	public void setVersionable(boolean versionable) {
		this.versionable = versionable;
	}


	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public long getLastModifiedOn() {
		return lastModifiedOn;
	}

	public void setLastModifiedOn(long lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public Long getLockedOn() {
		return lockedOn;
	}

	public void setLockedOn(Long lockedOn) {
		this.lockedOn = lockedOn;
	}

	public String getLockedBy() {
		return lockedBy;
	}

	public void setLockedBy(String lockedBy) {
		this.lockedBy = lockedBy;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
}
