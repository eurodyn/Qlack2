/**
 * @ngdoc controller
 * @name app.desktop.desktop.controller.js
 * @description DesktopController controller. This controller subscribes to 
 * several QPubSub topics in order to allow applications to communicate with
 * the desktop environment. Such topics include:
 * - "/wd/application/close": {appID: 123, instanceID: 456}
 * Allows an application to terminate/close its window programmatically.
 * - "/wd/notification": {notification message, see NotificationService}
 * Allows an application to utilise WD's notification service to show notification
 * messages. 
 * - "/wd/rpc/get/user": {rpcTopic}
 * Returns the security token of the logged in user published back to the channel
 * defined by rpcTopic.
 * 
 */
(function() {
	"use strict";

	angular
		.module("wd.desktop")
		.controller("DesktopController", DesktopController)
	
	/** @ngInject */
	function DesktopController($log, $scope, $filter, $rootScope, 
			uuid4, DesktopService, appsFromRouter, $timeout, UtilService,
			$ocLazyLoad, $injector, $window, QAtmosphereSrv, AppConstant,
			QNgPubSubService, NotificationService, SecuritySrv) {
		// Capture 'this'. 
		var vm = this;
		
		/***********************************************************************
		 * Local variables.
		 **********************************************************************/
		// Watching for ESC key. 
		var watchEsc;
		
		// Watching for changing list of opened apps.
		var watchOpenApps;
		
		// Watching for DesktopService events.
		var watchDesktopService = {};
		
		// Listening for Postmessages.
		var postMessageListener;
		
		// Animation classes for the start menu.
		var menuInAnimation = "visible animated bounceIn";
		var menuOutAnimation = "animated zoomOut hidden";
		
		/***********************************************************************
		 * Variables exports.
		 **********************************************************************/
		// CSS classes applied to the start menu.
		vm.startMenuClass = "hidden";
		
		// CSS classes applied to the overlay layer when the start menu is shown.
		vm.overlayClass = "hidden";
		
		// Controlling variable for prompt/welcome "Start here" text.
		vm.prompt = false;
		
		// The Desktop service to be used by apps.
		vm.desktopService = DesktopService;
		
		// The list of running application instances. The actual list of
		// open applications is maintained by DesktopService and its various
		// methods. This variable is provided here, so that it maintains
		// a watch for the directive drawing the windows on screen (i.e. never
		// interact directly with it, instead use the relevant methods of 
		// DesktopService).
		vm.applicationInstances = [];

		/***********************************************************************
		 * Functions exports.
		 **********************************************************************/
		vm.showMenu = showMenu;
		vm.hideMenu = hideMenu;
		vm.raiseWindow = raiseWindow;
		//TODO remove.
		vm.test123 = function() {
			NotificationService.add({
				title: "Closing {{abc}} app",
				title_data: {
					abc: "123"
				},
				content: "App is closed",
				audio: false,
				bubble: true,
				type: "success"
			});
		}
		
		// Calling controller's activation.
		activate();

		/***********************************************************************
		 * Controller activation.
		 **********************************************************************/
		function activate() {
			// Set available apps (which have been resolved as part of routing
			// to this Controller) in DesktopService.
			DesktopService.setApplications(appsFromRouter.data);
			
			// Setup a watch for when the list of open applications changes.
			watchOpenApps = $scope.$watch(
				function() {
					return DesktopService.getApplicationInstances();
				}, 
				function(newVal) {
					vm.applicationInstances = newVal;
					
					// Make all other windows inactive when an application is
					// opening or closing.
					makeAllWindowsInactive();
				}, true
			);
			
			// Switch on/off welcome prompt.
			$timeout(function() {
				vm.prompt = true;
				$timeout(function() {
					vm.prompt = false;
				}, 3000);
			}, 1000);
			
			// Listen for events emitted when DesktopService tries to open an
			// application which is already open. In that case, just raise the 
			// existing application window.
			watchDesktopService[DesktopService.events.APP_ALREADY_OPEN] = 
				$rootScope.$on(DesktopService.events.APP_ALREADY_OPEN, function(event, data) {
					raiseWindow(data.identification.uniqueId);
			})
			
			// Listen for events emitted by DesktopService when a script-only
			// app is opened. In that case, load the associated script and execute
			// it.
			watchDesktopService[DesktopService.events.SCRIPT_APP] =
				$rootScope.$on(DesktopService.events.SCRIPT_APP, function(event, data) {
					$log.debug("Openning script-app at:",data);
					$ocLazyLoad.load(data, {cache: false}).then(function() {
						$log.debug("Script app opened.");	 
					});
				});
			
			// Initialise QPubSub server.
			QNgPubSubService.init("wd-server", true);
			
			// Register handlers for QPubSub messages.
			// Close a running app.
			QNgPubSubService.subscribe("/wd/application/close", function(messageEvent) {
				DesktopService.setApplicationAsClosed(
						messageEvent.msg.appID, messageEvent.msg.instanceID);
			});
			
			// Display a notification.
			QNgPubSubService.subscribe("/wd/notification", function(messageEvent) {
				NotificationService.add(messageEvent.msg);
			});
			
			// Return the ticket of the logged-in user.
			QNgPubSubService.subscribe("/wd/rpc/get/user", function(messageEvent) {
				QNgPubSubService.publish(messageEvent.msg.rpcTopic, SecuritySrv.getUser());
			});
			
			// Initialise Atmosphere.
			QAtmosphereSrv.init();
		}
		
		/***********************************************************************
		 * $scope destroy.
		 **********************************************************************/
		$scope.$on("$destroy", function() {
			watchEsc();
			watchOpenApps();
			postMessageListener();
			watchDesktopService[DesktopService.events.APP_ALREADY_OPEN]();
			watchDesktopService[DesktopService.events.SCRIPT_APP]();
		});
		
		/***********************************************************************
		 * Functions.
		 **********************************************************************/
		function hideMenu() {
			if (vm.startMenuClass == menuInAnimation) {
				vm.overlayClass = "hidden";
				vm.startMenuClass = menuOutAnimation;
				watchEsc();
			}
		}
		
		function showMenu() {
			if (vm.startMenuClass == menuOutAnimation || vm.startMenuClass == "hidden") {
				vm.overlayClass = "visible";
				vm.startMenuClass = menuInAnimation;
				watchEsc = $scope.$on("keys:esc", function() {
					hideMenu();
				});
				angular.element(".start-menu").css("zIndex", UtilService.getMaxZIndexPlusOne());
			}
		}
		
		function makeAllWindowsInactive() {
			angular.element("[id^=qwin-id-]").removeClass("active");
		}
		
		function raiseWindow(appID, instanceID) {
			// Make all other windows inactive.
			makeAllWindowsInactive();
			
			// Make this window active.
			var appWindow;
			if (instanceID == undefined) {
				// Support for single-instance applications, where the instanceID
				// does not matter (since there is always a single instance of
				// such an application running).
				appWindow = angular.element("[id^=qwin-id-" + appID);
			} else {
				appWindow = angular.element("#qwin-id-" + appID + "\\$" + instanceID);	
			}
			
			appWindow.css("zIndex", UtilService.getMaxZIndexPlusOne());
			appWindow.addClass("active");
		}
	}
})();
