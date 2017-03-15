(function() {
	"use strict";

	angular
		.module("wd")
		.factory("NotificationService", NotificationService);

	/** @ngInject */
	function NotificationService(toastr, $log, $translate) {
		return {
			/**
			 * Add a new notification to WD.
			 * 
			 * The notification object is: {
			 * 		title: The title to be displayed,
			 * 		title_data: Data to be interpolated into the title,
			 * 		content: The content to be displayed,
			 * 		content_data: Data to be interpolated into the content,
			 * 		audio: true/false, whether to sound the speaker,
			 * 		type: "success", "warning", "error"
			 * 		bubble: true/false, whether to display this notification in a bubble. 
			 * }
			 */
			add: function(notification) {
				// Setup default values.
				if (notification.audio == undefined) {
					notification.audio = false;
				}
				
				if (notification.bubble == undefined) {
					notification.bubble = true;
				}
				
				if (notification.type == undefined) {
					notification.type = "success";
				}
				
				// Check if audio should be played.
				if (notification.audio) {
					//TODO sound service call					
				}
				
				if (notification.bubble) {
					switch (notification.type) {
						case "success":
							toastr.success(
								$translate.instant(notification.content, notification.content_data), 
								$translate.instant(notification.title, notification.title_data));
							break;
						case "error":
							toastr.error(
								$translate.instant(notification.content, notification.content_data), 
								$translate.instant(notification.title, notification.title_data));
							break;
						case "warning":
							toastr.warning(
								$translate.instant(notification.content, notification.content_data), 
								$translate.instant(notification.title, notification.title_data));
							break;
					}
				} else {
					$log.debug($translate.instant(notification.title, notification.title_data) + " " 
							+ $translate.instant(notification.content, notification.content_data));
				}
			}
		};
	}

})();
