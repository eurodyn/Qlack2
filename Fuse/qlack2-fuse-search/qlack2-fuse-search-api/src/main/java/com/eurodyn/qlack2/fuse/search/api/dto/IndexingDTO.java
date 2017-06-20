package com.eurodyn.qlack2.fuse.search.api.dto;

import java.io.Serializable;

import com.eurodyn.qlack2.fuse.search.api.IndexingService;

/**
 * Holds a document to be indexed. The source object to be indexed is specified
 * on the <i>sourceObject</i> property and can be internally converted to a JSON
 * string by the underling methods of {@link IndexingService} when convertToJSON
 * is true.
 */
public class IndexingDTO extends ESDocumentIdentifierDTO implements Serializable {
	private static final long serialVersionUID = 3713608609686319332L;
	/** The source object to be indexed. */
	private Object sourceObject;

	/** Whether to convert sourceObject to JSON or not */
	private boolean convertToJSON = true;

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
	private boolean refresh;

	public IndexingDTO() {
	}

	public IndexingDTO(String index, String type, String id, Object sourceObject) {
		super();
		this.index = index;
		this.type = type;
		this.id = id;
		this.sourceObject = sourceObject;
	}

	/**
	 * @return the sourceObject
	 */
	public Object getSourceObject() {
		return sourceObject;
	}

	/**
	 * @param sourceObject
	 *            the sourceObject to set
	 */
	public void setSourceObject(Object sourceObject) {
		this.sourceObject = sourceObject;
	}

	public boolean isConvertToJSON() {
		return convertToJSON;
	}

	public void setConvertToJSON(boolean convertToJSON) {
		this.convertToJSON = convertToJSON;
	}

	public boolean isRefresh() {
		return refresh;
	}

	public void setRefresh(boolean refresh) {
		this.refresh = refresh;
	}
}
