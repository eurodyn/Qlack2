package com.eurodyn.qlack2.fuse.search.api.dto.queries;

public class QueryExists extends QuerySpec {

  private String field;

  public String getField() {
    return field;
  }

  public QueryExists setField(String field) {
    this.field = field;
    return this;
  }
}
