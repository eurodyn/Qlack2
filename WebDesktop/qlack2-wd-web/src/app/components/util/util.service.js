/**
 * @ngdoc service
 * @name util.factory.js
 * @description Various utility services.
 */
(function() {
	"use strict";

	angular // eslint-disable-line no-undef
		.module("components.util")
		.factory("UtilService", UtilService);

	/** @ngInject */
	function UtilService() {
		/***********************************************************************
		 * Local variables.
		 **********************************************************************/

		/***********************************************************************
		 * Local functions.
		 **********************************************************************/

		/***********************************************************************
		 * Exported functions and variables.
		 **********************************************************************/
		return {
			getMaxZIndexPlusOne: function() {
				var maxIndex = 0;
				var elems = typeof elems !== 'undefined' ? elems : angular.element("*");
				angular.element(elems).each(
					function() {
						maxIndex = (parseInt(maxIndex) < parseInt(angular.element(this).css(
									'z-index'))) ? parseInt(angular.element(this).css('z-index'))
									: maxIndex;
					});
				return maxIndex + 1;
			}
		};
	}
})();
