package com.eurodyn.qlack2.fuse.blog;

import com.eurodyn.qlack2.fuse.blog.conf.ITTestConf;
import com.eurodyn.qlack2.fuse.blog.tests.BlogServiceImplTest;
import com.eurodyn.qlack2.fuse.blog.tests.CategoryServiceImplTest;
import com.eurodyn.qlack2.fuse.blog.tests.CommentServiceImplTest;
import com.eurodyn.qlack2.fuse.blog.tests.LayoutServiceImplTest;
import com.eurodyn.qlack2.fuse.blog.tests.PostServiceImplTest;
import com.eurodyn.qlack2.fuse.blog.tests.TagServiceImplTest;
import com.eurodyn.qlack2.util.availcheck.api.AvailabilityCheck;
import com.eurodyn.qlack2.util.docker.DockerContainer;
import com.eurodyn.qlack2.util.testing.TestingUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  BlogServiceImplTest.class,
  CategoryServiceImplTest.class,
  CommentServiceImplTest.class,
  LayoutServiceImplTest.class,
  PostServiceImplTest.class,
  TagServiceImplTest.class
})
public class FuseBlogIntegrationTests {
  /**
   * JUL reference
   */
  private final static Logger LOGGER = Logger.getLogger(FuseBlogIntegrationTests.class.getName());

  /**
   * The ID of the container created with the database
   */
  private static String dbContainerId;

  @BeforeClass
  public static void beforeClass()
    throws ClassNotFoundException, IllegalAccessException, InstantiationException {

    /** Start the DB container */
    dbContainerId = TestingUtil.startContainer(ITTestConf.testingEnv);
    Assert.assertNotNull(dbContainerId);

    /** Wait for the DB container to become accessible */
    LOGGER.log(Level.INFO, "Waiting for DB to become accessible...");
    AvailabilityCheck dbAvailabilityCheck = (AvailabilityCheck) Class
      .forName(ITTestConf.testingEnv.getDbAvailabilityCheckClass()).newInstance();
    if (!dbAvailabilityCheck
      .isAvailable(ITTestConf.testingEnv.getDbUrl(), ITTestConf.testingEnv.getDbUser(),
        ITTestConf.testingEnv.getDbPassword(),
        ITTestConf.testingEnv.getContainerMaxWait(), ITTestConf.testingEnv.getContainerWaitCycle(),
        (Map) ITTestConf.testingEnv.getContainerEnvParams())) {
      LOGGER.log(Level.SEVERE, "Could not connect to the DB. Tests will be terminated.");
      System.exit(1);
    } else {
      LOGGER.log(Level.INFO, "DB is accessible.");
    }
  }

  @AfterClass
  public static void afterClass() {
    if (dbContainerId != null) {
      DockerContainer.builder().withId(dbContainerId).clean();
    }
  }
}
