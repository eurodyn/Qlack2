(function() {
	"use strict";

	angular
		.module("wd")
		.directive("qStartMenuItem", QStartMenuItem);
	
	/** @ngInject */
	function QStartMenuItem($log, $templateCache, $http, $timeout, $q, $compile) {
		
		// Helper functions.
		// Extract the type of a file based on the name of the icon
		var extractTileType  = function(icon) {			
			return icon.replace(/{(.*)-.*}.*/, "$1");
		}
		
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
						$http.get("app/components/start-menu/templates/icon.tpl.html", { cache: $templateCache }),
						$http.get("app/components/start-menu/templates/icon-title.tpl.html", { cache: $templateCache }),
						$http.get("app/components/start-menu/templates/image.tpl.html", { cache: $templateCache })
					]).then( 
					function(responses) {
						$scope.templates = {
							icon: responses[0].data,
							iconWithTitle: responses[1].data,
							image: responses[2].data
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
						var template;
						switch (extractTileType(scope.app.menu.icon)) {
						case "icon":
							if (scope.app.menu.showTitle) {
								template = scope.templates.iconWithTitle;		
							} else {
								template = scope.templates.icon;
							}
							break;
						case "image":
								template = scope.templates.image;
							break;
						}
						
						// Prepare variables for the template, so that we don't
						// unnecessarily watch variables.
						scope.tpl = {
								appId: scope.app.identification.uniqueId,
								iconBg: scope.app.menu.bgColor,
								iconSize: "tile-" + scope.app.menu.icon.replace(/{.*-(.*)}.*/, "$1"),
								iconName: scope.app.menu.icon.replace(/{.*}(.*)/, "$1"),
								titleKey: scope.app.identification.titleKey,
								translationsGroup: scope.app.instantiation.translationsGroup,
								showTitle: scope.app.menu.showTitle
							}
							
							// Compile and append template.
							elem.html($compile(template)(scope));	
						
					}
				});
			}
		}
	}
})();
