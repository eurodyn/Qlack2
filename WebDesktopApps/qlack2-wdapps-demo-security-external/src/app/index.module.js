/**
 * @ngdoc overview
 * @name index.module.js
 * @description index module
 */
(function() {
	"use strict";

	angular	// eslint-disable-line no-undef
		.module("qlack2WdappsDemoSecurityExternal", [
			//Project modules.

			"qlack2WdappsDemoSecurityExternal.home",

			// External modules.
      "QNgPubSub"
		]);
})();
