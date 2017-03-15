package com.eurodyn.qlack2.fuse.search.api.dto.queries;

/**
 * Matches documents that have fields matching a wildcard expression (not
 * analyzed). Supported wildcards are *, which matches any character sequence
 * (including the empty one), and ?, which matches any single character. Note
 * this query can be slow, as it needs to iterate over many terms. In order to
 * prevent extremely slow wildcard queries, a wildcard term should not start
 * with one of the wildcards * or ?.
 * 
 * <pre>
 * new QueryWildcard()
 * 		.setTerm("fooField", "bar*")
 * 		.setIndex("foo")
 * 		.setType("FooBarDTO")
 * 		.setPageSize(10)
 * 		.setStartRecord(0)
 * 		.setExplain(false);
 * </pre>
 * 
 * See:<br>
 * https://www.elastic.co/guide/en/elasticsearch/reference/1.7/query-dsl-
 * wildcard-query.html
 */
public class QueryWildcard extends QuerySpec {
	private String field;
	private String wildcard;

	public QueryWildcard setTerm(String field, String wildcard) {
		this.field = field;
		this.wildcard = wildcard;

		return this;
	}

	public String getField() {
		return field;
	}

	public String getWildcard() {
		return wildcard;
	}

}
