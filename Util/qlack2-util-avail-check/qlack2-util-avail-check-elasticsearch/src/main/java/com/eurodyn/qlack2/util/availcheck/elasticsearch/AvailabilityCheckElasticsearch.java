package com.eurodyn.qlack2.util.availcheck.elasticsearch;

import com.eurodyn.qlack2.util.availcheck.api.AvailabilityCheck;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AvailabilityCheckElasticsearch implements AvailabilityCheck {
  /**
   * JUL reference
   */
  private final static Logger LOGGER = Logger
    .getLogger(AvailabilityCheckElasticsearch.class.getName());


  /**
   * Checks whether elasticsearch is up and running and can be queried.
   *
   * @param url The URL of the resource to be checked in the form of host:port.
   */
  @Override
  public boolean isAvailable(String url, String user, String password, long maxWait, long cycleWait,
    Map<String, Object> params) {
    boolean retVal = false;

    try {
      /** Setup the client */
      RestClient restClient;

      /** Setup auth if provided */
      if (StringUtils.isNotEmpty(user) && StringUtils.isNotEmpty(password)) {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
          new UsernamePasswordCredentials(user, password));
        restClient = RestClient
          .builder(new HttpHost(url.split(":")[0], Integer.parseInt(url.split(":")[1])))
          .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
              return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }
          }).build();
      } else {
        restClient = RestClient
          .builder(new HttpHost(url.split(":")[0], Integer.parseInt(url.split(":")[1]))).build();
      }


      long startTime = Instant.now().toEpochMilli();
      while (Instant.now().toEpochMilli() - startTime < maxWait && !retVal) {
        try {
          final Response response = restClient.performRequest("GET", "_cluster/health");
          int statusCode = response.getStatusLine().getStatusCode();
          if (statusCode == 200) {
            retVal = true;
          } else {
            LOGGER.log(Level.FINEST, "Checking Elasticsearch resulted in status code: {0}.",
              statusCode);
          }
        } catch (IOException e) {
          LOGGER.log(Level.INFO, "Could not connect to Elasticsearch", e);
        }
        Thread.sleep(cycleWait);
      }
    } catch (InterruptedException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }

    return retVal;
  }
}