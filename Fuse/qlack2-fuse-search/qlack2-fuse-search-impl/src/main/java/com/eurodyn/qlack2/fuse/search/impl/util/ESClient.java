package com.eurodyn.qlack2.fuse.search.impl.util;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.transport.TransportClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A client to communicate with ES. This client is using the {@link TransportClient} implementation
 * of the ES Java client. This client is configured using Blueprint and can then be injected to
 * services requiring access to ES.
 *
 * //TODO support credentials as well as SSL: https://qbox.io/blog/rest-calls-new-java-elasticsearch-client-tutorial
 */
public class ESClient {

  private static final Logger LOGGER = Logger.getLogger(ESClient.class.getName());
  // A comma-separated list of ES hosts in the form of
  // host1:port1,host2:port2.
  private String esHosts;

  public void setEsHosts(String esHosts) {
    this.esHosts = esHosts;
  }

  // The transport client to ES.
  private RestClient client;

  // Initialiser for this singleton instance.
  public void init() {
    LOGGER.log(Level.CONFIG, "Initialising connection to ES: {0}", esHosts);
    final RestClientBuilder restClientBuilder = RestClient.builder(
      Arrays.stream(esHosts.split(",")).map(host ->
        new HttpHost(host.split(":")[0], Integer.parseInt(host.split(":")[1]))
      ).collect(Collectors.toList()).toArray(new HttpHost[esHosts.split(",").length]));
    client = restClientBuilder.build();
  }

  /**
   * Default shutdown hook.
   */
  public void shutdown() throws IOException {
    LOGGER.log(Level.CONFIG, "Shutting down connection to ES.");
    client.close();
  }

  /**
   * Returns the client.
   */
  public RestClient getClient() {
    return client;
  }
}
