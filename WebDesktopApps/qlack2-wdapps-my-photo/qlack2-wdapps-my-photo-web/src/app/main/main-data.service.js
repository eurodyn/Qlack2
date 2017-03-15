(function() {
	"use strict";

	angular
		.module("qlack2WdappsMyPhotoWeb")
		.factory("MainDataService", MainDataService);
	
	/** @ngInject */
	function MainDataService($http, AppConstants) {
		// Resource URLs.
		var URLS = {
			ROOT : AppConstants.URLS.RESOURCE_PROVIDER, 
			RESOURCES : {
				PHOTO_UPDATE: "/myphoto",
				PHOTO_GET: "/myphoto"
			}
		}
		
		// Resource methods.
		return {
			getPhoto: function() {
				var url = URLS.ROOT + URLS.RESOURCES.PHOTO_GET;
				return $http.get(url);
			},
			
			updatePhoto: function(fileID) {
				var url = URLS.ROOT + URLS.RESOURCES.PHOTO_GET + "?fileID=" + fileID;
				return $http.put(url);
			}
		};
	}

})();
