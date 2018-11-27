package com.eurodyn.qlack2.fuse.search.api.dto.queries;

/**
 * This represents an elastic search nested query.
 */
public class QueryNested extends QuerySpec {

  private String path;
  private QuerySpec query;
  private InnerHits innerHits;

  public String getPath() {
    return path;
  }

  public QueryNested setPath(String path) {
    this.path = path;
    return this;
  }

  public QuerySpec getQuery() {
    return query;
  }

  public QueryNested setQuery(QuerySpec query) {
    this.query = query;
    return this;
  }

  public InnerHits getInnerHits() {
    return innerHits;
  }

  public QueryNested setInnerHits(InnerHits innerHits) {
    this.innerHits = innerHits;
    return this;
  }
}
