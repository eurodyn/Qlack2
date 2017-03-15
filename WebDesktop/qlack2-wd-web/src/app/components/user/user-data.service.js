(function() {
	"use strict";

	angular
		.module("wd")
		.factory("UserDataService", UserDataService);

	/** @ngInject */
	function UserDataService(AppConstant, $http) {
		// Resource URLs.
		var URLS = {
			ROOT : AppConstant.URLS.RESOURCE_PROVIDER + "/user/profile",
			RESOURCES: {
				USER: "/"
			}
		};
		
		// Resource methods.
		return {
			getProfile: function() {
				var url = URLS.ROOT + URLS.RESOURCES.USER;
				return $http.get(url);
			}
		}
	}
})();
