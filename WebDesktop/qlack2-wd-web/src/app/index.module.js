(function() {
	"use strict";

	angular.module("wd", [
		"wd.login",
		"wd.desktop",

		"QAngularSecurityIDM", 		// QLACK component to perform authentication.
		"QAtmosphere",				// AtmosphereJS angular component.
		"QNgPubSub",				// Pub/Sub library for cross-domain apps.
		
		"ngAnimate", 			    // Support for animations.
		"ngCookies", 				// Cookies support.
		"ngSanitize",				// Sanitise html.
		"ui.router", 				// UI router.
		"toastr",					// Popup notifications
		"pascalprecht.translate",	// Translations support
		"oc.lazyLoad"				// To load external apps as scripts.
	]);
})();
