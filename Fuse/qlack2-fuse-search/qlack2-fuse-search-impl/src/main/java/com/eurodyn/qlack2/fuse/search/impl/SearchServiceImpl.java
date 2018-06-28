package com.eurodyn.qlack2.fuse.search.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import com.eurodyn.qlack2.fuse.search.api.SearchService;
import com.eurodyn.qlack2.fuse.search.api.dto.SearchHitDTO;
import com.eurodyn.qlack2.fuse.search.api.dto.SearchResultDTO;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.HighlightField;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryBoolean;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryBoolean.BooleanType;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryExists;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryHighlight;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryMatch;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryMatchPhrase;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryMultiMatch;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryRange;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QuerySort;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QuerySpec;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryString;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryStringSpecField;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryStringSpecFieldNested;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryTerm;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryTermNested;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryTerms;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryTermsNested;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryWildcard;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryWildcardNested;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.SimpleQueryString;
import com.eurodyn.qlack2.fuse.search.api.exception.QSearchException;
import com.eurodyn.qlack2.fuse.search.api.request.ScrollRequest;
import com.eurodyn.qlack2.fuse.search.impl.mappers.request.InternalScollRequest;
import com.eurodyn.qlack2.fuse.search.impl.mappers.request.InternalSearchRequest;
import com.eurodyn.qlack2.fuse.search.impl.mappers.request.InternalSearchRequest.Source;
import com.eurodyn.qlack2.fuse.search.impl.mappers.response.QueryResponse;
import com.eurodyn.qlack2.fuse.search.impl.mappers.response.QueryResponse.Aggregations.Agg.Bucket;
import com.eurodyn.qlack2.fuse.search.impl.mappers.response.QueryResponse.Hits.Hit;
import com.eurodyn.qlack2.fuse.search.impl.util.ESClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 23/01/2018 : Introduction of Nested Objects functionality
 */
@Singleton
@OsgiServiceProvider(classes = {SearchService.class})
public class SearchServiceImpl implements SearchService {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static final Logger LOGGER = Logger.getLogger(IndexingServiceImpl.class.getName());

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

    if (dto.isCountOnly()) {
      endpointBuilder.append("/_count");
    }
    else {
      endpointBuilder.append("/_search");
    }

    Map<String, String> params = new HashMap<>();

    QuerySort dtoSort = dto.getQuerySort();
    InternalSearchRequest internalRequest = new InternalSearchRequest();
    if (!dto.isCountOnly()) {
      internalRequest.setFrom(dto.getStartRecord());
      internalRequest.setSize(dto.getPageSize());
      internalRequest.setExplain(dto.isExplain());
      internalRequest.setSort(buildSort(dtoSort));

      if (dto.getScroll() != null) {
        params.put("scroll", dto.getScroll().toString() + "m");
      }

      if (!dto.getIncludes().isEmpty() || !dto.getExcludes().isEmpty()) {
        Source source = new Source();
        if (!dto.getIncludes().isEmpty()) {
          source.setIncludes(new ArrayList<>());
          source.getIncludes().addAll(dto.getIncludes());
        }

        if (!dto.getExcludes().isEmpty()) {
          source.setExcludes(new ArrayList<>());
          source.getExcludes().addAll(dto.getExcludes());
        }

        internalRequest.setSource(source);
      }

      if (dto.getAggregate() != null) {
        Source source = new Source();
        source.setIncludes(new ArrayList<>());
        source.getIncludes().add(dto.getAggregate());
        internalRequest.setSource(source);
        internalRequest.setAggs(buildAggregate(dto.getAggregate(), dto.getAggregateSize()));
      }

      if (dto.getHighlight() != null) {
        internalRequest.setHighlight(buildHighlight(dto.getHighlight()));
      }
    }
    internalRequest.setQuery(buildQuery(dto));

    Response response;
    try {
      ContentType contentType = ContentType.APPLICATION_JSON.withCharset(Charset.forName("UTF-8"));
      response = esClient.getClient()
        .performRequest("GET", endpointBuilder.toString(), params,
          new NStringEntity(mapper.writeValueAsString(internalRequest), contentType));
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Could not execute query.", e);
      throw new QSearchException("Could not execute query.", e);
    }

    QueryResponse queryResponse;
    try {
      queryResponse = mapper.readValue(response.getEntity().getContent(), QueryResponse.class);
    } catch (UnsupportedOperationException | IOException e) {
      LOGGER.log(Level.SEVERE, "Could not deserialize response.", e);
      throw new QSearchException("Could not deserialize response.", e);
    }

    SearchResultDTO result = buildResultFrom(queryResponse, dto.isCountOnly(), dto.isIncludeAllSource(),
        dto.isIncludeResults());

    if (!dto.isCountOnly()) {
      result.setHasMore(queryResponse.getHits().getTotal() > dto.getPageSize());
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

      boolean appendComa = false;
      for (Entry<BooleanType, List<QuerySpec>> entry : queriesMap.entrySet()) {
        if (appendComa) {
          builder.append(",");
        }

        if (BooleanType.MUSTNOT.equals(entry.getKey())) {
          builder.append("\"must_not\" : [");
        } else if (BooleanType.SHOULD.equals(entry.getKey())) {
          builder.append("\"should\" : [");
        } else {
          builder.append("\"must\" : [");
        }

        for (QuerySpec querySpec : entry.getValue()) {
          if (entry.getValue().indexOf(querySpec) > 0) {
            builder.append(",");
          }

          builder.append(buildQuery(querySpec));
        }

        builder.append("]");
        appendComa = true;
      }

      builder.append("}");
    } else if (dto instanceof QueryMatch) {
      QueryMatch query = (QueryMatch) dto;

      builder.append("\"match\" : { \"")
        .append(query.getField())
        .append("\" : \"")
        .append(query.getValue())
        .append("\" }");
    } else if (dto instanceof QueryMultiMatch) {
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
    } else if (dto instanceof QueryString) {
      QueryString query = (QueryString) dto;

      builder.append("\"query_string\" : { \"query\" : \"")
        .append(query.getQueryString())
        .append("\"}");
    } else if (dto instanceof QueryTerm) {
      QueryTerm query = (QueryTerm) dto;

      builder.append("\"term\" : { \"")
        .append(query.getField())
        .append("\" : \"")
        .append(query.getValue())
        .append("\" }");
    } else if (dto instanceof QueryTermNested) {
      QueryTermNested query = (QueryTermNested) dto;
/** 21/02/2018 Adding the a QueryTerm for nested Objects*/
      builder.append("\"nested\" : { ")
        .append("\"path\": \"")
        .append(query.getPath())
        .append("\", \"query\": { ")
        .append("\"term\" : { \"")
        .append(query.getField())
        .append("\" : \"")
        .append(query.getValue())
        .append("\" }")
        .append(" } , \"inner_hits\": {")
        .append("\"_source\" : false, ")
        .append("\"docvalue_fields\" : [ \"")
        .append(query.getDocvalueFields())
        .append("\"]")
        .append("}}");
    } else if (dto instanceof QueryWildcard) {
      QueryWildcard query = (QueryWildcard) dto;

      builder.append("\"wildcard\" : { \"")
        .append(query.getField())
        .append("\" : \"")
        .append(query.getWildcard())
        .append("\" }");
    }
    /** 23/01/2018 Adding the a WildCard query for nested Objects*/
    else if (dto instanceof QueryWildcardNested) {
      QueryWildcardNested query = (QueryWildcardNested) dto;

      builder.append("\"nested\" : { ")
        .append("\"path\": \"")
        .append(query.getPath())
        .append("\", \"query\": { ")
        .append("\"wildcard\" : { \"")
        .append(query.getField())
        .append("\" : \"")
        .append(query.getWildcard())
        .append("\" }")
        .append(" } , \"inner_hits\": {")
        .append("\"_source\" : false, ")
        .append("\"docvalue_fields\" : [ \"")
        .append(query.getDocvalueFields())
        .append("\"]")
        .append("}}");
    } else if (dto instanceof QueryTerms) {
      QueryTerms query = (QueryTerms) dto;
      builder.append("\"terms\" : { \"")
        .append(query.getField())
        .append("\" : [ ")
        .append(query.getValues())
        .append(" ] }");
    }
    /** 23/01/2018 Adding the Query Terms for nested Objects*/
    else if (dto instanceof QueryTermsNested) {
      QueryTermsNested query = (QueryTermsNested) dto;
      builder.append("\"nested\" : { ")
        .append("\"path\": \"")
        .append(query.getPath())
        .append("\", \"query\": { ")
        .append("\"terms\" : { \"")
        .append(query.getField())
        .append("\" : [ ")
        .append(query.getValues())
        .append(" ] }")
        .append(" } , \"inner_hits\": {")
        .append("\"_source\" : false, ")
        .append("\"docvalue_fields\" : [ \"")
        .append(query.getDocvalueFields())
        .append("\"]")
        .append("}}");
    } else if (dto instanceof QueryExists) {
      QueryExists query = (QueryExists) dto;
      builder.append("\"exists\" : { ")
        .append("\"field\": \"")
        .append(query.getField())
        .append("\"")
        .append("}");
    } else if (dto instanceof QueryMatchPhrase) {
      QueryMatchPhrase query = (QueryMatchPhrase) dto;
      builder.append("\"match_phrase\" : { ")
        .append("\"")
        .append(query.getField())
        .append("\": \"")
        .append(query.getValue())
        .append("\"")
        .append("}");
    } else if (dto instanceof QueryRange) {
      QueryRange query = (QueryRange) dto;
      builder.append("\"range\" : { \"")
        .append(query.getField())
        .append("\" : { \"gte\" : \"")
        .append(query.getFromValue())
        .append("\" , \"lte\" : \"")
        .append(query.getToValue())
        .append("\" } }");
    } else if (dto instanceof QueryStringSpecField) {
      QueryStringSpecField query = (QueryStringSpecField) dto;
      builder.append("\"query_string\" : { \"fields\" : [\"")
        .append(query.getField())
        .append("\"] , \"query\" : \"")
        .append(query.getValue())
        .append("\" , \"default_operator\" : \"")
        .append(query.getOperator())
        .append("\" }");
    }
    /** 23/01/2018 A dding the a QueryStringSpecField query for nested Objects*/
    else if (dto instanceof QueryStringSpecFieldNested) {
      QueryStringSpecFieldNested query = (QueryStringSpecFieldNested) dto;
      builder.append("\"nested\" : { ")
        .append("\"path\": \"")
        .append(query.getPath())
        .append("\", \"query\": { ")
        .append("\"query_string\" : { \"fields\" : [\"")
        .append(query.getField())
        .append("\"] , \"query\" : \"")
        .append(query.getValue())
        .append("\" , \"default_operator\" : \"")
        .append(query.getOperator())
        .append("\" }")
        .append(" } , \"inner_hits\": {")
        .append("\"_source\" : false, ")
        .append("\"docvalue_fields\" : [ \"")
        .append(query.getDocvalueFields())
        .append("\"]")
        .append("}}");
    } else if (dto instanceof SimpleQueryString) {
      SimpleQueryString query = (SimpleQueryString) dto;

      builder.append("\"simple_query_string\" : { \"fields\" : [\"")
        .append(query.getField())
        .append("\"] , \"query\" : \"")
        .append(query.getValue())
        .append("\" , \"default_operator\" : \"")
        .append(query.getOperator())
        .append("\" }");
    }
    return builder.append("}")
      .toString().replace("\"null\"", "null");
  }

  private String buildAggregate(String aggregate, int aggregateSize) {
    return new StringBuilder("{")
        .append("\"agg\" : {\"terms\" : {\"field\" : \"")
        .append(aggregate)
        .append("\", \"size\" : ")
        .append(aggregateSize)
        .append(",\"order\" : {\"_term\" : \"desc\"}")
        .append("}}}")
        .toString();
  }

  private String buildHighlight(QueryHighlight highlight) {
    StringBuilder builder = new StringBuilder("{");

    if (highlight.getPreTag() != null) {
      builder.append("\"pre_tags\" : [\"")
        .append(highlight.getPreTag())
        .append("\"],");
    }

    if (highlight.getPostTag() != null) {
      builder.append("\"post_tags\" : [\"")
        .append(highlight.getPostTag())
        .append("\"],");
    }

    if (highlight.getHighlightQuery() != null) {
      builder.append("\"highlight_query\": ")
        .append(buildQuery(highlight.getHighlightQuery()))
        .append(",");
    }

    builder.append("\"require_field_match\": ")
      .append(highlight.isRequireFieldMatch())
      .append(",\"fields\": [");

    for (HighlightField field : highlight.getFields()) {
      builder.append("{\"")
        .append(field.getField())
        .append("\": {");

        if (field.getType() != null) {
          builder.append("\"type\": \"")
            .append(field.getType())
            .append("\",");
        }

        builder.append("\"force_source\": ")
          .append(field.isForceSource())
          .append(", \"fragment_size\" : ")
          .append(field.getFragmentSize())
          .append(", \"number_of_fragments\" : ")
          .append(field.getNumberOfFragments())
          .append(", \"no_match_size\": ")
          .append(field.getNoMatchSize())
          .append("}}");
    }

    builder.append("]}");

    return builder.toString();
  }

  private String buildSort(QuerySort dto) {
    StringBuilder builder = new StringBuilder("[");

    if (dto instanceof QuerySort) {
      QuerySort sort = dto;

      for (Entry<String, String> entry : sort.getSortMap().entrySet()) {
        if (builder.length() > 1) {
          builder.append(',');
        }

        builder.append("{")
          .append("\"")
          .append(entry.getKey())
          .append("\"")
          .append(" : {")
          .append("\"order\"").append(" : ").append("\"").append(entry.getValue()).append("\"")
          .append("}")
          .append("}");
      }
    }

    builder.append("]");

    return builder.toString();

  }

  @Override
  public boolean exists(String indexName, String typeName, String id) {
    String endpoint = indexName + "/" + typeName + "/" + id;
    try {
      Response response = esClient.getClient().performRequest("HEAD", endpoint);
      return response.getStatusLine().getStatusCode() == 200;
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, MessageFormat.format("Could not check if document with id: {0} exists", id), e);
      throw new QSearchException(MessageFormat.format("Could not check if document with id: {0} exists", id));
    }
  }

  @Override
  public SearchHitDTO findById(String indexName, String typeName, String id) {
    String endpoint = indexName + "/" + typeName + "/" + id;
    try {
      Response response = esClient.getClient().performRequest("GET", endpoint);
      if (response.getStatusLine().getStatusCode() == 200) {
        Hit hit = mapper.readValue(response.getEntity().getContent(), Hit.class);
        return map(hit);
      } else {
        return null;
      }
    } catch (IOException e) {
      return null;
    }
  }

  @Override
  public SearchResultDTO scroll(ScrollRequest request) {
    InternalScollRequest internalRequest = new InternalScollRequest();
    internalRequest.setScroll(request.getScroll().toString() + "m");
    internalRequest.setScrollId(request.getScrollId());

    Response response;
    try {
      ContentType contentType = ContentType.APPLICATION_JSON.withCharset(Charset.forName("UTF-8"));
      response = esClient.getClient().performRequest("GET", "_search/scroll", new HashMap<>(),
          new NStringEntity(mapper.writeValueAsString(internalRequest), contentType));
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Could not execute scroll query.", e);
      throw new QSearchException("Could not execute scroll query.", e);
    }

    QueryResponse queryResponse;
    try {
      queryResponse = mapper.readValue(response.getEntity().getContent(), QueryResponse.class);
    } catch (UnsupportedOperationException | IOException e) {
      LOGGER.log(Level.SEVERE, "Could not deserialize response.", e);
      throw new QSearchException("Could not deserialize response.", e);
    }

    SearchResultDTO result = buildResultFrom(queryResponse, false, true, true);
    result.setHasMore(!result.getHits().isEmpty());

    return result;
  }

  private SearchResultDTO buildResultFrom(QueryResponse queryResponse,
      boolean countOnly, boolean includeAllSource, boolean includeResults) {

    SearchResultDTO result = new SearchResultDTO();
    if (!countOnly) {
      result.setBestScore(queryResponse.getHits().getMaxScore());
      result.setExecutionTime(queryResponse.getTook());
      result.setTimedOut(queryResponse.isTimeOut());
      result.setTotalHits(queryResponse.getHits().getTotal());
      result.setScrollId(queryResponse.getScrollId());
    }
    else {
      result.setTotalHits(queryResponse.getCount());
    }
    result.setShardsFailed(queryResponse.getShards().getFailed());
    result.setShardsSuccessful(queryResponse.getShards().getSuccessful());
    result.setShardsTotal(queryResponse.getShards().getTotal());

    if (!countOnly && includeAllSource) {
      try {
        result.setSource(mapper.writeValueAsString(queryResponse));
      } catch (JsonProcessingException e) {
        LOGGER.log(Level.SEVERE, "Could not serialize response.", e);
        throw new QSearchException("Could not serialize response.", e);
      }
    }

    if (!countOnly && includeResults) {
      for (Hit hit : queryResponse.getHits().getHits()) {
        result.getHits().add(map(hit));
      }
    }

    if (queryResponse.getAggregations() != null && queryResponse.getAggregations().getAgg() != null) {
      for (Bucket bucket : queryResponse.getAggregations().getAgg().getBuckets()) {
        String key = bucket.getKey_as_string() != null ? bucket.getKey_as_string() : Long.toString(bucket.getKey());
        if (key != null) {
          result.getAggregations().put(key, bucket.getDoc_count());
        }
      }
    }

    return result;
  }

  private SearchHitDTO map(Hit hit) {
    SearchHitDTO sh = new SearchHitDTO();
    sh.setScore(hit.getScore());
    sh.setType(hit.getType());
    sh.setSource(hit.getSource());
    sh.setId(hit.getId());
    sh.setInnerHits(hit.getInnerHits());
    sh.setHighlight(hit.getHighlight());
    return sh;
  }
}
