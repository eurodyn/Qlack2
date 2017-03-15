package com.eurodyn.qlack2.fuse.search.api.dto;

import com.eurodyn.qlack2.fuse.search.api.IndexingService;

import java.io.Serializable;

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
}
