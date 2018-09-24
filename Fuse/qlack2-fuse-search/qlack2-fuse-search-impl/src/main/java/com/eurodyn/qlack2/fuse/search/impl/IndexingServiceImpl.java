package com.eurodyn.qlack2.fuse.search.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import com.eurodyn.qlack2.fuse.search.api.IndexingService;
import com.eurodyn.qlack2.fuse.search.api.SearchService;
import com.eurodyn.qlack2.fuse.search.api.dto.ESDocumentIdentifierDTO;
import com.eurodyn.qlack2.fuse.search.api.dto.IndexingDTO;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QuerySpec;
import com.eurodyn.qlack2.fuse.search.api.exception.QSearchException;
import com.eurodyn.qlack2.fuse.search.impl.util.ESClient;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Singleton
@OsgiServiceProvider(classes = {IndexingService.class})
public class IndexingServiceImpl implements IndexingService {

  private static ObjectMapper mapper;
  private static ObjectMapper updateMapper;
  private static final Logger LOGGER = Logger.getLogger(IndexingServiceImpl.class.getName());

  // The ES client injected by blueprint.
  @Inject
  @Named("ESClient")
  private ESClient esClient;

  @Inject
  private SearchService searchService;

  public IndexingServiceImpl() {
	  mapper = new ObjectMapper();
	  mapper.registerModule(new JavaTimeModule());
	  mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

	  updateMapper = new ObjectMapper();
	  updateMapper.registerModule(new JavaTimeModule());
	  updateMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	  updateMapper.setSerializationInclusion(Include.NON_NULL);
  }

  @Override
  public void indexDocument(IndexingDTO dto) {
    index(dto, false);
  }

  @Override
  public void updateDocument(IndexingDTO dto) {
    index(dto, true);
  }

  private void index(IndexingDTO dto, boolean update) {
    try {
      String endpoint = dto.getIndex() + "/" + dto.getType() + "/" + dto.getId();
      String method;
      String jsonBody;
      if (update) {
        endpoint += "/_update";
        method = "POST";
        jsonBody = "{\"doc\": " + updateMapper.writeValueAsString(dto.getSourceObject()) + "}";
      } else {
        method = "PUT";
        jsonBody = mapper.writeValueAsString(dto.getSourceObject());
      }

      Map<String, String> params = dto.isRefresh() ? Collections.singletonMap("refresh", "wait_for") : new HashMap<>();

      // Execute indexing request.
      ContentType contentType = ContentType.APPLICATION_JSON.withCharset(Charset.forName("UTF-8"));
      esClient.getClient().performRequest(method, endpoint, params, new NStringEntity(jsonBody, contentType));
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, MessageFormat.format("Could not index document with id: {0}", dto.getId()), e);
      throw new QSearchException(MessageFormat.format("Could not index document with id: {0}", dto.getId()));
    }
  }

  @Override
  public void unindexDocument(ESDocumentIdentifierDTO dto) {
	  try {
		  	String endpoint = dto.getIndex() + "/" + dto.getType() + "/" + dto.getId();
		  	Map<String, String> params = dto.isRefresh() ? Collections.singletonMap("refresh", "wait_for") : new HashMap<>();

			// Execute indexing request.
			esClient.getClient().performRequest("DELETE", endpoint, params);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, MessageFormat.format("Could not delete document with id: {0}", dto.getId()), e);
			throw new QSearchException(MessageFormat.format("Could not delete document with id: {0}", dto.getId()));
		}
  }

  @Override
  public void unindexByQuery(QuerySpec query) {
    StringBuilder endpointBuilder = new StringBuilder();

    // This is done to remove duplicates
    List<String> indeces = new ArrayList<>(new HashSet<>(query.getIndices()));

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
    List<String> types = new ArrayList<>(new HashSet<>(query.getTypes()));

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

    endpointBuilder.append("/_delete_by_query");

    Map<String, String> params = new HashMap<>(query.getParams());
    String q = "{\"query\": " + searchService.buildQuery(query) + "}";

    try {
      ContentType contentType = ContentType.APPLICATION_JSON.withCharset(Charset.forName("UTF-8"));
      esClient.getClient().performRequest("POST", endpointBuilder.toString(), params, new NStringEntity(q, contentType));
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Could not delete documents", e);
      throw new QSearchException("Could not delete documents", e);
    }
  }
}
