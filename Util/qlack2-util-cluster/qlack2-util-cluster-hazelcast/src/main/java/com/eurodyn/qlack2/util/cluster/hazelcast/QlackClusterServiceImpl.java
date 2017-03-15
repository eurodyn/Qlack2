package com.eurodyn.qlack2.util.cluster.hazelcast;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.eurodyn.qlack2.util.cluster.core.QlackClusterListener;
import com.eurodyn.qlack2.util.cluster.core.QlackClusterService;
import com.hazelcast.config.Config;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.MessageListener;

public class QlackClusterServiceImpl implements QlackClusterService {
	private final static Logger LOGGER = Logger
			.getLogger(QlackClusterServiceImpl.class.getName());
	private HazelcastInstance instance;
	private String interfaces;

	public void setInterfaces(String interfaces) {
		this.interfaces = interfaces;
	}

	public void init() {
		LOGGER.log(Level.CONFIG, "Initialising Hazelcast.");
		Config cfg = new Config();
		if (StringUtils.isNotBlank(interfaces)) {
			NetworkConfig nc = new NetworkConfig();
			InterfacesConfig ic = new InterfacesConfig();
			ic.setEnabled(true);
			for (String i : Arrays.asList(interfaces.split(","))) {
				LOGGER.log(Level.CONFIG, "Enabling interface: {0}", i);
				ic.addInterface(i);
				nc.setInterfaces(ic);
			}
			cfg.setNetworkConfig(nc);
		}

		instance = Hazelcast.newHazelcastInstance(cfg);
	}

	public void destroy() {
		LOGGER.log(Level.CONFIG, "Shutting down Hazelcast");
		instance.shutdown();
	}

	@Override
	public void publish(String topic, String message) {
		LOGGER.log(Level.FINEST, "Sending a message to the cluster: {0}.",
				message);
		ITopic<Object> hazelcastTopic = instance.getTopic(topic);
		hazelcastTopic.publish(message);
	}

	@Override
	public String addListener(QlackClusterListener qlc, String topic) {
		return instance.getTopic(topic).addMessageListener(
				new HazelcastMessageListener(qlc));
	}

	@Override
	//TODO This should be checked via stress test for memory leak on
	// HazelcastMessageListener.
	public boolean removeListener(String registrationID, String topic) {
		return instance.getTopic(topic).removeMessageListener(registrationID);
	}
}
