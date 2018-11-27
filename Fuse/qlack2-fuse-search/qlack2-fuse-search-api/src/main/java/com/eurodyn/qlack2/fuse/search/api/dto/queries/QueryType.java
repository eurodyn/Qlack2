package com.eurodyn.qlack2.fuse.search.api.dto.queries;

/**
 * The ealstic search type query.
 */
public class QueryType extends QuerySpec {

  private String term;

  public String getTerm() {
    return term;
  }

  public QueryType setTerm(String term) {
    this.term = term;
    return this;
  }
}
