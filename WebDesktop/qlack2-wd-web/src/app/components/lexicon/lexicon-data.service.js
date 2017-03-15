(function() {
	"use strict";

	angular
		.module("wd")
		.factory("LexiconDataService", LexiconDataService);

	/** @ngInject */
	function LexiconDataService(AppConstant, $http) {
		// Resource URLs.
		var URLS = {
			ROOT : AppConstant.URLS.RESOURCE_PROVIDER + "/i18n",
			RESOURCES : {
				LANGUAGES : "/languages"
			}
		};

		// Resource methods.
		return {
			// Get all languages.
			getLanguages: function() {
				var url = URLS.ROOT + URLS.RESOURCES.LANGUAGES;
				return $http.get(url);
			}
		}
	}	
})();
