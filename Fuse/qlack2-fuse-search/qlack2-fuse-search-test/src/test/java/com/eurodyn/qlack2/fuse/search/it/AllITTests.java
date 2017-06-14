package com.eurodyn.qlack2.fuse.search.it;

import com.eurodyn.qlack2.util.availcheck.api.AvailabilityCheck;
import com.eurodyn.qlack2.util.availcheck.elasticsearch.AvailabilityCheckElasticsearch;
import com.eurodyn.qlack2.util.docker.DockerContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.UUID;

/**
 * @author European Dynamics SA
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
  AdminServiceImplTest.class
})
public class AllITTests {

  private final static String ES_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch:5.4.1";
  private final static String ES_URL = "127.0.0.1:50124";
  private final static String ES_USERNAME = "elastic";
  private final static String ES_PASSWORD = "changeme";
  private static String containerId;

  @BeforeClass
  public static void beforeClass() throws ClassNotFoundException {
    /** Startup an ES container */
    containerId = DockerContainer.builder()
      .withImage(ES_IMAGE)
      .withPort("50124/tcp", "9200/tcp")
      .withEnv("http.host", "0.0.0.0")
      .withEnv("transport.host", "127.0.0.1")
      .withName("TEST-" + UUID.randomUUID())
      .run();
    Assert.assertNotNull(containerId);

    /** Wait for the ES container to become accessible */
    AvailabilityCheck check = new AvailabilityCheckElasticsearch();
    Assert.assertTrue(check.isAvailable(ES_URL, ES_USERNAME, ES_PASSWORD, 60000, 3000, null));
  }

  @AfterClass
  public static void afterClass() {
    DockerContainer.builder().withId(containerId).clean();
  }
}