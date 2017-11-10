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

	/**
     * If set to true then wait for the changes made by the request to be made
     * visible by a refresh before replying. This doesn’t force an immediate
     * refresh, rather, it waits for a refresh to happen. Elasticsearch
     * automatically refreshes shards that have changed every
     * index.refresh_interval which defaults to one second. That setting is
     * dynamic. Calling the Refresh API or setting refresh to true on any of the
     * APIs that support it will also cause a refresh, in turn causing already
     * running requests with refresh=wait_for to return.
     */
    protected boolean refresh;

	public ESDocumentIdentifierDTO() {
		super();
	}

	public ESDocumentIdentifierDTO(String index, String type, String id) {
	    this(index, type, id, false);
	}

	public ESDocumentIdentifierDTO(String index, String type, String id, boolean refresh) {
	    super();
	    this.index = index;
        this.type = type;
        this.id = id;
        this.refresh = refresh;
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

	public boolean isRefresh() {
	    return refresh;
	}

	public void setRefresh(boolean refresh) {
        this.refresh = refresh;
	}
}