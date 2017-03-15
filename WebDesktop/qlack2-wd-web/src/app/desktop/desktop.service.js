/**
 * @ngdoc service
 * @name desktop.service.js
 * @description DesktopService factory
 */
(function() {
	"use strict";

	angular
		.module("wd.desktop")
		.factory("DesktopService", DesktopService);

	/** @ngInject */
	function DesktopService($log, uuid4, $rootScope) {
		/***********************************************************************
		 * Local variables.
		 **********************************************************************/
		// The list of all available applications including their full details.
		var applications;
		
		/* 	The list of all running instances of applications. An application
			instance represents the runtime state of each application window.
		*/	
		var applicationInstances = [];
		
		/***********************************************************************
		 * Local functions.
		 **********************************************************************/
		// Checks if an instance of the specific application is already running.
		var isApplicationRunning = function(appID) {
			return _.findIndex(applicationInstances, {appID: appID}) > -1;
		}
		
		// A helper to update a property in running applications.
		var updateApplicationInstanceProperty = function(appID, instanceID, propertyName, value) {
			var idx = _.findIndex(applicationInstances, {appID: appID, instanceID: instanceID});
			applicationInstances[idx][propertyName] = value;
		}
		
		// A property to toggle a property in running applications. Toggle is
		// performed with logical NOT, so make sure the underlying value behaves
		// accordingly.
		var toggleApplicationInstanceProperty = function(appID, instanceID, propertyName) { 
			var idx = _.findIndex(applicationInstances, {appID: appID, instanceID: instanceID});
			applicationInstances[idx][propertyName] = !applicationInstances[idx][propertyName];
		}
		
		/***********************************************************************
		 * Exported functions.
		 **********************************************************************/
		return {
			// Events emitted from this service.
			events: {
				APP_ALREADY_OPEN: "APP_ALREADY_OPEN",
				SCRIPT_APP: "SCRIPT_APP"
			},
			
			// Sets the list of all available applications.
			setApplications: function(apps) {
				applications = apps;
			},
			
			// Gets the list of all available applications.
			getApplications: function() {
				return applications;
			},
			
			// Gets the details of a specific application.
			getApplication: function(appID) {
				return _.find(applications, {identification: {uniqueId: appID}});
			},
			
			// Get the details of a specific application instance.
			getApplicationInstance: function(appID, instanceID) {
				return _.find(applicationInstances, {appID: appID, instanceID: instanceID});
			},
			
			// Opens a new instance of an application.
			setApplicationAsOpen: function(appID) {
				var application = this.getApplication(appID);
				$log.debug("Openning app:", application);
				
				// Check if this is a script-only application or a normal window
				// app.
				if (!_.endsWith(application.instantiation.index, ".js")) {
					if (!application.instantiation.multipleInstances && isApplicationRunning(appID)) {
						$rootScope.$emit(this.events.APP_ALREADY_OPEN, application);
					} else {
						var appInstance = {
								appID: application.identification.uniqueId,
								instanceID: uuid4.generate(),
								maximised: false,
								minimised: false,
								iconType: application.menu.iconSmall.substring(0, application.menu.iconSmall.indexOf("}") + 1),
								iconName: application.menu.iconSmall.substring(application.menu.iconSmall.indexOf("}") + 1),
								titleKey: application.identification.titleKey,
								translationsGroup: application.instantiation.translationsGroup,
								url: application.instantiation.path + application.instantiation.index,
								height: application.window.height,
								width: application.window.width,
								maxHeight: application.window.maxHeight,
								maxWidth: application.window.maxWidth,
								draggable: application.window.draggable,
								resizable: application.window.resizable,
								closable: application.window.closable,
								maximizable: application.window.maximizable,
								minimizable: application.window.minimizable,
								showTitle: application.menu.showTitle
							};
						$log.debug("Application queued for opening:", appInstance);
						applicationInstances.push(appInstance);
					}
				} else {
					$rootScope.$emit(this.events.SCRIPT_APP, 
							application.instantiation.path 
							+ application.instantiation.index);
				}
			},
			
			// Returns the list of opened applications.
			getApplicationInstances: function() {
				return applicationInstances;
			},
			
			// Toggles minimised status of a window; it automatically unsets
			// maximised status.
			toggleMinimised: function(appID, instanceID) {
				toggleApplicationInstanceProperty(appID, instanceID, "minimised");
			},
			
			// Toggles the maximised status of a window; it automatically unsets
			// the minimised status.
			toggleMaximised: function(appID, instanceID) {
				toggleApplicationInstanceProperty(appID, instanceID, "maximised");
			},
			
			// Sets an application instance as closed, given an application ID
			// and the instance ID.
			setApplicationAsClosed: function(appID, instanceID) {
				$log.debug("Closing application instance:", appID, instanceID);
				_.remove(applicationInstances, {appID: appID, instanceID: instanceID});
			},
		};
	}
})();
