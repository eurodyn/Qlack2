package com.eurodyn.qlack2.fuse.search.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import com.eurodyn.qlack2.fuse.search.api.SearchService;
import com.eurodyn.qlack2.fuse.search.api.dto.SearchHitDTO;
import com.eurodyn.qlack2.fuse.search.api.dto.SearchResultDTO;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryBoolean;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryBoolean.BooleanType;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryMatch;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryMultiMatch;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QuerySpec;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryString;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryTerm;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryWildcard;
import com.eurodyn.qlack2.fuse.search.api.exception.QSearchException;
import com.eurodyn.qlack2.fuse.search.impl.mappers.request.InternalSearchRequest;
import com.eurodyn.qlack2.fuse.search.impl.mappers.response.QueryResponse;
import com.eurodyn.qlack2.fuse.search.impl.mappers.response.QueryResponse.Hits.Hit;
import com.eurodyn.qlack2.fuse.search.impl.util.ESClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton
@OsgiServiceProvider(classes = { SearchService.class })
public class SearchServiceImpl implements SearchService {
	private final ObjectMapper mapper = new ObjectMapper();

	// The ES client injected by blueprint.
	@Inject
	@Named("ESClient")
	private ESClient esClient;

	@Override
	public SearchResultDTO search(QuerySpec dto) {
		StringBuilder endpointBuilder = new StringBuilder();

		// This is done to remove duplicates
		List<String> indeces = new ArrayList<>(new HashSet<>(dto.getIndices()));

		// If no indeces are defind then search them all
		if (indeces.isEmpty()) {
			endpointBuilder.append("_all");
		}

		// append indeces to the query
		for (String index : indeces) {
			if (indeces.indexOf(index) > 0) {
				endpointBuilder.append(",");
			}

			endpointBuilder.append(index);
		}

		// This is done to remove duplicates
		List<String> types = new ArrayList<>(new HashSet<>(dto.getTypes()));

		// if no types are defined then search them all
		if (!types.isEmpty()) {
			endpointBuilder.append("/");
		}

		// append types to the query
		for (String type : types) {
			if (types.indexOf(type) > 0) {
				endpointBuilder.append(",");
			}

			endpointBuilder.append(type);
		}

		endpointBuilder.append("/_search");

		InternalSearchRequest internalRequest = new InternalSearchRequest();
		internalRequest.setFrom(dto.getStartRecord());
		internalRequest.setSize(dto.getPageSize());
		internalRequest.setQuery(buildQuery(dto));
		internalRequest.setExplain(dto.isExplain());

		Response response;
		try {
			response = esClient.getClient().performRequest("GET", endpointBuilder.toString(), new HashMap<>(),
					new NStringEntity(mapper.writeValueAsString(internalRequest)));
		} catch (IOException e) {
			throw new QSearchException("could not execute query.", e);
		}

		QueryResponse queryResponse;
		try {
			queryResponse = mapper.readValue(response.getEntity().getContent(), QueryResponse.class);
		} catch (UnsupportedOperationException | IOException e) {
			// throw new QSearchException("Could not deserialize response.", e);
			throw new RuntimeException(e);
		}

		SearchResultDTO result = new SearchResultDTO();
		result.setBestScore(queryResponse.getHits().getMaxScore());
		result.setExecutionTime(queryResponse.getTook());
		result.setHasMore(queryResponse.getHits().getTotal() > dto.getPageSize());
		result.setShardsFailed(queryResponse.getShards().getFailed());
		result.setShardsSuccessful(queryResponse.getShards().getSuccessful());
		result.setShardsTotal(queryResponse.getShards().getTotal());
		result.setTimedOut(queryResponse.isTimeOut());
		result.setTotalHits(queryResponse.getHits().getTotal());

		if (dto.isIncludeAllSource()) {
			try {
				result.setSource(mapper.writeValueAsString(queryResponse));
			} catch (JsonProcessingException e) {
				throw new QSearchException("Could not serialize response.", e);
			}
		}

		if (dto.isIncludeResults()) {
			for (Hit hit : queryResponse.getHits().getHits()) {
				SearchHitDTO sh = new SearchHitDTO();
				sh.setScore(hit.getScore());
				sh.setType(hit.getType());
				sh.setSource(hit.getSource());

				result.getHits().add(sh);
			}
		}

		return result;
	}

	private String buildQuery(QuerySpec dto) {
		StringBuilder builder = new StringBuilder("{");

		if (dto instanceof QueryBoolean) {
			QueryBoolean query = (QueryBoolean) dto;

			builder.append("\"bool\" : {");

			Map<BooleanType, List<QuerySpec>> queriesMap = new HashMap<>();
			for (Entry<QuerySpec, BooleanType> entry : query.getTerms().entrySet()) {
				if (entry.getValue() != null) {
					queriesMap.putIfAbsent(entry.getValue(), new ArrayList<>());
					queriesMap.get(entry.getValue()).add(entry.getKey());
				}
			}

			for (Entry<BooleanType, List<QuerySpec>> entry : queriesMap.entrySet()) {
				if (BooleanType.MUSTNOT.equals(entry.getKey())) {
					builder.append("\"must_not\" : [");
				}
				else if (BooleanType.SHOULD.equals(entry.getKey())) {
					builder.append("\"should\" : [");
				}
				else {
					builder.append("\"must\" : [");
				}

				for (QuerySpec querySpec : entry.getValue()) {
					if (entry.getValue().indexOf(querySpec) > 0) {
						builder.append(",");
					}

					builder.append("{")
						.append(buildQuery(querySpec))
						.append("}");
				}

				builder.append("]");
			}

			builder.append("}");
		}
		else if (dto instanceof QueryMatch) {
			QueryMatch query = (QueryMatch) dto;

			builder.append("\"match\" : { \"")
				.append(query.getField())
				.append("\" : \"")
				.append(query.getValue())
				.append("\" }");
		}
		else if (dto instanceof QueryMultiMatch) {
			QueryMultiMatch query = (QueryMultiMatch) dto;

			builder.append("\"multi_match\" : { \"query\" : \"")
				.append(query.getValue())
				.append("\", \"fields\" : [");

			for (int i = 0; i < query.getFields().length; i++) {
				if (i > 0) {
					builder.append(", ");
				}

				builder.append("\"")
					.append(query.getFields()[i])
					.append("\"");
			}

			builder.append("]}");
		}
		else if (dto instanceof QueryString) {
			QueryString query = (QueryString) dto;

			builder.append("\"query_string\" : { \"query\" : \"")
			.append(query.getQueryString())
			.append("\"}");
		}
		else if (dto instanceof QueryTerm) {
			QueryTerm query = (QueryTerm) dto;

			builder.append("\"term\" : { \"")
				.append(query.getField())
				.append("\" : \"")
				.append(query.getValue())
				.append("\" }");
		}
		else if (dto instanceof QueryWildcard) {
			QueryWildcard query = (QueryWildcard) dto;

			builder.append("\"wildcard\" : { \"")
				.append(query.getField())
				.append("\" : \"")
				.append(query.getWildcard())
				.append("\" }");
		}

		return builder.append("}")
			.toString();
	}
}
