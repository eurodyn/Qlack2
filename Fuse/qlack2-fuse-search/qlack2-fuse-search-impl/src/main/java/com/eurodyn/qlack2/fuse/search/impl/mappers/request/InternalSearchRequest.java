package com.eurodyn.qlack2.fuse.search.impl.mappers.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonRawValue;

public class InternalSearchRequest {

    @JsonInclude(Include.NON_NULL)
	private Integer from;
    @JsonInclude(Include.NON_NULL)
	private Integer size;
    @JsonInclude(Include.NON_NULL)
	private Boolean explain;
	@JsonInclude(Include.NON_NULL)
	@JsonRawValue
	private String query;
	@JsonRawValue
	@JsonInclude(Include.NON_NULL)
    private String sort;

	public Integer getFrom() {
		return from;
	}

	public void setFrom(Integer from) {
		this.from = from;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Boolean isExplain() {
		return explain;
	}

	public void setExplain(Boolean explain) {
		this.explain = explain;
	}

	public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

}
