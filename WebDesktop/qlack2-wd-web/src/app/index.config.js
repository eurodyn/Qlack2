/** Expose a global variable with the name of token, so that other applications
 * running in WD know which header to use.
 */
QLACK_WD_TOKEN_HEADER_NAME = 	"X-Qlack-Fuse-IDM-Token-WD";

(function() {
	"use strict";

	angular
		.module("wd")
		.config(config);

	/** @ngInject */
	function config($logProvider, toastrConfig, SecuritySrvProvider,
			AppConstant, QAtmosphereSrvProvider, $translateProvider,
			$translatePartialLoaderProvider, $provide) {
		// Enable log
		$logProvider.debugEnabled(true);

		// Configure security
		SecuritySrvProvider.setRestPrefix(AppConstant.URLS.RESOURCE_PROVIDER
				+ "/security-proxy");
		SecuritySrvProvider.setHeaderName(QLACK_WD_TOKEN_HEADER_NAME);
		SecuritySrvProvider.setStorageName(QLACK_WD_TOKEN_HEADER_NAME);
		SecuritySrvProvider.setCookieName(QLACK_WD_TOKEN_HEADER_NAME);
		SecuritySrvProvider.setDebug(true);

		// Configure Atmosphere framework.
		QAtmosphereSrvProvider.setLogLevel("info");
		QAtmosphereSrvProvider.setSubscriptionManagementServiceURL(
				 AppConstant.URLS.RESOURCE_PROVIDER + "/atmosphere");
		QAtmosphereSrvProvider.setSecurityFunction(function() {
			var ticket = sessionStorage.getItem(QLACK_WD_TOKEN_HEADER_NAME);
			if (ticket !== null) {
				//ticket = JSON.stringify(JSON.parse(ticket).ticket);
				ticket = angular.toJson(angular.fromJson(ticket).ticket);
			}
			return ticket;
		});
		QAtmosphereSrvProvider
				.setSecurityHederName(QLACK_WD_TOKEN_HEADER_NAME);
		
		// Configure translations
		$translateProvider
			.determinePreferredLanguage()
			.fallbackLanguage("en")
			.useLocalStorage()
			.useSanitizeValueStrategy('escaped');
			//.useUrlLoader(AppConstant.URLS.RESOURCE_PROVIDER + "/i18n/translations");
			$translatePartialLoaderProvider.addPart("wd");
			$translatePartialLoaderProvider.addPart("wd_apps");
			$translateProvider.useLoader("$translatePartialLoader", {
				urlTemplate: AppConstant.URLS.RESOURCE_PROVIDER + "/i18n/translations/{part}?lang={lang}"
			});
		
		// Configure Toastr.
		toastrConfig.allowHtml = true;
		toastrConfig.timeOut = 5000;
		toastrConfig.positionClass = 'toast-top-right';
		toastrConfig.preventDuplicates = false;
		toastrConfig.progressBar = true;
		toastrConfig.closebutton = true;
	}

})();
