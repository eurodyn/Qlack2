/**
 * @ngdoc object
 * @name app.index.constant.js
 * @description IndexConstant constant
 */
(function() {
	"use strict";

	// Constants definition.
	var constant = {
		URLS : {
			// The prefix of the REST/CXF server.
			RESOURCE_PROVIDER: "/api/rest-wd"
		},
		//
		// SECURITY : {
		// 	// The header name under which the token is passed.
		// 	HEADER_NAME: "X-Qlack-Fuse-IDM-Token-WD",
		// 	// The name under which the token is stored in local storage.
		// 	LOCAL_STORAGE_NAME: "X-Qlack-Fuse-IDM-Token-WD",
		// 	// The name under which the token is stored in cookies.
		// 	COOKIE_NAME: "X-Qlack-Fuse-IDM-Token-WD"
		// },
		
		START_MENU_GROUPS : {
			SYSTEM: "system"
		},
		
		POSTAL : {
			DEKSTOP_ID : "WD"
		}
	};
	
	angular
		.module("wd")
		.constant("AppConstant", constant);
})();
