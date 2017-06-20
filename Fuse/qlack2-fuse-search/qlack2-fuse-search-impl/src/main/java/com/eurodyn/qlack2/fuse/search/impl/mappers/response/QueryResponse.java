package com.eurodyn.qlack2.fuse.search.impl.mappers.response;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryResponse {

	private int took;
	@JsonProperty("timed_out")
	private boolean timeOut;
	@JsonProperty("_shards")
	private Shards shards;
	private Hits hits;

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
			/*@JsonRawValue
			@JsonProperty("_source")
			private String source;*/

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

			public String getSource() {
				return null; // return source;
			}

			public void setSource(String source) {
				// this.source = source;
			}

		}
	}
}
