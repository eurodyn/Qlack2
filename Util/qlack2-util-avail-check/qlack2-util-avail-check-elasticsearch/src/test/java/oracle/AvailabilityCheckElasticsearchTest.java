package com.eurodyn.qlack2.util.availcheck.oracle;

import com.eurodyn.qlack2.util.availcheck.api.AvailabilityCheck;
import com.eurodyn.qlack2.util.docker.DockerContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

public class AvailabilityCheckElasticsearchTest {
  private final static String ES_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch:5.4.1";
  private final static String ES_URL = "127.0.0.1:50124";
  private final static String ES_USERNAME = "elastic";
  private final static String ES_PASSWORD = "changeme";
  private static String containerId;

  @BeforeClass
  public static void setup() {
    containerId = DockerContainer.builder()
      .withImage(ES_IMAGE)
      .withPort("50124/tcp", "9200/tcp")
      .withEnv("http.host", "0.0.0.0")
      .withEnv("transport.host", "127.0.0.1")
      .withName("TEST-" + UUID.randomUUID())
      .run();
    Assert.assertNotNull(containerId);
  }

  @AfterClass
  public static void teardown() {
    DockerContainer.builder().withId(containerId).clean();
  }

  @Test
  public void isAvailable() throws Exception {
    AvailabilityCheck availabilityCheckElasticsearch = new AvailabilityCheckElasticsearch();
    Assert.assertTrue(availabilityCheckElasticsearch
      .isAvailable(ES_URL, ES_USERNAME, ES_PASSWORD, 30000, 3000, null));
  }

}