package com.eurodyn.qlack2.fuse.fileupload.api.dto;

public class DBFileChunkDTO {
	private String id;
	private byte[] binContent;
	private boolean hasMoreChunks = false;
	private long chunkIndex = 0;
	private String uploadedBy;
	private String filename;
	private long uploadedAt;
	private long totalChunks;
	private long totalSize;
	
	
	/**
	 * @return the uploadedBy
	 */
	public String getUploadedBy() {
		return uploadedBy;
	}

	/**
	 * @param uploadedBy the uploadedBy to set
	 */
	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @return the uploadedAt
	 */
	public long getUploadedAt() {
		return uploadedAt;
	}

	/**
	 * @param uploadedAt the uploadedAt to set
	 */
	public void setUploadedAt(long uploadedAt) {
		this.uploadedAt = uploadedAt;
	}


	/**
	 * @return the totalChunks
	 */
	public long getTotalChunks() {
		return totalChunks;
	}

	/**
	 * @param totalChunks the totalChunks to set
	 */
	public void setTotalChunks(long totalChunks) {
		this.totalChunks = totalChunks;
	}

	/**
	 * @return the totalSize
	 */
	public long getTotalSize() {
		return totalSize;
	}

	/**
	 * @param totalSize the totalSize to set
	 */
	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	/**
	 * @return the chunkIndex
	 */
	public long getChunkIndex() {
		return chunkIndex;
	}

	/**
	 * @param chunkIndex the chunkIndex to set
	 */
	public void setChunkIndex(long chunkIndex) {
		this.chunkIndex = chunkIndex;
	}

	/**
	 * @return the hasMoreChunks
	 */
	public boolean isHasMoreChunks() {
		return hasMoreChunks;
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
	 * @return the hasNextChunk
	 */
	public boolean hasMoreChunks() {
		return hasMoreChunks;
	}

	/**
	 * @param hasMoreChunks the hasMoreChunks to set
	 */
	public void setHasMoreChunks(boolean hasMoreChunks) {
		this.hasMoreChunks = hasMoreChunks;
	}

	/**
	 * @return the binContent
	 */
	public byte[] getBinContent() {
		return binContent;
	}

	/**
	 * @param binContent the binContent to set
	 */
	public void setBinContent(byte[] binContent) {
		this.binContent = binContent;
	}

	
}