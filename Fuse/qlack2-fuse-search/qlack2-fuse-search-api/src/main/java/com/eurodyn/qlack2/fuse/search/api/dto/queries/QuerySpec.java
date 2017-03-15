package com.eurodyn.qlack2.fuse.search.api.dto.queries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The superclass of all different types of queries supported by this module. It
 * provides commonly used properties between all subclasses as well as it allows
 * to tune the number and type of search results.
 */
public abstract class QuerySpec {
	// The list of indices a query is executed against.
	private List<String> indices = new ArrayList<>();
	
	// The list of document types a query is executed against.
	private List<String> types = new ArrayList<>();
	
	// Whether to include the complete query output (JSON string) as it comes
	// from ES - useful for debugging purposes or to extract information not
	// encapsulated in this module's logic.
	private boolean includeAllSource = false;
	
	// Whether to include the actual search results - useful in case you need to
	// execute queries such as "Are there any results matching?" without being
	// interested for the results themselves.
	private boolean includeResults = true;

	// The first record to return from the list of results - useful for paging.
	private int startRecord = 0;
	
	// The size of each page of search results - useful for paging.
	private int pageSize = 100;
	
	// Whether to include ES's explain info.
	// See: https://www.elastic.co/guide/en/elasticsearch/reference/1.7/search-explain.html
	private boolean explain = false;

	/**
	 * Sets the indices against which the query is executed.
	 * @param indexName The names of the indices to add. 
	 * @return
	 */
	public QuerySpec setIndex(String... indexName) {
		indices.addAll(Arrays.asList(indexName));
		return this;
	}

	/**
	 * Sets the document types against which the query is executed.
	 * @param typeName The names of the document types to search.
	 * @return
	 */
	public QuerySpec setType(String... typeName) {
		types.addAll(Arrays.asList(typeName));
		return this;
	}

	/**
	 * Sets the first record from which search results are paginated.
	 * @param startRecord The number of record to start from.
	 * @return
	 */
	public QuerySpec setStartRecord(int startRecord) {
		this.startRecord = startRecord;
		return this;
	}

	/**
	 * Sets the number of search results returned.
	 * @param pageSize The number of results to return.
	 * @return
	 */
	public QuerySpec setPageSize(int pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	/**
	 * Sets whether ES explain info is included in the results.
	 * @param explain Whether to enable or disable the ES explain info.
	 * @return
	 */
	public QuerySpec setExplain(boolean explain) {
		this.explain = explain;
		return this;
	}

	/**
	 * @return the indices
	 */
	public List<String> getIndices() {
		return indices;
	}

	/**
	 * @return the types
	 */
	public List<String> getTypes() {
		return types;
	}

	/**
	 * @return the startRecord
	 */
	public int getStartRecord() {
		return startRecord;
	}

	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @return the explain
	 */
	public boolean isExplain() {
		return explain;
	}

	/**
	 * @return the includeAllSource
	 */
	public boolean isIncludeAllSource() {
		return includeAllSource;
	}

	/**
	 * @return the includeResults
	 */
	public boolean isIncludeResults() {
		return includeResults;
	}

	/**
	 * Convenience method to include the complete query output in the results.
	 * @return
	 */
	public QuerySpec includeAllSources() {
		this.includeAllSource = true;
		return this;
	}

	/**
	 * Convenience method to exclude search hits from the results.
	 * @return
	 */
	public QuerySpec excludeResults() {
		this.includeResults = false;
		return this;
	}

}
