package com.eurodyn.qlack2.fuse.aaa;

import com.eurodyn.qlack2.fuse.aaa.conf.ITTestConf;
import com.eurodyn.qlack2.fuse.aaa.tests.AccountingServiceImplTest;
import com.eurodyn.qlack2.fuse.aaa.tests.JSONConfigServiceImplTest;
import com.eurodyn.qlack2.fuse.aaa.tests.OpTemplateServiceImplTest;
import com.eurodyn.qlack2.fuse.aaa.tests.OperationServiceImplTest;
import com.eurodyn.qlack2.fuse.aaa.tests.ResourceServiceImplTest;
import com.eurodyn.qlack2.fuse.aaa.tests.UserGroupServiceImplTest;
import com.eurodyn.qlack2.fuse.aaa.tests.UserServiceImplTest;
import com.eurodyn.qlack2.fuse.aaa.tests.VerificationServiceImplTest;
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
  AccountingServiceImplTest.class,
  UserServiceImplTest.class,
  VerificationServiceImplTest.class,
  UserGroupServiceImplTest.class,
  OperationServiceImplTest.class,
  ResourceServiceImplTest.class,
  OpTemplateServiceImplTest.class,
  JSONConfigServiceImplTest.class
})
public class FuseAAAIntegrationTests {

  /**
   * JUL reference
   */
  private final static Logger LOGGER = Logger.getLogger(FuseAAAIntegrationTests.class.getName());

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