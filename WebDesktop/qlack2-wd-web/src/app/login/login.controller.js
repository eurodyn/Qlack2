/**
 * @ngdoc controller
 * @name app.login.login.controller.js
 * @description LoginController controller
 */
(function() {
	"use strict";

	angular
		.module("wd.login")
		.controller("LoginController", LoginController);

	/** @ngInject */
	function LoginController($scope, LexiconDataService, $translate, 
			SecuritySrv, UserService, $rootScope, $log, $state, toastr) {
		// Capture 'this'. 
		var vm = this;
		
		/***********************************************************************
		 * Local variables.
		 **********************************************************************/
		// An array with listeners to destroy.
		var listeners = [];
		
		/***********************************************************************
		 * Variables exports.
		 **********************************************************************/
		vm.languages = [];
		vm.user = {};
		
		/***********************************************************************
		 * Functions exports.
		 **********************************************************************/
		vm.switchLg = switchLg;
		vm.login = login;
		
		// Calling controller's activation.
		activate();

		/***********************************************************************
		 * Controller activation.
		 **********************************************************************/
		function activate() {
			// Get available languages.
			LexiconDataService.getLanguages().then(function(response) {
				vm.languages = response.data;
			})

			// Authentication handler for success.
			listeners.push($rootScope.$on("SECURITYSRV_AUTH_SUCCESS", function() {
				UserService.loginActions().then(function() {
					// If the user prior to the login requested a protected page, forward
					// to this page instead of home.
					if (($rootScope.requestedState != null)) {
						$log.debug("Forwarding the user to previously saved state: ", 
								$rootScope.requestedState);
						$state.go($rootScope.requestedState.name, $rootScope.requestedState.params, {
							location: true,
							inherit: false
						});
						$rootScope.requestedState = undefined;
					} 
					else {
						$log.debug("Could not find a saved state prior to the login.");
						$state.go("desktop", {}, {
							location: true,
							inherit: false
						});
					}
				});
			}));
			
			// Authentication handler for failer.
			listeners.push($rootScope.$on("SECURITYSRV_AUTH_FAIL", function() {
				toastr.error($translate.instant("check_username_and_password"));
			}));
		}
		
		/***********************************************************************
		 * $scope destroy.
		 **********************************************************************/
		$scope.$on("$destroy", function() {
			listeners.forEach(function(callback) {
				callback();
			});
		});
		
		/***********************************************************************
		 * Functions.
		 **********************************************************************/
		/**
		 * Switches the UI languages to the given locale.
		 */
		function switchLg(locale) {
			$translate.use(locale);
		}
		
		function login() {
			// Login is an asynchronous process which emits a 'SECURITYSRV_AUTH_SUCCESS'
			// message at the end (or 'SECURITYSRV_AUTH_FAIL' if authentication failed).
			SecuritySrv.login(vm.user.username, vm.user.password);
		}
	}
})();
