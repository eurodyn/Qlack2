/**
 * @ngdoc object
 * @name index.route.js
 * @description index routes configuration
 */
(function() {
	"use strict";

	angular // eslint-disable-line no-undef
		.module("qlack2WdappsDemoSecurityExternal")
		.config(routerConfig);

	/** @ngInject */
	function routerConfig($stateProvider, $urlRouterProvider, $locationProvider) {
		// Enable HTML5 mode (requires server URL rewriting).
		$locationProvider.html5Mode({
			enabled : true,
			requireBase : true
		});

		$urlRouterProvider.otherwise("/");
	}

})();
