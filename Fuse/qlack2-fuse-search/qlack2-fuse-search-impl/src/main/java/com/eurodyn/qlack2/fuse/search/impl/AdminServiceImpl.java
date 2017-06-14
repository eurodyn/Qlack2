package com.eurodyn.qlack2.fuse.search.impl;

import com.eurodyn.qlack2.fuse.search.api.AdminService;
import com.eurodyn.qlack2.fuse.search.api.exception.QSearchException;
import com.eurodyn.qlack2.fuse.search.api.request.CreateIndexRequest;
import com.eurodyn.qlack2.fuse.search.impl.mappers.CreateIndexRequestMapper;
import com.eurodyn.qlack2.fuse.search.impl.util.ESClient;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Response;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@OsgiServiceProvider(classes = {AdminService.class})
public class AdminServiceImpl implements AdminService {

  private static final Logger LOGGER = Logger.getLogger(AdminServiceImpl.class.getName());
  @Inject
  @Named("ESClient")
  private ESClient esClient;


  @Override
  public boolean createIndex(CreateIndexRequest createIndexRequest) {
    boolean retVal = false;
    /** If the index already exists return without doing anything. */
    if (indexExists(createIndexRequest.getName())) {
      LOGGER.log(Level.WARNING, "Index already exists: {0}.", createIndexRequest.getName());
    } else {
      /** If an indexMapping is provided creaate the index using this mapping, otherwise create the
       * index with no specific mapping (ES will automatically map fields according to the
       * underlying data types, see 'Field datatypes' on
       * https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping.html).
       */
      if (StringUtils.isNotBlank(createIndexRequest.getIndexMapping())) {
//				esClient.getClient().admin().indices().prepareCreate(indexName)
//						.setSource(indexMapping).execute().get();
      } else {
        try {
          esClient.getClient().performRequest("PUT", createIndexRequest.getName(),
            new HashMap<>(),
            CreateIndexRequestMapper.INSTANCE.mapToNStringEntity(createIndexRequest));
          retVal = true;
        } catch (IOException e) {
          throw new QSearchException(
            MessageFormat.format("Could not create index {0}.", createIndexRequest.getName()), e);
        }
      }
    }

    return retVal;
  }

  @Override
  public boolean deleteIndex(String indexName) {
//		// If the index does not exist return without doing anything.
//		if (!indexExists(indexName)) {
//			LOGGER.log(Level.WARNING, "Index does not exist: {0}.", indexName);
//			return false;
//		}
//
//		// Delete the index.
//		esClient.getClient().admin().indices().prepareDelete(indexName).execute();

    return true;
  }

  @Override
  public boolean indexExists(String indexName) {
    Response response;
    try {
      response = esClient.getClient().performRequest("HEAD", indexName);
    } catch (IOException e) {
      throw new QSearchException("Could not check if index exists.", e);
    }

    return response != null ? (response.getStatusLine().getStatusCode() == 200) : false;
  }

  @Override
  public void updateTypeMapping(String indexName, String typeName, String indexMapping) {
//		try {
//			esClient.getClient().admin().indices().preparePutMapping(indexName)
//					.setType(typeName)
//					.setSource(indexMapping).execute().get();
//		} catch (InterruptedException | ExecutionException e) {
//			throw new QSearchException("Could update index mapping.", e);
//		}
  }

}
