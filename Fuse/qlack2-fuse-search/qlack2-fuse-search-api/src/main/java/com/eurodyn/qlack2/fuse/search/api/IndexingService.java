package com.eurodyn.qlack2.fuse.search.api;

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
	 * @param dto
	 *            The document to index with all necessary accompanying info.
	 */
	void indexDocument(IndexingDTO dto);

	/**
	 * Removes a previously indexed document from the index. Asynchronous
	 * operation.
	 *
	 * @param dto
	 *            The identification of the document to remove.
	 */
	void unindexDocument(ESDocumentIdentifierDTO dto);

    /**
     * Deletes data returned by the given query.
     *
     * @param query The query.
     */
    void unindexByQuery(QuerySpec query);
}
