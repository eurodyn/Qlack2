/*
* Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
*
* Licensed under the EUPL, Version 1.1 only (the "License").
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
* https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and
* limitations under the Licence.
*/
angular.module('QDate', [])
	.provider('QDateSrv', function() {
		this.$get = function($window, $translate, $rootScope) {
			return new QDateService($window, $translate, $rootScope);
		}
	});

function QDateService($window, $translate, $rootScope) {
	// Initialise the service with the locale used by angular-translate
	$window.moment.lang($translate.storage().get($translate.storageKey()));
	
	// Listen for changes to angular-translate's selected locale
	// and update moment's locale accordingly
	$rootScope.$on("$translateChangeEnd", function () {
		$window.moment.lang($translate.storage().get($translate.storageKey()));
	});

	// Function to allow changing the date service locale
	this.changeLocale = function(locale) {
		$window.moment.lang(locale);
	}

	this.localise = function(date, format) {
		var moment = $window.moment;
		var retVal = null;
		//Set the default format
		if (format == null) {
			format = 'l';
		}

		if (date != null) {
			retVal = moment(date).format(format);
		}
		return retVal;
	}
}

angular.module('QDate')
	.filter("qDate", ["QDateSrv", function (QDateSrv) {
		return function (date, format) {
			return QDateSrv.localise(date, format);
		};
	}]);
