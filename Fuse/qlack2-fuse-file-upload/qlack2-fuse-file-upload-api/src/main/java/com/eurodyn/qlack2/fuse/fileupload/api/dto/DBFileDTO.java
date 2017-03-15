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
package com.eurodyn.qlack2.fuse.fileupload.api.dto;

public class DBFileDTO {
	private String id;
	private String uploadedBy;
	private String filename;
	private long uploadedAt;
	private byte[] fileData;
	private long totalChunks;
	private long receivedChunks;
	private long reassemblyTime;
	private long totalSize;

	public long getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}
	public long getReassemblyTime() {
		return reassemblyTime;
	}
	public void setReassemblyTime(long reassemblyTime) {
		this.reassemblyTime = reassemblyTime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUploadedBy() {
		return uploadedBy;
	}
	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public long getUploadedAt() {
		return uploadedAt;
	}
	public void setUploadedAt(long uploadedAt) {
		this.uploadedAt = uploadedAt;
	}
	public byte[] getFileData() {
		return fileData;
	}
	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}
	public long getTotalChunks() {
		return totalChunks;
	}
	public void setTotalChunks(long totalChunks) {
		this.totalChunks = totalChunks;
	}
	public long getReceivedChunks() {
		return receivedChunks;
	}
	public void setReceivedChunks(long receivedChunks) {
		this.receivedChunks = receivedChunks;
	}



}
