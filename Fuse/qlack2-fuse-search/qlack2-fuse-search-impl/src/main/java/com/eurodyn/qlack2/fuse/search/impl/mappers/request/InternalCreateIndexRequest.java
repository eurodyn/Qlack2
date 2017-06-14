package com.eurodyn.qlack2.fuse.search.impl.mappers.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalCreateIndexRequest {
  private Settings settings;

  public Settings getSettings() {
    return settings;
  }

  public void setSettings(
    Settings settings) {
    this.settings = settings;
  }

  public static class Settings {
    private Index index;

    public Index getIndex() {
      return index;
    }

    public void setIndex(Index index) {
      this.index = index;
    }

    public static class Index {
      @JsonProperty("number_of_shards")
      private String numberOfShards;
      @JsonProperty("number_of_replicas")
      private String numberOfReplicas;

      public String getNumberOfShards() {
        return numberOfShards;
      }

      public void setNumberOfShards(String numberOfShards) {
        this.numberOfShards = numberOfShards;
      }

      public String getNumberOfReplicas() {
        return numberOfReplicas;
      }

      public void setNumberOfReplicas(String numberOfReplicas) {
        this.numberOfReplicas = numberOfReplicas;
      }
    }
  }
}
