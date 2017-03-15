/*
* Copyright 2015 EUROPEAN DYNAMICS SA <info@eurodyn.com>
*
* Licensed under the EUPL, Version 1.1 only (the "License").
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
* https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and
* limitations under the Licence.
*/

/**
 * A module to provide front-end connectivity to the underling Atmosphere
 * implementation.
 *
 */
angular.module('QAtmosphere', [])
.provider('QAtmosphereSrv', function() {
	// Configuration properties.
	var config = {
		// The content of the heartbeat message.
		heartbeatMessageBody: "X",

		// Whether to fire events when receiving a heartbeat message.
		ignoreHeartBeatMessage: true,

		// The prefix to use when emitting events.
		emitAtmoPrefix: "ATMO_",

		// The URL at which your custom Atmosphere subscription management resides.
		// This URL is called by this service on subscribe/unsubscribe events, thus
		// allowing your custom business logic to decide whether the request is
		// valid or not. The calls are HTTP GET requests to the following paths:
		// subscribe?topic=foo, unsubscribe?topic=bar
		subscriptionManagementServiceURL: "",

		// The amount of time (msec) this service waits before it tries to resubscribe
		// to previously subscribed topics onReopen.
		resubscriptionDelay: 5000,

		// How much time (msec) apart each subscription event is taking place
		// (to throttle concurrent subscriptions)
		subscriptionsThrottling: 1000,

		// A function to be called to fetch the security information of your
		// application to be attached to the Atmosphere messages. This is used
		// so that your code in the back-end knows the user behind each request.
		securityFunction: function() {
		},

		// The header name under which the output of the securityFunction will
		// be attached.
		securityHederName: "",

		// The log level (debug, info, error)
		logLevel: "error",

		// The primary transport to try.
		primaryTransport: "websocket",

		// The fallback transport to try if primary can not connect.
		fallbackTransport: "long-polling",

		// The URL at which Atmosphere server-side filter listens on.
		atmosphereURL: "./atmosphere"
	}

	this.setHeartbeatMessageBody = function(val) {
		config.heartbeatMessageBody = val;
	}

	this.setIgnoreHeartBeatMessage = function(val) {
		config.ignoreHeartBeatMessage = val;
	}

	this.setEmitAtmoPrefix = function(val) {
		config.emitAtmoPrefix = val;
	}

	this.setSubscriptionManagementServiceURL = function(val) {
		config.subscriptionManagementServiceURL = val;
	}

	this.setResubscriptionDelay = function(val) {
		config.resubscriptionDelay = val;
	}

	this.setSubscriptionsThrottling = function(val) {
		config.subscriptionsThrottling = val;
	}

	this.setSecurityFunction = function(val) {
		config.securityFunction = val;
	}

	this.setSecurityHederName = function(val) {
		config.securityHederName = val;
	}

	this.setLogLevel = function(val) {
		config.logLevel = val;
	}

	this.setPrimaryTransport = function(val) {
		config.primaryTransport = val;
	}

	this.setFallbackTransport = function(val) {
		config.fallbackTransport = val;
	}

	this.setAtmosphereURL = function(val) {
		config.atmosphereURL = val;
	}

	this.$get = function($rootScope, $http) {
		return new QAtmosphereService(config, $rootScope, $http);
	}
});

function QAtmosphereService(config, $rootScope, $http) {
	// A reference to the Atmosphere framework (that should be loaded from
	// the web application using this service).
	var socket;

	// The socket created in Atmosphere to communicate with the back-end.
	var subSocket;

	// An index of active subscriptions, so that they can be reinstantiated
	// in case the browser reconnects. Note that if you perform a subscription/
	// unsubscription on the server-side without calling the respective methods
	// of this service, such events will not be tracked.
	var topics = new Array();

	var request = {
		contentType : "application/json",
		logLevel : "",
		transport : "",
		fallbackTransport: "",
		url: "",
		trackMessageLength : true,
		reconnectInterval : 10000,
		timeout: -1,
		maxReconnectOnClose: 70,
		headers: {}
	};

	var debug = function(title, obj1, obj2) {
		if (request.logLevel == "debug") {
			if (obj1 == undefined) {
				obj1 = "";
			}
			if (obj2 == undefined) {
				obj2 = "";
			}
			console.debug(new Date() + " AtmosphereSrv:", title, obj1, obj2);
		}
	};

	// Invoked when the connection gets opened.
	request.onOpen = function(response) {
		debug ("onOpen", response);
		$rootScope.$emit(config.emitAtmoPrefix + "ON_OPEN");
	};

	// Invoked when the connection gets closed.
	request.onClose = function(response) {
		debug ("onClose", response);
		$rootScope.$emit(config.emitAtmoPrefix + "ON_CLOSE");
		for (var i = 0; i < topics.length; i++) {
			unsubscribe(topics[i]);
		}
		topics = new Array();

	};

	// Invoked when a message gets delivered.
	request.onMessage = function (response) {
		debug("onMessage " + response.responseBody);
		// Ignore heartbeats.
		if (response !== undefined && response.responseBody !== config.heartbeatMessageBody) {
			$rootScope.$emit(config.emitAtmoPrefix + "ON_MESSAGE", response.responseBody);
		}
	};

	// Invoked when an unexpected error occurs.
	request.onError = function(response) {
		debug("onError", response);
		$rootScope.$emit(config.emitAtmoPrefix + "ON_ERROR");
	};

	// Invoked when the client reconnects to the server.
	request.onReconnect = function(request, response) {
		debug("onReconnect", request, response);
		$rootScope.$emit(config.emitAtmoPrefix + "ON_RECONNECT");
	};

	// Invoked when the request.transport value is polling and a response was
	// sent back by the server.
	request.onMessagePublished = function(request, response) {
		debug("onMessagePublisher");
		$rootScope.$emit(config.emitAtmoPrefix + "ON_MESSAGE_PUBLISHED");
	}

	// Invoked when the request.timeout value expire. An application may decide
	// to reconnect in that case.
	request.onClientTimeout = function(request) {
		debug ("onClientTimeout", request);
		$rootScope.$emit(config.emitAtmoPrefix + "ON_ClIENT_TIMEOUT");k
	};

	// Invoked when the request.transport fail because it is not supported by
	// the client or the server. You can reconfigure a new transport
	// (request.transport) from that function.
	request.onTransportFailure = function(errorMsg, request) {
		debug ("onTransportFailure", errorMsg, request);
		$rootScope.$emit(config.emitAtmoPrefix + "ON_TRANSPORT_FAILURE");
	};

	request.onLocalMessage = function(response) {
		debug ("onLocalMessage", response);
		$rootScope.$emit(config.emitAtmoPrefix + "ON_LOCAL_MESSAGE");
	};

	request.onFailureToReconnect = function(request, response) {
		debug ("onFailureToReconnect", request, response);
		$rootScope.$emit(config.emitAtmoPrefix + "ON_FAILURE_TO_RECONNECT");
	};

	request.onOpenAfterResume = function(request) {
		debug ("onOpenAfterResume", request);
		$rootScope.$emit(config.emitAtmoPrefix + "ON_OPEN_AFTER_RESUME");
	};

	request.onReopen = function(event) {
		debug ("onReopen", event);
		$rootScope.$emit(config.emitAtmoPrefix + "ON_REOPEN");
		debug("Will try to resubscribe to previously subscribed topics after a " +
				"delay of " + config.resubscriptionDelay + "msec:");
		for (var i = 0; i < topics.length; i++) {
			debug("\t " + topics[i]);
		}
		// Try to re-established previous subscriptions. Because Atmosphere engine
		// on the server-side may be up and running before the web application
		// has completed deployment, resubscribe to topics with a delay.
		var delay = config.resubscriptionDelay;
		for (var i = 0; i < topics.length; i++) {
			(function(i){
				setTimeout(function() {
					debug("Trying to re-establish subscription to topic " + topics[i] + " due to reconnect.");
					subscribe(topics[i], true);
				}, delay);
			})(i);
			delay += config.subscriptionsThrottling;
		}
	};

	var subscribe = function(topic, isOnReopen) {
		if (!isOnReopen) {
			topics.push(topic);
		}
		return $http.get(config.subscriptionManagementServiceURL + "/subscribe?topic=" + topic);
	};
	var unsubscribe = function(topic) {
		topics.splice($.inArray(topic, topics), 1);
		return $http.get(config.subscriptionManagementServiceURL + "/unsubscribe?topic=" + topic);
	};

	this.init = function() {
		subSocket = undefined;
		socket = atmosphere;
		
		debug("Setting atmosphereURL: " + config.atmosphereURL);
		request.url = config.atmosphereURL;
		
		debug("Setting logLevel: " + config.logLevel);
		request.logLevel = config.logLevel;
		
		debug("Setting primary transport: " + config.primaryTransport);
		request.transport = config.primaryTransport;
		
		debug("Setting fallback transport: " + config.fallbackTransport);
		request.fallbackTransport = config.fallbackTransport;
		
		debug("Setting subscriptionManagementURL: " + config.subscriptionManagementURL);
		subscriptionManagementServiceURL =  config.subscriptionManagementURL;
		
		getSecurityTicket = config.securityFunction;
		request["headers"][config.securityHederName] = config.securityFunction();
		debug("Setting security ticket: " +
			request["headers"][config.securityHederName] + ", header name: " 
			+ config.securityHederName);
		subSocket = socket.subscribe(request);
		debug("Initialised.");
	},

	this.subscribe = function(topic) {
		return subscribe(topic, false);
	},

	this.unsubscribe = function(topic) {
		return unsubscribe(topic);
	},

	this.getSubscribedTopics = function() {
		return topics;
	},

	this.destroy = function() {
		socket.unsubscribe();
	}

	this.getConfig = function() {
		return config;
	}
};
