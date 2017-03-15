package com.eurodyn.qlack2.util.cluster.dummy;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eurodyn.qlack2.util.cluster.core.QlackClusterListener;
import com.eurodyn.qlack2.util.cluster.core.QlackClusterService;

public class QlackClusterServiceImpl implements QlackClusterService {
	private final static Logger LOGGER = Logger
			.getLogger(QlackClusterServiceImpl.class.getName());
	private Map<String, QlackClusterListener> localListeners =
			new HashMap<>();

	public void init() {
	}

	public void destroy() {
	}

	@Override
	public String addListener(QlackClusterListener listener, String topic) {
		localListeners.put(topic, listener);
		return topic;
	}

	@Override
	public boolean removeListener(String registrationID, String topic) {
		return localListeners.remove(topic) != null;
	}

	@Override
	public void publish(String topic, String message) {
		QlackClusterListener qlackClusterListener = localListeners.get(topic);
		if (qlackClusterListener == null) {
			LOGGER.log(Level.WARNING, "No listener registered for topic: {0}", topic);
		} else {
			qlackClusterListener.onMessage(message);
		}
	}

}
