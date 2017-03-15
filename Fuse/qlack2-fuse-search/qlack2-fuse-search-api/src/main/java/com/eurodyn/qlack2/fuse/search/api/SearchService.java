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

}
