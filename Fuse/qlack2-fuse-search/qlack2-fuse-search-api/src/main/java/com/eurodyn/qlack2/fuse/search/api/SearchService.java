package com.eurodyn.qlack2.fuse.search.api;

import com.eurodyn.qlack2.fuse.search.api.dto.SearchResultDTO;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QuerySpec;

/**
 * Provides search functionality to ES.
 */
public interface SearchService {

	/**
	 * Executes a search against ES.
	 *
	 * @param dto
	 *            The query to be executed.
	 * @return
	 */
	SearchResultDTO search(QuerySpec dto);

  /**
   * Checks if a document with the specified id exists in the index with the given type.
   *
   * @param indexName The name of the index to check.
   * @param typeName The type to check.
   * @param id The id of the document.
   * @return true if the document with the given id exists. false otherwise.
   */
  boolean exists(String indexName, String typeName, String id);

  /**
   * Finds a document by its id.
   *
   * @param indexName The name of the index to search
   * @param typeName The type of the document
   * @param id The id of the document
   * @param clazz The return type.
   * @return The document or null if not found
   */
  <T> T findById(String indexName, String typeName, String id, Class<T> clazz);
}
