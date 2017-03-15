package com.eurodyn.qlack2.fuse.search.api.dto.queries;

/**
 * A query performing a match against ES. The default match query is of type
 * boolean. The value provided is analyzed and the analysis process constructs a
 * boolean query from the provided text using boolean OR. Example:
 * 
 * <pre>
 * new QueryMatch()
 * 		.setTerm("fooField", "foo")
 * 		.setIndex("bar")
 * 		.setType("FooBarDTO")
 * 		.setPageSize(10)
 * 		.setStartRecord(0)
 * 		.setExplain(false);
 * </pre>
 * 
 * See also:<br>
 * https://www.elastic.co/guide/en/elasticsearch/reference/1.7/query-dsl-match-
 * query.html
 */
public class QueryMatch extends QuerySpec {
	// The field to execute the search against.
	private String field;

	// The value to lookup.
	private Object value;

	/**
	 * A convenience method to set the term of this query.
	 * 
	 * @param field
	 *            The field name to search against.
	 * @param value
	 *            The value to search.
	 * @return
	 */
	public QueryMatch setTerm(String field, Object value) {
		this.field = field;
		this.value = value;

		return this;
	}

	/**
	 * @return the field
	 */
	public String getField() {
		return field;
	}

	/**
	 * @param field
	 *            the field to set
	 */
	public void setField(String field) {
		this.field = field;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}

}
