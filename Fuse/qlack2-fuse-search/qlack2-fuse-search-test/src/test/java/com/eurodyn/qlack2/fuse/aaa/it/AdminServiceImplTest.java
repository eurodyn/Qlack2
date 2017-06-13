package com.eurodyn.qlack2.fuse.aaa.it;

import com.eurodyn.qlack2.fuse.search.api.AdminService;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;

import java.util.UUID;

/**
 * Created by nassos on 12/6/17.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class AdminServiceImplTest extends ITTestConf {
  @Inject
  @Filter(timeout = 1200000)
  AdminService adminService;

  @Test
  public void testCreateIndex() {
    String testIndexName = UUID.randomUUID().toString();

    Assert.assertTrue(adminService.createIndex(testIndexName));

    Assert.assertFalse(adminService.createIndex(testIndexName));
  }

}
