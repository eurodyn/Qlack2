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
package com.eurodyn.qlack2.fuse.mailing.api.dto;

import java.io.Serializable;
import java.util.Arrays;

/**
 * DTO for Attachment.
 *
 * @author European Dynamics SA.
 */
public class AttachmentDTO implements Serializable {
	private static final long serialVersionUID = 5835825483419031360L;

	private String id;
	private String filename;
	private String contentType;
	private byte[] data;

	/**
	 * Default Constructor
	 */
	public AttachmentDTO() {
	}

	/**
	 * Parameterized Constructor
	 *
	 * @param id
	 *            The ID of the attachment
	 * @param filename
	 *            The file name to be attached
	 * @param contentType
	 *            The content type for attachment
	 */
	public AttachmentDTO(String id, String filename, String contentType) {
		this.id = id;
		this.filename = filename;
		this.contentType = contentType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("Attchment id is :" + getId())
				.append("file name is:" + getFilename())
				.append("content type is :" + getContentType());
		return strBuf.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AttachmentDTO other = (AttachmentDTO) obj;
		if ((this.getId() == null) ? (other.getId() != null) : !this.id.equals(other.id)) {
			return false;
		}
		if ((this.getFilename() == null) ? (other.getFilename() != null) : !this.filename.equals(other.filename)) {
			return false;
		}
		if ((this.getContentType() == null) ? (other.getContentType() != null) : !this.contentType.equals(other.contentType)) {
			return false;
		}
		if (!Arrays.equals(this.data, other.data)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 13 * hash + (this.getId() != null ? this.getId().hashCode() : 0);
		hash = 13 * hash + (this.getFilename() != null ? this.getFilename().hashCode() : 0);
		hash = 13 * hash + (this.getContentType() != null ? this.getContentType().hashCode() : 0);
		hash = 13 * hash + Arrays.hashCode(this.getData());
		return hash;
	}
}
