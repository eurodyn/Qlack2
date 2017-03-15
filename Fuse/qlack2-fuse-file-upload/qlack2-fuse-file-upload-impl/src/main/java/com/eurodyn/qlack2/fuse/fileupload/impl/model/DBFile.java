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
package com.eurodyn.qlack2.fuse.fileupload.impl.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "flu_file")
public class DBFile {
	@EmbeddedId
	private DBFilePK id;
	@Column(name = "uploaded_by")
	private String uploadedBy;
	@Column(name = "file_name")
	private String fileName;
	@Column(name = "uploaded_at")
	private long uploadedAt;
	@Column(name = "file_size")
	private long fileSize;
	@Column(name = "expected_chunks")
	private long expectedChunks;
	@Column(name = "chunk_data")
	@Basic(fetch = FetchType.LAZY)
	private byte[] chunkData;
	@Column(name = "chunk_size")
	private long chunkSize;
	
	@Version
	private long dbversion;

	public DBFile() {

	}

	public DBFile(DBFilePK id) {
		super();
		this.id = id;
	}

	public DBFilePK getId() {
		return id;
	}

	public void setId(DBFilePK id) {
		this.id = id;
	}

	public long getExpectedChunks() {
		return expectedChunks;
	}

	public void setExpectedChunks(long expectedChunks) {
		this.expectedChunks = expectedChunks;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getUploadedAt() {
		return uploadedAt;
	}

	public void setUploadedAt(long uploadedAt) {
		this.uploadedAt = uploadedAt;
	}

	public byte[] getChunkData() {
		return chunkData;
	}

	public void setChunkData(byte[] chunkData) {
		this.chunkData = chunkData;
	}

	public long getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(long chunkSize) {
		this.chunkSize = chunkSize;
	}

	public static DBFile getChunk(String id, long chunkOrder, EntityManager em) {
		return em.find(DBFile.class, new DBFilePK(id, chunkOrder));
	}

	public static long delete(String id, EntityManager em) {
		Query q = em.createQuery("delete from DBFile f where f.id.id = :id")
				.setParameter("id", id);

		return q.executeUpdate();
	}

}
