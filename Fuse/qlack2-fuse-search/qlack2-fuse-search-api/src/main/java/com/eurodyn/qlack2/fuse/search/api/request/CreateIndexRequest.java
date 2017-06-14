package com.eurodyn.qlack2.fuse.search.api.request;

public class CreateIndexRequest extends BaseRequest {
  private String name;
  private int shards = 5;
  private int replicas = 1;
  private String indexMapping;

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
}
