(function() {
	"use strict";

	angular
		.module("wd")
		.run(runBlock);

	/** @ngInject */
	function runBlock($log, $rootScope, SecuritySrv, $q, $state, UserService, 
			$filter, AppConstant, toastr, SettingsService, $ocLazyLoad) {
		/** Load custom/third-party WD extensions */
		$ocLazyLoad.load("wd-extend.js", {cache: false}).then(function() {
			$log.debug("Custom WD extension executed.");
		});

		// Do not allow the application to enter any state before security is
		// initalised.
		$rootScope.$on("$stateChangeStart", function(event, to, toParams) { // eslint-disable-line angular/on-watch
			$log.debug("Intercepted changing state to:", to);
			if (!SecuritySrv.isInit()) {
				$log.debug("Security is not initialised.");
				event.preventDefault();
				$log.debug(("Initialising security."))
				/** Load custom/third-party WD extensions */
				$.get("wd-extend-state-change.js")
					.done(function(response) {
						eval(response.data);
						$log.debug("Custom WD extension on state-change executed.");
					})
					.fail(function() {})
					.always(function() {
						SecuritySrv.init().then(function() {
							$log.debug(("Security initialised."))

							// Setup security interceptors.
							SecuritySrv.setupInterceptors();
							$log.debug("Security interceptors initialised.");

							// If during security initialisation a user was found (from local
							// storage or cookie) load the default login actions for this
							// user (as it is going to be automatically authenticated).
							var loginActions = $q.defer();
							if (SecuritySrv.getUser()) {
								UserService.loginActions().then(function() {
									loginActions.resolve();
								})
							} else {
								loginActions.resolve();
							}
							loginActions.promise.then(function() {
								$log.debug("Resuming intercepted state change to: ", to);
								// Get system settings before showing the first page of
								// the application.
								SettingsService.refreshSystemSettings().then(function () {
									$state.go(to, toParams);
								});
							})
						});
					});
			}
		});

		// A user requested a protected state without first being logged-in.
		$rootScope.$on("SECURITYSRV_NOACCESS_AUTH", function(event, attributes) { // eslint-disable-line angular/on-watch
			$log.debug("User requested protected state '"
				+ attributes.to.name + "' without being logged in. Will be " +
				"redirected to the login screen.");

			// Keep the name of the protected state requested, so that the user can
			// be redirected back to it after authentication takes place.
			$log.debug("Saving state for future use: ", attributes);
			$rootScope.requestedState = {
				name: attributes.to.name,
				params: attributes.toParams
			};

			// Redirect the user to the login screen.
			$log.debug("Redirecting user to login.");
			$state.go("login", {}, {
				location: false,
				inherit: false
			});
		});
	}

})();