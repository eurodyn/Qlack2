package com.eurodyn.qlack2.fuse.search.impl.mappers.request;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
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
	@JsonInclude(Include.NON_NULL)
	@JsonProperty("_source")
	private List<String> source;
	@JsonInclude(Include.NON_NULL)
    @JsonRawValue
	private String aggs;
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

  public String getAggs() {
    return aggs;
  }

  public void setAggs(String aggs) {
    this.aggs = aggs;
  }

  public List<String> getSource() {
    if (source == null) {
      source = new ArrayList<String>();
    }

    return source;
  }

  public void setSource(List<String> source) {
    this.source = source;
  }

}
