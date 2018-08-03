package com.eurodyn.qlack2.fuse.search.impl.mappers.response;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 23/01/2018 : Json Property InnerHits for nested ES Objects
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryResponse {

  private int took;
  @JsonProperty("timed_out")
  private boolean timeOut;
  @JsonProperty("_shards")
  private Shards shards;
  private Hits hits;
  @JsonInclude(Include.NON_NULL)
  private Aggregations aggregations;
  private long count;
  @JsonProperty("_scroll_id")
  private String scrollId;

  public int getTook() {
    return took;
  }

  public void setTook(int took) {
    this.took = took;
  }

  public boolean isTimeOut() {
    return timeOut;
  }

  public void setTimeOut(boolean timeOut) {
    this.timeOut = timeOut;
  }

  public Shards getShards() {
    return shards;
  }

  public void setShards(Shards shards) {
    this.shards = shards;
  }

  public Hits getHits() {
    return hits;
  }

  public void setHits(Hits hits) {
    this.hits = hits;
  }

  public long getCount() {
    return count;
  }

  public void setCount(long count) {
    this.count = count;
  }

  public String getScrollId() {
    return scrollId;
  }

  public void setScrollId(String scrollId) {
    this.scrollId = scrollId;
  }

  public Aggregations getAggregations() {
    return aggregations;
  }

  public void setAggregations(Aggregations aggregations) {
    this.aggregations = aggregations;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Shards {

    private int total;
    private int successful;
    private int failed;

    public int getTotal() {
      return total;
    }

    public void setTotal(int total) {
      this.total = total;
    }

    public int getSuccessful() {
      return successful;
    }

    public void setSuccessful(int successful) {
      this.successful = successful;
    }

    public int getFailed() {
      return failed;
    }

    public void setFailed(int failed) {
      this.failed = failed;
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Hits {

    private int total;
    @JsonProperty("max_score")
    private float maxScore;
    private List<Hit> hits;

    public int getTotal() {
      return total;
    }

    public void setTotal(int total) {
      this.total = total;
    }

    public float getMaxScore() {
      return maxScore;
    }

    public void setMaxScore(float maxScore) {
      this.maxScore = maxScore;
    }

    public List<Hit> getHits() {
      if (hits == null) {
        hits = new ArrayList<>();
      }

      return hits;
    }

    public void setHits(List<Hit> hits) {
      this.hits = hits;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Hit {

      @JsonProperty("_index")
      private String index;
      @JsonProperty("_type")
      private String type;
      @JsonProperty("_id")
      private String id;
      @JsonProperty("_score")
      private float score;
      @JsonProperty("_source")
      private Object source;
      @JsonProperty("inner_hits")
      private Object innerHits;
      @JsonProperty("highlight")
      private Object highlight;

      public String getIndex() {
        return index;
      }

      public void setIndex(String index) {
        this.index = index;
      }

      public String getType() {
        return type;
      }

      public void setType(String type) {
        this.type = type;
      }

      public String getId() {
        return id;
      }

      public void setId(String id) {
        this.id = id;
      }

      public float getScore() {
        return score;
      }

      public void setScore(float score) {
        this.score = score;
      }

      @JsonRawValue
      public String getSource() {
        return source != null ? source.toString() : null;
      }

      public void setSource(JsonNode source) {
        this.source = source;
      }

      @JsonRawValue
      public String getInnerHits() {
        return innerHits != null ? innerHits.toString() : null;
      }

      public void setInnerHits(JsonNode innerHits) {
        this.innerHits = innerHits;
      }

      @JsonRawValue
      public String getHighlight() {
        return highlight != null ? highlight.toString() : null;
      }

      public void setHighlight(JsonNode highlight) {
        this.highlight = highlight;
      }

    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Aggregations {

    private Agg agg;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Agg {
      private long doc_count_error_upper_bound;
      private long sum_other_doc_count;
      private List<Bucket> buckets;

      @JsonIgnoreProperties(ignoreUnknown = true)
      public static class Bucket {
        private String key;
        private String key_as_string;
        private long doc_count;

        public String getKey() {
          return key;
        }

        public void setKey(String key) {
          this.key = key;
        }

        public String getKey_as_string() {
          return key_as_string;
        }

        public void setKey_as_string(String key_as_string) {
          this.key_as_string = key_as_string;
        }

        public long getDoc_count() {
          return doc_count;
        }

        public void setDoc_count(long doc_count) {
          this.doc_count = doc_count;
        }
      }

      public long getDoc_count_error_upper_bound() {
        return doc_count_error_upper_bound;
      }

      public void setDoc_count_error_upper_bound(long doc_count_error_upper_bound) {
        this.doc_count_error_upper_bound = doc_count_error_upper_bound;
      }

      public long getSum_other_doc_count() {
        return sum_other_doc_count;
      }

      public void setSum_other_doc_count(long sum_other_doc_count) {
        this.sum_other_doc_count = sum_other_doc_count;
      }

      public List<Bucket> getBuckets() {
        if (buckets == null) {
          buckets = new ArrayList<>();
        }

        return buckets;
      }

      public void setBuckets(List<Bucket> buckets) {
        this.buckets = buckets;
      }
    }

    public Agg getAgg() {
      return agg;
    }

    public void setAgg(Agg agg) {
      this.agg = agg;
    }

  }
}