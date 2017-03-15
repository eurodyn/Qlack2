package com.eurodyn.qlack2.util.atmosphere.api;

import org.atmosphere.cpr.AtmosphereFramework;

import com.eurodyn.qlack2.util.atmosphere.api.message.AtmosphereMessage;

public interface AtmosphereService {
	AtmosphereFramework getFramework();
	void publish(AtmosphereMessage msg);
	void subscribe(String userID, String topic);
	void unsubscribe(String userID, String topic);
}
