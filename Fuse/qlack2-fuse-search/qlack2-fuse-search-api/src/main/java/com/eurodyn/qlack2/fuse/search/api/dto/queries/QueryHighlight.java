package com.eurodyn.qlack2.fuse.search.api.dto.queries;

import java.util.ArrayList;
import java.util.List;

public class QueryHighlight {

  private String preTag;
  private String postTag;
  private boolean requireFieldMatch = true;
  private List<HighlightField> fields;

  public QueryHighlight addField(HighlightField field) {
    getFields().add(field);
    return this;
  }

  public String getPreTag() {
    return preTag;
  }

  public QueryHighlight setPreTag(String preTag) {
    this.preTag = preTag;
    return this;
  }

  public String getPostTag() {
    return postTag;
  }

  public QueryHighlight setPostTag(String postTag) {
    this.postTag = postTag;
    return this;
  }

  public boolean isRequireFieldMatch() {
    return requireFieldMatch;
  }

  public QueryHighlight setRequireFieldMatch(boolean requireFieldMatch) {
    this.requireFieldMatch = requireFieldMatch;
    return this;
  }

  public List<HighlightField> getFields() {
    if (fields == null) {
      fields = new ArrayList<>();
    }

    return fields;
  }

  public QueryHighlight setFields(List<HighlightField> fields) {
    this.fields = fields;
    return this;
  }
}
