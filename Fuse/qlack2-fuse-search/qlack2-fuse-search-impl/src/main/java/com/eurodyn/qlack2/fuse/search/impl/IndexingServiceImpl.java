package com.eurodyn.qlack2.fuse.search.impl;

import com.eurodyn.qlack2.fuse.search.api.IndexingService;
import com.eurodyn.qlack2.fuse.search.api.dto.ESDocumentIdentifierDTO;
import com.eurodyn.qlack2.fuse.search.api.dto.IndexingDTO;
import com.eurodyn.qlack2.fuse.search.api.exception.QSearchException;
import com.eurodyn.qlack2.fuse.search.impl.util.ESClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@OsgiServiceProvider(classes = {IndexingService.class})
public class IndexingServiceImpl implements IndexingService {
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final Logger LOGGER = Logger.getLogger(IndexingServiceImpl.class.getName());

	// The ES client injected by blueprint.
	@Inject @Named("ESClient")
	private ESClient esClient;
	
	@Override
	public void indexDocument(IndexingDTO dto) {
		IndexRequestBuilder builder;
		try {
			// Prepare indexing request.
			if (dto.isConvertToJSON()) {
				builder = esClient.getClient().prepareIndex(
						dto.getIndex(), dto.getType(), dto.getId())
						.setSource(mapper.writeValueAsString(dto.getSourceObject()));
			} else {
				builder = esClient.getClient().prepareIndex(
						dto.getIndex(), dto.getType(), dto.getId())
						.setSource((String)dto.getSourceObject());
			}

			// Execute indexing request.
			builder.execute();
		} catch (JsonProcessingException e) {
			LOGGER.log(Level.SEVERE, MessageFormat.format("Could not index document with id: {0}", dto.getId()), e);
			throw new QSearchException(MessageFormat.format("Could not index document with id: {0}", dto.getId()));
		}
	}

	@Override
	public void unindexDocument(ESDocumentIdentifierDTO dto) {
		// Prepare unindexing request.
		DeleteRequest req = new DeleteRequest(dto.getIndex(),
				dto.getType(), dto.getId());
		
		// Execute unindexing request.
		esClient.getClient().delete(req);
	}

}
