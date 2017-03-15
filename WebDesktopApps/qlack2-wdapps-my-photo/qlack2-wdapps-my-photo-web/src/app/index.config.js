(function() {
	'use strict';

	angular.module('qlack2WdappsMyPhotoWeb').config(config);

	/** @ngInject */
	function config($logProvider, flowFactoryProvider, AppConstants, $httpProvider,
		SecuritySrvProvider, $translateProvider, $translatePartialLoaderProvider) {
		// Enable log
		$logProvider.debugEnabled(true);

		// Configure security.
		SecuritySrvProvider.setRestPrefix(AppConstants.URLS.RESOURCE_PROVIDER + "/security-proxy");
    SecuritySrvProvider.setStorageName(window.parent.QLACK_WD_TOKEN_HEADER_NAME);
    SecuritySrvProvider.setHeaderName(window.parent.QLACK_WD_TOKEN_HEADER_NAME);
    SecuritySrvProvider.setCookieName(window.parent.QLACK_WD_TOKEN_HEADER_NAME);

		// Configure translations
		$translateProvider
			.determinePreferredLanguage()
			.fallbackLanguage("en")
			.useLocalStorage()
			.useSanitizeValueStrategy('escaped');
		$translatePartialLoaderProvider.addPart("myphoto");
		$translateProvider.useLoader("$translatePartialLoader", {
			urlTemplate: "/api/rest-wd/i18n/translations/{part}?lang={lang}"
		});

		// Configure ng-flow.
		flowFactoryProvider.defaults = {
			target: AppConstants.URLS.RESOURCE_PROVIDER + "/file-upload/upload",
			maxChunkRetries: 3,
			chunkRetryInterval: 5000,
			simultaneousUploads: 3,
			chunkSize: 2097152,
			successStatuses: [200, 201, 202],
			permanentErrors: [404, 403, 415, 500, 501],
			generateUniqueIdentifier: function() {
				return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, function(c) {
					var r, v;
					r = Math.random() * 16 | 0;
					v = (c === "x" ? r : r & 0x3 | 0x8);
					return v.toString(16);
				});
			},
			headers: function () {
				var ticket = {};
				ticket[AppConstants.SECURITY.HEADER_NAME] =
					$httpProvider.defaults.headers.common[AppConstants.SECURITY.HEADER_NAME];
				return ticket;
			}
		};
	}

})();
