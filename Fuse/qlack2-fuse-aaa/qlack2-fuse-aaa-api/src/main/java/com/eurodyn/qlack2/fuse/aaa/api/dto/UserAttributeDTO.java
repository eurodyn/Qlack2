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

/**
 * User attributes DTO
 *
 * @author European Dynamics
 */
public class UserAttributeDTO implements Serializable {
	private static final long serialVersionUID = -6811545393980630525L;

	private String id;
	private String name;
	private String data;
	private String userId;
	private byte[] binData;
	private String contentType;

	public UserAttributeDTO() {
	}

	public UserAttributeDTO(String name, String data) {
		this.name = name;
		this.data = data;
	}

	public UserAttributeDTO(String columnName, String columnData, String userId) {
		this.name = columnName;
		this.data = columnData;
		this.userId = userId;
	}

	public UserAttributeDTO(String columnName, String columnData,
			byte[] columnBinData, String userID, String contentType) {
		this.name = columnName;
		this.binData = columnBinData;
		this.userId = userID;
		this.data = columnData;
		this.contentType = contentType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public byte[] getBinData() {
		return binData;
	}

	public void setBinData(byte[] binData) {
		this.binData = binData;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
