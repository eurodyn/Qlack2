package com.eurodyn.qlack2.fuse.cm.impl.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.Table;

@Entity
@Table(name = "cm_version_bin")
public class VersionBin {
	@Id
	private String id;

	@javax.persistence.Version
	private long dbversion;

	@ManyToOne
	@JoinColumn(name = "version_id")
	private Version version;

	@Column(name = "chunk_index")
	private int chunkIndex;

	@Column(name = "bin_content")
	private byte[] binContent;

	@Column(name = "chunk_size")
	private int chunkSize;

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
	 * @return the dbversion
	 */
	public long getDbversion() {
		return dbversion;
	}

	/**
	 * @return the chunkIndex
	 */
	public int getChunkIndex() {
		return chunkIndex;
	}

	/**
	 * @param chunkIndex
	 *            the chunkIndex to set
	 */
	public void setChunkIndex(int chunkIndex) {
		this.chunkIndex = chunkIndex;
	}

	/**
	 * @return the binContent
	 */
	public byte[] getBinContent() {
		return binContent;
	}

	/**
	 * @param binContent
	 *            the binContent to set
	 */
	public void setBinContent(byte[] binContent) {
		this.binContent = binContent;
		this.chunkSize = binContent.length;
	}

	/**
	 * @return the version
	 */
	public Version getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(Version version) {
		this.version = version;
	}

	/**
	 * @return the chunkSize
	 */
	public int getChunkSize() {
		return chunkSize;
	}
	
}
