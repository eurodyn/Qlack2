/*
* Copyright 2016 EUROPEAN DYNAMICS SA <info@eurodyn.com>
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

public class BinChunkDTO {
	private String id;
	private String versionID;
	private byte[] binContent;
	private boolean hasMoreChunks = false;
	/**
	 * @return the hasMoreChunks
	 */
	public boolean isHasMoreChunks() {
		return hasMoreChunks;
	}

	/**
	 * @param hasMoreChunks the hasMoreChunks to set
	 */
	public void setHasMoreChunks(boolean hasMoreChunks) {
		this.hasMoreChunks = hasMoreChunks;
	}

	private int chunkIndex = 0;

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
	 * @return the versionID
	 */
	public String getVersionID() {
		return versionID;
	}

	/**
	 * @param versionID
	 *            the versionID to set
	 */
	public void setVersionID(String versionID) {
		this.versionID = versionID;
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

	/**
	 * @return the chunkIndex
	 */
	public int getChunkIndex() {
		return chunkIndex;
	}

	/**
	 * @param chunkIndex the chunkIndex to set
	 */
	public void setChunkIndex(int chunkIndex) {
		this.chunkIndex = chunkIndex;
	}
	
}

