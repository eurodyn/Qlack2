/*
 * Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
 *
 * Licensed under the EUPL, Version 1.1 only (the "License"). You may not use this work except in
 * compliance with the Licence. You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence
 * is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the Licence for the specific language governing permissions and limitations under
 * the Licence.
 */
package com.eurodyn.qlack2.fuse.cm.api.dto;

import java.util.Map;

/**
 * The Class VersionDTO.
 */
public class VersionDTO {

  /** The id. */
  private String id;

  /** The name. */
  private String name;

  /** The active. */
  private boolean active;

  /** The created on. */
  private long createdOn;

  /** The created by. */
  private String createdBy;

  /** The last modified on. */
  private long lastModifiedOn;

  /** The last modified by. */
  private String lastModifiedBy;

  /** The attributes. */
  private Map<String, String> attributes;

  /** The mimetype. */
  private String mimetype;

  /** The content size. */
  private Long contentSize;

  /** The filename. */
  private String filename;

  /** The node id. */
  private String nodeId;



  /**
   * Gets the id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Checks if is active.
   *
   * @return true, if is active
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Sets the active.
   *
   * @param active the new active
   */
  public void setActive(boolean active) {
    this.active = active;
  }

  /**
   * Gets the created on.
   *
   * @return the created on
   */
  public long getCreatedOn() {
    return createdOn;
  }

  /**
   * Sets the created on.
   *
   * @param createdOn the new created on
   */
  public void setCreatedOn(long createdOn) {
    this.createdOn = createdOn;
  }

  /**
   * Gets the created by.
   *
   * @return the created by
   */
  public String getCreatedBy() {
    return createdBy;
  }

  /**
   * Sets the created by.
   *
   * @param createdBy the new created by
   */
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  /**
   * Gets the last modified on.
   *
   * @return the last modified on
   */
  public long getLastModifiedOn() {
    return lastModifiedOn;
  }

  /**
   * Sets the last modified on.
   *
   * @param lastModifiedOn the new last modified on
   */
  public void setLastModifiedOn(long lastModifiedOn) {
    this.lastModifiedOn = lastModifiedOn;
  }

  /**
   * Gets the last modified by.
   *
   * @return the last modified by
   */
  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  /**
   * Sets the last modified by.
   *
   * @param lastModifiedBy the new last modified by
   */
  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  /**
   * Gets the attributes.
   *
   * @return the attributes
   */
  public Map<String, String> getAttributes() {
    return attributes;
  }

  /**
   * Sets the attributes.
   *
   * @param attributes the attributes
   */
  public void setAttributes(Map<String, String> attributes) {
    this.attributes = attributes;
  }

  /**
   * Gets the mimetype.
   *
   * @return the mimetype
   */
  public String getMimetype() {
    return mimetype;
  }

  /**
   * Sets the mimetype.
   *
   * @param mimetype the mimetype to set
   */
  public void setMimetype(String mimetype) {
    this.mimetype = mimetype;
  }

  /**
   * Gets the content size.
   *
   * @return the content size
   */
  public Long getContentSize() {
	  return contentSize;
  }

  /**
   * Sets the content size.
   *
   * @param contentSize the new content size
   */
  public void setContentSize(Long contentSize) {
	  this.contentSize = contentSize;
  }

/**
   * Gets the filename.
   *
   * @return the filename
   */
  public String getFilename() {
    return filename;
  }

  /**
   * Sets the filename.
   *
   * @param filename the new filename
   */
  public void setFilename(String filename) {
    this.filename = filename;
  }


  /**
   * Gets the node id.
   *
   * @return the node id
   */
  public String getNodeId() {
    return nodeId;
  }

  /**
   * Sets the node id.
   *
   * @param nodeId the new node id
   */
  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }
}
