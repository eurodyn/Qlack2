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
import com.eurodyn.qlack2.fuse.search.api.dto.IndexingDTO;
import com.eurodyn.qlack2.fuse.search.api.request.CreateIndexRequest;
import com.eurodyn.qlack2.fuse.search.api.request.UpdateMappingRequest;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ServiceImplTest extends ITTestConf {

  @Inject
  AdminService adminService;

  @Inject
  IndexingService indexingService;

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
}
