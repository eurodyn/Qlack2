package com.eurodyn.qlack2.fuse.search.api;

import com.eurodyn.qlack2.fuse.search.api.request.CreateIndexRequest;
import com.eurodyn.qlack2.fuse.search.api.request.UpdateMappingRequest;

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
	 * @param indexName
	 *            The name of index to delete.
	 * @return
	 */
	public boolean deleteIndex(String indexName);

	/**
	 * Checks if a index with the given name already exists. Synchronous
	 * operation.
	 *
	 * @param indexName
	 *            The name of the index to check for existence.
	 * @return
	 */
	boolean indexExists(String indexName);

	/**
	 * Updates the mapping definition of an existing type on an index. Note that
	 * only a specific set of index mapping settings can be updated this way
	 * (see
	 * https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-
	 * put-mapping.html).
	 * <br><br>
	 * Mapping example:
	 * <pre>
{
  "properties": {
    "def": {
      "type": "long"
    }
  }
}
	 * </pre>
	 * @param request The details of the mapping to be updated.
	 */
	boolean updateTypeMapping(UpdateMappingRequest request);
}
