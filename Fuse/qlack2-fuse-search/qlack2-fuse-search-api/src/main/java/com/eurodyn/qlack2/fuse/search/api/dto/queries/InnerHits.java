package com.eurodyn.qlack2.fuse.search.api.dto.queries;

import java.util.HashSet;
import java.util.Set;

/**
 * This class requests information about inner hits.
 */
public class InnerHits {

  private int size = 3;
  private final Set<String> excludes = new HashSet<>();
  private QueryHighlight highlight;

  public int getSize() {
    return size;
  }

  public InnerHits setSize(int size) {
    this.size = size;
    return this;
  }

  /**
   * Exclude the given field from the inner hits source.
   *
   * @param field The field name.
   * @return This object.
   */
  public InnerHits exclude(String field) {
    excludes.add(field);
    return this;
  }

  public Set<String> getExcludes() {
    return excludes;
  }

  public QueryHighlight getHighlight() {
    return highlight;
  }

  public InnerHits setHighlight(QueryHighlight highlight) {
    this.highlight = highlight;
    return this;
  }
}
