(function() {
	"use strict";

	angular
		.module("qlack2WdappsMyPhotoWeb")
		.constant("AppConstants", {
			URLS: {
				RESOURCE_PROVIDER: "/api/apps/myphoto"
			},
			
			SECURITY : {
				// The header name under which the token is passed.
				HEADER_NAME: "X-Qlack-Fuse-IDM-Token-WD",
				// The name under which the token is stored in local storage.
				LOCAL_STORAGE_NAME: "X-Qlack-Fuse-IDM-Token-WD",
				// The name under which the token is stored in cookies.
				COOKIE_NAME: "X-Qlack-Fuse-IDM-Token-WD"
			}
		});

})();
