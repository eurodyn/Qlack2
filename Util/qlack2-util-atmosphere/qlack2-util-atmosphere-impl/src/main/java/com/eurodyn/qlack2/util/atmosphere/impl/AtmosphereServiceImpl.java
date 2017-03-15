package com.eurodyn.qlack2.util.atmosphere.impl;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereServlet;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.cpr.BroadcasterLifeCyclePolicy;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import com.eurodyn.qlack2.util.atmosphere.api.AtmosphereService;
import com.eurodyn.qlack2.util.atmosphere.api.exception.QJSONSerialisationException;
import com.eurodyn.qlack2.util.atmosphere.api.message.AtmosphereMessage;
import com.eurodyn.qlack2.util.atmosphere.impl.handler.AtmosphereHandler;
import com.eurodyn.qlack2.util.atmosphere.impl.interceptor.AtmosphereSecurityInterceptor;
import com.eurodyn.qlack2.util.atmosphere.impl.util.Constants;
import com.eurodyn.qlack2.util.cluster.core.QlackClusterListener;
import com.eurodyn.qlack2.util.cluster.core.QlackClusterService;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AtmosphereServiceImpl extends QlackClusterListener implements AtmosphereService {
	private static final Logger LOGGER = Logger.getLogger(AtmosphereServiceImpl.class.getName());
	private final ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL);
	private AtmosphereServlet atmosphereServlet;
	private HttpService http;
	private String alias;
	private AtmosphereHandler handler;
	private AtmosphereSecurityInterceptor securityInterceptor;
	private QlackClusterService clusterService;

	public void setHandler(AtmosphereHandler handler) {
		this.handler = handler;
	}

	public void setClusterService(QlackClusterService clusterService) {
		this.clusterService = clusterService;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setSecurityInterceptor(AtmosphereSecurityInterceptor securityInterceptor) {
		this.securityInterceptor = securityInterceptor;
	}

	public void setHttp(HttpService http) {
		this.http = http;
	}

	private Broadcaster getBroadcasterForTopic(String topic, boolean create) {
		BroadcasterFactory broadcasterFactory = atmosphereServlet.framework().getBroadcasterFactory();
		LOGGER.log(Level.FINEST, "Looking up broadcaster for topic {0}.", topic);
		Broadcaster broadcaster = broadcasterFactory.lookup(topic);
		LOGGER.log(Level.FINEST, "Broadcaster for topic {0} found: {1}", new Object[] { topic, broadcaster });
		synchronized (topic) {
			if (broadcaster == null && create) {
				LOGGER.log(Level.FINEST, "Creating new broadcaster for topic {0}.", topic);
				broadcaster = broadcasterFactory.get(topic);
				// The Broadcaster is emptied when no resources are attached to
				// it. Note that the Broadcaster should not be destroyed (by
				// EMPTY_DESTROY) as in long-polling we need to allow the
				// AtmosphereResourceStateRecovery to re-bind the AtmosphereResource
				// of the client to the Broadcasters it was bound before each
				// new GET request (so that the user is automatically re-subscribed
				// to the topics he/she was subscribed before the long-polling
				// request timed-out and a new one was issued).
				broadcaster.setBroadcasterLifeCyclePolicy(BroadcasterLifeCyclePolicy.EMPTY);
			}
		}

		return broadcaster;
	}

	public void init() {
		LOGGER.log(Level.CONFIG, "Registering AtmosphereServlet under: {0}.", alias);
		// Create the Atmosphere servlet to configure it.
		atmosphereServlet = new AtmosphereServlet();

		// Provide servlet default configuration.
		Dictionary<String, String> props = new Hashtable<>();
		props.put("alias", alias);
		props.put("async-supported", "true");
		props.put("load-on-startup", "0");
		props.put("org.atmosphere.interceptor.HeartbeatInterceptor.clientHeartbeatFrequencyInSeconds", "10");

		// Do not send usage statistics to Atmosphere's developers.
		props.put("org.atmosphere.cpr.AtmosphereFramework.analytics", "false");

		// Share a thread pool between broadcasters (helpful when lots of
		// broadcasters are created).
		props.put("org.atmosphere.cpr.broadcaster.shareableThreadPool", "true");

		// The max number of threads per broadcaster.
		props.put("org.atmosphere.cpr.broadcaster.maxProcessingThreads", "10");
		props.put("org.atmosphere.cpr.broadcaster.maxAsyncWriteThreads", "10");

		props.put("org.atmosphere.cpr.broadcasterCacheClass", "org.atmosphere.cache.UUIDBroadcasterCache");

		// Add default Atmosphere interceptors.
		props.put("org.atmosphere.cpr.AtmosphereInterceptor",
				"org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor"
						+ ",org.atmosphere.client.TrackMessageSizeInterceptor"
						+ ",org.atmosphere.interceptor.AtmosphereResourceStateRecovery");
						// +
						// "org.atmosphere.interceptor.SuspendTrackerInterceptor");

		// Create and register the default handler for Atmosphere.
		atmosphereServlet.framework().addAtmosphereHandler(alias, handler);

		// Create and register the default security interceptor.
		if (securityInterceptor != null) {
			atmosphereServlet.framework().interceptor(securityInterceptor);
		} else { 
			LOGGER.log(Level.WARNING, "No security interceptor defined.");
		}

		// Register this class as a cluster listener for Atmosphere messages.
		clusterService.addListener(this, Constants.DEFAULT_ATMOSPHERE_TOPIC);

		// Register the servlet in OSGi.
		try {
			http.registerServlet(alias, atmosphereServlet, props, null);
		} catch (ServletException | NamespaceException e) {
			LOGGER.log(Level.SEVERE, "Could not register AtmosphereServlet.", e);
		}
	}

	public void destroy() {
		http.unregister(alias);
	}

	@Override
	public AtmosphereFramework getFramework() {
		return atmosphereServlet.framework();
	}

	private String toJSON(AtmosphereMessage msg) {
		String msgToString;
		try {
			msgToString = mapper.writeValueAsString(msg);
		} catch (IOException e) {
			throw new QJSONSerialisationException("Could not serialise message to JSON");
		}

		return msgToString;
	}

	@Override
	// Receive messages from the cluster.
	public void onMessage(String msg) {
		LOGGER.log(Level.FINEST, "Got a message from the cluster: {0}.", msg);

		try {
			AtmosphereMessage atmosphereMessage = mapper.readValue(msg, AtmosphereMessage.class);
			Broadcaster broadcaster = getFramework().getBroadcasterFactory().lookup(atmosphereMessage.getTopic());
			if (broadcaster != null) {
				if (CollectionUtils.isNotEmpty(broadcaster.getAtmosphereResources())) {
					LOGGER.log(Level.FINEST, "Broadcasting message: {0}.", msg);
					broadcaster.broadcast(msg);
				} else {
					LOGGER.log(Level.FINEST, "No local subscribers for topic {0}, broadcasting cancelled.",
							atmosphereMessage.getTopic());
				}
			} else {
				LOGGER.log(Level.FINEST, "No local broadcasters for topic {0}, broadcasting cancelled.",
						atmosphereMessage.getTopic());
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Could not deserialise cluster message: {0}.", msg);
		}
	}

	@Override
	public void publish(AtmosphereMessage msg) {
		// Publish the message to the cluster, so that all Atmosphere engines
		// pick it up. Note that the message's topic and the topic on which
		// the message is published on the cluster are not the same. The message
		// topic can be anything (its interpretation is up to the
		// end-application receiving this message) whereas the cluster topic is
		// the channel on which the remote Atmosphere listener expects messages
		// to deliver them to its local clients (which is always set to
		// /atmosphere).
		// Note: Although Hazelcast can deliver Objects by serialising them
		// itself, we opt to convert the message to JSON to avoid
		// deserialisation classpath issues.
		if (StringUtils.isNotBlank(msg.getTopic())) {
			clusterService.publish(Constants.DEFAULT_ATMOSPHERE_TOPIC, toJSON(msg));
		} else {
			LOGGER.log(Level.WARNING, "Cancelled publishing to an empty topic name of message: {0}.", toJSON(msg));
		}
	}

	@Override
	public void subscribe(String userID, String topic) {
		List<String> atmosphereUUIDs = handler.getUUIDForUserID(userID);
		if (CollectionUtils.isNotEmpty(atmosphereUUIDs)) {
			LOGGER.log(Level.FINEST, "Atmosphere resources found for userID {0}: {1}",
					new Object[] { userID, atmosphereUUIDs.size() });
			for (Iterator<String> i = atmosphereUUIDs.listIterator(); i.hasNext();) {
				String uuid = i.next();
				LOGGER.log(Level.FINEST, "\t {0}", uuid);
				AtmosphereResource atmosphereResource = atmosphereServlet.framework().atmosphereFactory().find(uuid);
				if (atmosphereResource != null) {
					Broadcaster broadcaster = getBroadcasterForTopic(topic, true);
					if (!broadcaster.getAtmosphereResources().contains(atmosphereResource)) {
						broadcaster.addAtmosphereResource(atmosphereResource);
					} else {
						LOGGER.log(Level.WARNING,
								"Atmosphere UUID {0} associated with user {1} tried "
										+ "to subscribe to an already subscribed topic " + "{2}.",
								new Object[] { uuid, userID, topic });
					}
				} else {
					LOGGER.log(Level.FINEST, "\t\t [seems to be no longer valid, user probably got "
							+ "disconnected without Atmosphere having a chance to "
							+ "detect it. For good housekeeping this resource will " + "be removed from the map now.]");
					i.remove();
				}
			}
		} else {
			LOGGER.log(Level.WARNING,
					"Did not find an AtmosphereResource for userID {0} while " + "trying to subscribe to topic {1}.",
					new Object[] { userID, topic });
		}

	}

	@Override
	public void unsubscribe(String userID, String topic) {
		List<String> atmosphereUUIDs = handler.getUUIDForUserID(userID);
		if (CollectionUtils.isNotEmpty(atmosphereUUIDs)) {
			LOGGER.log(Level.FINEST, "Atmosphere resources found for userID {0}: {1}",
					new Object[] { userID, atmosphereUUIDs.size() });
			for (String uuid : atmosphereUUIDs) {
				AtmosphereResource atmosphereResource = atmosphereServlet.framework().atmosphereFactory().find(uuid);
				Broadcaster broadcaster = getBroadcasterForTopic(topic, false);
				if (broadcaster != null) {
					if (broadcaster.getAtmosphereResources().contains(atmosphereResource)) {
						broadcaster.removeAtmosphereResource(atmosphereResource);
					} else {
						LOGGER.log(Level.WARNING,
								"Atmosphere UUID {0} associated with user {1} tried "
										+ "to unsubscribe from non-subscribed topic {2}.",
								new Object[] { uuid, userID, topic });
					}
				}
			}
		} else {
			LOGGER.log(Level.WARNING, "Did not find an AtmosphereResource for userID {0} while "
					+ "trying to unsubscribe from topic {1}.", new Object[] { userID, topic });
		}
	}

}