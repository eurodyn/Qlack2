(function() {
	"use strict";

	angular
		.module("wd")
		.factory("SettingsDataService", SettingsDataService);

	/** @ngInject */
	function SettingsDataService(AppConstant, $http) {
		// Resource URLs.
		var URLS = {
			ROOT : AppConstant.URLS.RESOURCE_PROVIDER + "/settings",
			RESOURCES : {
				USER : "/",
				SYSTEM : "/system"
			}
		};

		// Resource methods.
		return {
			// Get global system settings.
			getSystemSettings: function() {
				var url = URLS.ROOT + URLS.RESOURCES.SYSTEM;
				return $http.get(url);
			},

			// Get the settings of the logged-in user.
			getUserSettings: function() {
				var url = URLS.ROOT + URLS.RESOURCES.USER;
				return $http.get(url);
			}	
		}
	}	
})();
