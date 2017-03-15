/**
 * @ngdoc controller
 * @name index.controller.js
 * @description IndexController controller
 */
(function() {
	"use strict";

	angular // eslint-disable-line no-undef
		.module("qlack2WdappsDemoSecurityExternal.home")
		.controller("HomeController", HomeController);

	/** @ngInject */
	function HomeController($scope, QNgPubSubService, $window, $log) {
		var vm = this;

		/***********************************************************************
		 * Local variables.
		 **********************************************************************/
		var tokenRequestChannel = "/wd/rpc/get/user";
		var randomReplyChannel = "myRandomChannel";

		/***********************************************************************
		 * Exported variables.
		 **********************************************************************/

		/***********************************************************************
		 * Exported functions.
		 **********************************************************************/
		vm.publishNotification = publishNotification;
    vm.accessParentWindow = accessParentWindow;
    vm.getSecurityToken = getSecurityToken;

		// Activating the controller.
		activate();

		/***********************************************************************
		 * Controller activation.
		 **********************************************************************/
		function activate() {
		}

		/***********************************************************************
		 * $scope destroy.
		 **********************************************************************/
		$scope.$on("$destroy", function() {
      QNgPubSubService.unsubscribe(randomReplyChannel);
		});

		/***********************************************************************
		 * Functions.
		 **********************************************************************/
		function publishNotification(bar) {
		  $log.debug("Posting a notification to Web Desktop.");
      QNgPubSubService.publish("/wd/notification", {
        title: "PubSub works",
        content: "Hello!",
        audio: true
      });
		}

		function accessParentWindow() {
      $log.debug("Trying to access parent window...");
      $log.debug($window.parent.QLACK_WD_TOKEN_HEADER_NAME);
    }

    function getSecurityToken() {
      $log.debug("Getting security token.");

      /* Subscribe for reply */
      QNgPubSubService.subscribe(randomReplyChannel, function(messageEvent) {
        $log.debug("GOT: ", messageEvent.msg);
        vm.token = messageEvent.msg;
      });

      /* Send request */
      QNgPubSubService.publish(tokenRequestChannel, {
        rpcTopic: randomReplyChannel
      });
    }
	}
})();
