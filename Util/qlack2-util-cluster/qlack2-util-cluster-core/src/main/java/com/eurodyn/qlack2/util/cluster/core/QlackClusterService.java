package com.eurodyn.qlack2.util.cluster.core;

public interface QlackClusterService {
	String addListener(QlackClusterListener listener, String topic);
	boolean removeListener(String registrationID, String topic);
	void publish(String topic, String message);
}
