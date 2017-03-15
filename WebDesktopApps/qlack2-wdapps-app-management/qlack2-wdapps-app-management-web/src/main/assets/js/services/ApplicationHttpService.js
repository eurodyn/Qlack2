angular.module("appManagement").service("ApplicationHttpService", [
"$http", "SERVICES", function($http, SERVICES) {
	return {
		getApplicationsAsTree: function() {
			return $http({
				method: "GET",
				url: SERVICES._PREFIX + SERVICES.APPLICATIONS
			});
		},
		
		getApplicationById: function(appId) {
			return $http({
				method: "GET",
				url: SERVICES._PREFIX + SERVICES.APPLICATIONS + "/" + appId
			});
		},
		
		saveApplication: function(application) {
			return $http({
				method: "PUT",
				url: SERVICES._PREFIX + SERVICES.APPLICATIONS + "/" + application.id,
				data: application
			});
		}
	};
}]);
