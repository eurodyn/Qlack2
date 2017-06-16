package com.eurodyn.qlack2.fuse.search.impl.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

/**
 * A client to communicate with ES. This client is using the {@link RestClient} implementation
 * of the ES Java client. This client is configured using Blueprint and can then be injected to
 * services requiring access to ES.
 *
 * //TODO support credentials as well as SSL: https://qbox.io/blog/rest-calls-new-java-elasticsearch-client-tutorial
 */
public class ESClient {

  private static final Logger LOGGER = Logger.getLogger(ESClient.class.getName());
  /** A comma-separated list of ES hosts in the form of protocol1:host1:port1,protocol2:host2:port2. */
  private String esHosts;

  /** ES username */
  private String esUsername;

   /** ES password */
  private String esPassword;

  /** Enable or disable hostname verification. Only applies when https is used to communicate with elasticsearch. Must be false to disable hostname verification. */
  private String verifyHostName;

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

  public void setVerifyHostName(String verifyHostName) {
	  this.verifyHostName = verifyHostName;
  }

  /** Initialiser for this singleton instance */
  public void init() {
    LOGGER.log(Level.CONFIG, "Initialising connection to ES: {0}", esHosts);

    /** Process Http hosts for ES */
    final HttpHost[] httpHosts = Arrays.stream(esHosts.split(",")).map(host ->
      new HttpHost(host.split(":")[1], Integer.parseInt(host.split(":")[2]), host.split(":")[0])
    ).collect(Collectors.toList()).toArray(new HttpHost[esHosts.split(",").length]);

    client = RestClient
    	.builder(httpHosts)
    	.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {

			@Override
			public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
				if (StringUtils.isNotEmpty(esUsername) && StringUtils.isNotEmpty(esPassword)) {
					final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
					credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(esUsername, esPassword));

					httpClientBuilder = httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
				}

				if ("false".equals(verifyHostName)) {
					httpClientBuilder = httpClientBuilder.setSSLHostnameVerifier(new HostnameVerifier() {

						@Override
						public boolean verify(String hostname, SSLSession session) {
							return true;
						}
					});
				}

				return httpClientBuilder;
			}
    	})
    	.build();
  }

  	/**
	 * Default shutdown hook.
	 *
	 * @throws IOException
	 *             If client can not be closed.
	 */
  public void shutdown() throws IOException {
    LOGGER.log(Level.CONFIG, "Shutting down connection to ES.");
    client.close();
  }

  	/**
	 * Returns the client.
	 *
	 * @return A {@link RestClient} instance.
	 */
  public RestClient getClient() {
    return client;
  }
}
