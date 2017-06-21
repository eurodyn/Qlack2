package com.eurodyn.qlack2.fuse.search.impl.mappers.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;

public class InternalCreateIndexRequest {
	private Settings settings;

	@JsonInclude(Include.NON_NULL)
	@JsonRawValue
	private String mappings;

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public String getMappings() {
		return mappings;
	}

	public void setMappings(String mappings) {
		this.mappings = mappings;
	}

	public static class Settings {
		private Index index;
		private Analysis analysis;

		public Index getIndex() {
			return index;
		}

		public void setIndex(Index index) {
			this.index = index;
		}

		public Analysis getAnalysis() {
			return analysis;
		}

		public void setAnalysis(Analysis analysis) {
			this.analysis = analysis;
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

		public static class Analysis {
			private Filter filter;

			public Filter getFilter() {
				return filter;
			}

			public void setFilter(Filter filter) {
				this.filter = filter;
			}

			public static class Filter {
				@JsonProperty("my_stop")
				private MyStop myStop;

				public MyStop getMyStop() {
					return myStop;
				}

				public void setMyStop(MyStop myStop) {
					this.myStop = myStop;
				}

				public static class MyStop {
					private String type = "stop";
					private List<String>  stopwords;

					public String getType() {
						return type;
					}

					public void setType(String type) {
						this.type = type;
					}

					public List<String> getStopwords() {
						return stopwords;
					}

					public void setStopwords(List<String> stopwords) {
						this.stopwords = stopwords;
					}
				}
			}
		}
	}
}
