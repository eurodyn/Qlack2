(function() {
	"use strict";

	angular
		.module("wd")
		.factory("SettingsService", SettingsService);
	
	/** @ngInject */
	function SettingsService($http, $rootScope, $q, SettingsDataService) {
		// Holder for system settings.
		var systemSettings = undefined;
		
		// Holder for user settings.
		var userSettings = undefined;
		
		return {
			getSystemSettings:  function() {
				var defer = $q.defer();
				if (systemSettings) {
					defer.resolve(systemSettings);
				} else {
					SettingsDataService.getSystemSettings()
						.then(function (res) {
							systemSettings = res.data;
							defer.resolve(systemSettings);
						});
				}
				return defer.promise;
			},
			
			refreshSystemSettings: function() {
				return SettingsDataService.getSystemSettings()
					.then(function (res) {
						systemSettings = res.data;
					});
			},
			
			getUserSettings: function() {
				var defer = $q.defer();
				if (userSettings) {
					defer.resolve(userSettings);
				} else {
					SettingsDataService.getUserSettings()
						.then(function (res) {
							userSettings = res.data;
							defer.resolve(userSettings);
						});
				}
				return defer.promise;
			},
			
			refreshUserSettings: function() {
				return SettingsDataService.getUserSettings()
				.then(function (res) {
					userSettings = res.data;
				});
			}
		}
	}

})();
