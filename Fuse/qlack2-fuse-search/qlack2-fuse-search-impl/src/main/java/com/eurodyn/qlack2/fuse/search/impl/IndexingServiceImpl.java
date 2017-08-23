package com.eurodyn.qlack2.fuse.search.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
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
import com.eurodyn.qlack2.fuse.search.api.dto.ESDocumentIdentifierDTO;
import com.eurodyn.qlack2.fuse.search.api.dto.IndexingDTO;
import com.eurodyn.qlack2.fuse.search.api.exception.QSearchException;
import com.eurodyn.qlack2.fuse.search.impl.util.ESClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
@Singleton
@OsgiServiceProvider(classes = {IndexingService.class})
public class IndexingServiceImpl implements IndexingService {

  private static ObjectMapper mapper;
  private static final Logger LOGGER = Logger.getLogger(IndexingServiceImpl.class.getName());

  // The ES client injected by blueprint.
  @Inject
  @Named("ESClient")
  private ESClient esClient;

  public IndexingServiceImpl() {
	  mapper = new ObjectMapper();
	  mapper.registerModule(new JavaTimeModule());
	  mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }
  
  @Override
  public void indexDocument(IndexingDTO dto) {
		try {
			String endpoint = dto.getIndex() + "/" + dto.getType() + "/" + dto.getId();
			Map<String, String> params = dto.isRefresh() ? Collections.singletonMap("refresh", "wait_for") : new HashMap<>();

			// Execute indexing request.
      ContentType contentType = ContentType.APPLICATION_JSON.withCharset(Charset.forName("UTF-8"));
			esClient.getClient().performRequest("PUT", endpoint, params,
					new NStringEntity(mapper.writeValueAsString(dto.getSourceObject()), contentType));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, MessageFormat.format("Could not index document with id: {0}", dto.getId()), e);
			throw new QSearchException(MessageFormat.format("Could not index document with id: {0}", dto.getId()));
		}
  }

  @Override
  public void unindexDocument(ESDocumentIdentifierDTO dto) {
	  try {
		  	String endpoint = dto.getIndex() + "/" + dto.getType() + "/" + dto.getId();

			// Execute indexing request.
			esClient.getClient().performRequest("DELETE", endpoint);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, MessageFormat.format("Could not delete document with id: {0}", dto.getId()), e);
			throw new QSearchException(MessageFormat.format("Could not delete document with id: {0}", dto.getId()));
		}
  }
}
