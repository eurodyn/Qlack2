/**
 * @ngdoc object
 * @name app.login.login.route.js
 * @description app.login.login routes configuration.
 */
(function() {
	"use strict";

	angular
		.module("wd.login")
		.config(routerConfig);

	/** @ngInject */
	function routerConfig($stateProvider) {
		$stateProvider.state("login", {
			url: "/login",
			templateUrl: "app/login/login.html",
			controller: "LoginController",
			controllerAs: "login"
		});
	}
})();
