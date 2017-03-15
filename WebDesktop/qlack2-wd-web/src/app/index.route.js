(function() {
	"use strict";

	angular
		.module("wd")
		.config(routerConfig);

	/** @ngInject */
	function routerConfig($stateProvider, $urlRouterProvider, $locationProvider) {// Enable HTML5 mode (requires server URL rewriting).
		$locationProvider.html5Mode({
			enabled : true,
			requireBase : true
		});
		
		$urlRouterProvider.otherwise('/');
	}

})();
