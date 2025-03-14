package com.eurodyn.qlack2.fuse.search.api.dto.queries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // If set to true then a _count request is sent instead of a _search which only returns the count
    // of the query results. In this case aggregate, includeResults, includeAllSource, explain, startRecord,
    // pageSize, scroll, includes, excludes, highlight and querySort are ignored.
	private boolean countOnly = false;

    // If not null then a scroll request is generated. In this case startRecord is ignored. This
    // number indicates the number of minutes for which the scroll context remains active.
	private Integer scroll;

    // By giving a value to this field an aggregate query will be created. This field should contain
    // the name of a field of the searched document.
    // Only the values of this field are going to be returned (includes and excludes are ignored). Also the response will contain a set of
    // results contains distinct values for this field.
	// See https://www.elastic.co/guide/en/elasticsearch/reference/5.5/search-aggregations-bucket-terms-aggregation.html
	private String aggregate;
	// Only relevant if aggregate is given. In this case this sets the maximum result of the aggregation.
	private int aggregateSize = 100;

	// The next two lists are used to control which fields are to be given in the response.
	// See https://www.elastic.co/guide/en/elasticsearch/reference/5.5/search-request-source-filtering.html
	private final List<String> includes = new ArrayList<>();
	private final List<String> excludes = new ArrayList<>();

    // boost this query to influence ES score. At the moments works only for QueryStringSpecField and
    // QueryTerm
    private int boost = 1;

	protected QuerySort querySort;
	private QueryHighlight highlight;

	// A map for additional parameters in the url
	private final Map<String, String> params = new HashMap<>();

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

	public QuerySpec setQuerySort(QuerySort querySort) {
	  this.querySort = querySort;
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

  public QuerySort getQuerySort() {
	  return querySort;
  }

  public boolean isCountOnly() {
    return countOnly;
  }

  public QuerySpec setCountOnly(boolean countOnly) {
    this.countOnly = countOnly;
    return this;
  }

  public Integer getScroll() {
    return scroll;
  }

  public QuerySpec setScroll(Integer scroll) {
    this.scroll = scroll;
    return this;
  }

  public String getAggregate() {
    return aggregate;
  }

  public QuerySpec setAggregate(String aggregate) {
    this.aggregate = aggregate;
    return this;
  }

  public int getAggregateSize() {
    return aggregateSize;
  }

  public QuerySpec setAggregateSize(int aggregateSize) {
    this.aggregateSize = aggregateSize;
    return this;
  }

  public QuerySpec include(String include) {
    includes.add(include);
    return this;
  }

  public QuerySpec exclude(String exclude) {
    excludes.add(exclude);
    return this;
  }

  public List<String> getIncludes() {
    return includes;
  }

  public List<String> getExcludes() {
    return excludes;
  }

  public QueryHighlight getHighlight() {
    return highlight;
  }

  public QuerySpec setHighlight(QueryHighlight highlight) {
    this.highlight = highlight;
    return this;
  }

  public Map<String, String> getParams() {
    return params;
  }

  public int getBoost() {
    return boost;
  }

  public QuerySpec setBoost(int boost) {
    this.boost = boost;
    return this;
  }
}
