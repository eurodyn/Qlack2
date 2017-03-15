(function() {
	"use strict";

	angular
		.module("wd")
		.directive("qWindow", QWindow);

	/** @ngInject */
	function QWindow(DesktopService, $log, $q, $http, $compile, $sce, $timeout, 
			$window, UtilService, $templateCache) {
		return {
			restrict: "A",
			controller: function($scope) {
				$log.debug("Preparing application instance to open:", $scope.applicationInstance);
				// Load template.
				$http.get("app/components/windows/window.tpl.html", { cache: $templateCache }).then(
					function(response) {
						$scope.template = response.data;
						$scope.templateLoaded = true;
					}
				);
				
				// A helper function to allow us opening application URLs from
				// any domain name. Note that certain external applications 
				// (i.e. not deployed as native apps within WD) may not allow
				// to be opened in iframes (and this is something the WD can not
				// control).
				$scope.trustSrc = function(src) {
					return $sce.trustAsResourceUrl(src);
				}
			},
			link: function(scope, elem) {
				scope.$watch("templateLoaded", function(newVal) {
					if (newVal == true) {
						// Replace static-variables into the template.
						scope.template = scope.template.replace("${zindex}", UtilService.getMaxZIndexPlusOne());
						
						// Compile element to add it to the DOM.
						elem.html($compile(scope.template)(scope));
						
						// Setup window parameters.
						// This needs to be executed when the current digest
						// cycle is done, so it is wrapped in $timeout.
						$timeout(function() {
							// Grab the window created.
							var jqWindow = angular.element("#qwin-id-" + scope.applicationInstance.appID 
									+ "\\$" + scope.applicationInstance.instanceID);
							
							// Set height/width.
							jqWindow.height(scope.applicationInstance.height + "px");
							jqWindow.width(scope.applicationInstance.width + "px");

							// Make window draggable.
							if (scope.applicationInstance.draggable) {
								$log.debug("Application is draggable.");
								jqWindow.draggable({
									scroll: false,
									start: function() {
										scope.desktop.raiseWindow(scope.applicationInstance.appID, scope.applicationInstance.instanceID);
										angular.element(this).find(".qwin-content").fadeOut(100);
									},
									stop: function() {
										angular.element(this).find(".qwin-content").fadeIn();
									}
								});
							}
							
							// Make window resizable.
							if (scope.applicationInstance.resizable) {
								$log.debug("Application is resizable.");
								jqWindow.resizable({
									minHeight: scope.applicationInstance.minHeight,
									minWidth: scope.applicationInstance.minWidth,
									start: function() {
										angular.element(this).find(".qwin-content").fadeOut(100);
									},
									stop: function() {
										angular.element(this).find(".qwin-content").fadeIn();
									}
								});
							}
						});
					}
				});
			}
		}
	}
})();
