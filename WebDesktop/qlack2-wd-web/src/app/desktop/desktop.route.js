/**
 * @ngdoc object
 * @name app.login.login.route.js
 * @description app.login.login routes configuration.
 */
(function() {
	"use strict";

	angular
		.module("wd.desktop")
		.config(routerConfig);

	/** @ngInject */
	function routerConfig($stateProvider) {
		$stateProvider.state("desktop", {
			url: "/",
			resolve: {
				/** @ngInject */
				appsFromRouter: function(DesktopDataService) {
					return DesktopDataService.getActiveApps();
				} 
			},
			templateUrl: "app/desktop/desktop.html",
			controller: "DesktopController",
			controllerAs: "desktop",
			data: {
				isPublic: false
			}
		});
	}
})();
