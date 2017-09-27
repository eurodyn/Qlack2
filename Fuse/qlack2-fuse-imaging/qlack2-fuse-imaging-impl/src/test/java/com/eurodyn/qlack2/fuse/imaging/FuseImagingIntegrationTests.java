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

  @BeforeClass
  public static void beforeClass()
    throws ClassNotFoundException, IllegalAccessException, InstantiationException {
  }

  @AfterClass
  public static void afterClass() {
  }
}