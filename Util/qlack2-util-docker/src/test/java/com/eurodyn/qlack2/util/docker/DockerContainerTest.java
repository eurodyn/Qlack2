package com.eurodyn.qlack2.util.docker;

import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class DockerContainerTest {

  @Test
  public void dockerContainerTest() throws Exception {
    final String containerId = DockerContainer.builder()
      .withImage("docker.elastic.co/elasticsearch/elasticsearch:5.4.1")
      .withPort("9900/tcp", "9200/tcp")
      .withEnv("http.host", "0.0.0.0")
      .withEnv("transport.host", "127.0.0.1")
      .withName("TEST-" + UUID.randomUUID())
      .run();

    Assert.assertNotNull(containerId);

    DockerContainer.builder().withId(containerId).clean();
  }

}