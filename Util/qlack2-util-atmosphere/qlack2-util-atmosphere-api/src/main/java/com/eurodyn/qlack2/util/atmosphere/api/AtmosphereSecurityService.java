package com.eurodyn.qlack2.util.atmosphere.api;

import java.util.Map;

public interface AtmosphereSecurityService {
	boolean isValidRequest(Map<String, String> requestHeaders);
	boolean canSubscribe(String topic, String userID);
	String getUserID(Map<String, String> requestHeaders);
}
