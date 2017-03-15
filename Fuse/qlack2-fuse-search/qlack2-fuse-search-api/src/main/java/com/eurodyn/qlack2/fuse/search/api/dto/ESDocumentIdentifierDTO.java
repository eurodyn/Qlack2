package com.eurodyn.qlack2.fuse.search.api.dto;

import java.io.Serializable;

/**
 * Holds the minimum necessary information to uniquely identify a document in
 * ES.
 */
public class ESDocumentIdentifierDTO implements Serializable {
	private static final long serialVersionUID = 3216613727616909251L;
	// The index at which this document resides.
	protected String index;

	// The type of this document.
	protected String type;

	// The unique ID of this document.
	protected String id;

	public ESDocumentIdentifierDTO() {
		super();
	}

	public ESDocumentIdentifierDTO(String index, String type, String id) {
		super();
		this.index = index;
		this.type = type;
		this.id = id;
	}

	/**
	 * @return the index
	 */
	public String getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(String index) {
		this.index = index;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
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

}