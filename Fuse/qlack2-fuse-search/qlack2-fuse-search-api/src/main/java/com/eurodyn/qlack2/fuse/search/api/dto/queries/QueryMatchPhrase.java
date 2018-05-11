package com.eurodyn.qlack2.fuse.search.api.dto.queries;

public class QueryMatchPhrase extends QuerySpec {

  private String field;
  // The value to lookup.
  private String value;

  /**
   * A convenience method to set the term of this query.
   *
   * @param field The field name to search against.
   * @param value The value to search.
   * @return
   */
  public QueryMatchPhrase setTerm(String field, String value) {
    this.field = field;
    this.value = value;

    return this;
  }

  public String getField() {
    return field;
  }

  public QueryMatchPhrase setField(String field) {
    this.field = field;
    return this;
  }

  public String getValue() {
    return value;
  }

  public QueryMatchPhrase setValue(String value) {
    this.value = value;
    return this;
  }
}
