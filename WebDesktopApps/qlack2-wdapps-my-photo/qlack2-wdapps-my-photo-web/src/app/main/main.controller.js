(function() {
	"use strict";

	angular
		.module("qlack2WdappsMyPhotoWeb")
		.controller("MainController", MainController);

	/** @ngInject */
	function MainController($log, MainDataService, AppConstants, QNgPubSubService, $translate) {
		// Capture 'this'.
		var vm = this;

		// The list of files uploaded.
		vm.files = {};
		
		// Export functions & variables.
		vm.submit = submit;
		
		// The URL of the user photo.
		vm.spinner = false;
		
		activate();
		
		function activate() {
			vm.photo = AppConstants.URLS.RESOURCE_PROVIDER + "/myphoto";
		}
		
		function submit() {
			vm.spinner = true;
			return MainDataService.updatePhoto(vm.files.files[0].uniqueIdentifier).then(function() {
				// Refresh photo on the window.
				vm.photo = AppConstants.URLS.RESOURCE_PROVIDER + "/myphoto?" + new Date().getTime();
				vm.files.files.shift();
				vm.spinner = false;
				
				// Show a notification.
				QNgPubSubService.init("myphoto", false);
				QNgPubSubService.publish("/wd/notification", {
					content: $translate.instant("photo_updated"),
					bubble: true
				});
			});
		}
	}
})();
