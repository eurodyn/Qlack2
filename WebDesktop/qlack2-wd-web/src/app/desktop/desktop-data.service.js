(function() {
	"use strict";

	angular
		.module("wd.desktop")
		.factory("DesktopDataService", DesktopDataService);
	
	/** @ngInject */
	function DesktopDataService($http, AppConstant) {
		// Resource URLs.
		var URLS = {
			ROOT : AppConstant.URLS.RESOURCE_PROVIDER + "/desktop", 
			RESOURCES : {
				ALL_ACTIVE_APPS: "/applications/active"
			}
		}
		
		// Resource methods.
		return {
			getActiveApps: function() {
				var url = URLS.ROOT + URLS.RESOURCES.ALL_ACTIVE_APPS;
				return $http.get(url);
			}
		};
	}

})();
