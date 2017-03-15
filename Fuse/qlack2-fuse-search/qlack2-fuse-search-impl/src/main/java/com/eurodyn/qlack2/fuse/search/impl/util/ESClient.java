package com.eurodyn.qlack2.fuse.search.impl.util;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A client to communicate with ES. This client is using the
 * {@link TransportClient} implementation of the ES Java client. This client is
 * configured using Blueprint and can then be injected to services requiring
 * access to ES. 
 */
public class ESClient {
	private static final Logger LOGGER = Logger.getLogger(ESClient.class.getName());
	// A comma-separated list of ES hosts in the form of
	// host1:port1,host2:port2.
	private String esHosts;

	// The name of the cluster to join.
	private String esClusterName;

	public void setEsHosts(String esHosts) {
		this.esHosts = esHosts;
	}

	// The transport client to ES.
	private TransportClient client;

	/**
	 * @param esClusterName
	 *            the esClusterName to set
	 */
	public void setEsClusterName(String esClusterName) {
		this.esClusterName = esClusterName;
	}

	// Initialiser for this singleton instance.
	public void init() {
		LOGGER.log(Level.CONFIG, "Initialising connection to ES.");
		try {
			LOGGER.log(Level.CONFIG, "Cluster name: {0}.", esClusterName);
			Settings settings = ImmutableSettings.settingsBuilder()
					.put("cluster.name", esClusterName)
					.put("client.transport.sniff", true)
					.classLoader(Settings.class.getClassLoader())
					.build();
			client = new TransportClient(settings);
			for (String es : esHosts.split(",")) {
				String host = es.split(":")[0];
				int port = Integer.parseInt(es.split(":")[1]);
				LOGGER.log(Level.CONFIG, "Adding transport {0}:{1}", new Object[]{host, String.valueOf(port)});
				client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
			}
		} catch (UnknownHostException e) {
			LOGGER.log(Level.SEVERE, "Could not initialise connection to ES", e);
		}
	}

	// Default shutdown hook.
	public void shutdown() {
		LOGGER.log(Level.CONFIG, "Shutting down connection to ES.");
		client.close();
	}

	// Returns the client.
	public TransportClient getClient() {
		return client;
	}
}
