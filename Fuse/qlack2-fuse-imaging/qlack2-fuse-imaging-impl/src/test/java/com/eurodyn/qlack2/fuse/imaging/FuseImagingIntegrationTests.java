package com.eurodyn.qlack2.fuse.imaging;

import com.eurodyn.qlack2.fuse.imaging.tests.ImageServiceImplTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.logging.Logger;

/**
 * @author European Dynamics SA
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ImageServiceImplTest.class,
})
public class FuseImagingIntegrationTests {

  /**
   * JUL reference
   */
  private final static Logger LOGGER = Logger.getLogger(FuseImagingIntegrationTests.class.getName());

//  /**
//   * The ID of the container created with the database
//   */
//  private static String dbContainerId;

  @BeforeClass
  public static void beforeClass()
    throws ClassNotFoundException, IllegalAccessException, InstantiationException {

//    /** Start the DB container */
//    dbContainerId = TestingUtil.startContainer(ITTestConf.testingEnv);
//    Assert.assertNotNull(dbContainerId);
//
//    /** Wait for the DB container to become accessible */
//    LOGGER.log(Level.INFO, "Waiting for DB to become accessible...");
//    AvailabilityCheck dbAvailabilityCheck = (AvailabilityCheck) Class
//      .forName(ITTestConf.testingEnv.getDbAvailabilityCheckClass()).newInstance();
//    if (!dbAvailabilityCheck
//      .isAvailable(ITTestConf.testingEnv.getDbUrl(), ITTestConf.testingEnv.getDbUser(),
//        ITTestConf.testingEnv.getDbPassword(),
//        ITTestConf.testingEnv.getContainerMaxWait(), ITTestConf.testingEnv.getContainerWaitCycle(),
//        (Map) ITTestConf.testingEnv.getContainerEnvParams())) {
//      LOGGER.log(Level.SEVERE, "Could not connect to the DB. Tests will be terminated.");
//      System.exit(1);
//    } else {
//      LOGGER.log(Level.INFO, "DB is accessible.");
//    }
  }

  @AfterClass
  public static void afterClass() {
//    if (dbContainerId != null) {
//      DockerContainer.builder().withId(dbContainerId).clean();
//    }
  }
}