/**
 * @ngdoc object
 * @name index.route.js
 * @description index routes configuration
 */
(function() {
	"use strict";

	angular // eslint-disable-line no-undef
		.module("qlack2WdappsDemoSecurityExternal.home")
		.config(routerConfig);

	/** @ngInject */
	function routerConfig($stateProvider) { // eslint-disable-line no-unused-vars
		$stateProvider.state("home", {
			url : "/",
			templateUrl : "app/home/home.html",
			controller : "HomeController",
			controllerAs : "homeCtrl"
		});
	}
})();
