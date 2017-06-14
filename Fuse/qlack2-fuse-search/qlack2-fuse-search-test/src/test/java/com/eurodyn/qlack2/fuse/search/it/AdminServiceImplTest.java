package com.eurodyn.qlack2.fuse.search.it;

import com.eurodyn.qlack2.fuse.search.api.AdminService;
import com.eurodyn.qlack2.fuse.search.api.request.CreateIndexRequest;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import java.util.UUID;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class AdminServiceImplTest extends ITTestConf {
  @Inject
  AdminService adminService;

  @Test
  public void testCreateIndex() {
    /** Prepare data */
    CreateIndexRequest createIndexRequest = new CreateIndexRequest();
    createIndexRequest.setName(UUID.randomUUID().toString().replace("-", ""));

    /** Perform assertions */
    Assert.assertTrue(adminService.createIndex(createIndexRequest));
    Assert.assertFalse(adminService.createIndex(createIndexRequest));
  }

}
