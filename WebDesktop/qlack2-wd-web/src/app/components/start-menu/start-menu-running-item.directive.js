(function() {
	"use strict";

	angular
		.module("wd")
		.directive("qStartMenuRunningItem", QStartMenuRunningItem);
	
	/** @ngInject */
	function QStartMenuRunningItem($log, $templateCache, $http, $timeout, $q, 
			$compile, DesktopService) {
				
		return {
			restrict: "A",
			controller: function($scope) {
				// The templates for the tiles.
				$scope.templates = {};
				
				// A controller-variable to load templates asynchronously.
				$scope.templatesLoaded = false;
				
				// Load templates.
				$q.all(
					[
						$http.get("app/components/start-menu/templates/running-item.tpl.html", { cache: $templateCache })
					]).then( 
					function(responses) {
						$scope.templates = {
							runningItem: responses[0].data
						}
						$scope.templatesLoaded = true;
					}
				);
			},
			link: function(scope, elem) {
				// Start producing the output only when all templates are
				// loaded.
				scope.$watch("templatesLoaded", function(newVal) {
					if (newVal == true) {
						// Find the template to use.
						var template = scope.templates.runningItem;
						
						// Compile and append template.
						elem.html($compile(template)(scope));
					}
				});
			}
		}
	}
})();
