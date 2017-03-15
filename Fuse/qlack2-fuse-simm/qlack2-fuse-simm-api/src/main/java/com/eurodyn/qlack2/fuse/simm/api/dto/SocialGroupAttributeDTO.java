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

import java.io.Serializable;

/**
 * Social group attributes DTO
 *
 * @author European Dynamics
 */
public class SocialGroupAttributeDTO implements Serializable {
	private static final long serialVersionUID = -6039962348527945596L;

	// Id of attribute
	private String id;
	// Name of attribute 
	private String name;
	// Value of attribute
	private String data;
	// Group id which belongs
	private String groupId;
	private byte[] binData;
	// Type of content 
	private String contentType;

	public SocialGroupAttributeDTO() {
	}

	public SocialGroupAttributeDTO(String name, String data) {
		this.name = name;
		this.data = data;
	}

	public SocialGroupAttributeDTO(String columnName, String columnData, String userId) {
		this.name = columnName;
		this.data = columnData;
		this.groupId = userId;
	}

	public SocialGroupAttributeDTO(String columnName, String columnData, byte[] columnBinData, String userID,
			String contentType) {
		this.name = columnName;
		this.binData = columnBinData;
		this.groupId = userID;
		this.data = columnData;
		this.contentType = contentType;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
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
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId
	 *            the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return the binData
	 */
	public byte[] getBinData() {
		return binData;
	}

	/**
	 * @param binData
	 *            the binData to set
	 */
	public void setBinData(byte[] binData) {
		this.binData = binData;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType
	 *            the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
