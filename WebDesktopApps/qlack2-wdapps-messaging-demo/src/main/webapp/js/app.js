var app = angular.module("demoMsg", ["QNgPubSub", "QAtmosphere"]);

app.config(["QAtmosphereSrvProvider", function(QAtmosphereSrvProvider) {
	// Setup Atmosphere client.
	QAtmosphereSrvProvider.setLogLevel("debug");
	QAtmosphereSrvProvider.setAtmosphereURL = "/atmosphere";
	//QAtmosphereSrvProvider.setSubscriptionManagementServiceURL("/atmosphereXXX");
	QAtmosphereSrvProvider.setSecurityFunction(function() {
		return "demo-ticket";
	});
	QAtmosphereSrvProvider.setSecurityHederName("X-DEMO_MSG");	
}]);	

app.controller("MainCtrl", ["$scope", "QNgPubSubService", "QAtmosphereSrv", 
function($scope, QNgPubSubService, QAtmosphereSrv) {
	console.debug("MainCtrl init.");
	// Init PubSub.
	QNgPubSubService.init(Math.random().toString(36).slice(2), false);
	
	// Init Atmosphere.
	QAtmosphereSrv.init();
	
	// Front-end messaging.
	$scope.fe_sub = function() {
		var channel = document.getElementById("fe_channel").value;
		QNgPubSubService.subscribe(channel, function(msg) {
			alert("Received message: " + msg.msg);
			console.debug(msg);
		});
	}

	$scope.fe_unsub = function() {
		var channel = document.getElementById("fe_channel").value;
		QNgPubSubService.unsubscribe(channel);
	}

	$scope.fe_post = function() {
		var msgChannel = document.getElementById("fe_msg_channel").value;
		var msgText = document.getElementById("fe_msg").value;
		QNgPubSubService.publish(msgChannel, msgText);
	}

	// Web Desktop
	$scope.wd_notification = function() {
		var notification = document.getElementById("wd_notification").value;
		QNgPubSubService.publish("/wd/notification", {
			content : notification,
			bubble : true
		});
	}
}]);




