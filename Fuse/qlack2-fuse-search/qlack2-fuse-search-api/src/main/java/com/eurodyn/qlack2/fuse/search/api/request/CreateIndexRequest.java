package com.eurodyn.qlack2.fuse.search.api.request;

import java.util.ArrayList;
import java.util.List;

public class CreateIndexRequest extends BaseRequest {
  private String name;
  private int shards = 5;
  private int replicas = 1;
  private String indexMapping;
  private List<String> stopwords = new ArrayList<>();

  public void addStopWords(String... words) {
	  if (words == null) {
		  return;
	  }

	  for (String word : words) {
		  stopwords.add(word);
	  }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getShards() {
    return shards;
  }

  public void setShards(int shards) {
    this.shards = shards;
  }

  public int getReplicas() {
    return replicas;
  }

  public void setReplicas(int replicas) {
    this.replicas = replicas;
  }

  public String getIndexMapping() {
    return indexMapping;
  }

  public void setIndexMapping(String indexMapping) {
    this.indexMapping = indexMapping;
  }

  public List<String> getStopwords() {
	return stopwords;
  }

  public void setStopwords(List<String> stopwords) {
    this.stopwords = stopwords;
  }
}
