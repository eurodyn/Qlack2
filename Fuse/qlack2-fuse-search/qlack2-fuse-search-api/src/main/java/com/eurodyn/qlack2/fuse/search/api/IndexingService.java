package com.eurodyn.qlack2.fuse.search.api;

import com.eurodyn.qlack2.fuse.search.api.dto.BulkIndexingDTO;
import com.eurodyn.qlack2.fuse.search.api.dto.ESDocumentIdentifierDTO;
import com.eurodyn.qlack2.fuse.search.api.dto.IndexingDTO;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QuerySpec;

/**
 * Provides functionality to index (and unindex) documents.
 */
public interface IndexingService {

  /**
   * Indexes a document. Asynchronous operation.
   *
   * @param dto The document to index with all necessary accompanying info.
   */
  void indexDocument(IndexingDTO dto);

  /**
   * Updates a document.
   *
   * @param dto The document to index with all necessary accompanying info.
   */
  void updateDocument(IndexingDTO dto);

  /**
   * Removes a previously indexed document from the index. Asynchronous operation.
   *
   * @param dto The identification of the document to remove.
   */
  void unindexDocument(ESDocumentIdentifierDTO dto);

  /**
   * Indexes documents in a bulk operation.
   *
   * @param <T> The type of the documents.
   * @param dto The documents to index with all necessary accompanying info.
   * @return true if all operations were successful.
   */
  <T> boolean indexDocuments(BulkIndexingDTO<T> dto);

  /**
   * Updates documents in a bulk operation.
   *
   * @param <T> The type of the documents.
   * @param dto The documents to update with all necessary accompanying info.
   * @return true if all operations were successful.
   */
  <T> boolean updateDocuments(BulkIndexingDTO<T> dto);

  /**
   * Deletes data returned by the given query.
   *
   * @param query The query.
   */
  void unindexByQuery(QuerySpec query);

  /**
   * Refreshes an index making all data searchable after execution.
   *
   * @param index The index name.
   */
  void refresh(String index);
}
