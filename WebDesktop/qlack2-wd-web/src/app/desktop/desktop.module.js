/**
 * @ngdoc overview
 * @name app.desktop
 * @description app.desktop module
 */
(function() {
	"use strict";

	angular
		.module("wd.desktop", [
			"ds.clock",
			"uuid4",
			"components.util",
			"angular.img"
		]);
})();
