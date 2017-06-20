package com.eurodyn.qlack2.fuse.search.it;

import java.util.UUID;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import com.eurodyn.qlack2.fuse.search.api.AdminService;
import com.eurodyn.qlack2.fuse.search.api.IndexingService;
import com.eurodyn.qlack2.fuse.search.api.SearchService;
import com.eurodyn.qlack2.fuse.search.api.dto.IndexingDTO;
import com.eurodyn.qlack2.fuse.search.api.dto.SearchResultDTO;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QueryMatch;
import com.eurodyn.qlack2.fuse.search.api.dto.queries.QuerySpec;
import com.eurodyn.qlack2.fuse.search.api.request.CreateIndexRequest;
import com.eurodyn.qlack2.fuse.search.api.request.UpdateMappingRequest;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ServiceImplTest extends ITTestConf {

  @Inject
  AdminService adminService;

  @Inject
  IndexingService indexingService;

  @Inject
  SearchService searchService;

  @Test
  public void testCreateIndex() {
    /** Prepare data */
    CreateIndexRequest createIndexRequest = new CreateIndexRequest();
    createIndexRequest.setName(UUID.randomUUID().toString().replace("-", ""));

    /** Perform assertions */
    Assert.assertTrue(adminService.createIndex(createIndexRequest));
    Assert.assertFalse(adminService.createIndex(createIndexRequest));
  }

  @Test
  public void testDeleteIndex() {
	  CreateIndexRequest createIndexRequest = new CreateIndexRequest();
	  createIndexRequest.setName(UUID.randomUUID().toString().replace("-", ""));

	  adminService.createIndex(createIndexRequest);
	  Assert.assertTrue(adminService.deleteIndex(createIndexRequest.getName()));
	  Assert.assertFalse(adminService.deleteIndex(createIndexRequest.getName()));
  }

  @Test
  public void testIndexexists() {
	  String indexName = UUID.randomUUID().toString().replace("-", "");
	  Assert.assertFalse(adminService.indexExists(indexName));

	  CreateIndexRequest createIndexRequest = new CreateIndexRequest();
	  createIndexRequest.setName(indexName);

	  adminService.createIndex(createIndexRequest);
	  Assert.assertTrue(adminService.indexExists(indexName));
  }

  @Test
  public void testUpdateMapping() {
	  String indexName = UUID.randomUUID().toString().replace("-", "");

	  CreateIndexRequest createIndexRequest = new CreateIndexRequest();
	  createIndexRequest.setName(indexName);
	  createIndexRequest.setIndexMapping("{\"type1\": {\"properties\": {\"field1\": {\"type\": \"text\"}}}}");

	  adminService.createIndex(createIndexRequest);

	  UpdateMappingRequest updateMappingRequest = new UpdateMappingRequest();
	  updateMappingRequest.setIndexName(indexName);
	  updateMappingRequest.setTypeName("type1");
	  updateMappingRequest.setIndexMapping("{\"field2\": {\"type\": \"text\"}}");

	  Assert.assertTrue(adminService.updateTypeMapping(updateMappingRequest));
  }

	@Test
	public void testIndexDocument() {
		CreateIndexRequest createIndexRequest = new CreateIndexRequest();
		createIndexRequest.setName(UUID.randomUUID().toString().replace("-", ""));

		/** Perform assertions */
		adminService.createIndex(createIndexRequest);

		TestDocument doc = new TestDocument();
		doc.setName("something");

		IndexingDTO dto = new IndexingDTO();
		dto.setId("1");
		dto.setIndex(createIndexRequest.getName());
		dto.setSourceObject(doc);

		indexingService.indexDocument(dto);
	}

	@Test
	public void testUnindexDocument() {
		CreateIndexRequest createIndexRequest = new CreateIndexRequest();
		createIndexRequest.setName(UUID.randomUUID().toString().replace("-", ""));

		/** Perform assertions */
		adminService.createIndex(createIndexRequest);

		TestDocument doc = new TestDocument();
		doc.setName("something");

		IndexingDTO dto = new IndexingDTO();
		dto.setId("1");
		dto.setIndex(createIndexRequest.getName());
		dto.setSourceObject(doc);

		indexingService.indexDocument(dto);

		indexingService.unindexDocument(dto);
	}

	@Test
	public void testSearchService() {
		// create an index
		String indexName = UUID.randomUUID().toString().replace("-", "");

		CreateIndexRequest createIndexRequest = new CreateIndexRequest();
		createIndexRequest.setName(indexName);

		adminService.createIndex(createIndexRequest);

		// index a document
		TestDocument doc = new TestDocument();
		doc.setName("something");

		IndexingDTO dto = new IndexingDTO();
		dto.setId("1");
		dto.setIndex(indexName);
		dto.setSourceObject(doc);
		// wait for elasticsearch to refresh in order to make the doc searchable.
		dto.setRefresh(true);

		indexingService.indexDocument(dto);

		// QueryMatch
		QuerySpec query = new QueryMatch()
			.setTerm("_all", "something")
			.includeAllSources()
			.setExplain(true)
			.setIndex(indexName);

		SearchResultDTO result = searchService.search(query);

		Assert.assertEquals(1, result.getTotalHits());
		Assert.assertEquals(5, result.getShardsTotal());
		Assert.assertNotNull(result.getSource());
		Assert.assertFalse(result.getSource().isEmpty());
		Assert.assertFalse(result.isHasMore());
		Assert.assertFalse(result.isTimedOut());
		Assert.assertFalse(result.getHits().isEmpty());
	}
}
