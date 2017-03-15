(function() {
	'use strict';

	angular.module('qlack2WdappsMyPhotoWeb').run(runBlock);

	/** @ngInject */
	function runBlock($log, SecuritySrv, $rootScope, $q, $state,
			QNgPubSubService, $http) {
		// Do not allow the application to enter any state before security is
		// initalised.
		var scs = $rootScope.$on("$stateChangeStart", function(event, to, toParams) {	// eslint-disable-line no-unused-vars
			$log.debug("Intercepted changing state to:", to);
			if (!SecuritySrv.isInit()) {
				$log.debug("Security is not initialised.");
				event.preventDefault();
				$log.debug(("Initialising security."))
				SecuritySrv.init().then(function() {
					// If security was not initialised, try to get the ticket
					// of the user directly from WD (this is usually the case
					// during development, where each application runs on a 
					// different URL and therefore does not have access to the
					// underlying session storage of WD where the ticket is
					// saved.
					if (!SecuritySrv.getUser()) {
						$log.debug("Could not obtain security token, will try via PubSub.");
						QNgPubSubService.init("myphoto", false);		
						var tmpTopic = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
							var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
							return v.toString(16);
						});
						QNgPubSubService.subscribe(tmpTopic, function(event) {
							QNgPubSubService.unsubscribe(tmpTopic);
							SecuritySrv.init(event.msg).then(function() {
								SecuritySrv.setupInterceptors();
								$log.debug("Security interceptors initialised via PubSub:", $http.defaults.headers.common);
								$log.debug("Resuming intercepted state change to: ", to);
								$state.go(to, toParams);	
							})
						});
						QNgPubSubService.publish("/wd/rpc/get/user", {rpcTopic: tmpTopic});
					} else {
						$log.debug(("Security initialised."))
						// Setup security interceptors.
						SecuritySrv.setupInterceptors();
						$log.debug("Security interceptors initialised via session storage:", $http.defaults.headers.common);
						$log.debug("Resuming intercepted state change to: ", to);
						$state.go(to, toParams);
					}
				});
			}
		});
	}
})();
