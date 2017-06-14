package com.eurodyn.qlack2.fuse.search.impl.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
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
  /** A comma-separated list of ES hosts in the form of host1:port1,host2:port2. */
  private String esHosts;

  /** ES username */
  private String esUsername;

   /** ES password */
  private String esPassword;

  /** The client to ES */
  private RestClient client;

  public void setEsUsername(String esUsername) {
    this.esUsername = esUsername;
  }

  public void setEsPassword(String esPassword) {
    this.esPassword = esPassword;
  }

  public void setEsHosts(String esHosts) {
    this.esHosts = esHosts;
  }

  /** Initialiser for this singleton instance */
  public void init() {
    LOGGER.log(Level.CONFIG, "Initialising connection to ES: {0}", esHosts);

    /** Process Http hosts for ES */
    final HttpHost[] httpHosts = Arrays.stream(esHosts.split(",")).map(host ->
      new HttpHost(host.split(":")[0], Integer.parseInt(host.split(":")[1]))
    ).collect(Collectors.toList()).toArray(new HttpHost[esHosts.split(",").length]);

    if (StringUtils.isNotEmpty(esUsername) && StringUtils.isNotEmpty(esPassword)) {
      final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
      credentialsProvider.setCredentials(AuthScope.ANY,
        new UsernamePasswordCredentials(esUsername, esPassword));
      client = RestClient
        .builder(httpHosts)
        .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
          @Override
          public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
            return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
          }
        }).build();
    } else {
      client = RestClient.builder(httpHosts).build();
    }
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
