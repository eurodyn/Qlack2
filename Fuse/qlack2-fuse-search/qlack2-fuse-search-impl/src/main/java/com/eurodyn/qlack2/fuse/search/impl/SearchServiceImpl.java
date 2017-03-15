package com.eurodyn.qlack2.fuse.search.impl;

import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import com.eurodyn.qlack2.fuse.search.api.SearchService;
import com.eurodyn.qlack2.fuse.search.api.dto.SearchHitDTO;
import com.eurodyn.qlack2.fuse.search.api.dto.SearchResultDTO;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryBoolean;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryMatch;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryMultiMatch;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QuerySpec;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryString;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryTerm;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryWildcard;
import com.eurodyn.qlack2.fuse.search.api.exception.QSearchException;
import com.eurodyn.qlack2.fuse.search.impl.util.ESClient;
import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton
@OsgiServiceProvider(classes = { SearchService.class })
public class SearchServiceImpl implements SearchService {
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final Logger LOGGER = Logger.getLogger(SearchServiceImpl.class.getName());

	// The ES client injected by blueprint.
	@Inject
	@Named("ESClient")
	private ESClient esClient;

	/**
	 * Helper method to create a {@link QueryBuilder} based on a
	 * {@link QuerySpec} object.
	 * 
	 * @param qs The object specifying the query to be executed.
	 * @return
	 */
	private QueryBuilder createQueryBuilder(QuerySpec qs) {
		QueryBuilder builder = null;
		if (qs instanceof QueryMatch) {
			QueryMatch q = (QueryMatch) qs;
			builder = QueryBuilders.matchQuery(q.getField(), q.getValue());
		} else if (qs instanceof QueryMultiMatch) {
			QueryMultiMatch q = (QueryMultiMatch) qs;
			builder = QueryBuilders.multiMatchQuery(q.getValue(), q.getFields());
		} else if (qs instanceof QueryString) {
			QueryString q = (QueryString) qs;
			builder = QueryBuilders.queryStringQuery(q.getQueryString());
		} else if (qs instanceof QueryTerm) {
			QueryTerm q = (QueryTerm) qs;
			builder = QueryBuilders.termQuery(q.getField(), q.getValue());
		} else if (qs instanceof QueryWildcard) {
			QueryWildcard q = (QueryWildcard) qs;
			builder = QueryBuilders.wildcardQuery(q.getField(), q.getWildcard());
		}

		return builder;
	}

	@Override
	public SearchResultDTO search(QuerySpec dto) {
		// Prepare a search request.
		SearchRequestBuilder prepareSearch = esClient.getClient().prepareSearch();

		// Tune the query according to the information passed by the caller.
		prepareSearch.setIndices(dto.getIndices().toArray(new String[dto.getIndices().size()]));
		prepareSearch.setTypes(dto.getTypes().toArray(new String[dto.getTypes().size()]));
		prepareSearch.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		prepareSearch.setFrom(dto.getStartRecord());
		// Add an extra result to the pageSize to be able to check whether there
		// are more results or not.
		prepareSearch.setSize(dto.getPageSize() + 1);
		prepareSearch.setExplain(dto.isExplain());

		// Construct the terms of this query as per the query's type.
		if (dto instanceof QueryBoolean) {
			QueryBoolean q = (QueryBoolean) dto;
			BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
			for (QuerySpec t : q.getTerms().keySet()) {
				switch (q.getTerms().get(t)) {
				case MUST:
					boolQuery.must(createQueryBuilder(t));
					break;
				case MUSTNOT:
					boolQuery.mustNot(createQueryBuilder(t));
					break;
				case SHOULD:
					boolQuery.should(createQueryBuilder(t));
					break;
				}
			}
			prepareSearch.setQuery(boolQuery);
		} else {
			prepareSearch.setQuery(createQueryBuilder(dto));
		}

		// Execute the search.
		try {
			SearchResponse searchResponse = prepareSearch.execute().get();
			SearchResultDTO retVal = new SearchResultDTO();
			
			// Add query execution statistics.
			if (dto.isIncludeAllSource()) {
				retVal.setSource(searchResponse.toString());
			}
			retVal.setExecutionTime(searchResponse.getTookInMillis());
			retVal.setTimedOut(searchResponse.isTimedOut());
			retVal.setShardsTotal(searchResponse.getTotalShards());
			retVal.setShardsSuccessful(searchResponse.getSuccessfulShards());
			retVal.setShardsFailed(searchResponse.getFailedShards());
			retVal.setTotalHits(searchResponse.getHits().getTotalHits());
			retVal.setHasMore(searchResponse.getHits().getTotalHits() > dto.getPageSize());
			retVal.setBestScore(searchResponse.getHits().getMaxScore());

			// Add search results taking into account the extra record added
			// to deduce if more results are available (e.g. the extra record is
			// removed to respect the originally requested pageSize).
			if (dto.isIncludeResults()) {
				long resultsLength = searchResponse.getHits().getTotalHits();
				if (resultsLength > dto.getPageSize()) {
					resultsLength = dto.getPageSize();
				}
				for (int i = 0; i < (int)resultsLength; i++) {
					SearchHitDTO searchHitDTO = new SearchHitDTO();
					searchHitDTO.setSource(searchResponse.getHits().getHits()[i].getSourceAsString());
					searchHitDTO.setScore(searchResponse.getHits().getHits()[i].getScore());
					searchHitDTO.setType(searchResponse.getHits().getHits()[i].getType());
					retVal.addHit(searchHitDTO);
				}
			}

			return retVal;
		} catch (InterruptedException | ExecutionException e) {
			throw new QSearchException("Could not execute search.", e);
		}
	}

}
