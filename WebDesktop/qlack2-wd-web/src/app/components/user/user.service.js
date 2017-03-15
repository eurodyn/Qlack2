(function() {
	"use strict";

	angular
		.module("wd")
		.factory("UserService", UserService);

	/** @ngInject */
	function UserService($http, $rootScope, $q, $state, $location, 
			UserDataService, AppConstant, $filter, $log) {
		// A holder for generic user permissions.
		var permissions = []; // eslint-disable-line no-unused-vars

		// A holder for the user profile.
		var profile = {}; // eslint-disable-line no-unused-vars

		return {
			// Actions that should be performed as soon as the user is logged in
			// (or when a page refresh is taking place for a previously logged in user).
			loginActions: function () {
				$log.debug("Performing login actions.");
				
				// Execute all actions inside a promise.
				return $q.all([UserDataService.getProfile()]
				).then(function(res) {
					$log.debug("Login actions requests terminated.");
					
					// Extract information obtain from HTTP calls that took part on this
					// promise (the order is identical to the calls embedded in $q.all).
					profile = res[0].data;
				});
			}
		};
	}

})();
