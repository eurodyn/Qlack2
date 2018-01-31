package com.eurodyn.qlack2.fuse.search.api;

import com.eurodyn.qlack2.fuse.search.api.request.CreateIndexRequest;
import com.eurodyn.qlack2.fuse.search.api.request.UpdateMappingRequest;
import java.util.Map;

/**
 * Provides functionality to manipulate the indices of ES.
 */
public interface AdminService {

  /**
   * Creates a new index.
   *
   * @param createIndexRequest The details of the index to be created.
   * @return True if the index was created, false if the index already exists.
   */
  boolean createIndex(CreateIndexRequest createIndexRequest);

  /**
   * Creates a new index with specific mappings.
   * <br><br>
   * Mapping example:
   * <pre>
   {
   "mappings": {
   "TmpDTO": {
   "_source": {
   "excludes": ["file"]
   },
   "properties": {
   "file": {
   "type": "attachment"
   }
   }
   }
   }
   }
   * </pre>
   *
   * @param indexName
   *            The name of the index to exist.
   * @param indexMapping
   *            A JSON string with the index mappings to create, see
   *            https://www.elastic.co/guide/en/elasticsearch/reference/
   *            current/indices-put-mapping.html.
   * @return True if the index was created, false if the index alredy exists.
   *
   */
//	boolean createIndex(C);

  /**
   * Deletes an index by name. Asynchronous operation.
   *
   * @param indexName The name of index to delete.
   */
  public boolean deleteIndex(String indexName);

  /**
   * Checks if a index with the given name already exists. Synchronous operation.
   *
   * @param indexName The name of the index to check for existence.
   */
  boolean indexExists(String indexName);

  /**
   * Updates the mapping definition of an existing type on an index. Note that only a specific set
   * of index mapping settings can be updated this way (see https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-
   * put-mapping.html). <br><br> Mapping example:
   * <pre>
   * {
   * "properties": {
   * "def": {
   * "type": "long"
   * }
   * }
   * }
   * </pre>
   *
   * @param request The details of the mapping to be updated.
   */
  boolean updateTypeMapping(UpdateMappingRequest request);

  /**
   * Close an index - for maintainance perhaps
   *
   * @return Whether the operation was successful or not
   */
  boolean closeIndex(String indexName);

  /**
   * Open an index - after an index has been closed previously
   *
   * @param indexName - open a previously closed index - perhaps after maintainance is done
   * @return Whether the operation was successful or not
   */
  boolean openIndex(String indexName);

  /**
   * Performs updates on a given index by using key-value pairs of settings
   *
   * @param indexName - the index to change settings for
   * @param settings - the settings as key-value pairs
   * @param preserveExisting - whether or not to preserve existing settings on the index
   * @return Whether the operation was successful or not
   */
  boolean updateIndexSettings(String indexName, Map<String, String> settings, boolean preserveExisting);
}
