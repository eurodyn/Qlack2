package com.eurodyn.qlack2.fuse.search;

import com.eurodyn.qlack2.common.util.net.NetUtils;
import com.eurodyn.qlack2.fuse.search.tests.ServiceImplTest;
import com.eurodyn.qlack2.util.availcheck.api.AvailabilityCheck;
import com.eurodyn.qlack2.util.availcheck.elasticsearch.AvailabilityCheckElasticsearch;
import com.eurodyn.qlack2.util.docker.DockerContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author European Dynamics SA
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
  ServiceImplTest.class
})
public class FuseSearchIntegrationTests {
  // JUL reference.
  private final static Logger LOGGER = Logger.getLogger(FuseSearchIntegrationTests.class.getName());

  // The prefix name of the test container to start.
  public static final String TEST_CONTAINER_PREFIX = "TEST-qlack-";

  // The ID of the container created with ES.
  private static String esContainerId;

  private final static String ES_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch:5.4.1";
  public static String esUrl;
  private final static String ES_USERNAME = "elastic";
  private final static String ES_PASSWORD = "changeme";
  private static int esPort;

  static {
    try {
      esPort =  NetUtils.getAvailablePort();
      esUrl =  "127.0.0.1:" + esPort;
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Could not allocate random port.", e);
      System.exit(1);
    }
  }


  @BeforeClass
  public static void beforeClass() throws ClassNotFoundException {
    // Startup an ES container.
    esContainerId = DockerContainer.builder()
      .withImage(ES_IMAGE)
      .withPort(esPort + "/tcp", "9200/tcp")
      .withEnv("http.host", "0.0.0.0")
      .withEnv("transport.host", "127.0.0.1")
      .withName(TEST_CONTAINER_PREFIX + UUID.randomUUID())
      .run();
    Assert.assertNotNull(esContainerId);

    // Wait for the ES container to become accessible.
    AvailabilityCheck check = new AvailabilityCheckElasticsearch();
    Assert.assertTrue(check.isAvailable(esUrl, ES_USERNAME, ES_PASSWORD, 60000, 3000, null));
  }

  @AfterClass
  public static void afterClass() {
    DockerContainer.builder().withId(esContainerId).clean();
  }
}