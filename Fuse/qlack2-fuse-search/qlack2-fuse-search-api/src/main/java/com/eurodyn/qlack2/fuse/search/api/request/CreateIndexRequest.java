package com.eurodyn.qlack2.fuse.search.api.request;

public class CreateIndexRequest extends BaseRequest {
  private String name;
  private String aliasName;
  private int shards = 5;
  private int replicas = 1;
  private String indexMapping;
  private String analysis;


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

  public String getAliasName() {
    return aliasName;
  }

  public void setAliasName(String aliasName) {
    this.aliasName = aliasName;
  }

  public String getAnalysis() {
    return analysis;
  }

  public void setAnalysis(String analysis) {
    this.analysis = analysis;
  }

}
